package com.deathmotion.marlowcrystal.network;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

final class OptOutNotice {

    private static final Component PREFIX = Component.literal("[").withStyle(ChatFormatting.GRAY)
            .append(Component.literal("Marlow's Crystal Optimizer").withStyle(ChatFormatting.AQUA))
            .append(Component.literal("] ").withStyle(ChatFormatting.GRAY));

    private OptOutNotice() {
    }

    static void showLater(Minecraft client) {
        CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS)
                .execute(() -> client.execute(() -> show(client)));
    }

    private static void show(Minecraft client) {
        if (client.player == null) {
            return;
        }

        //? if >=26.1 {
        client.player.sendSystemMessage(message());
        //?} else {
        /*client.player.displayClientMessage(message(), false);
        *///?}
    }

    private static Component message() {
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
        Component text = Component.literal("Optimizer disabled on this server.")
                .withStyle(hoverStyle.withColor(ChatFormatting.RED));

        return PREFIX.copy()
                .withStyle(hoverStyle)
                .append(text);
    }
}
