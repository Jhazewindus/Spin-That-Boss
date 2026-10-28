# Spin That Boss

**Pick your bosses. Spin the wheel. Take on the challenge together.**

Spin That Boss turns bossing with friends into a shared challenge. Let the wheel choose your next boss, work towards a goal, and follow everyone's progress in one sidebar.

*Currently awaiting [Plugin Hub review](https://github.com/runelite/plugin-hub/pull/19606).*

## What it does

- **Your own boss wheel** — choose the bosses your group wants to fight. Each boss comes up once before the wheel reshuffles.
- **Shared challenges** — race for a rare drop, chase a pet, or set a kill-count or loot-value target.
- **Party progress** — see everyone's kills, tracked rare drops, and loot value alongside the current boss and goal.
- **Match history** — look back at completed rounds and keep your progress between sessions.

## Getting started

1. Enable RuneLite's **Party** plugin. Create a party and share its passphrase with your friends.
2. Have everyone join the same party and enable **Spin That Boss**.
3. In the plugin settings, set **Bosses in wheel** and **Leg goal**. A leg is one round of the challenge.
4. Open the Spin That Boss sidebar. One player clicks **Start match** to become the host, then **Spin the wheel**.
5. Go bossing! Once the goal is met, the host can spin for the next round or use **End match** to finish.

The boss list is locked when the match starts. Only the host can spin or end the match. Keep everyone on the same plugin version, and rejoin the same party to pick up a saved match.

## Choose your challenge

Enter one of these values in **Leg goal**:

| Challenge | Setting |
| --- | --- |
| First player to get a chosen rare drop | `FIRST_RARE` |
| Everyone gets a chosen rare drop | `EVERYONE_RARE` |
| First player to get a pet | `FIRST_PET` |
| Everyone gets a pet | `EVERYONE_PET` |
| First player to reach the kill target | `FIRST_KILLS` |
| Everyone reaches the kill target | `KILLS_EACH` |
| First player to reach the loot-value target | `FIRST_VALUE` |

Use **Kills per member** to set the kill target, or **Drop value target** for the GP target. Loot value uses Grand Exchange prices.

For rare-drop challenges, everyone should use the same **Rare items by boss** list. Add a line for each boss in your wheel, separating drops with `|`:

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
