package com.deathmotion.marlowcrystal.crystal;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

final class AttackDamage {

    // The server ends an effect up to a round trip before the client hears about it.
    private static final int STRENGTH_ENDING_TICKS = 30;

    private AttackDamage() {
    }

    static boolean breaksCrystal(LocalPlayer player) {
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
}
