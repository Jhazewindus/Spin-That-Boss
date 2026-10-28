package com.jhazewindus.spinthatboss;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.io.IOException;
import java.util.UUID;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PartyChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.events.ServerNpcLoot;
import net.runelite.client.game.ItemManager;
import net.runelite.client.party.PartyMember;
import net.runelite.client.party.PartyService;
import net.runelite.client.party.WSClient;
import net.runelite.client.party.events.UserJoin;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
	name = "Spin That Boss",
	configName = "spin-that-boss",
	description = "Run a shared, randomized boss challenge with a RuneLite party",
	tags = {"boss", "party", "challenge", "loot"},
	enabledByDefault = false
)
public class SpinThatBossPlugin extends Plugin
{
	private static final Logger log = LoggerFactory.getLogger(SpinThatBossPlugin.class);
	private static final String SAVED_MATCH_KEY = "savedMatch";
	private static final String CONFIG_GROUP = "spin-that-boss";
	private static final String RARE_GOAL_FIRST = "FIRST_RARE";
	private static final String RARE_GOAL_EVERYONE = "EVERYONE_RARE";
	private static final String KILL_GOAL_EACH = "KILLS_EACH";
	private static final String KILL_GOAL_FIRST = "FIRST_KILLS";
	private static final String PET_GOAL_FIRST = "FIRST_PET";
	private static final String PET_GOAL_EVERYONE = "EVERYONE_PET";
	private static final String VALUE_GOAL_FIRST = "FIRST_VALUE";

	@Inject
	private SpinThatBossConfig config;
	@Inject
	private ConfigManager configManager;
	@Inject
	private PartyService partyService;
	@Inject
	private WSClient wsClient;
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private ItemManager itemManager;
	@Inject
	private Gson gson;
	@Inject
	private okhttp3.OkHttpClient httpClient;

	private SpinThatBossPanel panel;
	private NavigationButton navigationButton;
	private MatchState match;
	private final Map<Integer, Long> bossStartTimes = new HashMap<>();

	@Override
	protected void startUp()
	{
		wsClient.registerMessage(MatchMessage.class);
		loadSavedMatch();
		panel = new SpinThatBossPanel(this::startMatch, this::spin, this::endMatch);
		navigationButton = NavigationButton.builder()
			.tooltip("Spin That Boss")
			.icon(makeIcon())
			.priority(8)
			.panel(panel)
			.build();
		clientToolbar.addNavigation(navigationButton);
		refreshPanel();
		if (partyService.isInParty())
		{
			requestMatchSync();
		}
	}

	@Override
	protected void shutDown()
	{
		bossStartTimes.clear();
		if (navigationButton != null)
		{
			clientToolbar.removeNavigation(navigationButton);
		}
		wsClient.unregisterMessage(MatchMessage.class);
		panel = null;
		navigationButton = null;
	}

	@Provides
	SpinThatBossConfig provideConfig(ConfigManager manager)
	{
		return manager.getConfig(SpinThatBossConfig.class);
	}

	@Subscribe
	public synchronized void onPartyChanged(PartyChanged event)
	{
		refreshPanel();
		if (partyService.isInParty())
		{
			requestMatchSync();
		}
	}

	@Subscribe
	public synchronized void onUserJoin(UserJoin event)
	{
		if (match != null && !match.ended && isLocalHost())
		{
			PartyMember member = partyService.getMemberById(event.getMemberId());
			if (member != null)
			{
				addMember(member);
				persistMatch();
			}
			sendMatchState();
		}
	}

	@Subscribe
	public synchronized void onMatchMessage(MatchMessage message)
	{
		if ("sync-request".equals(message.operation))
		{
			if (match != null && !match.ended && isLocalHost())
			{
				sendMatchState();
			}
			return;
		}

		if ("state".equals(message.operation) && message.state != null)
		{
			PartyMember sender = partyService.getMemberById(message.getMemberId());
			if (sender != null && samePlayer(sender.getDisplayName(), message.state.hostName)
				&& message.state.partyId == partyService.getPartyId())
			{
				match = message.state;
				persistMatch();
				refreshPanel();
				fetchWiseOldManBaseline();
			}
			return;
		}

		PartyMember sender = partyService.getMemberById(message.getMemberId());
		String senderKey = sender == null ? null : playerKey(sender.getDisplayName());
		if (!"progress".equals(message.operation) || message.memberProgress == null || senderKey == null
			|| match == null || match.ended || !match.matchId.equals(message.matchId)
			|| !sameBoss(match.currentBoss, message.boss)
			|| !match.memberNames.contains(senderKey))
		{
			return;
		}

		PlayerProgress progress = message.memberProgress;
		progress.memberName = sender.getDisplayName();
		match.progress.put(senderKey, progress);
		updateGoalCompletion();
		persistMatch();
		refreshPanel();
	}

