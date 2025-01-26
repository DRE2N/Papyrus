package de.erethon.papyrus.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.entity.CraftMob;

public class CraftCustomMob extends CraftMob implements CustomMob {

    private String papyrusId;

    public CraftCustomMob(CraftServer server, Mob entity) {
        super(server);
        this.server = server;
        setHandle(entity);
        paperPathfinder = new com.destroystokyo.paper.entity.PaperPathfinder(entity);
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
