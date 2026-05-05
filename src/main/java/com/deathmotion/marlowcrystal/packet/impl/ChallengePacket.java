package com.deathmotion.marlowcrystal.packet.impl;

import com.deathmotion.marlowcrystal.packet.ModPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class ChallengePacket implements CustomPacketPayload {
    public static final ResourceLocation ID = ModPackets.id("challenge");

    private final int challengeId;

    public ChallengePacket(int challengeId) {
        this.challengeId = challengeId;
    }

    public int challengeId() {
        return challengeId;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(challengeId);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
