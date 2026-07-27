package de.erethon.papyrus.world;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A collision and ray-tracing view that overlays virtual blocks while retaining physical fluids,
 * entities, unloaded-chunk behavior, and the world border.
 */
public final class VirtualTerrainCollisionView implements CollisionGetter {

    private final Level level;
    private final VirtualTerrainProvider provider;

    public VirtualTerrainCollisionView(Level level, VirtualTerrainProvider provider) {
        this.level = level;
        this.provider = provider;
    }

    @Override
    public WorldBorder getWorldBorder() {
        return level.getWorldBorder();
    }

    @Override
    public @Nullable BlockGetter getChunkForCollisions(int chunkX, int chunkZ) {
        // The provider may supply a section even when the physical chunk is absent. Physical
        // fallbacks are only reached around an entity, whose current chunk is already loaded.
        return this;
    }

    @Override
    public boolean isUnobstructed(@Nullable Entity source, VoxelShape shape) {
        return level.isUnobstructed(source, shape);
    }

    @Override
    public List<VoxelShape> getEntityCollisions(@Nullable Entity source, AABB testArea) {
        return level.getEntityCollisions(source, testArea);
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return level.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        BlockState virtualState = provider.isValid() ? provider.getBlockState(pos) : null;
        return virtualState == null ? level.getBlockState(pos) : virtualState;
    }

    @Override
    public @Nullable BlockState getBlockStateIfLoaded(BlockPos pos) {
        BlockState virtualState = provider.isValid() ? provider.getBlockState(pos) : null;
        return virtualState == null ? level.getBlockStateIfLoaded(pos) : virtualState;
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return level.getFluidState(pos);
    }

    @Override
    public @Nullable FluidState getFluidIfLoaded(BlockPos pos) {
        return level.getFluidIfLoaded(pos);
    }

    @Override
    public int getMinY() {
        return level.getMinY();
    }

    @Override
    public int getHeight() {
        return level.getHeight();
    }
}
