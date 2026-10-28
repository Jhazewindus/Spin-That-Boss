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

Choose a leg goal in the plugin settings. Rare targets are configured with one line per boss, for example:

```text
Zulrah: Tanzanite fang | Magic fang | Serpentine visage | Jar of swamp
```

## Plugin Hub

The project follows the [RuneLite Plugin Hub workflow](https://github.com/runelite/plugin-hub). After this source is committed and pushed to the public repository, fork `runelite/plugin-hub`, add a manifest under `plugins/spin-that-boss` with the source URL and exact reviewed commit, and open a PR against Plugin Hub. Plugin Hub maintainers review and merge that PR; plugin authors cannot publish directly to the catalog. See the hub's current contribution instructions for the manifest format and required checks.

This is an early prototype. Build it and try a complete party match in RuneLite before submitting; boss naming, game-message text, loot attribution, Wise Old Man boss metric names, and party synchronization still need in-client validation.
