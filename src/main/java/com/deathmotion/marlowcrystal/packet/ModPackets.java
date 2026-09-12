package com.deathmotion.marlowcrystal.packet;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import net.minecraft.resources.Identifier;

public final class ModPackets {

    public static Identifier id(String path) {
        return Identifier.tryParse(MarlowCrystal.MOD_ID + ":" + path);
    }
}

