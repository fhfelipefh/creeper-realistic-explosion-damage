package com.fhfelipefh.preview;

import com.fhfelipefh.CreeperExplosionPreviewContext;
import com.fhfelipefh.CreeperExplosionRules;
import com.fhfelipefh.RealisticCreeperExplosionBehavior;
import com.fhfelipefh.mixin.CreeperEntityAccessor;
import com.fhfelipefh.mixin.ExplosionImplAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class CreeperPreviewManager {
    private static final String TAG_ALL = "creeper_preview";
    private static final String TAG_PREFIX = "creeper_preview:";
    private static final String TAG_VANILLA = "creeper_preview:vanilla";
    private static final String TAG_CURRENT = "creeper_preview:current";
    private static final int UPDATE_INTERVAL_TICKS = 5;
    private static final float CHARGED_MULTIPLIER = 2.0F;
    private static final BlockState PREVIEW_VANILLA = Blocks.RED_STAINED_GLASS.getDefaultState();
    private static final BlockState PREVIEW_CURRENT = Blocks.LIME_STAINED_GLASS.getDefaultState();

    private static final Set<UUID> ENABLED_PLAYERS = new HashSet<>();
    private static final Map<RegistryKey<World>, Set<UUID>> ACTIVE_CREEPERS = new HashMap<>();

    private CreeperPreviewManager() {
    }

    public static boolean toggle(ServerPlayerEntity player) {
        UUID id = player.getUuid();
        if (ENABLED_PLAYERS.contains(id)) {
            ENABLED_PLAYERS.remove(id);
            return false;
        }
        ENABLED_PLAYERS.add(id);
        return true;
    }

    public static void onPlayerDisconnect(ServerPlayerEntity player) {
        ENABLED_PLAYERS.remove(player.getUuid());
    }

    public static void onWorldTick(ServerWorld world) {
        if (!hasEnabledPlayers(world)) {
            clearAllInWorld(world);
            return;
        }

        if (world.getTime() % UPDATE_INTERVAL_TICKS != 0) {
            return;
        }

        Map<UUID, CreeperEntity> creepersToPreview = collectCreepers(world);
        syncActivePreviews(world, creepersToPreview.keySet());

        for (CreeperEntity creeper : creepersToPreview.values()) {
            updatePreview(world, creeper);
        }
    }

    private static boolean hasEnabledPlayers(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (ENABLED_PLAYERS.contains(player.getUuid())) {
                return true;
            }
        }
        return false;
    }

    private static Map<UUID, CreeperEntity> collectCreepers(ServerWorld world) {
        Map<UUID, CreeperEntity> creepers = new HashMap<>();
        int viewDistanceChunks = world.getServer().getPlayerManager().getViewDistance();
        double range = (viewDistanceChunks + 1) * 16.0;

        for (ServerPlayerEntity player : world.getPlayers()) {
            if (!ENABLED_PLAYERS.contains(player.getUuid())) {
                continue;
            }

            Box box = player.getBoundingBox().expand(range);
            List<CreeperEntity> nearby = world.getEntitiesByType(
                    EntityType.CREEPER,
                    box,
                    creeper -> !creeper.isRemoved()
            );
            for (CreeperEntity creeper : nearby) {
                creepers.putIfAbsent(creeper.getUuid(), creeper);
            }
        }

        return creepers;
    }

    private static void syncActivePreviews(ServerWorld world, Set<UUID> current) {
        Set<UUID> active = ACTIVE_CREEPERS.computeIfAbsent(world.getRegistryKey(), key -> new HashSet<>());
        if (!active.isEmpty()) {
            Set<UUID> stale = new HashSet<>(active);
            stale.removeAll(current);
            for (UUID creeperId : stale) {
                clearPreviewForCreeper(world, creeperId);
            }
        }
        active.clear();
        active.addAll(current);
    }

    private static void updatePreview(ServerWorld world, CreeperEntity creeper) {
        UUID creeperId = creeper.getUuid();
        clearPreviewForCreeper(world, creeperId);

        PreviewSets sets = calculatePreview(world, creeper);
        if (!sets.current.isEmpty()) {
            spawnPreviewBlocks(world, sets.current, PREVIEW_CURRENT, creeperId, TAG_CURRENT, false);
        }
        if (isFuseActive(creeper) && !sets.vanilla.isEmpty()) {
            spawnPreviewBlocks(world, sets.vanilla, PREVIEW_VANILLA, creeperId, TAG_VANILLA, true);
        }
    }

    private static PreviewSets calculatePreview(ServerWorld world, CreeperEntity creeper) {
        Vec3d center = new Vec3d(creeper.getX(), creeper.getY(), creeper.getZ());
        float power = ((CreeperEntityAccessor) creeper).creeperRealisticExplosionDamage$getExplosionRadius();
        power *= creeper.isCharged() ? CHARGED_MULTIPLIER : 1.0F;
        power = CreeperExplosionRules.scaleCreeperPower(power);

        DamageSource damageSource = world.getDamageSources().explosion(creeper, creeper);
        ExplosionBehavior realisticBehavior = new RealisticCreeperExplosionBehavior(creeper);
        ExplosionImpl realisticExplosion = new ExplosionImpl(
                world,
                creeper,
                damageSource,
                realisticBehavior,
                center,
                power,
                false,
                Explosion.DestructionType.DESTROY
        );
        List<BlockPos> realisticBlocks = ((ExplosionImplAccessor) realisticExplosion)
                .creeperRealisticExplosionDamage$invokeGetBlocksToDestroy();

        ExplosionBehavior vanillaBehavior = new EntityExplosionBehavior(creeper);
        ExplosionImpl vanillaExplosion = new ExplosionImpl(
                world,
                creeper,
                damageSource,
                vanillaBehavior,
                center,
                power,
                false,
                Explosion.DestructionType.DESTROY
        );
        List<BlockPos> vanillaBlocks = CreeperExplosionPreviewContext.withoutModifications(
                () -> ((ExplosionImplAccessor) vanillaExplosion)
                        .creeperRealisticExplosionDamage$invokeGetBlocksToDestroy()
        );

        Set<BlockPos> current = filterPreviewable(world, realisticBlocks);
        Set<BlockPos> vanilla = filterPreviewable(world, vanillaBlocks);

        return new PreviewSets(current, vanilla);
    }

    private static boolean isPreviewable(BlockState state) {
        return !state.isAir() && !CreeperExplosionRules.isResistant(state);
    }

    private static Set<BlockPos> filterPreviewable(ServerWorld world, List<BlockPos> blocks) {
        Set<BlockPos> filtered = new HashSet<>();
        for (BlockPos pos : blocks) {
            BlockPos immutable = pos.toImmutable();
            BlockState state = world.getBlockState(immutable);
            if (!isPreviewable(state)) {
                continue;
            }
            filtered.add(immutable);
        }
        return filtered;
    }

    private static boolean isFuseActive(CreeperEntity creeper) {
        return creeper.getFuseSpeed() > 0 || creeper.isIgnited();
    }

    private static void spawnPreviewBlocks(
            ServerWorld world,
            Set<BlockPos> blocks,
            BlockState state,
            UUID creeperId,
            String colorTag,
            boolean glowing
    ) {
        String creeperTag = TAG_PREFIX + creeperId;
        for (BlockPos pos : blocks) {
            DisplayEntity.BlockDisplayEntity display =
                    new DisplayEntity.BlockDisplayEntity(EntityType.BLOCK_DISPLAY, world);
            display.setBlockState(state);
            display.refreshPositionAndAngles(
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    0.0F,
                    0.0F
            );
            display.setNoGravity(true);
            display.setInvulnerable(true);
            display.setGlowing(glowing);
            display.addCommandTag(TAG_ALL);
            display.addCommandTag(creeperTag);
            display.addCommandTag(colorTag);
            world.spawnEntity(display);
        }
    }

    private static int clearPreviewForCreeper(ServerWorld world, UUID creeperId) {
        String creeperTag = TAG_PREFIX + creeperId;
        TypeFilter<Entity, DisplayEntity.BlockDisplayEntity> filter =
                TypeFilter.instanceOf(DisplayEntity.BlockDisplayEntity.class);
        List<? extends DisplayEntity.BlockDisplayEntity> previews = world.getEntitiesByType(
                filter,
                entity -> entity.getCommandTags().contains(creeperTag)
        );
        for (DisplayEntity.BlockDisplayEntity preview : previews) {
            preview.discard();
        }
        return previews.size();
    }

    private static void clearAllInWorld(ServerWorld world) {
        TypeFilter<Entity, DisplayEntity.BlockDisplayEntity> filter =
                TypeFilter.instanceOf(DisplayEntity.BlockDisplayEntity.class);
        List<? extends DisplayEntity.BlockDisplayEntity> previews = world.getEntitiesByType(
                filter,
                entity -> entity.getCommandTags().contains(TAG_ALL)
        );
        for (DisplayEntity.BlockDisplayEntity preview : previews) {
            preview.discard();
        }
        ACTIVE_CREEPERS.remove(world.getRegistryKey());
    }

    private static final class PreviewSets {
        private final Set<BlockPos> current;
        private final Set<BlockPos> vanilla;

        private PreviewSets(Set<BlockPos> current, Set<BlockPos> vanilla) {
            this.current = current;
            this.vanilla = vanilla;
        }
    }
}