	@Subscribe
	public synchronized void onServerNpcLoot(ServerNpcLoot event)
	{
		if (match == null || match.ended || match.currentBoss == null || match.legComplete
			|| !partyService.isInParty() || event.getComposition() == null || event.getItems() == null
			|| !sameBoss(match.currentBoss, event.getComposition().getName()))
		{
			return;
		}

		PartyMember localMember = partyService.getLocalMember();
		if (localMember == null)
		{
			return;
		}

		PlayerProgress progress = getOrCreateProgress(localMember.getDisplayName());
		progress.kills++;
		progress.currentLegKills++;
		for (net.runelite.client.game.ItemStack item : event.getItems())
		{
			String itemName = itemManager.getItemComposition(item.getId()).getName();
			long value = Math.max(0, itemManager.getItemPrice(item.getId())) * (long) item.getQuantity();
			progress.totalDropValue += value;
			progress.currentLegDropValue += value;
			if (value > progress.mostValuableDrop)
			{
				progress.mostValuableDrop = value;
				progress.mostValuableDropName = itemName;
			}
			if (isConfiguredRare(match.currentBoss, itemName))
			{
				progress.rareDrops.add(itemName);
				progress.currentLegRareDrops.add(itemName);
			}
		}
		recordFastestKill(progress);

		updateGoalCompletion();
		persistMatch();
		sendProgress(progress);
		refreshPanel();
	}

	@Subscribe
	public synchronized void onNpcSpawned(NpcSpawned event)
	{
		if (match != null && !match.ended && match.currentBoss != null
			&& sameBoss(match.currentBoss, event.getNpc().getName()))
		{
			bossStartTimes.put(event.getNpc().getIndex(), System.nanoTime());
		}
	}

	@Subscribe
	public synchronized void onChatMessage(ChatMessage event)
	{
		if (match == null || match.ended || match.currentBoss == null
			|| event.getType() != ChatMessageType.GAMEMESSAGE || event.getMessage() == null)
		{
			return;
		}
		String message = event.getMessage().replaceAll("<[^>]*>", "").toLowerCase(Locale.ROOT);
		if (!message.contains("you have a funny feeling like you're being followed")
			&& !message.contains("you feel something weird sneaking into your backpack"))
		{
			return;
		}
		PartyMember localMember = partyService.getLocalMember();
		if (localMember == null)
		{
			return;
		}
		PlayerProgress progress = getOrCreateProgress(localMember.getDisplayName());
		progress.hasPet = true;
		progress.currentLegHasPet = true;
		updateGoalCompletion();
		persistMatch();
		sendProgress(progress);
		refreshPanel();
	}

	private void recordFastestKill(PlayerProgress progress)
	{
		if (bossStartTimes.isEmpty())
		{
			return;
		}
		long started = bossStartTimes.values().stream().mapToLong(Long::longValue).min().orElse(0);
		bossStartTimes.clear();
		long duration = Math.max(1, (System.nanoTime() - started) / 1_000_000L);
		if (progress.fastestKillMillis == 0 || duration < progress.fastestKillMillis)
		{
			progress.fastestKillMillis = duration;
			progress.fastestKillBoss = match.currentBoss;
		}
	}

	private synchronized void startMatch()
	{
		if (!partyService.isInParty())
		{
			return;
		}
		PartyMember host = partyService.getLocalMember();
		if (host == null)
		{
			return;
		}

		List<String> pool = BossWheel.parsePool(config.bossPool());
		if (pool.isEmpty())
		{
			log.warn("Cannot start a boss match with an empty boss pool");
			return;
		}

		match = new MatchState();
		match.matchId = UUID.randomUUID().toString();
		match.partyId = partyService.getPartyId();
		match.hostName = host.getDisplayName();
		match.bossPool = pool;
		match.remainingBosses = BossWheel.shuffledBag(pool, null);
		match.goalType = normalizeGoal(config.goalType());
		match.killsPerMember = Math.max(1, config.killsPerMember());
		match.valueTarget = Math.max(1, config.valueTarget());
		for (PartyMember member : partyService.getMembers())
		{
			addMember(member);
		}
		addMember(host);
		persistMatch();
		sendMatchState();
		refreshPanel();
	}

