package com.deathmotion.marlowcrystal.listener;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.cache.OptOutCache;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.jetbrains.annotations.NotNull;

public final class DisconnectEventListener implements ClientPlayConnectionEvents.Disconnect {

    private final OptOutCache optOutCache;

    public DisconnectEventListener() {
        optOutCache = MarlowCrystal.getInstance().getOptOutCache();
    }

    @Override
    public void onPlayDisconnect(@NotNull ClientPacketListener handler, @NotNull Minecraft client) {
        optOutCache.clearCurrentSession();
    }
}
