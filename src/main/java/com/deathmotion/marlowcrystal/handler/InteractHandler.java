package com.deathmotion.marlowcrystal.handler;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.cache.OptOutCache;
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
import org.spongepowered.asm.mixin.Unique;

public class InteractHandler implements ServerboundInteractPacket.Handler {

    @Unique
    private final Minecraft client;

    @Unique
    private OptOutCache optOutCache;

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
        if (optOutCache == null) {
            optOutCache = MarlowCrystal.getInstance().getOptOutCache();
        }
        if (optOutCache.isOptedOut()) {
            return;
        }

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
            retargetCrosshair(crystal);
        }
    }

    private void retargetCrosshair(EndCrystal crystal) {
        LocalPlayer player = client.player;
        if (player == null || client.hitResult == null || client.crosshairPickEntity != crystal) {
            return;
        }

        HitResult retraced = player.pick(player.blockInteractionRange(), 1.0F, false);
        client.crosshairPickEntity = null;
        client.hitResult = retraced;
    }

    private boolean canDestroyCrystal(LocalPlayer player) {
        double damage = player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
        damage += getWeaponDamage(player.getMainHandItem());

        MobEffectInstance strength = player.getEffect(MobEffects.DAMAGE_BOOST);
        if (strength != null) {
            damage += 3.0D * (strength.getAmplifier() + 1);
        }

        MobEffectInstance weakness = player.getEffect(MobEffects.WEAKNESS);
        if (weakness != null) {
            damage -= 4.0D * (weakness.getAmplifier() + 1);
        }

        return damage > 0.0D;
    }

    private double getWeaponDamage(ItemStack item) {
        if (item.isEmpty()) return 0.0D;
        final double[] sum = {0.0D};
        item.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            if (Attributes.ATTACK_DAMAGE.equals(attribute)) {
                sum[0] += modifier.amount();
            }
        });
        return sum[0];
    }

    private void destroyCrystal(Entity crystal) {
        crystal.remove(Entity.RemovalReason.KILLED);
        crystal.gameEvent(GameEvent.ENTITY_DIE);
    }
}
