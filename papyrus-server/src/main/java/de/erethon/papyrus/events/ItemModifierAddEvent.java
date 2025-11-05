package de.erethon.papyrus.events;

import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ItemModifierAddEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private LivingEntity living;
    private ItemStack stack;
    private AttributeModifier modifier;
    private boolean cancelled = false;

    public ItemModifierAddEvent(LivingEntity living, ItemStack stack, AttributeModifier modifier) {
        this.living = living;
        this.stack =  stack;
        this.modifier = modifier;
    }

    public LivingEntity getLivingEntity() {
        return living;
    }

    public ItemStack getItemStack() {
        return stack;
    }

    public AttributeModifier getAttributeModifier() {
        return modifier;
    }

    public void setAttributeModifier(AttributeModifier modifier) {
        this.modifier = modifier;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
