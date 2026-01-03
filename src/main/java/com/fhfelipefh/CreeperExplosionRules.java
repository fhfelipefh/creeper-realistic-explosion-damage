package com.fhfelipefh;

import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.BlockSoundGroup;

public final class CreeperExplosionRules {
    public static final float RESISTANT_BLAST_RESISTANCE = 6.0F;
    public static final float WEAK_RESISTANCE_THRESHOLD = 3.0F;
    public static final float WEAK_RESISTANCE_MULTIPLIER = 0.125F;
    public static final float DIRT_RESISTANCE_MULTIPLIER = 0.05F;
    public static final float FRAGILE_RESISTANCE_MULTIPLIER = 0.075F;
    public static final float GLASS_RESISTANCE_MULTIPLIER = 0.025F;
    public static final float CREEPER_POWER_MULTIPLIER = 1.2F;

    private CreeperExplosionRules() {
    }

    public static boolean isResistant(BlockState state) {
        return state.isIn(ModTags.CREEPER_IMMUNE)
                || state.getBlock().getBlastResistance() >= RESISTANT_BLAST_RESISTANCE;
    }

    public static boolean isFragile(BlockState state) {
        return state.isIn(ModTags.CREEPER_FRAGILE);
    }

    public static boolean isGlass(BlockState state) {
        return state.getSoundGroup() == BlockSoundGroup.GLASS;
    }

    public static boolean isDirt(BlockState state) {
        return state.isIn(BlockTags.DIRT);
    }

    public static float applyFragility(BlockState state, float resistance) {
        if (isResistant(state)) {
            return resistance;
        }
        if (isDirt(state)) {
            return Math.max(0.0F, resistance * DIRT_RESISTANCE_MULTIPLIER);
        }
        if (isGlass(state)) {
            return Math.max(0.0F, resistance * GLASS_RESISTANCE_MULTIPLIER);
        }
        if (isFragile(state)) {
            return Math.max(0.0F, resistance * FRAGILE_RESISTANCE_MULTIPLIER);
        }
        if (resistance <= WEAK_RESISTANCE_THRESHOLD) {
            return Math.max(0.0F, resistance * WEAK_RESISTANCE_MULTIPLIER);
        }
        return resistance;
    }

    public static float scaleCreeperPower(float power) {
        return power * CREEPER_POWER_MULTIPLIER;
    }
}
