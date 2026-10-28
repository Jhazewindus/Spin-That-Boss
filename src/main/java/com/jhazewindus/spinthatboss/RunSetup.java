package com.jhazewindus.spinthatboss;

import java.util.ArrayList;
import java.util.List;

/** Choices made by the host before a run starts. */
final class RunSetup
{
	List<String> bosses = new ArrayList<>(BossCatalog.bosses());
	String goalType = "FIRST_KILLS";
	int killTarget = 25;
	long valueTarget = 100_000_000L;
	String rareItems = "Zulrah: Tanzanite fang | Magic fang | Serpentine visage | Jar of swamp";
}
