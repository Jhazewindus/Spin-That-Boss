package com.jhazewindus.spinthatboss;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/** Pure match rules shared by local events, saved state and party messages. */
final class MatchRules
{
	private MatchRules()
	{
	}

	static boolean validName(String name)
	{
		return name != null && !name.trim().isEmpty() && !"<unknown>".equals(name);
	}

	static boolean validProgress(PlayerProgress progress)
	{
		return progress != null && validName(progress.memberName)
			&& progress.kills >= 0 && progress.currentLegKills >= 0
			&& progress.totalDropValue >= 0 && progress.currentLegDropValue >= 0
			&& progress.fastestKillMillis >= 0 && progress.mostValuableDrop >= 0
			&& validStrings(progress.rareDrops) && validStrings(progress.currentLegRareDrops);
	}

	static boolean validState(MatchState state)
	{
		return state != null && validName(state.matchId) && validName(state.hostName)
			&& state.partyId != 0 && state.legNumber >= 0
			&& state.killsPerMember > 0 && state.valueTarget > 0
			&& validStrings(state.bossPool) && !state.bossPool.isEmpty()
			&& validStrings(state.remainingBosses) && state.bossPool.containsAll(state.remainingBosses)
			&& validStrings(state.memberNames) && !state.memberNames.isEmpty()
			&& validStrings(state.history) && state.progress != null
			&& state.progress.entrySet().stream().allMatch(e -> validName(e.getKey()) && validProgress(e.getValue()))
			&& (state.legNumber == 0 ? state.currentBoss == null : state.bossPool.contains(state.currentBoss))
			&& state.goalType != null;
	}

	static boolean acceptsState(MatchState current, MatchState incoming, long partyId, String sender)
	{
		if (!validState(incoming) || incoming.partyId != partyId || !sameName(sender, incoming.hostName))
		{
			return false;
		}
		if (current == null || current.partyId != partyId)
		{
			return true;
		}
		if (!Objects.equals(current.matchId, incoming.matchId))
		{
			// Rejoiners may have missed the host ending a match before starting the next.
			return current.ended || sameName(current.hostName, incoming.hostName);
		}
		return sameName(current.hostName, incoming.hostName)
			&& incoming.legNumber >= current.legNumber
			&& (!current.ended || incoming.ended)
			&& (incoming.ended || incoming.legNumber != current.legNumber || !current.legComplete || incoming.legComplete);
	}

	static boolean acceptsProgress(MatchState state, long partyId, String senderKey, MatchMessage message)
	{
		return state != null && !state.ended && state.partyId == partyId
			&& Objects.equals(state.matchId, message.matchId) && state.legNumber == message.legNumber
			&& sameName(state.currentBoss, message.boss) && state.memberNames.contains(senderKey)
			&& validProgress(message.memberProgress);
	}

	static PlayerProgress retainLocalProgress(MatchState current, MatchState incoming, String localKey)
	{
		if (current == null || !Objects.equals(current.matchId, incoming.matchId)
			|| current.legNumber != incoming.legNumber || !incoming.memberNames.contains(localKey))
		{
			return null;
		}
		PlayerProgress local = current.progress.get(localKey);
		if (validProgress(local))
		{
			// The local client owns its loot counters; a host snapshot may lag behind them.
			incoming.progress.put(localKey, local);
			updateGoal(incoming);
			return local;
		}
		return null;
	}

	static void updateGoal(MatchState state)
	{
		if (state == null || state.ended || state.currentBoss == null || state.legComplete)
		{
			return;
		}
		Predicate<PlayerProgress> goal;
		boolean everyone;
		switch (state.goalType)
		{
			case "FIRST_RARE":
			case "EVERYONE_RARE":
				goal = p -> !p.currentLegRareDrops.isEmpty();
				everyone = "EVERYONE_RARE".equals(state.goalType);
				break;
			case "FIRST_PET":
			case "EVERYONE_PET":
				goal = p -> p.currentLegHasPet;
				everyone = "EVERYONE_PET".equals(state.goalType);
				break;
			case "FIRST_VALUE":
				goal = p -> p.currentLegDropValue >= state.valueTarget;
				everyone = false;
				break;
			case "FIRST_KILLS":
			case "KILLS_EACH":
				goal = p -> p.currentLegKills >= state.killsPerMember;
				everyone = "KILLS_EACH".equals(state.goalType);
				break;
			default:
				return;
		}
		Predicate<String> memberGoal = name -> state.progress.containsKey(name) && goal.test(state.progress.get(name));
		state.legComplete = !state.memberNames.isEmpty() && (everyone
			? state.memberNames.stream().allMatch(memberGoal)
			: state.memberNames.stream().anyMatch(memberGoal));
	}

	private static boolean validStrings(List<String> values)
	{
		return values != null && values.stream().allMatch(MatchRules::validName);
	}

	private static boolean sameName(String first, String second)
	{
		return first != null && second != null && first.trim().equalsIgnoreCase(second.trim());
	}
}
