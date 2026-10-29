package com.jhazewindus.spinthatboss;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.runelite.client.ui.PluginPanel;

final class SpinThatBossPanel extends PluginPanel
{
	private static final GoalChoice[] GOALS = {
		new GoalChoice("FIRST_KILLS", "First to kill target"),
		new GoalChoice("KILLS_EACH", "Everyone reaches kill target"),
		new GoalChoice("FIRST_VALUE", "First to loot-value target"),
		new GoalChoice("FIRST_UNIQUE", "First to chosen boss unique"),
		new GoalChoice("EVERYONE_UNIQUE", "Everyone gets chosen boss unique"),
		new GoalChoice("FIRST_PET", "First to pet"),
		new GoalChoice("EVERYONE_PET", "Everyone gets pet")
	};

	private final CardLayout cards = new CardLayout();
	private final JLabel partyLabel = new JLabel("Not in a RuneLite party", SwingConstants.CENTER);
	private final JLabel matchLabel = new JLabel("No run running", SwingConstants.CENTER);
	private final JLabel bossLabel = new JLabel("Current boss: —");
	private final JLabel goalLabel = new JLabel("Goal: —");
	private final JTextArea players = new JTextArea(8, 22);
	private final JTextArea history = new JTextArea(6, 22);
	private final JButton setupButton = new JButton("Setup run");
	private final JButton spinButton = new JButton("Spin the wheel");
	private final JButton endButton = new JButton("End run");
	private final JButton startButton = new JButton("Start run");
	private final JButton createPartyButton = new JButton("Create party and invite friends");
	private final JTextField inviteCode = new JTextField();
	private final JComboBox<String> uniqueChoice = new JComboBox<>();
	private final JLabel setupStatus = new JLabel(" ");
	private final JTextField search = new JTextField();
	private final JPanel bossChoices = new JPanel();
	private final Map<String, JCheckBox> bossChecks = new LinkedHashMap<>();
	private final JComboBox<GoalChoice> goalChoice = new JComboBox<>(GOALS);
	private final JSpinner killTarget = new JSpinner(new SpinnerNumberModel(25, 1, Integer.MAX_VALUE, 1));
	private final JSpinner valueTarget = new JSpinner(new SpinnerNumberModel(100_000_000L, 1L, Long.MAX_VALUE, 1_000_000L));
	private final JPanel killRow = new JPanel(new BorderLayout(4, 2));
	private final JPanel valueRow = new JPanel(new BorderLayout(4, 2));
	private final JPanel rareRow = new JPanel(new BorderLayout(4, 2));
	private boolean inParty;
	private boolean setupVisible;

	SpinThatBossPanel(Consumer<RunSetup> createParty, Consumer<RunSetup> start, Runnable spin, Runnable end, RunSetup initial)
	{
		super(false);
		setLayout(cards);
		setPreferredSize(new Dimension(250, 480));
		add(createRunView(spin, end), "run");
		add(createSetupView(createParty, start), "setup");
		applySetup(initial);
		cards.show(this, "run");
	}

	SpinThatBossPanel(Consumer<RunSetup> start, Runnable spin, Runnable end, RunSetup initial)
	{
		this(setup -> { }, start, spin, end, initial);
	}

