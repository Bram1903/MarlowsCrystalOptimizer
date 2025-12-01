package com.deathmotion.marlowcrystal.packets;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class OptOutPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<@NotNull OptOutPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.parse("mco"));
    public static final OptOutPacket INSTANCE = new OptOutPacket();

    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull OptOutPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(RegistryFriendlyByteBuf buffer, OptOutPacket optOutPacket) {
            // No data to encode
        }

        @Override
        public @NotNull OptOutPacket decode(RegistryFriendlyByteBuf buffer) {
            return INSTANCE;
        }
    };

    private OptOutPacket() {
    }

    @Override
    public @NotNull Type<@NotNull OptOutPacket> type() {
        return TYPE;
    }
}
