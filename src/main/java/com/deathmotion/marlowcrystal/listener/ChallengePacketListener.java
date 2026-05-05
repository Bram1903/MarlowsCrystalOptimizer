package com.deathmotion.marlowcrystal.listener;

import com.deathmotion.marlowcrystal.packet.impl.ChallengePacket;
import com.deathmotion.marlowcrystal.packet.impl.ChallengeResponsePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class ChallengePacketListener {

    private ChallengePacketListener() {
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ChallengePacket.TYPE, (payload, context) ->
                ClientPlayNetworking.send(new ChallengeResponsePacket(payload.id())));
    }
}
