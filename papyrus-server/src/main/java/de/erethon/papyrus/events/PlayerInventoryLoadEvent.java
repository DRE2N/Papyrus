package de.erethon.papyrus.events;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerInventoryLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public Player player;
    public NonNullList<ItemStack> items;
    public EntityEquipment equipment;

    public PlayerInventoryLoadEvent(Player player, NonNullList<ItemStack> items, EntityEquipment equipment) {
        this.player = player;
        this.items = items;
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
