package com.deathmotion.marlowcrystal.network.packet;

import com.deathmotion.marlowcrystal.network.ServerboundPacket;
import com.deathmotion.marlowcrystal.version.CurrentBuild;
import com.deathmotion.marlowcrystal.version.ModLoader;
import com.deathmotion.marlowcrystal.version.ModVersion;
import net.minecraft.network.FriendlyByteBuf;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
//?} else {
/*import net.minecraft.resources.Identifier;
*///?}
import org.jetbrains.annotations.NotNull;

public record VersionPacket(int major, int minor, int patch, boolean snapshot, ModLoader loader, String commit,
                            boolean dirty, long buildTimestamp) implements ServerboundPacket {

    private static final int COMMIT_MAX_LENGTH = 40;

    @SuppressWarnings("ConstantValue") // CurrentBuild.COMMIT is generated and is null when git is unavailable
    public static VersionPacket current() {
        ModVersion version = CurrentBuild.VERSION;
        String commit = CurrentBuild.COMMIT;
        return new VersionPacket(version.major(), version.minor(), version.patch(), version.snapshot(), CurrentBuild.LOADER,
                commit != null ? commit : "", CurrentBuild.DIRTY, CurrentBuild.TIMESTAMP.toEpochMilli());
    }

    private static void encode(FriendlyByteBuf buf, VersionPacket packet) {
        buf.writeVarInt(packet.major());
        buf.writeVarInt(packet.minor());
        buf.writeVarInt(packet.patch());
        buf.writeBoolean(packet.snapshot());
        buf.writeVarInt(packet.loader().networkId());
        buf.writeUtf(packet.commit(), COMMIT_MAX_LENGTH);
        buf.writeBoolean(packet.dirty());
        buf.writeLong(packet.buildTimestamp());
    }

    //? if >=1.20.5 {
    public static final Type<VersionPacket> TYPE = new Type<>(Channels.of("version"));

    public static final StreamCodec<FriendlyByteBuf, VersionPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull FriendlyByteBuf buf, @NotNull VersionPacket packet) {
            VersionPacket.encode(buf, packet);
        }

        @Override
        public @NotNull VersionPacket decode(@NotNull FriendlyByteBuf buf) {
            int major = buf.readVarInt();
            int minor = buf.readVarInt();
            int patch = buf.readVarInt();
            boolean snapshot = buf.readBoolean();
            if (!buf.isReadable()) {
                return new VersionPacket(major, minor, patch, snapshot, null, "", false, 0L);
            }

            VersionPacket packet = new VersionPacket(major, minor, patch, snapshot, ModLoader.byNetworkId(buf.readVarInt()),
                    buf.readUtf(COMMIT_MAX_LENGTH), buf.readBoolean(), buf.readLong());
            buf.skipBytes(buf.readableBytes());
            return packet;
        }
    };

    @Override
    public @NotNull Type<VersionPacket> type() {
        return TYPE;
    }
    //?} else {
    /*public static final Identifier ID = Channels.of("version");

    @Override
    public @NotNull Identifier id() {
        return ID;
    }

    @Override
    public void write(@NotNull FriendlyByteBuf buf) {
        encode(buf, this);
    }
    *///?}
}
