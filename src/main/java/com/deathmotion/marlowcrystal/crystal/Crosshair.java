package com.deathmotion.marlowcrystal.crystal;

import net.minecraft.client.Minecraft;
//? if >=26.1 {
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
//?}
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.EntityHitResult;

final class Crosshair {

    private Crosshair() {
    }

    static void retargetPast(Minecraft client, EndCrystal crystal) {
        // Not crosshairPickEntity: before 1.20.3 Minecraft only put living entities and item frames there.
        if (!(client.hitResult instanceof EntityHitResult target) || target.getEntity() != crystal) {
            return;
        }

        //? if >=26.1 {
        LocalPlayer player = client.player;
        Entity camera = client.getCameraEntity();
        if (player == null || camera == null) {
            return;
        }

        client.hitResult = player.raycastHitResult(1.0F, camera);
        client.crosshairPickEntity = client.hitResult instanceof EntityHitResult hit ? hit.getEntity() : null;
        //?} else {
        /*client.gameRenderer.pick(1.0F);
        *///?}
    }
}
