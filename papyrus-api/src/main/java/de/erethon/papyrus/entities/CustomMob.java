package de.erethon.papyrus.entities;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

public interface CustomMob extends Mob {

    void setType(EntityType type);
    String getPapyrusId();
    void setPapyrusId(String papyrusId);
}
