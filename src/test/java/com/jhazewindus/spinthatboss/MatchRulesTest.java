package com.jhazewindus.spinthatboss;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class MatchRulesTest
{
	private MatchState state(String goal)
	{
		MatchState state = new MatchState();
		state.matchId = "match-one";
		state.partyId = 42;
		state.hostName = "Alice";
		state.legNumber = 1;
		state.currentBoss = "Zulrah";
		state.goalType = goal;
		state.killsPerMember = 2;
		state.valueTarget = 1000;
		state.bossPool.addAll(Arrays.asList("Zulrah", "Vorkath"));
		state.remainingBosses.add("Vorkath");
		for (String name : Arrays.asList("alice", "bob"))
		{
			state.memberNames.add(name);
			PlayerProgress progress = new PlayerProgress();
			progress.memberName = name;
			state.progress.put(name, progress);
		}
		return state;
	}

	@Test
	public void firstGoalsCompleteWhenOneMemberReachesTarget()
	{
		for (String goal : Arrays.asList("FIRST_RARE", "FIRST_PET", "FIRST_KILLS", "FIRST_VALUE"))
		{
			MatchState state = state(goal);
			MatchRules.updateGoal(state);
			assertFalse(goal, state.legComplete);
			meetGoal(state.progress.get("alice"));
			MatchRules.updateGoal(state);
			assertTrue(goal, state.legComplete);
		}
	}

	@Test
	public void everyoneGoalsRequireAllMembers()
	{
		for (String goal : Arrays.asList("EVERYONE_RARE", "EVERYONE_PET", "KILLS_EACH"))
		{
			MatchState state = state(goal);
			meetGoal(state.progress.get("alice"));
			MatchRules.updateGoal(state);
			assertFalse(goal, state.legComplete);
			meetGoal(state.progress.get("bob"));
			MatchRules.updateGoal(state);
			assertTrue(goal, state.legComplete);
		}
	}

	@Test
	public void matchTotalsDoNotSatisfyCurrentLegGoals()
	{
		for (String goal : Arrays.asList("FIRST_RARE", "FIRST_PET", "FIRST_KILLS", "FIRST_VALUE"))
		{
			MatchState state = state(goal);
			PlayerProgress player = state.progress.get("alice");
			player.kills = 100;
			player.totalDropValue = 100_000;
			player.hasPet = true;
			player.rareDrops.add("Tanzanite fang");
			MatchRules.updateGoal(state);
			assertFalse(goal, state.legComplete);
		}
	}

	@Test
	public void missingMemberAndEmptyRosterDoNotCompleteEveryoneGoal()
	{
		MatchState state = state("KILLS_EACH");
		meetGoal(state.progress.get("alice"));
		state.progress.remove("bob");
		MatchRules.updateGoal(state);
		assertFalse(state.legComplete);
		state.memberNames.clear();
		MatchRules.updateGoal(state);
		assertFalse(state.legComplete);
	}

	@Test
	public void outsiderProgressDoesNotCompleteGoal()
	{
		MatchState state = state("FIRST_KILLS");
		PlayerProgress outsider = new PlayerProgress();
		meetGoal(outsider);
		state.progress.put("outsider", outsider);
		MatchRules.updateGoal(state);
		assertFalse(state.legComplete);
	}

	@Test
	public void completedLegCannotBeReopenedByOlderProgress()
	{
		MatchState state = state("FIRST_KILLS");
		meetGoal(state.progress.get("alice"));
		MatchRules.updateGoal(state);
		state.progress.get("alice").currentLegKills = 0;
		MatchRules.updateGoal(state);
		assertTrue(state.legComplete);
	}

	@Test
	public void progressMustMatchPartyMatchLegBossAndMember()
	{
		MatchState state = state("FIRST_KILLS");
		MatchMessage message = progressMessage(state);
		assertTrue(MatchRules.acceptsProgress(state, 42, "alice", message));
		assertFalse(MatchRules.acceptsProgress(state, 99, "alice", message));
		assertFalse(MatchRules.acceptsProgress(state, 42, "outsider", message));
		message.legNumber = 0;
		assertFalse(MatchRules.acceptsProgress(state, 42, "alice", message));
		message.legNumber = 1;
		message.matchId = "older-match";
		assertFalse(MatchRules.acceptsProgress(state, 42, "alice", message));
		message.matchId = state.matchId;
		message.boss = "Vorkath";
		assertFalse(MatchRules.acceptsProgress(state, 42, "alice", message));
		message.boss = state.currentBoss;
		state.ended = true;
		assertFalse(MatchRules.acceptsProgress(state, 42, "alice", message));
	}

	@Test
	public void laterVisitToSameBossRejectsEarlierLegProgress()
	{
		MatchState state = state("FIRST_KILLS");
		MatchMessage old = progressMessage(state);
		state.legNumber = 3;
		assertFalse(MatchRules.acceptsProgress(state, 42, "alice", old));
	}

	@Test
	public void stateMustComeFromHostAndCurrentParty()
	{
		MatchState incoming = state("FIRST_KILLS");
		assertTrue(MatchRules.acceptsState(null, incoming, 42, "ALICE"));
		assertFalse(MatchRules.acceptsState(null, incoming, 42, "Bob"));
		assertFalse(MatchRules.acceptsState(null, incoming, 99, "Alice"));
	}

	@Test
	public void activeMatchCannotBeReplacedOrRewound()
	{
		MatchState current = state("FIRST_KILLS");
		MatchState incoming = state("FIRST_KILLS");
		incoming.matchId = "other-match";
		incoming.hostName = "Bob";
		assertFalse(MatchRules.acceptsState(current, incoming, 42, "Bob"));
		current.ended = true;
		assertTrue(MatchRules.acceptsState(current, incoming, 42, "Bob"));
		incoming.hostName = "Alice";
		incoming.matchId = current.matchId;
		assertFalse(MatchRules.acceptsState(current, incoming, 42, "Alice"));
		current.ended = false;
		current.legNumber = 3;
		assertFalse(MatchRules.acceptsState(current, incoming, 42, "Alice"));
	}

	@Test
	public void rejoinerCanReceiveHostsNextMatchOrEndMessage()
	{
		MatchState current = state("FIRST_KILLS");
		MatchState incoming = state("FIRST_KILLS");
		incoming.matchId = "next-match";
		assertTrue(MatchRules.acceptsState(current, incoming, 42, "Alice"));
		incoming.matchId = current.matchId;
		current.legComplete = true;
		incoming.ended = true;
		assertTrue(MatchRules.acceptsState(current, incoming, 42, "Alice"));
	}

	@Test
	public void incompleteOrMalformedStateIsRejected()
	{
		assertFalse(MatchRules.validState(null));
		assertFalse(MatchRules.validState(new MatchState()));
		MatchState state = state("FIRST_KILLS");
		assertTrue(MatchRules.validState(state));
		state.progress.get("alice").currentLegRareDrops = null;
		assertFalse(MatchRules.validState(state));
		state = state("FIRST_KILLS");
		state.bossPool = null;
		assertFalse(MatchRules.validState(state));
		state = state("FIRST_KILLS");
		state.memberNames.add(null);
		assertFalse(MatchRules.validState(state));
	}

	@Test
	public void hostSnapshotCannotEraseNewerLocalLoot()
	{
		MatchState current = state("FIRST_KILLS");
		MatchState incoming = state("FIRST_KILLS");
		meetGoal(current.progress.get("alice"));
		assertSame(current.progress.get("alice"), MatchRules.retainLocalProgress(current, incoming, "alice"));
		assertEquals(2, incoming.progress.get("alice").currentLegKills);
		assertTrue(incoming.legComplete);
		assertEquals(0, incoming.progress.get("bob").currentLegKills);
	}

	@Test
	public void newLegDoesNotRetainPreviousLegProgress()
	{
		MatchState current = state("FIRST_KILLS");
		MatchState incoming = state("FIRST_KILLS");
		incoming.legNumber = 2;
		meetGoal(current.progress.get("alice"));
		assertNull(MatchRules.retainLocalProgress(current, incoming, "alice"));
		assertEquals(0, incoming.progress.get("alice").currentLegKills);
		assertFalse(incoming.legComplete);
	}

	@Test
	public void unknownAndBlankNamesAreNotMembers()
	{
		assertFalse(MatchRules.validName(null));
		assertFalse(MatchRules.validName("  "));
		assertFalse(MatchRules.validName("<unknown>"));
		assertTrue(MatchRules.validName("Alice"));
	}

	private MatchMessage progressMessage(MatchState state)
	{
		MatchMessage message = new MatchMessage();
		message.matchId = state.matchId;
		message.legNumber = state.legNumber;
		message.boss = state.currentBoss;
		message.memberProgress = state.progress.get("alice");
		return message;
	}

	private void meetGoal(PlayerProgress progress)
	{
		progress.currentLegKills = 2;
		progress.currentLegDropValue = 1000;
		progress.currentLegHasPet = true;
		progress.currentLegRareDrops.add("Tanzanite fang");
	}
}
