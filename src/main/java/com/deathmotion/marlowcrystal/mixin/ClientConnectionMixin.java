package com.deathmotion.marlowcrystal.mixin;

import com.deathmotion.marlowcrystal.MarlowCrystal;
import com.deathmotion.marlowcrystal.cache.OptOutCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.DoubleAdder;

@Mixin(Connection.class)
public class ClientConnectionMixin {

    @Unique
    private final DoubleAdder damageAdder = new DoubleAdder();

    @Unique
    private OptOutCache optOutCache;

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"))
    private void onPacketSend(Packet<?> packet, CallbackInfo ci) {
        if (!(packet instanceof ServerboundAttackPacket(int entityId))) {
            return;
        }

        if (optOutCache == null) {
            optOutCache = MarlowCrystal.getInstance().getOptOutCache();
        }
        if (optOutCache.isOptedOut()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        Entity target = mc.level.getEntity(entityId);
        if (!(target instanceof EndCrystal crystal)) {
            return;
        }

        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }

        if (canDestroyCrystal(player)) {
            destroyCrystal(crystal);
        }
    }

    @Unique
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

    @Unique
    private double calculateTotalDamage(LocalPlayer player) {
        double baseDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double weaponDamage = getWeaponDamage(player.getMainHandItem());

        MobEffectInstance strength = player.getEffect(MobEffects.STRENGTH);
        double strengthBonus = (strength != null) ? 3.0D * (strength.getAmplifier() + 1) : 0.0D;

        MobEffectInstance weakness = player.getEffect(MobEffects.WEAKNESS);
        double weaknessPenalty = (weakness != null) ? 4.0D * (weakness.getAmplifier() + 1) : 0.0D;

        return Math.max(0.0D, baseDamage + weaponDamage + strengthBonus - weaknessPenalty);
    }

    @Unique
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

    @Unique
    private void destroyCrystal(EndCrystal crystal) {
        crystal.remove(Entity.RemovalReason.KILLED);
        crystal.gameEvent(GameEvent.ENTITY_DIE);
    }
}
