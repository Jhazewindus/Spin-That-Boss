# Spin That Boss

**Pick your bosses. Spin the wheel. Take on the challenge together.**

Spin That Boss turns bossing with friends into a shared challenge. Let the wheel choose your next boss, work towards a goal, and follow everyone's progress in one sidebar.

*Currently awaiting [Plugin Hub review](https://github.com/runelite/plugin-hub/pull/19606).*

## What it does

- **Your own boss wheel** — choose the bosses your group wants to fight. Each boss comes up once before the wheel reshuffles.
- **Bosses ready to pick** — search and tick bosses in the sidebar, or use **Select all**. Raids, Barrows, and Moons each have one entry; other boss variants stay separate.
- **Shared challenges** — race for a boss unique or pet, or set a kill-count or loot-value target.
- **Party progress** — see everyone's kills, tracked rare drops, and loot value alongside the current boss and goal.
- **Match history** — look back at completed rounds and keep your progress between sessions.

## Getting started

1. Have everyone enable **Spin That Boss** and RuneLite's built-in **Loot Tracker**.
2. Open the Spin That Boss sidebar. The host clicks **Setup run**, chooses bosses and a goal, then clicks **Create party and invite friends**. Share the displayed passphrase with friends.
3. Friends enter the passphrase in Spin That Boss and click **Join party with code**. The built-in RuneLite Party panel is not needed for creating or joining the contest party.
4. Once at least one friend has joined, click **Start run**, then **Spin the wheel** to pick the first boss.
5. Go bossing! Once the goal is met, the host can spin for the next round or use **End run** to finish.

The boss list is locked when the match starts. Only the host can spin or end the match. Keep everyone on the same plugin version. Leaving the party pauses your own tracking; rejoin the same passphrase to resume.

Run choices live in the Spin That Boss sidebar. The plugin settings contain only the optional Wise Old Man baseline toggle.

## Choose your challenge

Choose a goal from the dropdown in **Setup run**:

| Challenge | What completes a leg |
| --- | --- |
| First to chosen rare drop | One member gets a drop listed for the current boss |
| Everyone gets chosen rare | Every member gets a listed drop |
| First to pet | One member gets a pet |
| Everyone gets pet | Every member gets a pet |
| First to kill target | One member reaches the selected number of kills |
| Everyone reaches kill target | Every member reaches the selected number of kills |
| First to loot-value target | One member's drops for this boss reach the GP target |

The setup menu shows **Kills needed** or **Loot value needed (GP)** when that goal uses a target. Loot value adds up the tracked drops for the current boss using Grand Exchange prices; the sidebar shows each member's progress against the target.

For a unique-drop goal, choose **Any unique drop** or a particular collection-log drop from the dropdown. The choices are based on OSRS Wiki drop tables and refreshed boss collection-log data. The data source is [Bossman Mode's public boss ladder](https://github.com/erikrahm/osrs-runelite-bossman-mode/blob/main/src/main/resources/bossman/ladder.json); item names and game data are from the [OSRS Wiki](https://oldschool.runescape.wiki/).

The starting goal is **first to kill target**. Use **Test spin** to preview the result in a popup without starting a run. A live spin displays the selected boss in the sidebar.

```text
Zulrah: Tanzanite fang | Magic fang | Serpentine visage
Vorkath: Skeletal visage | Draconic visage
```

## Optional Wise Old Man lookup

Turn on **Use Wise Old Man baselines** to show your recorded boss KC at the start of a round. It's off by default. Enabling it sends your character name to Wise Old Man, which also receives your IP address.

## Things to know

This is an early release and still needs in-game testing. Pet tracking can count a pet earned from another activity, and displayed kill times are estimates. Wise Old Man lookups also still need live testing.

## Help

Found a problem or have an idea? [Open an issue](https://github.com/Jhazewindus/Spin-That-Boss/issues). Include the boss, your chosen goal, and what happened.

[Developer guide](docs/DEVELOPMENT.md) · [License](https://github.com/Jhazewindus/Spin-That-Boss/blob/plugin-hub-submission/LICENSE)
