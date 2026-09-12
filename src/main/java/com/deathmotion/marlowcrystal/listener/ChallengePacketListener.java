package com.deathmotion.marlowcrystal.listener;

import com.deathmotion.marlowcrystal.packet.impl.ChallengePacket;
import com.deathmotion.marlowcrystal.packet.impl.ChallengeResponsePacket;
//? if <1.20.5 {
/*import io.netty.buffer.Unpooled;
*///?}
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//? if <1.20.5 {
/*import net.minecraft.network.FriendlyByteBuf;
*///?}

public final class ChallengePacketListener {

    private ChallengePacketListener() {
    }

    public static void register() {
        //? if >=1.20.5 {
        ClientPlayNetworking.registerGlobalReceiver(ChallengePacket.TYPE, (payload, context) ->
                ClientPlayNetworking.send(new ChallengeResponsePacket(payload.challengeId())));
        //?} else {
        /*ClientPlayNetworking.registerGlobalReceiver(ChallengePacket.ID, (client, handler, buf, sender) -> {
            FriendlyByteBuf out = new FriendlyByteBuf(Unpooled.buffer());
            new ChallengeResponsePacket(buf.readInt()).write(out);
            ClientPlayNetworking.send(ChallengeResponsePacket.ID, out);
        });
        *///?}
    }
}
