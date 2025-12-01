package com.deathmotion.marlowcrystal.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
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
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Unique;

import java.util.concurrent.atomic.DoubleAdder;

public class InteractHandler implements ServerboundInteractPacket.Handler {

    @Unique
    private final Minecraft client;

    @Unique
    private final DoubleAdder damageAdder = new DoubleAdder();

    public InteractHandler(Minecraft client) {
        this.client = client;
    }

    @Override
    public void onInteraction(@NotNull InteractionHand interactionHand) {
    }

    @Override
    public void onInteraction(@NotNull InteractionHand interactionHand, @NotNull Vec3 vec3) {
    }

    @Override
    public void onAttack() {
        HitResult hitResult = client.hitResult;
        if (!(hitResult instanceof EntityHitResult entityHitResult)) {
            return;
        }

        Entity entity = entityHitResult.getEntity();
        if (!(entity instanceof EndCrystal crystal)) {
            return;
        }

        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }

        if (canDestroyCrystal(player)) {
            destroyCrystal(crystal);
        }
    }

    private boolean canDestroyCrystal(LocalPlayer player) {
        MobEffectInstance weakness = player.getEffect(MobEffects.WEAKNESS);

        if (weakness == null) {
            return true;
        }

        double baseDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double weaknessPenalty = 4.0D * (weakness.getAmplifier() + 1);

        if (baseDamage > weaknessPenalty + 5.0D) {
            return true;
        }

        return calculateTotalDamage(player) > 0.0D;
    }

    private double calculateTotalDamage(LocalPlayer player) {
        double baseDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double weaponDamage = getWeaponDamage(player.getMainHandItem());

        MobEffectInstance strength = player.getEffect(MobEffects.STRENGTH);
        double strengthBonus = strength != null ? 3.0D * (strength.getAmplifier() + 1) : 0.0D;

        MobEffectInstance weakness = player.getEffect(MobEffects.WEAKNESS);
        double weaknessPenalty = weakness != null ? 4.0D * (weakness.getAmplifier() + 1) : 0.0D;

        return Math.max(0.0D, baseDamage + weaponDamage + strengthBonus - weaknessPenalty);
    }

    private double getWeaponDamage(ItemStack item) {
        if (item.isEmpty()) {
            return 0.0D;
        }

        damageAdder.reset();
        item.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            if (Attributes.ATTACK_DAMAGE.equals(attribute)) {
                damageAdder.add(modifier.amount());
            }
        });
        return damageAdder.sum();
    }

    private void destroyCrystal(Entity crystal) {
        crystal.remove(Entity.RemovalReason.KILLED);
        crystal.gameEvent(GameEvent.ENTITY_DIE);
    }
}
