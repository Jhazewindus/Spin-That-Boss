package com.jhazewindus.spinthatboss;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class BossCatalogTest
{
	@Test
	public void defaultWheelContainsRewardActivitiesOnce()
	{
		List<String> pool = BossWheel.parsePool(BossCatalog.defaultPool());
		for (String activity : new String[]{"Barrows", "Moons of Peril", "Chambers of Xeric",
			"Theatre of Blood", "Tombs of Amascut", "Fractured Archive"})
		{
			assertTrue(activity, pool.contains(activity));
		}
		assertFalse(pool.contains("Dharok"));
		assertFalse(pool.contains("Blood Moon"));
		assertFalse(pool.contains("The Maiden of Sugadinti"));
	}

	@Test
	public void rewardChestsCountForGroupedActivities()
	{
		assertTrue(BossCatalog.matchesLoot("Barrows", "Barrows"));
		assertTrue(BossCatalog.matchesLoot("Moons of Peril", "Lunar Chest"));
		assertTrue(BossCatalog.matchesLoot("Tombs of Amascut", "Tombs of Amascut"));
		assertTrue(BossCatalog.matchesLoot("Chambers of Xeric", "Chambers of Xeric"));
		assertFalse(BossCatalog.matchesLoot("Barrows", "Dharok"));
		assertFalse(BossCatalog.matchesLoot("Moons of Peril", "Blood Moon"));
	}

	@Test
	public void bossVariantsStaySeparate()
	{
		List<String> pool = BossWheel.parsePool(BossCatalog.defaultPool());
		for (String variant : new String[]{"Nightmare", "Phosani's Nightmare", "The Gauntlet",
			"The Corrupted Gauntlet", "Callisto", "Artio", "Venenatis", "Spindel",
			"Vet'ion", "Cal'varion"})
		{
			assertTrue(variant, pool.contains(variant));
		}
		assertTrue(BossCatalog.matchesLoot("The Gauntlet", "Crystalline Hunllef"));
		assertTrue(BossCatalog.matchesLoot("The Corrupted Gauntlet", "Corrupted Hunllef"));
		assertFalse(BossCatalog.matchesLoot("The Gauntlet", "Corrupted Hunllef"));
		assertFalse(BossCatalog.matchesLoot("Nightmare", "Phosani's Nightmare"));
	}

	@Test
	public void eventRewardsMatchTheirBossesOnly()
	{
		assertTrue(BossCatalog.matchesLoot("Tempoross", "Reward pool (Tempoross)"));
		assertTrue(BossCatalog.matchesLoot("Wintertodt", "Supply crate (Wintertodt)"));
		assertTrue(BossCatalog.matchesLoot("Grotesque Guardians", "Dusk"));
		assertFalse(BossCatalog.matchesLoot("Tempoross", "Supply crate (Wintertodt)"));
	}

	@Test
	public void groupedActivitiesAndApostrophesUseTheirHiscoreNames()
	{
		assertEquals("barrows_chests", BossCatalog.wiseOldManMetric("Barrows"));
		assertEquals("lunar_chests", BossCatalog.wiseOldManMetric("Moons of Peril"));
		assertEquals("the_fractured_archive", BossCatalog.wiseOldManMetric("Fractured Archive"));
		assertEquals("kreearra", BossCatalog.wiseOldManMetric("Kree'arra"));
		assertEquals("the_corrupted_gauntlet", BossCatalog.wiseOldManMetric("The Corrupted Gauntlet"));
	}
}
