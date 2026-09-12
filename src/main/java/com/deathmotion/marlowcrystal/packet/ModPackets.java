package com.deathmotion.marlowcrystal.packet;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import net.minecraft.resources.ResourceLocation;

public final class ModPackets {

    public static ResourceLocation id(String path) {
        return ResourceLocation.tryParse(MarlowCrystal.MOD_ID + ":" + path);
    }
}
