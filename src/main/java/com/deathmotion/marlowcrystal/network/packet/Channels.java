package com.deathmotion.marlowcrystal.network.packet;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import net.minecraft.resources.Identifier;

final class Channels {

    private Channels() {
    }

    static Identifier of(String path) {
        return Identifier.tryParse(MarlowCrystal.MOD_ID + ":" + path);
    }
}
