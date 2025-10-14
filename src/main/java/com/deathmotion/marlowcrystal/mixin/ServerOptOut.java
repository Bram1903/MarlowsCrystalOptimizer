package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.packets.OptOutPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ServerOptOut {

    @Inject(at = @At("TAIL"), method = "handleLogin")
    private void sendInfoPackage(ClientboundLoginPacket packet, CallbackInfo ci) {
        ClientPlayNetworking.send(OptOutPacket.INSTANCE);
    }
}
