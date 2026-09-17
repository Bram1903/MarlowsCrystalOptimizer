package com.deathmotion.marlowcrystal.network.packet;

import com.deathmotion.marlowcrystal.network.ServerboundPacket;
import net.minecraft.network.FriendlyByteBuf;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
//?} else {
/*import net.minecraft.resources.Identifier;
*///?}
import org.jetbrains.annotations.NotNull;

public record ChallengeResponsePacket(int challengeId) implements ServerboundPacket {

    //? if >=1.20.5 {
    public static final Type<@NotNull ChallengeResponsePacket> TYPE = new Type<>(Channels.of("challenge_response"));

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
    public @NotNull Type<@NotNull ChallengeResponsePacket> type() {
        return TYPE;
    }
    //?} else {
    /*public static final Identifier ID = Channels.of("challenge_response");

    @Override
    public @NotNull Identifier id() {
        return ID;
    }

    @Override
    public void write(@NotNull FriendlyByteBuf buf) {
        buf.writeInt(challengeId);
    }
    *///?}
}
