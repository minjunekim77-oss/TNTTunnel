# TNTTunnel (experimental)
Paper server plugin: real primed TNT detonations along the direction you face, 2 TNT every 4 blocks, one wave every 8 ticks. Ancient debris survives ordinary TNT blasts. Nether only. OP-only.

Commands: `/tnttunnel 100` (10–1000 blocks) and `/tnttunnel stop`.

**Warning:** This is not instant excavation; it uses real explosions and may destroy builds or expose lava. The plugin only proceeds through already loaded chunks. Walk forward at a safe distance if needed. Test in a backup world first. `stop` does not remove TNT already spawned.

Build: upload the contents of this directory to a new GitHub repository and run Actions > Build TNTTunnel. Download the `TNTTunnel-jar` artifact ZIP and extract the JAR into server `plugins/`.

The Paper API version in `build.gradle.kts` may need to be updated to the actual published version for your server. If Gradle cannot resolve the dependency, use the exact verified API coordinate from your successful TNTFreeze build instead.
