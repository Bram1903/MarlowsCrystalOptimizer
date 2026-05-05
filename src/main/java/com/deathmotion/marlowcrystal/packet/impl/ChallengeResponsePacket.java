package com.deathmotion.marlowcrystal.packet.impl;

import com.deathmotion.marlowcrystal.packet.ModPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public final class ChallengeResponsePacket {
    public static final ResourceLocation ID = ModPackets.id("challenge_response");

    private final int challengeId;

    public ChallengeResponsePacket(int challengeId) {
        this.challengeId = challengeId;
    }

    public int challengeId() {
        return challengeId;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(challengeId);
    }
}
