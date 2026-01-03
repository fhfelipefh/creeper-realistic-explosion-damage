package com.fhfelipefh;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModTags {
    public static final String MOD_ID = "creeper-realistic-explosion-damage";

    public static final TagKey<Block> CREEPER_IMMUNE =
            TagKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "creeper_immune"));
    public static final TagKey<Block> CREEPER_FRAGILE =
            TagKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "creeper_fragile"));

    private ModTags() {
    }
}
