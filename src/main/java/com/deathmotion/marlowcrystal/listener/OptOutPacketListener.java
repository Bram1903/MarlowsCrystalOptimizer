package com.deathmotion.marlowcrystal.listener;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.packet.impl.OptOutAckPacket;
import com.deathmotion.marlowcrystal.packet.impl.OptOutPacket;
import com.deathmotion.marlowcrystal.state.OptOutState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class OptOutPacketListener {

    private OptOutPacketListener() {
    }

    private static Component optimizerDisabledMessage() {
        Component hover = Component.empty()
                .append(Component.literal("Why is this disabled?\n").withStyle(ChatFormatting.AQUA))
                .append(Component.literal("• This server has requested Marlow's Crystal Optimizer to be disabled.\n").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("• This may be to enforce server rules or avoid compatibility issues.\n").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("\nThis only applies while you are connected to this server.").withStyle(ChatFormatting.DARK_GRAY));

        //? if >=1.21.5 {
        Style hoverStyle = Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(hover));
        //?} else {
        /*Style hoverStyle = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
        *///?}
        Component message = Component.literal("Optimizer disabled on this server.")
                .withStyle(hoverStyle.withColor(ChatFormatting.RED));

        return MarlowCrystal.PREFIX.copy()
                .withStyle(hoverStyle)
                .append(message);
    }

    private static void showDisabledMessage(Minecraft client) {
        if (client.player == null) {
            return;
        }

        //? if >=26.1 {
        client.player.sendSystemMessage(optimizerDisabledMessage());
        //?} else {
        /*client.player.displayClientMessage(optimizerDisabledMessage(), false);
        *///?}
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(OptOutPacket.TYPE, (payload, context) -> {
            Minecraft client = context.client();
            OptOutState state = MarlowCrystal.getInstance().getOptOutState();

            state.markOptedOut();

            if (state.claimNotification()) {
                CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS)
                        .execute(() -> client.execute(() -> showDisabledMessage(client)));
            }

            ClientPlayNetworking.send(OptOutAckPacket.INSTANCE);
        });
    }
}
