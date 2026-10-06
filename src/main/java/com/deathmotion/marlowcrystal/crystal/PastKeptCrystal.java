package com.deathmotion.marlowcrystal.crystal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

// A hidden crystal stays under the crosshair, so a click reaches it the way it reaches any crystal the server still
// holds. Two clicks are shown what lies behind it instead, both of them only for crystals: an attack that would land on
// another end crystal, and a use with an end crystal in hand that would land on obsidian or bedrock. Minecraft aims
// both from Minecraft.hitResult, which is swapped for the length of the click and put back after it.
public final class PastKeptCrystal {

    private final KeptCrystals keptCrystals;

    private HitResult crosshair;

    private EntityHitResult nextCrystal;

    private BlockHitResult crystalBase;

    public PastKeptCrystal(KeptCrystals keptCrystals) {
        this.keptCrystals = keptCrystals;
    }

    private static boolean isCrystal(ItemStack stack) {
        return stack.is(Items.END_CRYSTAL);
    }

    public void attackStarted(Minecraft client) {
        HitResult behind = behindKeptCrystal(client);
        if (behind instanceof EntityHitResult target && target.getEntity() instanceof EndCrystal) {
            crosshair = client.hitResult;
            nextCrystal = target;
            client.hitResult = target;
        }
    }

    public void attackEnded(Minecraft client) {
        if (nextCrystal != null) {
            client.hitResult = crosshair;
        }

        crosshair = null;
        nextCrystal = null;
    }

    public void useStarted(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null || !isCrystal(player.getMainHandItem()) && !isCrystal(player.getOffhandItem())) {
            return;
        }

        HitResult behind = behindKeptCrystal(client);
        if (!(behind instanceof BlockHitResult block) || block.getType() != HitResult.Type.BLOCK) {
            return;
        }

        // What EndCrystalItem.useOn asks of the block before it looks for entities.
        ClientLevel level = client.level;
        BlockPos pos = block.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(Blocks.OBSIDIAN) && !state.is(Blocks.BEDROCK) || !level.isEmptyBlock(pos.above())) {
            return;
        }

        crosshair = client.hitResult;
        crystalBase = block;
    }

    // Minecraft.startUseItem tries the main hand and then the off hand against one hit result, so a hand that does not
    // hold an end crystal still meets the hidden crystal.
    public void handTried(Minecraft client, ItemStack stack) {
        if (crystalBase != null) {
            client.hitResult = isCrystal(stack) ? crystalBase : crosshair;
        }
    }

    public void useEnded(Minecraft client) {
        if (crystalBase != null) {
            client.hitResult = crosshair;
        }

        crosshair = null;
        crystalBase = null;
    }

    // The pick Minecraft aims with, run again with the hidden crystals left out, so an entity behind one still wins.
    private HitResult behindKeptCrystal(Minecraft client) {
        LocalPlayer player = client.player;
        ClientLevel level = client.level;
        if (player == null || level == null || player.isSpectator()) {
            return null;
        }

        if (!(client.hitResult instanceof EntityHitResult target) || !keptCrystals.isHidden(target.getEntity())) {
            return null;
        }

        return keptCrystals.seePast(() -> pick(client, player));
    }

    private static HitResult pick(Minecraft client, LocalPlayer player) {
        //? if >=26.1 {
        Entity camera = client.getCameraEntity();
        return camera != null ? player.raycastHitResult(1.0F, camera) : null;
        //?} else {
        /*// GameRenderer.pick only writes its answer into the client, so the crosshair is put back.
        HitResult hitResult = client.hitResult;
        Entity pickEntity = client.crosshairPickEntity;
        client.gameRenderer.pick(1.0F);
        HitResult behind = client.hitResult;
        client.hitResult = hitResult;
        client.crosshairPickEntity = pickEntity;
        return behind;
        *///?}
    }
}
