package de.erethon.papyrus.events;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ContainerLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public NonNullList<ItemStack> stacks;

    public ContainerLoadEvent(NonNullList<ItemStack> stacks, boolean async) {
        super(async);
        this.stacks = stacks;
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
