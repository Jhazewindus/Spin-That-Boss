package com.jhazewindus.spinthatboss;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public final class SpinThatBossDev
{
	private SpinThatBossDev()
	{
	}

	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SpinThatBossPlugin.class);
		RuneLite.main(args);
	}
}
