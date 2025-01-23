package de.erethon.papyrus.events;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerInventoryLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public Player player;
    public NonNullList<ItemStack> items;
    public NonNullList<ItemStack> armor;
    public NonNullList<ItemStack> offHand;

    public PlayerInventoryLoadEvent(Player player, NonNullList<ItemStack> items, NonNullList<ItemStack> armor, NonNullList<ItemStack> offHand) {
        this.player = player;
        this.items = items;
        this.armor = armor;
        this.offHand = offHand;
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