	private synchronized void spin()
	{
		if (!isLocalHost() || match.ended || (match.currentBoss != null && !match.legComplete))
		{
			return;
		}

		if (match.currentBoss != null)
		{
			match.history.add("Leg " + match.legNumber + ": " + match.currentBoss + " — " + legSummary());
			for (PlayerProgress progress : match.progress.values())
			{
				progress.currentLegKills = 0;
				progress.currentLegDropValue = 0;
				progress.currentLegHasPet = false;
				progress.currentLegRareDrops.clear();
			}
			bossStartTimes.clear();
		}
		if (match.remainingBosses.isEmpty())
		{
			match.remainingBosses = BossWheel.shuffledBag(match.bossPool, match.currentBoss);
		}
		match.currentBoss = match.remainingBosses.remove(0);
		match.legNumber++;
		match.legComplete = false;
		for (PlayerProgress progress : match.progress.values())
		{
			progress.wiseOldManBaseline = -1;
			progress.baselineBoss = null;
		}
		persistMatch();
		sendMatchState();
		fetchWiseOldManBaseline();
		refreshPanel();
	}

	private synchronized void endMatch()
	{
		if (!isLocalHost() || match.ended)
		{
			return;
		}
		match.ended = true;
		if (match.currentBoss != null)
		{
			match.history.add("Leg " + match.legNumber + ": " + match.currentBoss
				+ (match.legComplete ? " — complete" : " — ended early"));
		}
		persistMatch();
		sendMatchState();
		refreshPanel();
	}

	private void updateGoalCompletion()
	{
		if (match == null || match.currentBoss == null)
		{
			return;
		}
		if (RARE_GOAL_FIRST.equals(match.goalType))
		{
			match.legComplete = match.progress.values().stream()
				.anyMatch(p -> !p.currentLegRareDrops.isEmpty());
		}
		else if (RARE_GOAL_EVERYONE.equals(match.goalType))
		{
			match.legComplete = !match.memberNames.isEmpty() && match.memberNames.stream()
				.allMatch(name -> match.progress.containsKey(name)
					&& !match.progress.get(name).currentLegRareDrops.isEmpty());
		}
		else if (PET_GOAL_FIRST.equals(match.goalType))
		{
			match.legComplete = match.progress.values().stream().anyMatch(p -> p.currentLegHasPet);
		}
		else if (PET_GOAL_EVERYONE.equals(match.goalType))
		{
			match.legComplete = !match.memberNames.isEmpty() && match.memberNames.stream()
				.allMatch(name -> match.progress.containsKey(name) && match.progress.get(name).currentLegHasPet);
		}
		else if (KILL_GOAL_FIRST.equals(match.goalType))
		{
			match.legComplete = match.progress.values().stream()
				.anyMatch(p -> p.currentLegKills >= match.killsPerMember);
		}
		else if (VALUE_GOAL_FIRST.equals(match.goalType))
		{
			match.legComplete = match.progress.values().stream()
				.anyMatch(p -> p.currentLegDropValue >= match.valueTarget);
		}
		else
		{
			match.legComplete = !match.memberNames.isEmpty() && match.memberNames.stream()
				.allMatch(name -> match.progress.containsKey(name)
					&& match.progress.get(name).currentLegKills >= match.killsPerMember);
		}
	}

	private String legSummary()
	{
		List<String> counts = new ArrayList<>();
		for (String memberName : match.memberNames)
		{
			PlayerProgress progress = match.progress.get(memberName);
			if (progress != null)
			{
				counts.add(displayName(progress) + ": " + progress.currentLegKills + " kills"
					+ (progress.currentLegRareDrops.isEmpty() ? "" : ", rare: " + progress.currentLegRareDrops));
			}
		}
		return String.join(" · ", counts);
	}

	private void addMember(PartyMember member)
	{
		String playerKey = playerKey(member.getDisplayName());
		if (!match.memberNames.contains(playerKey))
		{
			match.memberNames.add(playerKey);
		}
		PlayerProgress progress = getOrCreateProgress(member.getDisplayName());
		progress.memberName = member.getDisplayName();
	}

	private PlayerProgress getOrCreateProgress(String memberName)
	{
		String playerKey = playerKey(memberName);
		PlayerProgress progress = match.progress.get(playerKey);
		if (progress == null)
		{
			progress = new PlayerProgress();
			progress.memberName = memberName;
			match.progress.put(playerKey, progress);
		}
		return progress;
	}

