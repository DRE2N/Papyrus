package de.erethon.papyrus;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerSwitchProfileEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private int oldProfileID;
    private int newProfileID;


    public PlayerSwitchProfileEvent(@NotNull Player who, int oldProfileID, int newProfileID) {
        super(who);
        this.oldProfileID = oldProfileID;
        this.newProfileID = newProfileID;
    }

    public int getOldProfileID() {
        return oldProfileID;
    }

    public int getNewProfileID() {
        return newProfileID;
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
