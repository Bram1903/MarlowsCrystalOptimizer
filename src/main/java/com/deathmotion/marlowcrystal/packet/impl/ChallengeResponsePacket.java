package com.deathmotion.marlowcrystal.packet.impl;

import com.deathmotion.marlowcrystal.packet.ModPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ChallengeResponsePacket(int id) implements CustomPacketPayload {

    public static final Type<@NotNull ChallengeResponsePacket> TYPE = new Type<>(ModPackets.id("challenge_response"));

    public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull ChallengeResponsePacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, ChallengeResponsePacket value) {
            buf.writeInt(value.id);
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
}
