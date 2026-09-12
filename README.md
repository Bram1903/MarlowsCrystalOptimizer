<div align="center">
  <h1>Marlow's Crystal Optimizer</h1>
  <img alt="Build" src="https://github.com/Bram1903/MarlowsCrystalOptimizer/actions/workflows/gradle.yml/badge.svg">
  <img alt="GitHub Release" src="https://img.shields.io/github/release/Bram1903/MarlowsCrystalOptimizer.svg">
  <br>
  <a href="https://modrinth.com/mod/marlow-crystal-optimizer"><img alt="MarlowsCrystalOptimizer" src="https://img.shields.io/badge/-Modrinth-green?style=for-the-badge&logo=Modrinth"></a>
  <a href="https://discord.deathmotion.com"><img alt="Discord" src="https://img.shields.io/badge/-Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white"></a>
  <br>
  <h2>Showcase Video</h2>

[![Showcase Video](https://img.youtube.com/vi/CcFT5KuQoZk/maxresdefault.jpg)](https://www.youtube.com/watch?v=CcFT5KuQoZk)
</div>

## Overview

Marlow's Crystal Optimizer is a mod that optimizes the handling of using end crystals by removing the crystal client
side,
instead of waiting for the server to remove it. This can especially be useful when you are on higher ping.

### Prerequisites

Marlow's Crystal Optimizer requires different minimum Fabric Loader versions depending on the Minecraft version you are
using:

* **Minecraft 1.19 – 1.21.4:** Requires **Fabric Loader `0.16.5` or newer**
* **Minecraft 1.21.5 – 1.21.10:** Requires **Fabric Loader `0.16.10` or newer**
* **Minecraft 1.21.11:** Requires **Fabric Loader `0.17.3` or newer**
* **Minecraft 26.1 – 26.2:** Requires **Fabric Loader `0.18.4` or newer**
* **Minecraft 26.3 and above:** Requires **Fabric Loader `0.19.5` or newer**

Make sure you have the correct Fabric Loader version installed to ensure full compatibility.

## Table of Contents

- [Overview](#overview)
    - [Prerequisites](#prerequisites)
- [Supported Platforms & Versions](#supported-platforms--versions)
- [Installation](#installation)
- [Optional Integrations](#optional-integrations)
- [Opt-Out Support](#opt-out-support)
    - [Client side opt-out showcase](#client-side-opt-out-showcase)
- [Compiling From Source](#compiling-from-source)
    - [Prerequisites](#prerequisites)
    - [Steps](#steps)
- [License](#license)

## Supported Platforms & Versions

| Platform | Supported Versions |
|----------|--------------------|
| Fabric   | 1.19 - 26.3        |

## Installation

1. **Download**: Get the latest release from
   the [GitHub release page](https://github.com/Bram1903/MarlowsCrystalOptimizer/releases/latest).
2. **Install**: Place the mod in your `mods` folder, located in your `.minecraft` directory (`%appdata%`).
3. **Launch**: Start the game with the Fabric Loader profile.

## Optional Integrations

Both mods are optional.

| Mod                                                  | Adds                                                  | Minecraft |
|------------------------------------------------------|-------------------------------------------------------|-----------|
| [Mod Menu](https://modrinth.com/mod/modmenu)         | Update badge in the mod list                          | 1.20.5+   |
| [YetAnotherConfigLib](https://modrinth.com/mod/yacl) | Settings screen for the update source, needs Mod Menu | 1.20.2+   |

The update check only runs when Mod Menu asks for it, once per session. Modrinth is the default source and only offers
releases for your Minecraft version. GitHub follows the latest release. The setting is stored in
`config/marlowcrystal.json`.

## Opt-Out Support

Servers can disable the optimizer for a connection. Channels and payloads are documented in [PROTOCOL.md](PROTOCOL.md).

### Client side opt-out showcase

The client side opt-out message is shown once per connection.

![opt_out_message.png](docs/images/opt_out_message.png)

![opt_out_hover_message.png](docs/images/opt_out_hover_message.png)

## Compiling From Source

All supported Minecraft versions are built from this one branch. [Stonecutter](https://stonecutter.kikugie.dev/)
resolves the per-version differences at build time, so there is no branch to switch.

### Prerequisites

- Java Development Kit (JDK) 21 or higher
- [Git](https://git-scm.com/downloads)

### Steps

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Bram1903/MarlowsCrystalOptimizer.git
   ```
2. **Navigate to the Project Directory**:
   ```bash
   cd MarlowsCrystalOptimizer
   ```
3. **Build every supported version**:
   Jars are written to `build/libs/`, one per supported Minecraft range.

   <details>
   <summary><strong>Linux / macOS</strong></summary>

   ```bash
   ./gradlew buildAndCollect
   ```
   </details>
   <details>
   <summary><strong>Windows</strong></summary>

   ```cmd
   .\gradlew buildAndCollect
   ```
   </details>

### Working on a single version

`src/` is shown for one Minecraft version at a time. Switch which one with the
`Set active project to ...` Gradle task, and run `Reset active project` before committing.
To build just one, use `./gradlew 1.21.5:build`.

Supported versions and their compatibility ranges are declared in `stonecutter.properties.toml`.
That file is the single source of truth: it drives the jar name, the `depends.minecraft` range in
`fabric.mod.json`, and the release targets.

Shared build conventions live in `build-logic`. `build.gradle.kts` only holds the Minecraft and Loom setup.

## Releasing

Publishing is a Gradle task rather than a CI job, driven by two environment variables:

| Variable | Used for |
|----------|----------|
| `MODRINTH_TOKEN` | Uploading one Modrinth version per supported range |
| `GITHUB_TOKEN` | Creating the GitHub release and attaching every jar to it |

```bash
./gradlew publishMods
```

That creates a single GitHub release tagged `v<mod.version>` with all jars attached, and one Modrinth
version per supported range. The game versions each Modrinth upload is tagged with come from
`mod.mc_releases` in `stonecutter.properties.toml`, and the release notes come from `CHANGELOG.md`.

Set `MCO_PUBLISH_DRY_RUN` to any value to print what would be published without uploading anything:

```bash
MCO_PUBLISH_DRY_RUN=1 ./gradlew publishMods
```

## License

This project is licensed under the [MIT License](LICENSE).