	private JPanel createRunView(Runnable spin, Runnable end)
	{
		JPanel view = new JPanel(new BorderLayout(6, 6));
		view.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		JLabel title = new JLabel("Spin That Boss", SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
		JPanel header = new JPanel(new GridLayout(0, 1, 0, 4));
		header.add(title);
		header.add(partyLabel);
		header.add(matchLabel);
		header.add(bossLabel);
		header.add(goalLabel);
		view.add(header, BorderLayout.NORTH);

		players.setEditable(false);
		players.setLineWrap(true);
		players.setWrapStyleWord(true);
		history.setEditable(false);
		history.setLineWrap(true);
		history.setWrapStyleWord(true);
		JPanel details = new JPanel(new BorderLayout(4, 4));
		details.add(new JLabel("Party cards"), BorderLayout.NORTH);
		details.add(new JScrollPane(players), BorderLayout.CENTER);
		JPanel historyPanel = new JPanel(new BorderLayout(4, 4));
		historyPanel.add(new JLabel("Run history"), BorderLayout.NORTH);
		historyPanel.add(new JScrollPane(history), BorderLayout.CENTER);
		JPanel center = new JPanel(new GridLayout(2, 1, 0, 8));
		center.add(details);
		center.add(historyPanel);
		view.add(center, BorderLayout.CENTER);

		setupButton.addActionListener(e -> openSetup());
		spinButton.addActionListener(e -> spin.run());
		endButton.addActionListener(e -> end.run());
		JPanel actions = new JPanel(new GridLayout(0, 1, 0, 4));
		actions.add(setupButton);
		actions.add(spinButton);
		actions.add(endButton);
		view.add(actions, BorderLayout.SOUTH);
		return view;
	}

	private JPanel createSetupView(Consumer<RunSetup> createParty, Consumer<RunSetup> start)
	{
		JPanel view = new JPanel(new BorderLayout(6, 6));
		view.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		JLabel title = new JLabel("Setup run", SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
		JPanel top = new JPanel(new GridLayout(0, 1, 0, 4));
		top.add(title);
		top.add(new JLabel("Choose bosses for the wheel"));
		search.setToolTipText("Search bosses");
		top.add(search);
		JPanel selection = new JPanel(new GridLayout(1, 2, 4, 0));
		JButton selectAll = new JButton("Select all");
		selectAll.addActionListener(e -> bossChecks.values().forEach(check -> check.setSelected(true)));
		JButton clear = new JButton("Clear all");
		clear.addActionListener(e -> bossChecks.values().forEach(check -> check.setSelected(false)));
		selection.add(selectAll);
		selection.add(clear);
		top.add(selection);

		bossChoices.setLayout(new BoxLayout(bossChoices, BoxLayout.Y_AXIS));
		for (String boss : BossCatalog.bosses())
		{
			JCheckBox check = new JCheckBox(boss);
			check.addActionListener(e -> updateUniqueChoices());
			bossChecks.put(boss, check);
			bossChoices.add(check);
		}
		search.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override public void insertUpdate(DocumentEvent e) { filterBosses(); }
			@Override public void removeUpdate(DocumentEvent e) { filterBosses(); }
			@Override public void changedUpdate(DocumentEvent e) { filterBosses(); }
		});
		JScrollPane bossScroll = new JScrollPane(bossChoices);
		bossScroll.setPreferredSize(new Dimension(220, 200));

		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
		options.add(new JLabel("Goal for each boss"));
		options.add(goalChoice);
		killRow.add(new JLabel("Kills needed"), BorderLayout.NORTH);
		killRow.add(killTarget, BorderLayout.CENTER);
		valueRow.add(new JLabel("Loot value needed (GP)"), BorderLayout.NORTH);
		valueRow.add(valueTarget, BorderLayout.CENTER);
		rareRow.add(new JLabel("Boss unique drop"), BorderLayout.NORTH);
		rareRow.add(uniqueChoice, BorderLayout.CENTER);
		options.add(killRow);
		options.add(valueRow);
		options.add(rareRow);
		goalChoice.addActionListener(e -> updateGoalFields());
		JPanel middle = new JPanel(new BorderLayout(4, 6));
		middle.add(bossScroll, BorderLayout.CENTER);
		middle.add(options, BorderLayout.SOUTH);
		JPanel content = new JPanel(new BorderLayout(4, 6));
		content.add(top, BorderLayout.NORTH);
		content.add(middle, BorderLayout.CENTER);
		view.add(new JScrollPane(content), BorderLayout.CENTER);

		startButton.addActionListener(e -> {
			try
			{
				if (killRow.isVisible())
				{
					killTarget.commitEdit();
				}
				if (valueRow.isVisible())
				{
					valueTarget.commitEdit();
				}
			}
			catch (ParseException ex)
			{
				setupStatus.setText("Enter a valid target number.");
				return;
			}
			RunSetup setup = selectedSetup();
			if (setup.bosses.isEmpty())
			{
				setupStatus.setText("Select at least one boss.");
				return;
			}
			if (!inParty)
			{
				setupStatus.setText("Create a party and share its invite code first.");
				return;
			}
			setupStatus.setText(" ");
			start.accept(setup);
		});
		createPartyButton.addActionListener(e -> createParty.accept(selectedSetup()));
		JButton testSpin = new JButton("Test spin");
		testSpin.addActionListener(e -> {
			List<String> pool = selectedSetup().bosses;
			if (pool.isEmpty()) { setupStatus.setText("Select a boss to preview."); return; }
			String boss = pool.get(new java.util.Random().nextInt(pool.size()));
			JOptionPane.showMessageDialog(this, "The wheel lands on\\n\\n" + boss + "\\n\\nPreview only — no run has started.", "Spin That Boss · Test spin", JOptionPane.INFORMATION_MESSAGE);
		});
		inviteCode.setEditable(false);
		inviteCode.setToolTipText("Share this RuneLite Party passphrase with your friends");
		JButton copyInvite = new JButton("Copy invite");
		copyInvite.addActionListener(e -> { if (!inviteCode.getText().isEmpty()) java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(inviteCode.getText()), null); });
		JButton back = new JButton("Back");
		back.addActionListener(e -> closeSetup());
		JPanel bottom = new JPanel(new GridLayout(0, 1, 0, 3));
		bottom.add(setupStatus);
		bottom.add(createPartyButton);
		bottom.add(inviteCode);
		bottom.add(copyInvite);
		bottom.add(testSpin);
		bottom.add(startButton);
		bottom.add(back);
		view.add(bottom, BorderLayout.SOUTH);
		return view;
	}

	private void filterBosses()
	{
		String query = search.getText().trim().toLowerCase(Locale.ROOT);
		bossChecks.forEach((boss, check) -> check.setVisible(boss.toLowerCase(Locale.ROOT).contains(query)));
		bossChoices.revalidate();
		bossChoices.repaint();
	}

	private void updateGoalFields()
	{
		GoalChoice choice = (GoalChoice) goalChoice.getSelectedItem();
		String id = choice == null ? "FIRST_KILLS" : choice.id;
		killRow.setVisible("FIRST_KILLS".equals(id) || "KILLS_EACH".equals(id));
		valueRow.setVisible("FIRST_VALUE".equals(id));
		rareRow.setVisible("FIRST_UNIQUE".equals(id) || "EVERYONE_UNIQUE".equals(id));
	}

	private RunSetup selectedSetup()
	{
		RunSetup setup = new RunSetup();
		setup.bosses = new ArrayList<>();
		bossChecks.forEach((boss, check) -> { if (check.isSelected()) { setup.bosses.add(boss); } });
		setup.goalType = ((GoalChoice) goalChoice.getSelectedItem()).id;
		setup.killTarget = ((Number) killTarget.getValue()).intValue();
		setup.valueTarget = ((Number) valueTarget.getValue()).longValue();
		setup.uniqueDrop = (String) uniqueChoice.getSelectedItem();
		return setup;
	}

	private void applySetup(RunSetup setup)
	{
		bossChecks.forEach((boss, check) -> check.setSelected(setup.bosses.contains(boss)));
		for (int i = 0; i < goalChoice.getItemCount(); i++)
		{
			if (goalChoice.getItemAt(i).id.equals(setup.goalType))
			{
				goalChoice.setSelectedIndex(i);
				break;
			}
		}
		killTarget.setValue(Math.max(1, setup.killTarget));
		valueTarget.setValue(Math.max(1L, setup.valueTarget));
		updateUniqueChoices();
		uniqueChoice.setSelectedItem(setup.uniqueDrop);
		updateGoalFields();
	}

	private void updateUniqueChoices()
	{
		String selected = (String) uniqueChoice.getSelectedItem();
		uniqueChoice.removeAllItems();
		uniqueChoice.addItem("Any unique drop");
		for (String item : BossUniqueCatalog.allDrops(selectedBosses())) uniqueChoice.addItem(item);
		if (selected != null) uniqueChoice.setSelectedItem(selected);
	}

	private List<String> selectedBosses()
	{
		List<String> selected = new ArrayList<>();
		bossChecks.forEach((boss, check) -> { if (check.isSelected()) selected.add(boss); });
		return selected;
	}

	void showInviteCode(String code)
	{
		inviteCode.setText(code == null ? "" : code);
		setupStatus.setText(code == null ? "Party not ready" : "Share this code in RuneLite Party so friends can join.");
	}

	private void openSetup()
	{
		setupVisible = true;
		setupStatus.setText(" ");
		cards.show(this, "setup");
	}

	private void closeSetup()
	{
		setupVisible = false;
		cards.show(this, "run");
	}

	void showState(MatchState state, String playerSummary, String matchHistory, boolean inParty, boolean isHost)
	{
		this.inParty = inParty;
		partyLabel.setText(inParty ? "RuneLite party connected" : "Join a party in RuneLite's Party panel");
		if (state == null)
		{
			matchLabel.setText("No run running");
			bossLabel.setText("Current boss: —");
			goalLabel.setText("Goal: —");
			players.setText("Set up a run to choose your bosses and goal.");
			history.setText("");
			setupButton.setEnabled(true);
			if (inParty && inviteCode.getText().isEmpty()) inviteCode.setText("Party connected — share its passphrase");
			spinButton.setEnabled(false);
			endButton.setEnabled(false);
			return;
		}

		matchLabel.setText(state.ended ? "Run ended" : "Run active · leg " + state.legNumber);
		bossLabel.setText("Current boss: " + (state.currentBoss == null ? "—" : state.currentBoss));
		goalLabel.setText(goalDescription(state));
		players.setText(playerSummary);
		history.setText(matchHistory);
		setupButton.setEnabled(state.ended);
		spinButton.setEnabled(inParty && isHost && !state.ended && (state.currentBoss == null || state.legComplete));
		endButton.setEnabled(inParty && isHost && !state.ended);
		if (!state.ended && setupVisible)
		{
			closeSetup();
		}
	}

	private static String goalDescription(MatchState state)
	{
		switch (state.goalType)
		{
			case "FIRST_UNIQUE": return "Goal: first player gets a boss unique";
			case "EVERYONE_UNIQUE": return "Goal: everyone gets a boss unique";
			case "EVERYONE_RARE": return "Goal: everyone gets a chosen rare drop";
			case "FIRST_RARE": return "Goal: first player gets a chosen rare drop";
			case "FIRST_PET": return "Goal: first player gets a pet";
			case "EVERYONE_PET": return "Goal: everyone gets a pet";
			case "FIRST_KILLS": return "Goal: first player reaches " + state.killsPerMember + " kills";
			case "KILLS_EACH": return "Goal: everyone reaches " + state.killsPerMember + " kills";
			case "FIRST_VALUE": return "<html>Goal: first to " + String.format(Locale.ROOT, "%,d", state.valueTarget)
				+ " GP<br>in drops</html>";
			default: return "Goal: —";
		}
	}

	private static final class GoalChoice
	{
		private final String id;
		private final String label;

		private GoalChoice(String id, String label)
		{
			this.id = id;
			this.label = label;
		}

		@Override
		public String toString()
		{
			return label;
		}
	}
}
