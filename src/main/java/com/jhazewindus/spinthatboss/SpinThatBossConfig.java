package com.jhazewindus.spinthatboss;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("spin-that-boss")
public interface SpinThatBossConfig extends Config
{
	@ConfigItem(
		keyName = "bossPool",
		name = "Bosses in wheel",
		description = "Comma-separated bosses locked into the wheel when a match starts"
	)
	default String bossPool()
	{
		return "Zulrah, Vorkath, Cerberus, Kraken";
	}

	@ConfigItem(
		keyName = "goalType",
		name = "Leg goal",
		description = "Goal: FIRST_RARE, EVERYONE_RARE, FIRST_PET, EVERYONE_PET, FIRST_KILLS, KILLS_EACH, or FIRST_VALUE"
	)
	default String goalType()
	{
		return "FIRST_RARE";
	}

	@ConfigItem(
		keyName = "killsPerMember",
		name = "Kills per member",
		description = "Kill target for KILLS_EACH legs"
	)
	default int killsPerMember()
	{
		return 25;
	}

	@ConfigItem(
		keyName = "valueTarget",
		name = "Drop value target",
		description = "Total Grand Exchange value for the FIRST_VALUE goal"
	)
	default long valueTarget()
	{
		return 100_000_000L;
	}

	@ConfigItem(
		keyName = "wiseOldManBaseline",
		name = "Use Wise Old Man baselines",
		description = "Fetch each member's boss kill count when a new boss leg starts",
		warning = "This feature submits your IP address to a 3rd-party server not controlled or verified by RuneLite developers"
	)
	default boolean wiseOldManBaseline()
	{
		return false;
	}

	@ConfigItem(
		keyName = "rareItems",
		name = "Rare items by boss",
		description = "One boss per line: Boss: Item name | Another item name"
	)
	default String rareItems()
	{
		return "Zulrah: Tanzanite fang | Magic fang | Serpentine visage | Jar of swamp";
	}
}
