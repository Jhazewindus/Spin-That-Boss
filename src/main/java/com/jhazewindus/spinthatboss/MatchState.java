package com.jhazewindus.spinthatboss;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MatchState
{
	public String matchId;
	public long partyId;
	public String hostName;
	public boolean ended;
	public int legNumber;
	public String currentBoss;
	public String goalType;
	public int killsPerMember;
	public long valueTarget;
	public String rareItems;
	public boolean legComplete;
	public List<String> bossPool = new ArrayList<>();
	public List<String> remainingBosses = new ArrayList<>();
	public List<String> memberNames = new ArrayList<>();
	public Map<String, PlayerProgress> progress = new LinkedHashMap<>();
	public List<String> history = new ArrayList<>();
}
