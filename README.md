# Hermes

Hermes is a mod that outputs a lot of information about the game for external tools to use. It provides no configuration
or in-game functionality.

## Features

### `[PID].json`

On initialize, a json file named [PID].json (PID = process id) will be placed in a global location and contain the game
path, game version, and all fabric mods loaded (display names, ids, versions). A shared lock is held on the file to
indicate the instance is still alive. The file should be deleted on exit, but bad shutdowns may lead to the file
lingering.

### `.minecraft/hermes/alive`

A file used to indicate that the instance is still alive. The first 8 bytes are the PID, and the next 8 are the
timestamp which is updated every second. The file should be deleted on exit, but bad shutdowns may lead to the file
lingering. To check for the existence of an instance, first check for the [PID].json file, optionally check with the OS
if a process with that PID exists, then check if the PID in the `.minecraft/hermes/alive` file matches, and that the
timestamp is within a reasonable range.

### `.minecraft/hermes/state.json`

A file that updates with a few useful pieces of information for external tools and macros. This serves as a practical
replacement for the state output mod. Contained in the file is:

- The current screen (class, title, if pause screen)
- The current world (null while not in a world)
- The last joined world (null until first world join)

### `.minecraft/hermes/latest_worlds_log.txt`

- Contains the path of the latest `worlds_[timestamp created].log` file.

### `.minecraft/hermes/world_logs/worlds_[timestamp created].log`

A log of worlds entered and exited where each line is a valid json object. Each object contains a log type (`entering`
or `leave`), the world's save folder path, and the time. The word `entering` is chosen as it can either mean the world
is being loaded into, or the world is already loaded and the player is just entering it (Pressing play on a SeedQueue
world). This means that worlds generating in the background (e.g. via SeedQueue) will not be logged unless the player
enters it. Note that fast resets are not distinguished from regular world exits. A fast reset log type does exist in the
play log.

### `.minecraft/saves/[World Name]/hermes/play.log`

> Data related to player activity in the world will logged in real-time in a ciphered format in a "restricted" folder
> that most external tools should not be able to access (The cipher isn't very secure, and is only meant to convey
> intention). Then on server shutdown, an unciphered copy is made. A warning.txt file is also placed in the restricted
> folder to make it extra obvious.

- `initialize` - Runs when the play log is initialized (creating or joining a world), contains generator options,
  entered seed (if creating), the world time (total ticks ran in world, should be 0 for new world), and if atum is
  installed, a field noting if atum is running.
- `stat_change` - Every stat update, the player it's for, the new value, and the difference from the last one. Excludes
  stats that naturally update every tick or extremely spammy ones (e.g. walking/sprinting/flying)
- `screen_change` - Every screen change, the class (will look like class_xxxxx bc of intermediary), the title (
  untranslated key, or sometimes the text in case of a renamed container), if the screen is supposed to pause the game.
- `advancement_change` - Every advancement update, the criteria earned, the player it's for, and if it is completed. It
  also contains the "display" data for the advancement (hidden, announce to chat, show toast).
- `dimension_change` - Every change to a player's dimension including their initial dimension when they join.
- `respawn` - Every time a player respawns, the player it's for, the position they respawned at, and if they were alive
  before (e.g. `true` if coming out of the end). The dimension of the respawn can be determined by the
  `dimension_change` event (no order guaranteed).
- `game_info` - The "Game Info" whenever a change happens to it. Game Info includes:
    - Changed Game Rules (compares to a default `new GameRules()`)
    - If cheats are allowed
    - If the world is opened to LAN
    - Hardcore/difficulty/difficulty locked
    - Default Gamemode
    - Datapacks/enabled datapacks
    - Player Infos
        - Names
        - Uuids
        - Gamemodes
- `inventory_change` - Every update to a player's inventory (stats aren't accurate enough to determine full item history
  because of taking from chests and such).
- `command`- Every command ran by a player and who entered it.
- `fast_reset` - Every time the fast reset's quit functionality runs.
- `server_shutdown` - Runs when the server shuts down, even if using fast reset, which is also when the unciphered copy
  is made.

