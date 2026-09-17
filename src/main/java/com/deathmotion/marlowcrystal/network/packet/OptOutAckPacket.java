package com.deathmotion.marlowcrystal.network.packet;

import com.deathmotion.marlowcrystal.network.ServerboundPacket;
import net.minecraft.network.FriendlyByteBuf;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
//?} else {
/*import net.minecraft.resources.Identifier;
*///?}
import org.jetbrains.annotations.NotNull;

public final class OptOutAckPacket implements ServerboundPacket {

    //? if >=1.20.5 {
    public static final Type<@NotNull OptOutAckPacket> TYPE = new Type<>(Channels.of("opt_out_ack"));
    //?} else {
    /*public static final Identifier ID = Channels.of("opt_out_ack");
    *///?}

    public static final OptOutAckPacket INSTANCE = new OptOutAckPacket();

    //? if >=1.20.5 {
    public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull OptOutAckPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull FriendlyByteBuf buffer, @NotNull OptOutAckPacket value) {
        }

        @Override
        public @NotNull OptOutAckPacket decode(@NotNull FriendlyByteBuf buffer) {
            return INSTANCE;
        }
    };
    //?}

    private OptOutAckPacket() {
    }

    //? if >=1.20.5 {
    @Override
    public @NotNull Type<@NotNull OptOutAckPacket> type() {
        return TYPE;
    }
    //?} else {
    /*@Override
    public @NotNull Identifier id() {
        return ID;
    }

    @Override
    public void write(@NotNull FriendlyByteBuf buf) {
    }
    *///?}
}
