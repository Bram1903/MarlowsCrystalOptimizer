package com.deathmotion.marlowcrystal.util;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public class ConnectionUtil {
    public static @Nullable String currentServerKey(Minecraft client) {
        if (client.getCurrentServer() == null) {
            return null;
        }

        return client.getCurrentServer().ip;
    }
}
