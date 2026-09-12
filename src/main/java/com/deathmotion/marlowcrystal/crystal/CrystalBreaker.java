package com.deathmotion.marlowcrystal.crystal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;

public final class CrystalBreaker {

    private CrystalBreaker() {
    }

    public static void breakIfPossible(Minecraft client, EndCrystal crystal) {
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }

        if (!canDestroy(player)) {
            return;
        }

        destroy(crystal);
        retargetCrosshair(client, crystal);
    }

    private static boolean canDestroy(LocalPlayer player) {
        double damage = player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
        damage += weaponDamage(player.getMainHandItem());

        MobEffectInstance strength = player.getEffect(MobEffects.STRENGTH);
        if (strength != null) {
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
        item.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            if (Attributes.ATTACK_DAMAGE.equals(attribute)) {
                sum[0] += modifier.amount();
            }
        });
        return sum[0];
    }

    private static void destroy(EndCrystal crystal) {
        crystal.remove(Entity.RemovalReason.KILLED);
        crystal.gameEvent(GameEvent.ENTITY_DIE);
    }

    private static void retargetCrosshair(Minecraft client, EndCrystal crystal) {
        LocalPlayer player = client.player;
        if (player == null || client.hitResult == null || client.crosshairPickEntity != crystal) {
            return;
        }

        HitResult retraced = player.pick(player.blockInteractionRange(), 1.0F, false);
        client.crosshairPickEntity = null;
        client.hitResult = retraced;
    }
}
