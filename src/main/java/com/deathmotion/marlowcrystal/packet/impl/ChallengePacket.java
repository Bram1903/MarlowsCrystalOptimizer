package com.deathmotion.marlowcrystal.packet.impl;

import com.deathmotion.marlowcrystal.packet.ModPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ChallengePacket(int id) implements CustomPacketPayload {

    public static final Type<@NotNull ChallengePacket> TYPE = new Type<>(ModPackets.id("challenge"));

    public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull ChallengePacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, ChallengePacket value) {
            buf.writeInt(value.id);
        }

        @Override
        public @NotNull ChallengePacket decode(FriendlyByteBuf buf) {
            return new ChallengePacket(buf.readInt());
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
