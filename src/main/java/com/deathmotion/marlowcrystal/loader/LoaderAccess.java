package com.deathmotion.marlowcrystal.loader;

//? if fabric {
import com.deathmotion.marlowcrystal.loader.fabric.FabricLoaderAccess;
//?} else {
/*import com.deathmotion.marlowcrystal.loader.neoforge.NeoForgeLoaderAccess;
*///?}

import java.nio.file.Path;
import java.util.Optional;

public interface LoaderAccess {

    //? if fabric {
    LoaderAccess INSTANCE = new FabricLoaderAccess();
    //?} else {
    /*LoaderAccess INSTANCE = new NeoForgeLoaderAccess();
    *///?}

    Path configDirectory();

    Optional<String> minecraftVersion();
}
