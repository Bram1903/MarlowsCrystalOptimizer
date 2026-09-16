//? if neoforge {
/*package com.deathmotion.marlowcrystal.loader.neoforge;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.integration.yacl.YaclScreenFactory;
import com.deathmotion.marlowcrystal.listener.OptOutPacketListener;
import com.deathmotion.marlowcrystal.packet.impl.ChallengePacket;
import com.deathmotion.marlowcrystal.packet.impl.ChallengeResponsePacket;
import com.deathmotion.marlowcrystal.packet.impl.OptOutAckPacket;
import com.deathmotion.marlowcrystal.packet.impl.OptOutPacket;
//? if >=1.20.5 {
import com.deathmotion.marlowcrystal.packet.impl.VersionPacket;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
//? if >=1.20.5 {
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
//?} else {
/^import net.neoforged.neoforge.client.ConfigScreenHandler;
^///?}
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
//? if >=1.20.5 {
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
//?} else {
/^import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
^///?}

@Mod(MarlowCrystal.MOD_ID)
public class MarlowCrystalNeoForge {

    private static final String YACL_MOD_ID = "yet_another_config_lib_v3";

    public MarlowCrystalNeoForge(IEventBus modBus, ModContainer container, Dist dist) {
        if (!dist.isClient()) {
            return;
        }

        MarlowCrystal.initialize();

        modBus.addListener(MarlowCrystalNeoForge::registerPayloads);
        NeoForge.EVENT_BUS.addListener(MarlowCrystalNeoForge::onLoggingIn);
        NeoForge.EVENT_BUS.addListener(MarlowCrystalNeoForge::onLoggingOut);

        if (ModList.get().isLoaded(YACL_MOD_ID)) {
            registerConfigScreen(container);
        }
    }

    @SuppressWarnings("unused") // the lambda signature is fixed by NeoForge
    private static void registerConfigScreen(ModContainer container) {
        //? if >=1.20.5 {
        container.registerExtensionPoint(IConfigScreenFactory.class, (owner, parent) -> YaclScreenFactory.create(parent));
        //?} else {
        /^container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> YaclScreenFactory.create(parent)));
        ^///?}
    }

    //? if >=1.20.5 {
    @SuppressWarnings("unused") // the lambda signatures are fixed by NeoForge
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();

        registrar.playToClient(OptOutPacket.TYPE, OptOutPacket.STREAM_CODEC, (payload, context) -> onOptOut());
        registrar.playToClient(ChallengePacket.TYPE, ChallengePacket.STREAM_CODEC, (payload, context) -> onChallenge(payload));

        // Registered so NeoForge can encode them; the server side handlers never run on a client.
        registrar.playToServer(OptOutAckPacket.TYPE, OptOutAckPacket.STREAM_CODEC, (payload, context) -> {});
        registrar.playToServer(VersionPacket.TYPE, VersionPacket.STREAM_CODEC, (payload, context) -> {});
        registrar.playToServer(ChallengeResponsePacket.TYPE, ChallengeResponsePacket.STREAM_CODEC, (payload, context) -> {});
    }
    //?} else {
    /^@SuppressWarnings("unused") // the lambda signatures are fixed by NeoForge
    private static void registerPayloads(RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(MarlowCrystal.MOD_ID).optional();

        registrar.play(OptOutPacket.ID, buf -> OptOutPacket.INSTANCE, handlers -> handlers.client(
                (OptOutPacket payload, PlayPayloadContext context) -> context.workHandler().execute(MarlowCrystalNeoForge::onOptOut)));
        registrar.play(ChallengePacket.ID, buf -> new ChallengePacket(buf.readInt()), handlers -> handlers.client(
                (ChallengePacket payload, PlayPayloadContext context) -> context.workHandler().execute(() -> onChallenge(payload))));
    }
    ^///?}

    private static void onOptOut() {
        OptOutPacketListener.handle(Minecraft.getInstance());

        send(OptOutAckPacket.INSTANCE);
    }

    private static void onChallenge(ChallengePacket payload) {
        send(new ChallengeResponsePacket(payload.challengeId()));
    }

    private static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        if (Minecraft.getInstance().isLocalServer()) return;

        send(MarlowCrystal.getInstance().getVersionPacket());
    }

    private static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        MarlowCrystal.getInstance().onDisconnect();
    }

    // Through the connection because NeoForge's packet listener drops payloads on channels the server never
    // registered, which a Fabric client sends anyway.
    private static void send(CustomPacketPayload payload) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            connection.getConnection().send(new ServerboundCustomPayloadPacket(payload));
        }
    }
}
*///?}
