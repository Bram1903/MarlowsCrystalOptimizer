package com.deathmotion.marlowcrystal.listener;

import com.deathmotion.marlowcrystal.packet.impl.ChallengePacket;
import com.deathmotion.marlowcrystal.packet.impl.ChallengeResponsePacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;

public final class ChallengePacketListener {

    private ChallengePacketListener() {
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ChallengePacket.ID, (client, handler, buf, sender) -> {
            int challengeId = buf.readInt();
            FriendlyByteBuf out = new FriendlyByteBuf(Unpooled.buffer());
            new ChallengeResponsePacket(challengeId).write(out);
            ClientPlayNetworking.send(ChallengeResponsePacket.ID, out);
        });
    }
}
