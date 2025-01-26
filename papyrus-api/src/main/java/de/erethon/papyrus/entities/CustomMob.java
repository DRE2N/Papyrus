package de.erethon.papyrus.entities;

import org.bukkit.entity.EntityType;

public interface CustomMob {

    void setType(EntityType type);
    String getPapyrusId();
    void setPapyrusId(String papyrusId);
}
