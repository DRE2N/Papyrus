package de.erethon.spellbook.api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

enum RemovalReason {
    EXPIRED,
    CLEANSED
}
public class SpellEffectRemoveEvent extends Event {

    private final SpellCaster target;
    private final SpellEffect effect;
    private final EffectData data;
    private final RemovalReason reason;

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

}
