package com.deathmotion.marlowcrystal.listener;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.cache.OptOutCache;
import com.deathmotion.marlowcrystal.packet.impl.VersionPacket;
import com.deathmotion.marlowcrystal.util.ConnectionUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public final class ConnectEventListener implements ClientPlayConnectionEvents.Join {

    private final OptOutCache optOutCache;

    public ConnectEventListener() {
        this.optOutCache = MarlowCrystal.getInstance().getOptOutCache();
    }

    @Override
    public void onPlayReady(@NotNull ClientPacketListener handler, @NotNull PacketSender sender, @NotNull Minecraft client) {
        if (client.isLocalServer()) return;

        VersionPacket vp = MarlowCrystal.getInstance().getVersionPacket();
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        vp.write(buf);
        ClientPlayNetworking.send(VersionPacket.ID, buf);

        String key = ConnectionUtil.currentServerKey(client);
        boolean shouldOptOut = optOutCache.isServerOptedOut(key);

        if (shouldOptOut) {
            optOutCache.setOptedOut(true);
        }
    }
}
