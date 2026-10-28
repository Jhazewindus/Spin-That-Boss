# Spin That Boss

A RuneLite Plugin Hub plugin for long-running boss challenges with friends.

## First prototype

- Uses RuneLite's built-in Party plugin and passphrase parties for live sharing.
- The host chooses the boss pool before starting. The wheel draws without repeats until the pool is used, then reshuffles without immediately repeating the previous boss.
- Boss leg goals include first configured rare, one configured rare per member, a pet for the first/every member, first to a kill target, a kill target per member, or first to a drop-value target.
- Server-provided NPC loot events update each member's own counters and configured rare drops. Boss spawn and loot events estimate personal fastest times. Pet goals use the game's pet notification chat messages. The plugin shares those updates only with members of the current RuneLite party.
- An opt-in setting fetches each member's starting boss KC from Wise Old Man when a new leg begins. It is off by default and shows RuneLite's third-party-service warning.
- The host can spin after the leg goal is met and can end the match. Match state is saved locally so members can resume after restarting the client.

## Development

Use Java 11 or newer. In RuneLite, enable the built-in **Party** plugin and create or join a party first. Then start this plugin and open its sidebar panel. The host starts the match and spins the wheel; other members see the host's match state and their party's progress.

Run the development client with:

```sh
./gradlew run
```

For a Jagex account, follow RuneLite's [Using Jagex Accounts](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts) instructions. The development client needs a graphical desktop.

Build the plugin, run the automated model tests, and package the development launcher with:

```sh
./gradlew build shadowJar
```

The cloud workspace has a prepared JDK and Gradle cache. In that workspace, first run `source /workspace/.setup/spin-that-boss/env.sh`. Other machines can use their usual JDK installation. Compilation targets Java 11.

All party members should run the same plugin version. Progress messages include the leg number so delayed messages from an earlier visit to the same boss are ignored. Local loot counters survive a host snapshot for the same leg. Changing parties pauses the saved match; rejoining its party allows synchronization to resume. RuneLite's temporary `<unknown>` member name is excluded from the roster until the character name arrives.

Choose a leg goal in the plugin settings. Rare targets are configured with one line per boss, for example:

```text
Zulrah: Tanzanite fang | Magic fang | Serpentine visage | Jar of swamp
```

## Plugin Hub

The project follows the [RuneLite Plugin Hub workflow](https://github.com/runelite/plugin-hub). After this source is committed and pushed to the public repository, fork `runelite/plugin-hub`, add a manifest under `plugins/spin-that-boss` with the source URL and exact reviewed commit, and open a PR against Plugin Hub. Plugin Hub maintainers review and merge that PR; plugin authors cannot publish directly to the catalog. See the hub's current contribution instructions for the manifest format and required checks.

This is an early prototype. Boss naming, game-message text, loot attribution, Wise Old Man boss metric names, and party synchronization still need in-client validation. The submission has automated build and model-test coverage; it has not been tested in-game.

## Manual validation

1. Use two clients in the same RuneLite party, enable Spin That Boss, and start a match. Both panels should show the same boss pool and host. Only the host should be able to spin or end it.
2. Set `FIRST_KILLS` to a small target. Confirm that only the assigned boss's personal loot increments your counter and that both clients see the progress. Complete a leg, spin again, and confirm that leg counters reset while match totals remain.
3. Exercise every boss in the pool. A refill should contain every boss once and should not immediately repeat the last boss when the pool has more than one entry.
4. Join late, restart a client, disable/re-enable the plugin, and leave/rejoin the party. Confirm names, saved progress, and host controls recover. In another party, the old match must not collect or send progress.
5. Check configured rare drops and pet goals manually. Pet recognition currently uses generic game chat text, so attribution to the assigned boss is a prototype limitation; an unrelated pet can be misattributed. Fastest times are spawn-to-loot estimates rather than official kill-time records.
6. Leave Wise Old Man disabled for the core checks. If you opt in, verify the baseline for a tracked player and the current boss, then switch legs or disable the plugin while a request is pending. Live Wise Old Man access is a separate check; the cloud allowlist currently includes RuneLite's Maven host but not `api.wiseoldman.net`.

The automated suite covers shuffle-bag invariants, all seven leg goals, malformed state, party/host/leg validation, and retention of local progress. It does not establish that the plugin works in-game. The Plugin Hub manifest must point to the exact reviewed source commit.
