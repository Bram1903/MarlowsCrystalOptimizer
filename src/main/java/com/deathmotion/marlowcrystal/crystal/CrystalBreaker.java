package com.deathmotion.marlowcrystal.crystal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
//? if >=26.1 {
import net.minecraft.world.entity.Entity;
//?}
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

public final class CrystalBreaker {

    // The server ends an effect up to a round trip before the client hears about it.
    private static final int STRENGTH_ENDING_TICKS = 30;

    private CrystalBreaker() {
    }

    public static void breakIfPossible(Minecraft client, EndCrystal crystal) {
        LocalPlayer player = client.player;
        ClientLevel level = client.level;
        if (player == null || level == null) {
            return;
        }

        // The server silently ignores hits on entities outside the world border.
        if (!level.getWorldBorder().isWithinBounds(crystal.blockPosition()) || !canDestroy(player)) {
            return;
        }

        KeptCrystals.keep(crystal);
        retargetCrosshair(client, crystal);
    }

    private static boolean canDestroy(LocalPlayer player) {
        double damage = player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
        damage += weaponDamage(player.getMainHandItem());

        MobEffectInstance strength = player.getEffect(MobEffects.STRENGTH);
        if (strength != null && (strength.getDuration() < 0 || strength.getDuration() > STRENGTH_ENDING_TICKS)) {
            damage += 3.0D * (strength.getAmplifier() + 1);
        }

        MobEffectInstance weakness = player.getEffect(MobEffects.WEAKNESS);
        if (weakness != null) {
            damage -= 4.0D * (weakness.getAmplifier() + 1);
        }

        return damage > 0.0D;
    }

    private static double weaponDamage(ItemStack item) {
        if (item.isEmpty()) {
            return 0.0D;
        }

        final double[] sum = {0.0D};
        //? if >=1.20.5 {
        item.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            if (Attributes.ATTACK_DAMAGE.equals(attribute)) {
                sum[0] += modifier.amount();
            }
        });
        //?} else {
        /*item.getAttributeModifiers(EquipmentSlot.MAINHAND).forEach((attribute, modifier) -> {
            if (Attributes.ATTACK_DAMAGE.equals(attribute)) {
                sum[0] += modifier.getAmount();
            }
        });
        *///?}
        return sum[0];
    }

    private static void retargetCrosshair(Minecraft client, EndCrystal crystal) {
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
