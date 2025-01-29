package com.marlowcrystal.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Unique;

public class InteractHandler implements ServerboundInteractPacket.Handler {

    @Unique
    private final Minecraft client;

    public InteractHandler(Minecraft client) {
        this.client = client;
    }

    @Override
    public void onInteraction(InteractionHand interactionHand) {
    }

    @Override
    public void onInteraction(InteractionHand interactionHand, Vec3 vec3) {
    }

    @Override
    public void onAttack() {
        HitResult hitResult = client.hitResult;
        if (!(hitResult instanceof EntityHitResult entityHitResult)) {
            return;
        }

        Entity entity = entityHitResult.getEntity();
        if (!(entity instanceof EndCrystal)) {
            return;
        }

        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }

        // Calculate the total damage
        double totalDamage = calculateTotalDamage(player);

        if (totalDamage > 0.0D) {
            destroyCrystal(entity, player);
        }
    }

    private double calculateTotalDamage(LocalPlayer player) {
        double baseDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double weaponDamage = getWeaponDamage(player.getMainHandItem());

        MobEffectInstance strength = player.getEffect(MobEffects.DAMAGE_BOOST);
        double strengthBonus = strength != null ? 3.0D * (strength.getAmplifier() + 1) : 0.0D;

        MobEffectInstance weakness = player.getEffect(MobEffects.WEAKNESS);
        double weaknessPenalty = weakness != null ? 4.0D * (weakness.getAmplifier() + 1) : 0.0D;

        // Total damage, ensuring it doesn't go negative
        return Math.max(0.0D, baseDamage + weaponDamage + strengthBonus - weaknessPenalty);
    }

    private double getWeaponDamage(ItemStack item) {
        if (item.isEmpty()) {
            return 0.0D;
        }

        final double[] totalDamage = {0.0D};

        // Iterate through modifiers and sum the damage values
        item.getAttributeModifiers(EquipmentSlot.MAINHAND).forEach((attribute, modifier) -> {
            if (Attributes.ATTACK_DAMAGE.equals(attribute)) {
                totalDamage[0] += modifier.getAmount();
            }
        });

        return totalDamage[0];
    }

    private void destroyCrystal(Entity crystal, LocalPlayer player) {
        crystal.remove(Entity.RemovalReason.KILLED);
        crystal.gameEvent(GameEvent.ENTITY_DIE);

        player.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F);
    }
}
