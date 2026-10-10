# RawInput

Raw mouse input for Minecraft 1.8.9 Forge, same idea as the raw input option in Lunar/Badlion.

Vanilla 1.8.9 gets mouse movement through Windows, so if "Enhance pointer precision" is on (or some other software is messing with the cursor) your sens isn't 1:1 with your hand. This mod reads the mouse directly and feeds that into the game instead.

## Install

1. Install Forge 1.8.9
2. Grab the jar from [Releases](../../releases)
3. Drop it in `.minecraft/mods`

That's it, no config. If it can't find a mouse it just falls back to normal input, you'll see a line about it in the log.

## How it works

- a background thread polls every mouse JInput can see (DirectInput/RawInput plugin on Windows) about once per ms
- movement gets added up and the game takes it each frame, leftover fractions carry over so nothing is lost
- vanilla `MouseHelper` is swapped for one that reads from that buffer
- when the cursor gets grabbed again (closing a gui etc.) the buffer is cleared so you don't snap
- if the mouse is unplugged it rescans once a second

Only tested on Windows. Should fall back to vanilla anywhere the raw plugin isn't available.

## Building

Needs JDK 17 to run gradle (the mod itself compiles for Java 8).

```
./gradlew build
```

Jar ends up in `build/libs`.

## License

MIT
