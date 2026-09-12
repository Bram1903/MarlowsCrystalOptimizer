## 1.1.1

### Fixed

- Fixed a crash when joining a server that disables the optimizer on Minecraft 1.21.2 through 1.21.4.
  The mod called a Minecraft method whose return type changed in 1.21.2, which threw `NoSuchMethodError`
  the moment the server sent its opt-out packet.
- Fixed the mod failing to start the game on Minecraft 1.20.5 and 1.20.6. Packet identifiers were built
  with a method that only exists from 1.21 onwards.

### Changed

- The opt-out is now tracked per connection instead of being remembered per server address. A backend
  switch behind a proxy keeps the opt-out, and disconnecting clears it, which is what the documented
  protocol always said should happen.
- The message shown when a server disables the optimizer now appears once per connection rather than
  once per server per game session.
