package de.erethon.spellbook.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SpellChannelStartEvent extends Event {
    private final SpellCaster caster;
    private final SpellbookSpell spell;

    private static final HandlerList handlers = new HandlerList();

    public SpellChannelStartEvent(SpellCaster caster, SpellbookSpell spell) {
        this.caster = caster;
        this.spell = spell;
    }

    public SpellCaster getCaster() {
        return caster;
    }

    public SpellbookSpell getSpell() {
        return spell;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
