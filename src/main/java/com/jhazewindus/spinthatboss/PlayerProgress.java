package com.jhazewindus.spinthatboss;

import java.util.ArrayList;
import java.util.List;

public class PlayerProgress
{
	public String memberName;
	public int kills;
	public int currentLegKills;
	public long totalDropValue;
	public long currentLegDropValue;
	public boolean hasPet;
	public boolean currentLegHasPet;
	public int wiseOldManBaseline = -1;
	public String baselineBoss;
	public long fastestKillMillis;
	public String fastestKillBoss;
	public List<String> rareDrops = new ArrayList<>();
	public List<String> currentLegRareDrops = new ArrayList<>();
	public long mostValuableDrop;
	public String mostValuableDropName;
}
