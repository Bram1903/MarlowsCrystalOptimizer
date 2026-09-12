package com.deathmotion.marlowcrystal.listener;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.packet.impl.VersionPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public final class ConnectEventListener implements ClientPlayConnectionEvents.Join {

    @Override
    public void onPlayReady(@NotNull ClientPacketListener handler, @NotNull PacketSender sender, @NotNull Minecraft client) {
        if (client.isLocalServer()) return;

        VersionPacket vp = MarlowCrystal.getInstance().getVersionPacket();
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        vp.write(buf);
        ClientPlayNetworking.send(VersionPacket.ID, buf);
    }
}
