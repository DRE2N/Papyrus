package de.erethon.papyrus.entities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftEntityType;

public class CraftCustomEntity extends CraftEntity implements CustomEntity {

    private String papyrusId;

    public CraftCustomEntity(CraftServer server, Entity entity) {
        super(server);
        this.entity = entity;
        setHandle(entity);
    }

    @Override
    public void setType(org.bukkit.entity.EntityType type) {
        entityType = type;
    }

    @Override
    public String getPapyrusId() {
        return papyrusId;
    }

    @Override
    public void setPapyrusId(String papyrusId) {
        this.papyrusId = papyrusId;
    }

    public void setType(EntityType<?> type) {
        entityType = CraftEntityType.minecraftToBukkit(type);
    }
}
