package com.jhazewindus.spinthatboss;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import net.runelite.client.ui.PluginPanel;

final class SpinThatBossPanel extends PluginPanel
{
	private final JLabel partyLabel = new JLabel("Not in a RuneLite party", SwingConstants.CENTER);
	private final JLabel matchLabel = new JLabel("No match running", SwingConstants.CENTER);
	private final JLabel bossLabel = new JLabel("Current boss: —");
	private final JLabel goalLabel = new JLabel("Goal: —");
	private final JTextArea players = new JTextArea(8, 22);
	private final JTextArea history = new JTextArea(6, 22);
	private final JButton startButton = new JButton("Start match");
	private final JButton spinButton = new JButton("Spin the wheel");
	private final JButton endButton = new JButton("End match");

	SpinThatBossPanel(Runnable start, Runnable spin, Runnable end)
	{
		super(false);
		setLayout(new BorderLayout(6, 6));
		setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		setPreferredSize(new Dimension(250, 480));

		JLabel title = new JLabel("Spin That Boss", SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
		JPanel header = new JPanel(new GridLayout(0, 1, 0, 4));
		header.add(title);
		header.add(partyLabel);
		header.add(matchLabel);
		header.add(bossLabel);
		header.add(goalLabel);
		add(header, BorderLayout.NORTH);

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
		historyPanel.add(new JLabel("Match history"), BorderLayout.NORTH);
		historyPanel.add(new JScrollPane(history), BorderLayout.CENTER);
		JPanel center = new JPanel(new GridLayout(2, 1, 0, 8));
		center.add(details);
		center.add(historyPanel);
		add(center, BorderLayout.CENTER);

		startButton.addActionListener(e -> start.run());
		spinButton.addActionListener(e -> spin.run());
		endButton.addActionListener(e -> end.run());
		JPanel actions = new JPanel(new GridLayout(0, 1, 0, 4));
		actions.add(startButton);
		actions.add(spinButton);
		actions.add(endButton);
		add(actions, BorderLayout.SOUTH);
	}

	void showParty(boolean inParty)
	{
		partyLabel.setText(inParty ? "RuneLite party connected" : "Join a party in RuneLite's Party panel");
		startButton.setEnabled(inParty);
	}

	void showState(MatchState state, String playerSummary, String matchHistory, boolean inParty, boolean isHost)
	{
		showParty(inParty);
		if (state == null)
		{
			matchLabel.setText("No match running");
			bossLabel.setText("Current boss: —");
			goalLabel.setText("Goal: —");
			players.setText("Start a match to lock the selected boss pool.");
			history.setText("");
			startButton.setEnabled(inParty);
			spinButton.setEnabled(false);
			endButton.setEnabled(false);
			return;
		}

		matchLabel.setText(state.ended ? "Match ended" : "Match active · leg " + state.legNumber);
		bossLabel.setText("Current boss: " + (state.currentBoss == null ? "—" : state.currentBoss));
		goalLabel.setText(goalDescription(state));
		players.setText(playerSummary);
		history.setText(matchHistory);
		startButton.setEnabled(inParty && state.ended);
		spinButton.setEnabled(inParty && isHost && !state.ended && (state.currentBoss == null || state.legComplete));
		endButton.setEnabled(inParty && isHost && !state.ended);
	}

	private static String goalDescription(MatchState state)
	{
		if ("EVERYONE_RARE".equals(state.goalType))
		{
			return "Goal: one configured rare for everyone";
		}
		if ("FIRST_PET".equals(state.goalType))
		{
			return "Goal: first party member to get a pet";
		}
		if ("EVERYONE_PET".equals(state.goalType))
		{
			return "Goal: every party member gets a pet";
		}
		if ("FIRST_KILLS".equals(state.goalType))
		{
			return "Goal: first member to " + state.killsPerMember + " kills";
		}
		if ("KILLS_EACH".equals(state.goalType))
		{
			return "Goal: " + state.killsPerMember + " kills per member";
		}
		if ("FIRST_VALUE".equals(state.goalType))
		{
			return "Goal: first member to " + state.valueTarget + " GP in drops";
		}
		return "Goal: first configured rare";
	}
}
