package com.marlowcrystal.mixin;

import com.marlowcrystal.packets.OptOutPacket;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ServerOptOut {

    @Inject(at = @At("TAIL"), method = "handleLogin")
    private void sendInfoPackage(ClientboundLoginPacket packet, CallbackInfo ci) {
        ClientPacketListener networkHandler = (ClientPacketListener) ((Object) this);
        networkHandler.send(new ServerboundCustomPayloadPacket(new OptOutPacket()));
    }
}
