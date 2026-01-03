package com.deathmotion.marlowcrystal.packet.impl;

import com.deathmotion.marlowcrystal.packet.ModPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public final class OptOutAckPacket implements CustomPacketPayload {
    public static final Type<@NotNull OptOutAckPacket> TYPE = new CustomPacketPayload.Type<>(ModPackets.id("opt_out_ack"));

    public static final OptOutAckPacket INSTANCE = new OptOutAckPacket();

    public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull OptOutAckPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buffer, OptOutAckPacket value) {
            // No data to encode
        }

        @Override
        public @NotNull OptOutAckPacket decode(FriendlyByteBuf buffer) {
            return INSTANCE;
        }
    };

    private OptOutAckPacket() {
    }

    @Override
    public @NotNull Type<@NotNull OptOutAckPacket> type() {
        return TYPE;
    }
}
