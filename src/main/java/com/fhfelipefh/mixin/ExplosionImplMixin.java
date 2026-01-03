package com.fhfelipefh.mixin;

import com.fhfelipefh.CreeperExplosionPreviewContext;
import com.fhfelipefh.CreeperExplosionRules;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(ExplosionImpl.class)
public abstract class ExplosionImplMixin {
    @Shadow
    @Final
    private Entity entity;

    @Redirect(
            method = "getBlocksToDestroy",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/explosion/ExplosionBehavior;canDestroyBlock(Lnet/minecraft/world/explosion/Explosion;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;F)Z"
            )
    )
    private boolean redirectCanDestroyBlock(
            ExplosionBehavior behavior,
            Explosion explosion,
            BlockView world,
            BlockPos pos,
            BlockState state,
            float power
    ) {
        if (CreeperExplosionPreviewContext.isBypassing()) {
            return behavior.canDestroyBlock(explosion, world, pos, state, power);
        }

        if (isCreeperExplosion() && CreeperExplosionRules.isResistant(state)) {
            return false;
        }

        return behavior.canDestroyBlock(explosion, world, pos, state, power);
    }

    @Redirect(
            method = "getBlocksToDestroy",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/explosion/ExplosionBehavior;getBlastResistance(Lnet/minecraft/world/explosion/Explosion;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/FluidState;)Ljava/util/Optional;"
            )
    )
    private Optional<Float> redirectBlastResistance(
            ExplosionBehavior behavior,
            Explosion explosion,
            BlockView world,
            BlockPos pos,
            BlockState state,
            FluidState fluidState
    ) {
        Optional<Float> base = behavior.getBlastResistance(explosion, world, pos, state, fluidState);
        if (base.isEmpty()) {
            return base;
        }

        if (CreeperExplosionPreviewContext.isBypassing()
                || !isCreeperExplosion()
                || CreeperExplosionRules.isResistant(state)) {
            return base;
        }

        float resistance = base.get();
        float adjusted = CreeperExplosionRules.applyFragility(state, resistance);
        return adjusted == resistance ? base : Optional.of(adjusted);
    }

    private boolean isCreeperExplosion() {
        return entity instanceof CreeperEntity;
    }
}
