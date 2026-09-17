package com.deathmotion.marlowcrystal.network;

//? if >=1.20.2 {
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else {
/*import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
*///?}

//? if >=1.20.2 {
public interface ServerboundPacket extends CustomPacketPayload {
}
//?} else {
/*public interface ServerboundPacket {

    Identifier id();

    void write(FriendlyByteBuf buf);
}
*///?}
