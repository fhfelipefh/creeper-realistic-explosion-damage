package com.fhfelipefh;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import net.minecraft.world.explosion.Explosion;

public final class RealisticCreeperExplosionBehavior extends EntityExplosionBehavior {

    public RealisticCreeperExplosionBehavior(net.minecraft.entity.Entity entity) {
        super(entity);
    }

    @Override
    public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
        if (CreeperExplosionRules.isResistant(state)) {
            return false;
        }
        return super.canDestroyBlock(explosion, world, pos, state, power);
    }
}
