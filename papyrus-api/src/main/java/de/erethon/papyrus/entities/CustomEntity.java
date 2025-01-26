package de.erethon.papyrus.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public interface CustomEntity extends Entity {

    void setType(EntityType type);
    String getPapyrusId();
    void setPapyrusId(String papyrusId);
}
