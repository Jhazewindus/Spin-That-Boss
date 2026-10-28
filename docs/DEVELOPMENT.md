# Development

The plugin source is on the [`plugin-hub-submission` branch](https://github.com/Jhazewindus/Spin-That-Boss/tree/plugin-hub-submission). Use that branch for development. Use Java 11 or newer; compilation targets Java 11. The project includes a Gradle wrapper.

## Build and tests

```sh
./gradlew build shadowJar
```

The automated suite covers shuffle-bag invariants, all seven leg goals, malformed state, party/host/leg validation, reconnects, and retention of local progress. Passing these tests does not establish that the plugin works in-game.

## Development client

```sh
./gradlew run
```

The client needs a graphical desktop. For Jagex accounts, follow RuneLite's [Using Jagex Accounts](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts) instructions.

Enable RuneLite's built-in **Party** plugin and have the test players join the same party. Do not automate game input; in-game validation must be performed manually.

## Match synchronization

All participants should use the same plugin version. Progress messages include the leg number to reject delayed updates from an earlier visit to the same boss. Local loot counters survive host snapshots for the same leg. Changing parties pauses the saved match, and rejoining its party allows synchronization to resume. RuneLite's temporary `<unknown>` member name is excluded until the character name arrives.

Each client tracks its own loot and shares match updates through RuneLite's Party service. Wise Old Man lookups are opt-in and request only the local player's baseline.

## Manual testing

1. Use two clients in the same party, enable Spin That Boss, and start a match. Both panels should show the same host, boss, and goal. Only the host should be able to spin or end it.
2. Set `FIRST_KILLS` to a small target. Confirm that only the assigned boss's personal loot increments your counter and that both clients see the progress. Finish a round and spin again: round counters should reset while match totals remain.
3. Exercise every boss in the pool. Each refill should contain every boss once and should not immediately repeat the previous boss when the pool has more than one entry.
4. Join late, restart a client, disable/re-enable the plugin, and leave/rejoin the party. Confirm names, saved progress, and host controls recover. In another party, the old match must not collect or send progress.
5. Check configured rare drops and pet goals. Pet recognition uses generic game chat text and can misattribute an unrelated pet to the assigned boss. Fastest times are spawn-to-loot estimates rather than official kill-time records.
6. Leave Wise Old Man disabled for the core checks. When testing the optional lookup, use a tracked player and verify the current boss's baseline. Switch rounds or disable the plugin while a request is pending to check cancellation and stale-response handling.

Boss naming, loot attribution, pet-message recognition, party synchronization, and live Wise Old Man lookups still need in-game validation.

## Plugin Hub submission

Follow the [Plugin Hub contribution instructions](https://github.com/runelite/plugin-hub#submitting-a-plugin). Push the source commit first, then update `plugins/spin-that-boss` in the Plugin Hub fork to its full 40-character hash. Keep subsequent fixes in the existing pull request and check its CI results.

The project uses `build=standard`. Plugin Hub maintainers review and merge submissions before they appear in the catalog.
