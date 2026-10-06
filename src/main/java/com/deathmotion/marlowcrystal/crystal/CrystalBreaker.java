package com.deathmotion.marlowcrystal.crystal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

public final class CrystalBreaker {

    private final KeptCrystals keptCrystals;

    public CrystalBreaker(KeptCrystals keptCrystals) {
        this.keptCrystals = keptCrystals;
    }

    // The crosshair stays on the hidden crystal until the server removes it, so a click meets it the way it meets any
    // crystal the server still holds. Only a click meant for another crystal sees past it, see PastKeptCrystal.
    public void breakIfPossible(Minecraft client, EndCrystal crystal) {
        LocalPlayer player = client.player;
        ClientLevel level = client.level;
        if (player == null || level == null) {
            return;
        }

        // The server silently ignores hits on entities outside the world border.
        if (!level.getWorldBorder().isWithinBounds(crystal.blockPosition()) || !AttackDamage.breaksCrystal(player)) {
            return;
        }

        keptCrystals.keep(crystal, ((SequencedLevel) level).marlowcrystal$blockSequence());
    }
}
