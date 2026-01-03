package com.fhfelipefh.mixin;

import com.fhfelipefh.CreeperExplosionRules;
import com.fhfelipefh.RealisticCreeperExplosionBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreeperEntity.class)
public abstract class CreeperExplosionMixin {

    @Redirect(
            method = "explode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/World$ExplosionSourceType;)V"
            )
    )
    private void redirectExplosion(
            ServerWorld world,
            Entity entity,
            double x, double y, double z,
            float power,
            World.ExplosionSourceType type
    ) {
        ExplosionBehavior behavior = new RealisticCreeperExplosionBehavior(entity);
        float adjustedPower = CreeperExplosionRules.scaleCreeperPower(power);

        world.createExplosion(
                entity,
                null,
                behavior,
                x, y, z,
                adjustedPower,
                false,
                type
        );
    }
}