	private void sendProgress(PlayerProgress progress)
	{
		MatchMessage message = new MatchMessage();
		message.operation = "progress";
		message.matchId = match.matchId;
		message.boss = match.currentBoss;
		message.memberProgress = gson.fromJson(gson.toJson(progress), PlayerProgress.class);
		partyService.send(message);
	}

	private synchronized void fetchWiseOldManBaseline()
	{
		if (!config.wiseOldManBaseline() || match == null || match.ended || match.currentBoss == null)
		{
			return;
		}
		PartyMember localMember = partyService.getLocalMember();
		if (localMember == null)
		{
			return;
		}
		PlayerProgress progress = getOrCreateProgress(localMember.getDisplayName());
		if (progress.wiseOldManBaseline >= 0 && sameBoss(progress.baselineBoss, match.currentBoss))
		{
			return;
		}

		String matchId = match.matchId;
		String boss = match.currentBoss;
		String memberName = localMember.getDisplayName();
		String metric = bossMetric(boss);
		HttpUrl url = new HttpUrl.Builder()
			.scheme("https")
			.host("api.wiseoldman.net")
			.addPathSegment("v2")
			.addPathSegment("players")
			.addPathSegment(memberName)
			.build();
		Request request = new Request.Builder()
			.url(url)
			.header("User-Agent", "SpinThatBoss RuneLite plugin")
			.get()
			.build();
		httpClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException exception)
			{
				log.debug("Wise Old Man baseline request failed", exception);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException
			{
				try (Response closeableResponse = response)
				{
					if (!closeableResponse.isSuccessful() || closeableResponse.body() == null)
					{
						log.debug("Wise Old Man baseline request returned HTTP {}", closeableResponse.code());
						return;
					}
					WiseOldManPlayer player = gson.fromJson(closeableResponse.body().charStream(), WiseOldManPlayer.class);
					if (player == null || player.latestSnapshot == null || player.latestSnapshot.data == null
						|| player.latestSnapshot.data.bosses == null)
					{
						return;
					}
					WiseOldManPlayer.BossScore score = player.latestSnapshot.data.bosses.get(metric);
					if (score != null)
					{
						applyWiseOldManBaseline(matchId, boss, memberName, score.kills);
					}
				}
				catch (RuntimeException exception)
				{
					log.debug("Could not read Wise Old Man baseline response", exception);
				}
			}
		});
	}

	private synchronized void applyWiseOldManBaseline(String matchId, String boss, String memberName, int kills)
	{
		if (match == null || match.ended || !match.matchId.equals(matchId) || !sameBoss(match.currentBoss, boss))
		{
			return;
		}
		PlayerProgress progress = getOrCreateProgress(memberName);
		progress.wiseOldManBaseline = Math.max(0, kills);
		progress.baselineBoss = boss;
		persistMatch();
		sendProgress(progress);
		refreshPanel();
	}

	private static String bossMetric(String boss)
	{
		String metric = boss.trim().toLowerCase(Locale.ROOT).replace(' ', '_').replace('-', '_');
		if ("corrupted_gauntlet".equals(metric))
		{
			return "the_corrupted_gauntlet";
		}
		if ("gauntlet".equals(metric))
		{
			return "the_gauntlet";
		}
		return metric;
	}

	private void requestMatchSync()
	{
		MatchMessage request = new MatchMessage();
		request.operation = "sync-request";
		partyService.send(request);
	}

	private void sendMatchState()
	{
		if (match == null || !partyService.isInParty())
		{
			return;
		}
		MatchMessage message = new MatchMessage();
		message.operation = "state";
		message.state = gson.fromJson(gson.toJson(match), MatchState.class);
		partyService.send(message);
	}

	private boolean isLocalHost()
	{
		PartyMember localMember = partyService.getLocalMember();
		return match != null && localMember != null && partyService.getPartyId() == match.partyId
			&& samePlayer(localMember.getDisplayName(), match.hostName);
	}

	private void persistMatch()
	{
		if (match != null)
		{
			configManager.setConfiguration(CONFIG_GROUP, SAVED_MATCH_KEY, gson.toJson(match));
		}
	}

	private void loadSavedMatch()
	{
		String saved = configManager.getConfiguration("spin-that-boss", SAVED_MATCH_KEY);
		if (saved == null || saved.trim().isEmpty())
		{
			return;
		}
		try
		{
			match = gson.fromJson(saved, MatchState.class);
		}
		catch (JsonParseException ex)
		{
			log.warn("Ignoring invalid saved Spin That Boss match state", ex);
		}
	}

	private void refreshPanel()
	{
		if (panel == null)
		{
			return;
		}
		boolean inParty = partyService.isInParty();
		boolean host = isLocalHost();
		StringBuilder playerSummary = new StringBuilder();
		StringBuilder historySummary = new StringBuilder();
		if (match != null)
		{
			for (String memberName : match.memberNames)
			{
				PlayerProgress progress = match.progress.get(memberName);
				if (progress == null)
				{
					continue;
				}
				playerSummary.append(displayName(progress))
					.append("\nMatch kills: ").append(progress.kills)
					.append("\nThis leg: ").append(progress.currentLegKills).append(" kills / ")
					.append(progress.currentLegDropValue).append(" GP");
				if (progress.currentLegHasPet)
				{
					playerSummary.append("\nPet: got one this leg");
				}
				if (progress.wiseOldManBaseline >= 0 && match.currentBoss != null
					&& match.currentBoss.equalsIgnoreCase(progress.baselineBoss))
				{
					playerSummary.append("\nWOM start KC: ").append(progress.wiseOldManBaseline);
				}
				if (progress.currentLegRareDrops.isEmpty())
				{
					playerSummary.append("\nRare: none yet");
				}
				else
				{
					playerSummary.append("\nRare: ").append(String.join(", ", progress.currentLegRareDrops));
				}
				if (progress.mostValuableDropName != null)
				{
					playerSummary.append("\nBest drop: ").append(progress.mostValuableDropName)
						.append(" (GE ").append(progress.mostValuableDrop).append(")");
				}
				if (progress.fastestKillMillis > 0)
				{
					playerSummary.append("\nFastest: ").append(progress.fastestKillBoss)
						.append(" ").append(String.format(Locale.ROOT, "%.1f", progress.fastestKillMillis / 1000.0)).append("s");
				}
				if (progress.hasPet)
				{
					playerSummary.append("\nMatch pet: yes");
				}
				playerSummary.append("\n\n");
			}
			for (String line : match.history)
			{
				historySummary.append(line).append('\n');
			}
		}
		String playerText = playerSummary.length() == 0 ? "Waiting for progress…" : playerSummary.toString().trim();
		String historyText = historySummary.length() == 0 ? "No completed boss legs yet." : historySummary.toString();
		SwingUtilities.invokeLater(() -> panel.showState(match, playerText, historyText, inParty, host));
	}

	private static String displayName(PlayerProgress progress)
	{
		if (progress.memberName != null && !progress.memberName.trim().isEmpty())
		{
			return progress.memberName;
		}
		return "Party member";
	}

	private static boolean samePlayer(String a, String b)
	{
		return a != null && b != null && a.trim().equalsIgnoreCase(b.trim());
	}

	private static String playerKey(String name)
	{
		return name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
	}

	private boolean isConfiguredRare(String boss, String itemName)
	{
		if (itemName == null)
		{
			return false;
		}
		for (String line : config.rareItems().split("\\R"))
		{
			int separator = line.indexOf(':');
			if (separator < 1 || !sameBoss(boss, line.substring(0, separator).trim()))
			{
				continue;
			}
			for (String rareName : line.substring(separator + 1).split("\\|"))
			{
				if (itemName.equalsIgnoreCase(rareName.trim()))
				{
					return true;
				}
			}
		}
		return false;
	}

	private static boolean sameBoss(String a, String b)
	{
		return a != null && b != null && a.trim().equalsIgnoreCase(b.trim());
	}

	private static String normalizeGoal(String value)
	{
		String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
		if (RARE_GOAL_EVERYONE.equals(normalized)
			|| KILL_GOAL_EACH.equals(normalized)
			|| KILL_GOAL_FIRST.equals(normalized)
			|| PET_GOAL_FIRST.equals(normalized)
			|| PET_GOAL_EVERYONE.equals(normalized)
			|| VALUE_GOAL_FIRST.equals(normalized))
		{
			return normalized;
		}
		return RARE_GOAL_FIRST;
	}

	private static BufferedImage makeIcon()
	{
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(new Color(227, 161, 45));
		graphics.fillOval(1, 1, 14, 14);
		graphics.setColor(new Color(77, 54, 29));
		graphics.drawLine(8, 8, 13, 5);
		graphics.drawLine(8, 8, 11, 13);
		graphics.drawLine(8, 8, 3, 11);
		graphics.setColor(Color.WHITE);
		graphics.fillOval(6, 6, 4, 4);
		graphics.dispose();
		return image;
	}
}
