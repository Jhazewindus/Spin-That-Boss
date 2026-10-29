package com.jhazewindus.spinthatboss;

import java.util.*;

/** Collection-log unique drops, sourced from the OSRS Wiki drop tables. */
final class BossUniqueCatalog {
 private static final Map<String, List<String>> DROPS = new LinkedHashMap<>();
 static {
  DROPS.put("Brutus", Arrays.asList("Bottomless milk bucket (empty)", "Cow slippers", "Mooleta"));
  DROPS.put("Obor", Arrays.asList("Hill giant club"));
  DROPS.put("Barrows", Arrays.asList("Ahrim's hood", "Ahrim's staff", "Ahrim's robetop", "Ahrim's robeskirt", "Dharok's helm", "Dharok's greataxe", "Dharok's platebody", "Dharok's platelegs", "Guthan's helm", "Guthan's warspear", "Guthan's platebody", "Guthan's chainskirt", "Karil's coif", "Karil's crossbow", "Karil's leathertop", "Karil's leatherskirt", "Bolt rack", "Torag's helm", "Torag's hammers", "Torag's platebody", "Torag's platelegs", "Verac's helm", "Verac's flail", "Verac's brassard", "Verac's plateskirt"));
  DROPS.put("Bryophyta", Arrays.asList("Bryophyta's essence"));
  DROPS.put("Scurrius", Arrays.asList("Scurrius' spine"));
  DROPS.put("Chaos Fanatic", Arrays.asList("Odium shard 1", "Malediction shard 1"));
  DROPS.put("Crazy Archaeologist", Arrays.asList("Odium shard 2", "Malediction shard 2", "Fedora"));
  DROPS.put("Scorpia", Arrays.asList("Dragon 2h sword", "Odium shard 3", "Malediction shard 3"));
  DROPS.put("Giant Mole", Arrays.asList("Mole claw", "Mole skin", "Immaculate mole skin"));
  DROPS.put("Shellbane gryphon", Arrays.asList("Belle's folly (tarnished)", "Gryphon feather", "Jar of Feathers"));
  DROPS.put("Grotesque Guardians", Arrays.asList("Granite dust", "Black tourmaline core", "Granite gloves", "Granite ring", "Granite hammer", "Jar of Stone"));
  DROPS.put("Amoxliatl", Arrays.asList("Glacial temotli", "Pendant of Ates (inert)", "Frozen tear"));
  DROPS.put("Cal'varion", Arrays.asList("Dragon 2h sword", "Dragon pickaxe", "Ring of the gods", "Skull of Vet'ion", "Voidwaker blade"));
  DROPS.put("King Black Dragon", Arrays.asList("Kbd heads", "Draconic visage", "Dragon pickaxe"));
  DROPS.put("Deranged Archaeologist", Arrays.asList("Steel ring"));
  DROPS.put("Hespori", Arrays.asList("Attas seed", "Iasor seed", "Kronos seed", "Bottomless compost bucket"));
  DROPS.put("Kraken", Arrays.asList("Trident of the Seas (full)", "Kraken tentacle", "Jar of Dirt"));
  DROPS.put("Thermonuclear Smoke Devil", Arrays.asList("Dragon chainbody", "Smoke battlestaff", "Occult necklace", "Jar of Smoke"));
  DROPS.put("Spindel", Arrays.asList("Dragon 2h sword", "Dragon pickaxe", "Treasonous ring", "Fangs of Venenatis", "Voidwaker gem"));
  DROPS.put("Dagannoth Prime", Arrays.asList("Seercull", "Archers ring", "Dragon axe", "Warrior ring", "Berserker ring", "Mud battlestaff", "Seers ring"));
  DROPS.put("Dagannoth Rex", Arrays.asList("Seercull", "Archers ring", "Dragon axe", "Warrior ring", "Berserker ring", "Mud battlestaff", "Seers ring"));
  DROPS.put("Dagannoth Supreme", Arrays.asList("Seercull", "Archers ring", "Dragon axe", "Warrior ring", "Berserker ring", "Mud battlestaff", "Seers ring"));
  DROPS.put("Chaos Elemental", Arrays.asList("Dragon 2h sword", "Dragon pickaxe"));
  DROPS.put("Sarachnis", Arrays.asList("Giant egg sac(full)", "Sarachnis cudgel", "Pristine spider silk", "Jar of Eyes"));
  DROPS.put("Cerberus", Arrays.asList("Eternal crystal", "Pegasian crystal", "Primordial crystal", "Smouldering stone", "Key master teleport", "Jar of Souls"));
  DROPS.put("Artio", Arrays.asList("Dragon 2h sword", "Dragon pickaxe", "Tyrannical ring", "Claws of Callisto", "Voidwaker hilt"));
  DROPS.put("Skotizo", Arrays.asList("Uncut onyx", "Ancient shard", "Dark totem", "Dark claw", "Jar of Darkness"));
  DROPS.put("Moons of Peril", Arrays.asList("Blue Moon spear", "Atlatl dart", "Dual macuahuitl", "Eclipse atlatl", "Eclipse Moon chestplate", "Eclipse Moon tassets", "Eclipse Moon helm", "Blue Moon chestplate", "Blue Moon tassets", "Blue Moon helm", "Blood Moon chestplate", "Blood Moon tassets", "Blood Moon helm"));
  DROPS.put("Kalphite Queen", Arrays.asList("Dragon chainbody", "Dragon 2h sword", "Kq head", "Dragon pickaxe", "Jar of Sand"));
  DROPS.put("Zalcano", Arrays.asList("Uncut onyx", "Zalcano shard", "Crystal tool seed"));
  DROPS.put("The Royal Titans", Arrays.asList("Deadeye prayer scroll", "Mystic vigour prayer scroll", "Ice element staff crown", "Fire element staff crown", "Giantsoul amulet", "Desiccated page"));
  DROPS.put("Abyssal Sire", Arrays.asList("Unsired", "Abyssal whip", "Abyssal dagger", "Abyssal head", "Bludgeon spine", "Bludgeon claw", "Bludgeon axon"));
  DROPS.put("Alchemical Hydra", Arrays.asList("Dragon thrownaxe", "Dragon knife", "Hydra's claw", "Hydra's heart", "Hydra's fang", "Hydra's eye", "Hydra leather", "Hydra tail", "Alchemical hydra heads", "Jar of Chemicals"));
  DROPS.put("Vet'ion", Arrays.asList("Dragon 2h sword", "Dragon pickaxe", "Ring of the gods", "Skull of Vet'ion", "Voidwaker blade"));
  DROPS.put("Venenatis", Arrays.asList("Dragon 2h sword", "Dragon pickaxe", "Treasonous ring", "Fangs of Venenatis", "Voidwaker gem"));
  DROPS.put("Callisto", Arrays.asList("Dragon 2h sword", "Dragon pickaxe", "Tyrannical ring", "Claws of Callisto", "Voidwaker hilt"));
  DROPS.put("Tombs of Amascut", Arrays.asList("Lightbearer", "Elidinis' ward", "Osmumten's fang", "Masori mask", "Masori body", "Masori chaps", "Cursed phalanx", "Menaphite ornament kit", "Tumeken's shadow (uncharged)", "Thread of Elidinis", "Breach of the Scarab", "Eye of the Corruptor", "Jewel of the Sun", "Cache of runes", "Masori crafting kit", "Remnant of Akkha", "Remnant of Ba-Ba", "Remnant of Kephri", "Remnant of Zebak", "Ancient remnant", "Jewel of Amascut"));
  DROPS.put("Chambers of Xeric", Arrays.asList("Dragon claws", "Twisted bow", "Twisted buckler", "Elder maul", "Dragon hunter crossbow", "Dinh's bulwark", "Ancestral hat", "Ancestral robe top", "Ancestral robe bottom", "Dark relic", "Dexterous prayer scroll", "Kodai insignia", "Torn prayer scroll", "Arcane prayer scroll", "Metamorphic dust", "Twisted ancestral colour kit", "Onyx"));
  DROPS.put("Doom of Mokhaiotl", Arrays.asList("Avernic treads", "Mokhaiotl cloth", "Mokhaiotl waystone", "Demon tear", "Eye of Ayak"));
  DROPS.put("Kree'arra", Arrays.asList("Armadyl hilt", "Godsword shard 1", "Godsword shard 2", "Godsword shard 3", "Armadyl helmet", "Armadyl chestplate", "Armadyl chainskirt"));
  DROPS.put("Mad Angel", Arrays.asList("Granite dust", "Hallowfell", "Ardeaglais teleport", "Jar of Light"));
  DROPS.put("Commander Zilyana", Arrays.asList("Armadyl crossbow", "Saradomin hilt", "Godsword shard 1", "Godsword shard 2", "Godsword shard 3", "Saradomin sword", "Saradomin's light"));
  DROPS.put("General Graardor", Arrays.asList("Bandos hilt", "Godsword shard 1", "Godsword shard 2", "Godsword shard 3", "Bandos chestplate", "Bandos tassets", "Bandos boots"));
  DROPS.put("The Hueycoatl", Arrays.asList("Tome of Earth (empty)", "Soiled page", "Dragon hunter wand", "Hueycoatl hide", "Huasca seed"));
  DROPS.put("K'ril Tsutsaroth", Arrays.asList("Steam battlestaff", "Staff of the Dead", "Zamorak hilt", "Godsword shard 1", "Godsword shard 2", "Godsword shard 3", "Zamorakian spear"));
  DROPS.put("The Gauntlet", Arrays.asList("Crystal weapon seed", "Crystal armour seed", "Enhanced crystal weapon seed", "Gauntlet cape"));
  DROPS.put("TzTok-Jad", Arrays.asList("Fire cape"));
  DROPS.put("Zulrah", Arrays.asList("Uncut onyx", "Tanzanite fang", "Serpentine visage", "Magic fang", "Zulrah's scales", "Zul-andra teleport", "Tanzanite mutagen", "Magma mutagen", "Jar of Swamp"));
  DROPS.put("Vorkath", Arrays.asList("Draconic visage", "Vorkath's head", "Skeletal visage", "Dragonbone necklace", "Jar of Decay"));
  DROPS.put("Phantom Muspah", Arrays.asList("Venator shard", "Ancient essence", "Frozen cache", "Ancient icon", "Charged ice"));
  DROPS.put("Maggot King", Arrays.asList("Crimson kisten", "Elder venator fang"));
  DROPS.put("Duke Sucellus", Arrays.asList("Virtus mask", "Virtus robe top", "Virtus robe bottom", "Ice quartz", "Chromium ingot", "Magus vestige", "Eye of the Duke", "Frozen tablet", "Awakener's orb"));
  DROPS.put("Vardorvis", Arrays.asList("Virtus mask", "Virtus robe top", "Virtus robe bottom", "Blood quartz", "Chromium ingot", "Ultor vestige", "Executioner's axe head", "Strangled tablet", "Awakener's orb"));
  DROPS.put("Corporeal Beast", Arrays.asList("Elysian sigil", "Spectral sigil", "Arcane sigil", "Spirit shield", "Holy elixir", "Jar of Spirits"));
  DROPS.put("The Whisperer", Arrays.asList("Virtus mask", "Virtus robe top", "Virtus robe bottom", "Shadow quartz", "Chromium ingot", "Bellator vestige", "Siren's staff", "Sirenic tablet", "Awakener's orb"));
  DROPS.put("The Leviathan", Arrays.asList("Virtus mask", "Virtus robe top", "Virtus robe bottom", "Smoke quartz", "Chromium ingot", "Venator vestige", "Leviathan's lure", "Scarred tablet", "Awakener's orb"));
  DROPS.put("Nightmare", Arrays.asList("Inquisitor's mace", "Inquisitor's great helm", "Inquisitor's hauberk", "Inquisitor's plateskirt", "Nightmare staff", "Harmonised orb", "Volatile orb", "Eldritch orb", "Jar of Dreams"));
  DROPS.put("Araxxor", Arrays.asList("Coagulated venom", "Araxyte head", "Noxious point", "Noxious blade", "Noxious pommel", "Araxyte fang", "Araxyte venom sac", "Spider cave teleport", "Jar of Venom"));
  DROPS.put("The Corrupted Gauntlet", Arrays.asList("Crystal weapon seed", "Gauntlet cape", "Crystal armour seed", "Enhanced crystal weapon seed"));
  DROPS.put("Nex", Arrays.asList("Nihil shard", "Zaryte vambraces", "Ancient hilt", "Nihil horn", "Torva full helm (damaged)", "Torva platebody (damaged)", "Torva platelegs (damaged)"));
  DROPS.put("Phosani's Nightmare", Arrays.asList("Inquisitor's mace", "Inquisitor's great helm", "Inquisitor's hauberk", "Inquisitor's plateskirt", "Nightmare staff", "Harmonised orb", "Volatile orb", "Eldritch orb", "Slepey tablet", "Parasitic egg", "Jar of Dreams"));
  DROPS.put("Theatre of Blood", Arrays.asList("Ghrazi rapier", "Justiciar faceguard", "Justiciar chestguard", "Justiciar legguards", "Vial of blood", "Avernic defender hilt", "Sanguinesti staff (uncharged)", "Scythe of Vitur (uncharged)", "Holy ornament kit", "Sanguine ornament kit", "Sanguine dust"));
  DROPS.put("Yama", Arrays.asList("Chasm teleport scroll", "Oathplate helm", "Oathplate chest", "Oathplate legs", "Soulflame horn", "Oathplate shards", "Forgotten lockbox", "Dossier", "Barrel of demonic tallow (full)", "Rite of vile transference"));
  DROPS.put("TzKal-Zuk", Arrays.asList("Infernal cape"));
  DROPS.put("Fortis Colosseum", Arrays.asList("Uncut onyx", "Tonalztics of Ralos (uncharged)", "Sunfire splinters", "Sunfire fanatic helm", "Sunfire fanatic cuirass", "Sunfire fanatic chausses", "Echo crystal", "Dizana's quiver (uncharged)"));
  DROPS.put("Tempoross", Arrays.asList("Dragon harpoon", "Big harpoonfish", "Tome of Water (empty)", "Soaked page", "Tackle box", "Fish barrel", "Spirit flakes", "Spirit angler headband", "Spirit angler top", "Spirit angler waders", "Spirit angler boots"));
  DROPS.put("Wintertodt", Arrays.asList("Dragon axe", "Pyromancer garb", "Pyromancer robe", "Pyromancer hood", "Pyromancer boots", "Warm gloves", "Tome of Fire (empty)", "Burnt page", "Bruma torch"));
 }
 static List<String> drops(String boss) { return DROPS.getOrDefault(boss, Collections.emptyList()); }
 static List<String> allDrops(List<String> bosses) { Set<String> all = new TreeSet<>(String.CASE_INSENSITIVE_ORDER); for (String boss : bosses) all.addAll(drops(boss)); return new ArrayList<>(all); }
 private BossUniqueCatalog() {}
}
