package de.erethon.spellbook.api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SpellCastEvent extends Event implements Cancellable {

    private final LivingEntity caster;
    private final SpellbookSpell activeSpell;
    private final SpellData data;

    private boolean cancelled = false;

    public SpellCastEvent(LivingEntity caster, SpellbookSpell activeSpell, SpellData data) {
        this.caster = caster;
        this.activeSpell = activeSpell;
        this.data = data;
    }

    public LivingEntity getCaster() {
        return caster;
    }

    public SpellbookSpell getActiveSpell() {
        return activeSpell;
    }

    public SpellData getData() {
        return data;
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
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
