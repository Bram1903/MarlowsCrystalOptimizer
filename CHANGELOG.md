## 2.0.0

### Added

- Added NeoForge support for Minecraft 1.20.4 through 26.3, released next to the Fabric jars. It sends
  the same packets as the Fabric version. With YetAnotherConfigLib installed, the settings open from
  NeoForge's mod list, which also shows new versions from Modrinth.
- The version packet now also sends the mod loader, the git commit the build came from, whether it had
  uncommitted changes, and when it was built. PROTOCOL.md lists the fields.
- Added a Keep Render setting, off by default. A broken crystal stays visible until the server removes
  it, but it stops blocking the crosshair, crystal placement and block placement straight away. A crystal
  the server does not remove in time can be hit again, so a rejected hit does not leave a crystal you
  cannot touch. That time is twice the slowest of your last 16 confirmed breaks, measured from the hit to
  the server removing the crystal, kept between half a second and five seconds. It sends exactly the same
  packets as the default mode.
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
  own attack, so the client never lost the speed and the sprint a sprint hit costs. It is now removed
  right after that attack, still inside the same click, so crystals break and the block behind them is
  targeted exactly as fast as before.
- Fixed the click after breaking a crystal passing through a second crystal standing behind it, which
  anticheats flagged as breaking or placing out of sight. The crosshair was moved to the block behind the
  broken crystal without looking for entities. It is now moved the way Minecraft aims, so that click lands
  on the crystal behind. With nothing behind the broken crystal it still reaches the block in the same
  tick.

### Changed

- The opt-out is now tracked per connection instead of being remembered per server address. A backend
  switch behind a proxy keeps the opt-out, and disconnecting clears it, which is what the documented
  protocol always said should happen.
- The message shown when a server disables the optimizer now appears once per connection rather than
  once per server per game session.
