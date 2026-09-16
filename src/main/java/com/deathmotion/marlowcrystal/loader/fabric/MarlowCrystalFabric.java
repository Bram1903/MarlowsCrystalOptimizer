//? if fabric {
package com.deathmotion.marlowcrystal.loader.fabric;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.listener.OptOutPacketListener;
import com.deathmotion.marlowcrystal.packet.impl.ChallengePacket;
import com.deathmotion.marlowcrystal.packet.impl.ChallengeResponsePacket;
import com.deathmotion.marlowcrystal.packet.impl.OptOutAckPacket;
import com.deathmotion.marlowcrystal.packet.impl.OptOutPacket;
import com.deathmotion.marlowcrystal.packet.impl.VersionPacket;
//? if <1.20.5 {
/*import io.netty.buffer.Unpooled;
*///?}
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//? if >=1.20.2 {
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
//?}
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//? if >=1.20.5 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//?}
//? if <1.20.5 {
/*import net.minecraft.network.FriendlyByteBuf;
*///?}

@Environment(EnvType.CLIENT)
public class MarlowCrystalFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MarlowCrystal.initialize();

        //? if >=1.20.5 {
        PayloadTypeRegistry.clientboundConfiguration().register(OptOutPacket.TYPE, OptOutPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OptOutPacket.TYPE, OptOutPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ChallengePacket.TYPE, ChallengePacket.STREAM_CODEC);

        PayloadTypeRegistry.serverboundPlay().register(OptOutAckPacket.TYPE, OptOutAckPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(VersionPacket.TYPE, VersionPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ChallengeResponsePacket.TYPE, ChallengeResponsePacket.STREAM_CODEC);
        //?}

        registerListeners();
    }

    @SuppressWarnings("unused") // the lambda signatures are fixed by Fabric
    private static void registerListeners() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.isLocalServer()) return;

            //? if >=1.20.5 {
            sender.sendPacket(MarlowCrystal.getInstance().getVersionPacket());
            //?} else {
            /*FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            MarlowCrystal.getInstance().getVersionPacket().write(buf);
            ClientPlayNetworking.send(VersionPacket.ID, buf);
            *///?}
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> MarlowCrystal.getInstance().onDisconnect());
        //? if >=1.20.2 {
        ClientConfigurationConnectionEvents.DISCONNECT.register((handler, client) -> MarlowCrystal.getInstance().onDisconnect());
        //?}

        //? if >=1.20.5 {
        ClientPlayNetworking.registerGlobalReceiver(OptOutPacket.TYPE, (payload, context) -> {
            OptOutPacketListener.handle(context.client());

            ClientPlayNetworking.send(OptOutAckPacket.INSTANCE);
        });
        ClientPlayNetworking.registerGlobalReceiver(ChallengePacket.TYPE, (payload, context) ->
                ClientPlayNetworking.send(new ChallengeResponsePacket(payload.challengeId())));
        //?} else {
        /*ClientPlayNetworking.registerGlobalReceiver(OptOutPacket.ID, (client, handler, buf, sender) -> {
            OptOutPacketListener.handle(client);

            FriendlyByteBuf ackBuf = new FriendlyByteBuf(Unpooled.buffer());
            OptOutAckPacket.INSTANCE.write(ackBuf);
            ClientPlayNetworking.send(OptOutAckPacket.ID, ackBuf);
        });
        ClientPlayNetworking.registerGlobalReceiver(ChallengePacket.ID, (client, handler, buf, sender) -> {
            FriendlyByteBuf out = new FriendlyByteBuf(Unpooled.buffer());
            new ChallengeResponsePacket(buf.readInt()).write(out);
            ClientPlayNetworking.send(ChallengeResponsePacket.ID, out);
        });
        *///?}
    }
}
//?}
