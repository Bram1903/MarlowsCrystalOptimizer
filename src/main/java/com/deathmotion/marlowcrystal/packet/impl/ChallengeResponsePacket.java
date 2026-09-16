package com.deathmotion.marlowcrystal.packet.impl;

import com.deathmotion.marlowcrystal.packet.ModPackets;
import net.minecraft.network.FriendlyByteBuf;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
//?}
//? if >=1.20.2 {
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?}
//? if <1.20.5 {
/*import net.minecraft.resources.Identifier;
*///?}
//? if >=1.20.2 {
import org.jetbrains.annotations.NotNull;
//?}

//? if >=1.20.2 {
public record ChallengeResponsePacket(int challengeId) implements CustomPacketPayload {
//?} else {
/*public record ChallengeResponsePacket(int challengeId) {
*///?}

    //? if >=1.20.5 {
    public static final Type<@NotNull ChallengeResponsePacket> TYPE = new Type<>(ModPackets.id("challenge_response"));

    public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull ChallengeResponsePacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, ChallengeResponsePacket value) {
            buf.writeInt(value.challengeId());
        }

        @Override
        public @NotNull ChallengeResponsePacket decode(FriendlyByteBuf buf) {
            return new ChallengeResponsePacket(buf.readInt());
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final Identifier ID = ModPackets.id("challenge_response");

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(challengeId);
    }
    *///?}

    //? if >=1.20.2 <1.20.5 {
    /*@Override
    public @NotNull Identifier id() {
        return ID;
    }
    *///?}
}
