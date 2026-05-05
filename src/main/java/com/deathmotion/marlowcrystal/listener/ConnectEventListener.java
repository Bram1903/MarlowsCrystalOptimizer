package com.deathmotion.marlowcrystal.listener;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.cache.OptOutCache;
import com.deathmotion.marlowcrystal.util.ConnectionUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.jspecify.annotations.NonNull;

public final class ConnectEventListener implements ClientPlayConnectionEvents.Join {

    private final OptOutCache optOutCache;

    public ConnectEventListener() {
        this.optOutCache = MarlowCrystal.getInstance().getOptOutCache();
    }

    @Override
    public void onPlayReady(@NonNull ClientPacketListener handler, @NonNull PacketSender sender, @NonNull Minecraft client) {
        if (client.isLocalServer()) return;

        sender.sendPacket(MarlowCrystal.getInstance().getVersionPacket());

        String key = ConnectionUtil.currentServerKey(client);
        boolean shouldOptOut = optOutCache.isServerOptedOut(key);

        if (shouldOptOut) {
            optOutCache.setOptedOut(true);
        }
    }
}
