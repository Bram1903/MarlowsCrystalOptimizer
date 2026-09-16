//? if fabric {
package com.deathmotion.marlowcrystal.loader.fabric;

import com.deathmotion.marlowcrystal.loader.LoaderAccess;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.Optional;

public final class FabricLoaderAccess implements LoaderAccess {

    @Override
    public Path configDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Optional<String> minecraftVersion() {
        return FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(container -> container.getMetadata().getVersion().getFriendlyString());
    }
}
//?}
