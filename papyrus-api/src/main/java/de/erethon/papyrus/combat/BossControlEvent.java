package de.erethon.papyrus.combat;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * A skill's request to apply control pressure to an encounter boss.
 * The encounter owner handles the request and reports whether an active
 * defiance window accepted it. Handled bosses should not receive ordinary
 * displacement or long control effects from the same skill.
 */
public final class BossControlEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final LivingEntity actor;
    private final LivingEntity target;
    private final double pressure;
    private boolean handled;
    private boolean activeWindow;
    private boolean broken;

    public BossControlEvent(@NotNull LivingEntity actor, @NotNull LivingEntity target, double pressure) {
        this.actor = actor;
        this.target = target;
        this.pressure = Math.max(0.0, pressure);
    }

    public @NotNull LivingEntity getActor() { return actor; }
    public @NotNull LivingEntity getTarget() { return target; }
    public double getPressure() { return pressure; }
    public boolean isHandled() { return handled; }
    public boolean isActiveWindow() { return activeWindow; }
    public boolean isBroken() { return broken; }

    public void setResult(boolean activeWindow, boolean broken) {
        this.handled = true;
        this.activeWindow = activeWindow;
        this.broken = broken;
    }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static @NotNull HandlerList getHandlerList() { return HANDLERS; }
}
