//? if <26.1 {
/*package com.deathmotion.marlowcrystal.handler;

import com.deathmotion.marlowcrystal.crystal.CrystalBreaker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public final class InteractHandler implements ServerboundInteractPacket.Handler {

    private final Minecraft client;

    public InteractHandler(Minecraft client) {
        this.client = client;
    }

    @Override
    public void onInteraction(InteractionHand hand) {
    }

    @Override
    public void onInteraction(InteractionHand hand, Vec3 position) {
    }

    @Override
    public void onAttack() {
        if (client.hitResult instanceof EntityHitResult hit && hit.getEntity() instanceof EndCrystal crystal) {
            CrystalBreaker.breakIfPossible(client, crystal);
        }
    }
}
*///?}
