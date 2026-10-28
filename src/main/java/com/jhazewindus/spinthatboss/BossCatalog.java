package com.jhazewindus.spinthatboss;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Wheel names and the corresponding names emitted by RuneLite's Loot Tracker. */
final class BossCatalog
{
	private static final List<String> BOSSES = Arrays.asList(
		"Abyssal Sire", "Alchemical Hydra", "Amoxliatl", "Araxxor", "Artio", "Barrows",
		"Brutus", "Bryophyta", "Callisto", "Cal'varion", "Cerberus", "Chambers of Xeric",
		"Chaos Elemental", "Chaos Fanatic", "Commander Zilyana", "Corporeal Beast",
		"Crazy Archaeologist", "Dagannoth Prime", "Dagannoth Rex", "Dagannoth Supreme",
		"Deranged Archaeologist", "Doom of Mokhaiotl", "Duke Sucellus", "Fortis Colosseum",
		"Fractured Archive", "General Graardor", "Giant Mole", "Grotesque Guardians",
		"Guardians of the Rift", "Hespori", "Kalphite Queen", "King Black Dragon", "Kraken",
		"Kree'arra", "K'ril Tsutsaroth", "Mad Angel", "Maggot King", "Moons of Peril",
		"Nex", "Nightmare", "Obor", "Phantom Muspah", "Phosani's Nightmare",
		"Sarachnis", "Scorpia", "Scurrius", "Shellbane Gryphon", "Skotizo", "Spindel",
		"Tempoross", "The Corrupted Gauntlet", "The Gauntlet", "The Hueycoatl",
		"The Leviathan", "The Royal Titans", "The Whisperer", "Theatre of Blood",
		"Thermonuclear Smoke Devil", "Tombs of Amascut", "Vardorvis", "Venenatis",
		"Vet'ion", "Vorkath", "Wintertodt", "Yama", "Zalcano", "Zulrah"
	);
	private static final Map<String, String> LOOT_NAMES = new LinkedHashMap<>();

	static
	{
		for (String boss : BOSSES)
		{
			alias(boss, boss);
		}
		alias("Lunar Chest", "Moons of Peril");
		alias("Barrows Chest", "Barrows");
		alias("Dusk", "Grotesque Guardians");
		alias("Crystalline Hunllef", "The Gauntlet");
		alias("Corrupted Hunllef", "The Corrupted Gauntlet");
		alias("Gauntlet", "The Gauntlet");
		alias("Corrupted Gauntlet", "The Corrupted Gauntlet");
		alias("Reward pool (Tempoross)", "Tempoross");
		alias("Supply crate (Wintertodt)", "Wintertodt");
		alias("Reward cart (Wintertodt)", "Wintertodt");
		alias("Branda the Fire Queen", "The Royal Titans");
		alias("Eldric the Ice King", "The Royal Titans");
	}

	private BossCatalog()
	{
	}

	static String defaultPool()
	{
		return String.join(", ", BOSSES);
	}

	static List<String> bosses()
	{
		return Collections.unmodifiableList(BOSSES);
	}

	static boolean matchesLoot(String wheelBoss, String lootName)
	{
		if (wheelBoss == null || lootName == null)
		{
			return false;
		}
		String canonical = LOOT_NAMES.getOrDefault(lootName.trim().toLowerCase(Locale.ROOT), lootName);
		return wheelBoss.trim().equalsIgnoreCase(canonical);
	}

	static String wiseOldManMetric(String boss)
	{
		String metric = boss.trim().toLowerCase(Locale.ROOT).replace("'", "")
			.replace(' ', '_').replace('-', '_');
		switch (metric)
		{
			case "barrows":
				return "barrows_chests";
			case "moons_of_peril":
				return "lunar_chests";
			case "fractured_archive":
				return "the_fractured_archive";
			default:
				return metric;
		}
	}

	private static void alias(String lootName, String boss)
	{
		LOOT_NAMES.put(lootName.toLowerCase(Locale.ROOT), boss);
	}
}
