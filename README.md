# Spin That Boss

**Pick your bosses. Spin the wheel. Take on the challenge together.**

Spin That Boss turns bossing with friends into a shared challenge. Let the wheel choose your next boss, work towards a goal, and follow everyone's progress in one sidebar.

*Currently awaiting [Plugin Hub review](https://github.com/runelite/plugin-hub/pull/19606).*

## What it does

- **Your own boss wheel** — choose the bosses your group wants to fight. Each boss comes up once before the wheel reshuffles.
- **Bosses ready to pick** — search and tick bosses in the sidebar, or use **Select all**. Raids, Barrows, and Moons each have one entry; other boss variants stay separate.
- **Shared challenges** — race for a rare drop, chase a pet, or set a kill-count or loot-value target.
- **Party progress** — see everyone's kills, tracked rare drops, and loot value alongside the current boss and goal.
- **Match history** — look back at completed rounds and keep your progress between sessions.

## Getting started

1. Enable RuneLite's **Party** plugin. Create a party and share its passphrase with your friends.
2. Have everyone join the same party and enable **Spin That Boss** and RuneLite's built-in **Loot Tracker**.
3. Open the Spin That Boss sidebar. One player clicks **Setup run**, chooses the bosses and a goal, then clicks **Start run** to become the host. A leg is one round of the challenge.
4. The host clicks **Spin the wheel** to pick the first boss.
5. Go bossing! Once the goal is met, the host can spin for the next round or use **End run** to finish.

The boss list is locked when the match starts. Only the host can spin or end the match. Keep everyone on the same plugin version, and rejoin the same party to pick up a saved match.

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

The starting goal is **first to kill target**. For rare-drop challenges, the host enters **Rare drops** in setup and shares that list with the party. Add a line for each boss in your wheel, separating drops with `|`:

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
