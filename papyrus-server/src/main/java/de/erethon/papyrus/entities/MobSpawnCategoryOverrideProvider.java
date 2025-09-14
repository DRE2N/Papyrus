package de.erethon.papyrus.entities;

import net.minecraft.world.entity.MobCategory;

public interface MobSpawnCategoryOverrideProvider {

    /**
     * @return The MobCategory this entity should count towards for mob caps.
     */
    MobCategory getEffectiveMobCategory();
}
