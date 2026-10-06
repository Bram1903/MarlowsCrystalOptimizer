## 2.0.0

### Added

- Added NeoForge support for Minecraft 1.20.4 through 26.3, released next to the Fabric jars. It sends
  the same packets as the Fabric version. With YetAnotherConfigLib installed, the settings open from
  NeoForge's mod list, which also shows new versions.
- Added an update check, shown in Mod Menu on Fabric and in NeoForge's mod list. It offers new versions
  built for your Minecraft version and mod loader, from Modrinth or from GitHub as picked in the
  settings. Beta builds are only offered with the Experimental Builds setting, which is off by default.
- The version packet now also sends the mod loader, the git commit the build came from, whether it had
  uncommitted changes, and when it was built. PROTOCOL.md lists the fields.
- Added a Keep Render setting, off by default. A broken crystal stays visible until the server removes
  it instead of being hidden, and the next crystal can still be placed where it stood straight away. It
  sends exactly the same packets as the default mode.
- Added Minecraft 26.3 to the supported versions. The 26.1 build covers it unchanged, so it ships as the
  same jar. On 26.3 Fabric API itself asks for Fabric Loader `0.19.3` or newer.

### Fixed

- Fixed a crash when joining a server that disables the optimizer on Minecraft 1.21.2 through 1.21.4.
  The mod called a Minecraft method whose return type changed in 1.21.2, which threw `NoSuchMethodError`
  the moment the server sent its opt-out packet.
- Fixed the mod failing to start the game on Minecraft 1.20.5 and 1.20.6. Packet identifiers were built
  with a method that only exists from 1.21 onwards.
- Fixed a crystal struck while sprinting skipping the vanilla attack slowdown, which anticheats flagged
  as movement. The crystal was removed the moment the attack packet was sent, before Minecraft ran its
  own attack, so the client never lost the speed and the sprint a sprint hit costs. It is now hidden
  right after that attack, still inside the same click, so the next crystal is placed exactly as fast as
  before.
- Fixed a crystal the server did not break staying invisible until you moved out of its range, for
  example one that cannot be damaged or a hit a plugin cancelled. A broken crystal is now hidden rather
  than removed. It comes back as soon as the server confirms a block you placed or started breaking after
  the hit without having removed the crystal first, and otherwise 30 ticks after the hit, a second and a
  half at the normal tick rate, so a placement stops going through a crystal that is still there.
- Fixed a crystal outside the world border disappearing when hit. The server ignores those hits.
- Fixed a crystal disappearing without breaking when hit with Weakness just as Strength ran out. The
  server ends Strength before the client is told, so its last 1.5 seconds no longer count.

### Changed

- A broken crystal now only lets crystals through. Until the server removes it, it stands in the way of
  everything else exactly as it does without the mod: a click never breaks the block under it or reaches
  anyone behind it, and no other item is used past it. An attack on it goes to the next end crystal
  behind it, and a right click with an end crystal in either hand places that crystal on the obsidian or
  bedrock behind it, both in the same tick it was broken and as fast as before. Only the hand holding the
  end crystal sees past it. Anticheats flagged hitting, breaking and placing blocks through a crystal the
  server had not removed yet.
- The opt-out is now tracked per connection instead of being remembered per server address. A backend
  switch behind a proxy keeps the opt-out, and disconnecting clears it, which is what the documented
  protocol always said should happen.
- The message shown when a server disables the optimizer now appears once per connection rather than
  once per server per game session.
