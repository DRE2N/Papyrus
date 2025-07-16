package de.erethon.papyrus.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MobSpawnFinalizedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final MobCategory mobCategory;
    private final ServerLevel level;
    private final ChunkAccess chunkAccess;
    private BlockPos pos;
    private Mob mob;

    public MobSpawnFinalizedEvent(MobCategory category, ServerLevel level, ChunkAccess chunk, BlockPos pos, Mob mob) {
        this.mobCategory = category;
        this.level = level;
        this.chunkAccess = chunk;
        this.pos = pos;
        this.mob = mob;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


    public MobCategory getMobCategory() {
        return mobCategory;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public ChunkAccess getChunkAccess() {
        return chunkAccess;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public Mob getMob() {
        return mob;
    }

    public void setMob(Mob mob) {
        this.mob = mob;
    }
}
