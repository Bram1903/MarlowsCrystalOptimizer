//? if neoforge {
/*package com.deathmotion.marlowcrystal.loader.neoforge;

import com.deathmotion.marlowcrystal.loader.LoaderAccess;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.Optional;

public final class NeoForgeLoaderAccess implements LoaderAccess {

    @Override
    public Path configDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Optional<String> minecraftVersion() {
        return ModList.get()
                .getModContainerById("minecraft")
                .map(container -> container.getModInfo().getVersion().toString());
    }
}
*///?}
