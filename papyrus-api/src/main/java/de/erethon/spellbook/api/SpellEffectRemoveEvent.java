package de.erethon.spellbook.api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

enum RemovalReason {
    EXPIRED,
    CLEANSED
}
public class SpellEffectRemoveEvent extends Event implements Cancellable {

    private final SpellCaster target;
    private final SpellEffect effect;
    private final EffectData data;
    private final RemovalReason reason;
    private boolean cancelled = false;

    public SpellEffectRemoveEvent(SpellCaster target, SpellEffect activeSpell, EffectData data, RemovalReason removalReason) {
        this.target = target;
        this.effect = activeSpell;
        this.data = data;
        this.reason = removalReason;
    }

    public SpellCaster getTarget() {
        return target;
    }

    public SpellEffect getEffect() {
        return effect;
    }

    public EffectData getData() {
        return data;
    }

    public RemovalReason getReason() {
        return reason;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
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
