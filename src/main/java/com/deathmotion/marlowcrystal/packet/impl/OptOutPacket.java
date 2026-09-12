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
import org.jetbrains.annotations.NotNull;

//? if >=1.20.2 {
public final class OptOutPacket implements CustomPacketPayload {
//?} else {
/*public final class OptOutPacket {
*///?}

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<@NotNull OptOutPacket> TYPE = new CustomPacketPayload.Type<>(ModPackets.id("opt_out"));
    //?} else {
    /*public static final Identifier ID = ModPackets.id("opt_out");
    *///?}

    public static final OptOutPacket INSTANCE = new OptOutPacket();

    //? if >=1.20.5 {
    public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull OptOutPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buffer, OptOutPacket value) {
        }

        @Override
        public @NotNull OptOutPacket decode(FriendlyByteBuf buffer) {
            return INSTANCE;
        }
    };
    //?}

    private OptOutPacket() {
    }

    //? if >=1.20.5 {
    @Override
    public @NotNull Type<@NotNull OptOutPacket> type() {
        return TYPE;
    }
    //?} else {
    /*public void write(FriendlyByteBuf buf) {
    }
    *///?}

    //? if >=1.20.2 <1.20.5 {
    /*@Override
    public @NotNull Identifier id() {
        return ID;
    }
    *///?}
}
