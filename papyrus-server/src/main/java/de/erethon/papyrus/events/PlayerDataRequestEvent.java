package de.erethon.papyrus.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PlayerDataRequestEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final String uuid;
    private Optional<CompoundTag> tag = null;

    public PlayerDataRequestEvent(String uuid) {
        this.uuid = uuid;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getUUID() {
        return uuid;
    }

    public Optional<CompoundTag> getData() {
        return tag;
    }

    public void setData(Optional<CompoundTag> tag) {
        this.tag = tag;
    }
}
