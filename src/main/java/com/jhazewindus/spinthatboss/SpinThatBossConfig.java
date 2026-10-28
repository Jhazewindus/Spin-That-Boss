package com.jhazewindus.spinthatboss;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("spin-that-boss")
public interface SpinThatBossConfig extends Config
{
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
}
