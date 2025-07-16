package de.erethon.papyrus.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PreCategorySpawnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private boolean shouldAbortVanillaLogic = false;
    private MobCategory mobCategory;
    private ServerLevel level;
    private ChunkAccess chunk;
    private BlockPos pos;
    private NaturalSpawner.SpawnPredicate filter;
    private NaturalSpawner.AfterSpawnCallback callback;
    private int maxSpawns;
    private Consumer<Entity> trackEntity;


    public PreCategorySpawnEvent(MobCategory category, ServerLevel level, ChunkAccess chunk, BlockPos pos, NaturalSpawner.SpawnPredicate filter,
                                 NaturalSpawner.AfterSpawnCallback callback, final int maxSpawns, final @Nullable Consumer<Entity> trackEntity) {
        this.mobCategory = category;
        this.level = level;
        this.chunk = chunk;
        this.pos = pos;
        this.filter = filter;
        this.callback = callback;
        this.maxSpawns = maxSpawns;
        this.trackEntity = trackEntity;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Consumer<Entity> getTrackEntity() {
        return trackEntity;
    }

    public void setTrackEntity(Consumer<Entity> trackEntity) {
        this.trackEntity = trackEntity;
    }

    public int getMaxSpawns() {
        return maxSpawns;
    }

    public void setMaxSpawns(int maxSpawns) {
        this.maxSpawns = maxSpawns;
    }

    public NaturalSpawner.AfterSpawnCallback getCallback() {
        return callback;
    }

    public void setCallback(NaturalSpawner.AfterSpawnCallback callback) {
        this.callback = callback;
    }

    public NaturalSpawner.SpawnPredicate getFilter() {
        return filter;
    }

    public void setFilter(NaturalSpawner.SpawnPredicate filter) {
        this.filter = filter;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public ChunkAccess getChunk() {
        return chunk;
    }

    public void setChunk(ChunkAccess chunk) {
        this.chunk = chunk;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public void setLevel(ServerLevel level) {
        this.level = level;
    }

    public MobCategory getMobCategory() {
        return mobCategory;
    }

    public void setMobCategory(MobCategory mobCategory) {
        this.mobCategory = mobCategory;
    }

    public boolean isShouldAbortVanillaLogic() {
        return shouldAbortVanillaLogic;
    }

    public void setShouldAbortVanillaLogic(boolean abort) {
        shouldAbortVanillaLogic = abort;
    }
}
