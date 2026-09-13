package com.deathmotion.marlowcrystal.listener;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.crystal.KeptCrystals;
import com.deathmotion.marlowcrystal.state.OptOutState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.jetbrains.annotations.NotNull;

public final class DisconnectEventListener implements ClientPlayConnectionEvents.Disconnect {

    private final OptOutState optOutState;

    public DisconnectEventListener() {
        optOutState = MarlowCrystal.getInstance().getOptOutState();
    }

    @Override
    public void onPlayDisconnect(@NotNull ClientPacketListener handler, @NotNull Minecraft client) {
        optOutState.reset();
        KeptCrystals.reset();
    }
}
