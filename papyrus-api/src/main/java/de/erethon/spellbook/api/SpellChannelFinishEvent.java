package de.erethon.spellbook.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SpellChannelFinishEvent extends Event {

    private final SpellCaster caster;
    private final SpellbookSpell spell;
    private final boolean interrupted;

    private static final HandlerList handlers = new HandlerList();

    public SpellChannelFinishEvent(SpellCaster caster, SpellbookSpell spell, boolean interrupted) {
        this.caster = caster;
        this.spell = spell;
        this.interrupted = interrupted;
    }

    public SpellCaster getCaster() {
        return caster;
    }

    public SpellbookSpell getSpell() {
        return spell;
    }

    public boolean wasInterrupted() {
        return interrupted;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