All of these events will be logged with currentTimeMillis and speedrunigt times if available (rta, igt, retime)

### `.minecraft/saves/[World Name]/hermes/ghosts/[Player UUID].ghost`

Ghosts contain enough information to fully animate what a player looked like during the run (minus inventory data which
can be found in the play log). The data is taken from the server-side player, so might not accurately represent the
exact path or states of the client-side player. The data is saved as its raw bytes for file size purposes; every 42
bytes represents a single tick for that player. The layout is as follows:

| Byte(s)   | Type               | Description          |
|-----------|--------------------|----------------------|
| 0-7       | long               | Timestamp            |
| 8-15      | double             | X Position           |
| 16-23     | double             | Y Position           |
| 24-31     | double             | Z Position           |
| 32-35     | float              | Yaw                  |
| 36-39     | float              | Pitch                |
| 40 (0xF0) | N/A                | 4 unused bits        |
| 40 (0x0F) | 4-bit unsigned int | Selected Hotbar Slot |
| 41        | N/A                | Flags (see below)    |

The flags are as follows:

| Bitmask | Description                                     |
|---------|-------------------------------------------------|
| 0x01    | Is swinging hand (mining, placing blocks, etc.) |
| 0x02    | Is using an item (shield, bow, etc.)            |
| 0x04    | Is sneaking                                     |
| 0x08    | Is sprinting                                    |
| 0x10    | Is hit cooldown active (being visually red)     |
| 0x20    | Is alive                                        |
| 0x40    | Is fall flying (Elytra gliding) (1.9+)          |
| 0x80    | Is swimming (1.13+)                             |

Ghosts can be used by stat trackers to calculate what bastions were visited, or be used to create a ghost replay like
those in Mario Kart or Trackmania. While Hermes creates the data for this, it is important to note that **Hermes itself
is not a replay mod and cannot replay ghosts**.

## For Dedicated Servers

Hermes can be used on dedicated servers, but there are a few things to note:

- The "should show toast" field in advancement data is not available on the server, as that data is client-side only.
- The state.json file will not contain screen data as there is no client, and also won't contain the last world joined
  data as there is no client to join from. The field that Hermes provides for the current world will be accurate and
  unchanging.
- The world log will not exist, as there is no client to join worlds from.

## Developing

### API

A `me.duncanruns.hermes.api.HermesModAPI` class is provided for modders to use Hermes' features. Its methods have
javadoc
comments somewhat explaining their usage. Refer to the documentation of features above to get a better idea of what the
methods might be used for.

### Codebase Structure

For working on Hermes, a few notes on how the codebase is structured:

- `me.duncanruns.hermes` - The main mod class and some utilities
- `me.duncanruns.hermes.api` - The public API for other mods to use
- `me.duncanruns.hermes.[feature]` - Implementations of features, ideally should not reference each other
- `me.duncanruns.hermes.mixin` - General mixins that apply on both client and server
- `me.duncanruns.hermes.mixin.[client/server]` - General mixins that apply on only client or server
- `me.duncanruns.hermes.mixin.[feature]` - Mixins specific to a feature that apply on both client and server
- `me.duncanruns.hermes.mixin.[feature].[client/server]` - Mixins specific to a feature that apply on only client or
  server

## Ideology

The most core ideology of Hermes is **no data interpretation**. The data that Hermes outputs should be as close to the
raw data as possible. There should exist no concept of speedrunning splits such as "blinds" or likewise, but rather such
a concept should be able to be determined by external tools using the data Hermes provides. This comes with the
following benefits:

- Easier development: The mod is simpler as it just outputs data. The mixins are simpler, and as a whole the mod is
  easier to port to other versions.
- Less updating: Whenever a tool wants to do something new and unique, it is less likely that Hermes will need to be
  updated to support it.

The no data interpretation rule is broken in one particular place: Stats that would spam the play log are filtered out,
These include any stat that updates once per tick (`play_one_minute`, or "time since some action"), and the
`[movement type]_one_cm` stats.