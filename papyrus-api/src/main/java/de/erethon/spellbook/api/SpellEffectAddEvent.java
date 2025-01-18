package de.erethon.spellbook.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SpellEffectAddEvent extends Event {

    private final SpellCaster target;
    private final SpellEffect effect;
    private final EffectData data;
    private final boolean isExtension;

    public SpellEffectAddEvent(SpellCaster target, SpellEffect activeSpell, EffectData data, boolean isExtension) {
        this.target = target;
        this.effect = activeSpell;
        this.data = data;
        this.isExtension = isExtension;
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

    public boolean wasExtended() {
        return isExtension;
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
