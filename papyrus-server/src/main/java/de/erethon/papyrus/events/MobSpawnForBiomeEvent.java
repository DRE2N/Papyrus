package de.erethon.papyrus.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class MobSpawnForBiomeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final MobCategory mobCategory;
    private final ServerLevel level;
    private final ChunkGenerator generator;
    private BlockPos pos;
    private final Holder<Biome> biome;
    private WeightedList<MobSpawnSettings.SpawnerData> spawnerData;

    public MobSpawnForBiomeEvent(ServerLevel level, StructureManager structureManager, ChunkGenerator generator, MobCategory category, BlockPos pos, @Nullable Holder<Biome> biome, WeightedList<MobSpawnSettings.SpawnerData> spawnerData) {
        this.level = level;
        this.mobCategory = category;
        this.generator = generator;
        this.pos = pos;
        this.biome = biome;
        this.spawnerData = spawnerData;
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

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public WeightedList<MobSpawnSettings.SpawnerData> getSpawnerData() {
        return spawnerData;
    }

    public void setSpawnerData(WeightedList<MobSpawnSettings.SpawnerData> spawnerData) {
        this.spawnerData = spawnerData;
    }

    public Holder<Biome> getBiome() {
        return biome;
    }

    public ChunkGenerator getGenerator() {
        return generator;
    }
}
