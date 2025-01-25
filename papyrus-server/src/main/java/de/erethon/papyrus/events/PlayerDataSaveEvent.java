package de.erethon.papyrus.events;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerDataSaveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    public PlayerDataSaveEvent(ServerPlayer player) {
        this.player = player.getBukkitEntity();
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
