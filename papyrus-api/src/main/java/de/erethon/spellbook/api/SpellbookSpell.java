package de.erethon.spellbook.api;

import de.erethon.papyrus.PDamageType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.sql.Array;
import java.util.*;

public abstract class SpellbookSpell {

    protected SpellbookAPI spellbookAPI = SpellbookAPI.getInstance();
    protected final UUID uuid;

    protected final SpellData data;
    protected final LivingEntity caster;

    protected int keepAliveTicks = 0;
    protected int currentTicks = 0;
    public int channelDuration = 0;
    public boolean isMovementInterrupted = false;
    public int currentChannelTicks = 0;

    protected int tickInterval = 1;
    protected int currentTickInterval = 0;

    protected boolean failed = false;
    protected boolean interrupted = false;
    public boolean isChanneling = false;

    public SpellbookSpell(LivingEntity caster, SpellData spellData) {
        this.data = spellData;
        this.caster = caster;
        uuid = UUID.randomUUID();
    }

    /**
     * This should be used to check for prerequisites for the spell, such as mana, target, location, etc.
     *
     * @return true if the spell can be cast, false otherwise
     */
    protected boolean onPrecast() {
        return true;
    }

    /**
     * This should be used to implement the spell itself.
     *
     * @return true if the spell was successfully cast, false otherwise
     */
    protected boolean onCast() {
        return true;
    }

    /**
     * This should be used do execute code after the spell was cast, like removing mana.
     */
    protected void onAfterCast() {
    }

    /**
     * This method is called every tick, based on tickInterval.
     */
    protected void onTick() {
    }

    /**
     * This method is called after keepAliveTicks is over
     * This will automatically call cleanup()
     */
    protected void onTickFinish() {
        cleanup();
    }

    /**
     * This is method is called after channeling was finished.
     */
    protected void onChannelFinish() {
    }

    /**
     * This method is called when channeling is interrupted for whatever reason. cleanup() will be called afterwards
     */
    protected void onChannelInterrupt() {

    }

    /**
     * This method should be used for cleaning up after a Spell cast. For example spawned entities should be
     * removed here.
     */
    protected void cleanup() {
    }

    /**
     * This method is called when the caster takes damage.
     * @param attacker the attacker
     */
    public double onDamage(LivingEntity attacker, double damage, PDamageType type) {
        return damage;
    }

    /**
     * This method is called when the caster deals damage.
     * @param target the target the caster is attacking
     */
    public double onAttack(LivingEntity target, double damage, PDamageType type) {
        return damage;
    }

    /**
     * This method is called when a spell is cast
     * @param spell the spell
     * @return if the spell can be cast
     */
    public boolean onCast(SpellbookSpell spell) {
        return true;
    }

    /**
     * This method is called when an active Spell on the caster is ticked.
     */
    public boolean onSpellTick(SpellbookSpell spell) {
        return true;
    }

    /**
     * This method is called when a StatusEffect is removed from the caster.
     * @return if the effect should re removed
     */
    public boolean onRemoveEffect(SpellEffect effect) {
        return true;
    }

    /**
     * This method is called when a StatusEffect is added to the caster.
     * @return if the effect should be added
     */
    public boolean onAddEffect(SpellEffect effect, boolean isNew) {
        return true;
    }

    /**
     * Triggers traits that might be activated by this spell
     */
    public void triggerTraits() {
        executeTraitTrigger(new TraitTrigger(this));
    }

    /**
     * Triggers traits that might be activated by this spell
     * @param id id that can be used for references
     */
    public void triggerTraits(int id) {
        executeTraitTrigger(new TraitTrigger(this, id));
    }

    /**
     * Triggers traits that might be activated by this spell
     * @param target the target
     */
    public void triggerTraits(LivingEntity target) {
        executeTraitTrigger(new TraitTrigger(this, target));
    }

    /**
     * Triggers traits that might be activated by this spell
     * @param targets set of entities affected by the spell
     */
    public void triggerTraits(Set<LivingEntity> targets) {
        executeTraitTrigger(new TraitTrigger(this, targets));
    }

    /**
     * Triggers traits that might be activated by this spell
     * @param target the target
     * @param id id that can be used for references
     */
    public void triggerTraits(LivingEntity target, int id) {
        executeTraitTrigger(new TraitTrigger(this, target, id));
    }

    /**
     * Triggers traits that might be activated by this spell
     * @param targets set of entities affected by the spell
     * @param id id that can be used for references
     */
    public void triggerTraits(Set<LivingEntity> targets, int id) {
        executeTraitTrigger(new TraitTrigger(this, targets, id));
    }

    /**
     * Triggers traits that might be activated by this spell
     * @param target the target
     * @param id id that can be used for references
     * @param optional other objects to be passed to the trait
     */
    public void triggerTraits(LivingEntity target, int id, Object... optional) {
        executeTraitTrigger(new TraitTrigger(this, target, id, optional));
    }

    /**
     * Triggers traits that might be activated by this spell
     * @param targets set of entities affected by the spell
     * @param id id that can be used for references
     * @param optional other objects to be passed to the trait
     */
    public void triggerTraits(Set<LivingEntity> targets, int id, Object... optional) {
        executeTraitTrigger(new TraitTrigger(this, targets, id, optional));
    }

    private void executeTraitTrigger(TraitTrigger trigger) {
        caster.triggerTraits(trigger);
    }

    /**
     * Interrupts the spell. It will stop ticking, but cleanup() will still
     * be called. onChannelInterrupt() will be called first if the Spell is currently channeling.
     */
    public void interrupt() {
        if (isChanneling) {
            SpellChannelFinishEvent finishEvent = new SpellChannelFinishEvent(caster, this, true);
            Bukkit.getPluginManager().callEvent(finishEvent);
            onChannelInterrupt();
            channelDuration = -1; // Don't continue channeling
        }
        interrupted = true;
    }

    /**
     * Returns a list of description placeholders for this spell.
     */
    public List<Component> getPlaceholders(SpellCaster caster) {
        return new ArrayList<>();
    }

    /**
     * Immediately stops spell execution.
     * <b>Careful! No other Spell methods with be called afterwards.</b>
     */
    public void cancel() {
        failed = true;
    }

    public void ready() {
        for (SpellTrait trait : caster.getActiveTraits()) {
            trait.onSpellPreCast(this);
        }
        if (onPrecast()) {
            caster.onSpellCast(this);
            if (onCast()) {
                onAfterCast();
            } else {
                failed = true;
            }
        } else {
            failed = true;
        }
    }

    public void tick() {
        if (failed) {
            return;
        }
        if (interrupted) {
            cleanup();
            return;
        }
        if (channelDuration > 0) {
            currentChannelTicks++;
            if (!isChanneling && currentChannelTicks < channelDuration) {
                isChanneling = true;
                caster.setChanneling(true);
                SpellChannelStartEvent startEvent = new SpellChannelStartEvent(caster, this);
                Bukkit.getPluginManager().callEvent(startEvent);
            }
            if (currentChannelTicks == channelDuration) {
                SpellChannelFinishEvent finishEvent = new SpellChannelFinishEvent(caster, this, false);
                Bukkit.getPluginManager().callEvent(finishEvent);
                onChannelFinish();
                isChanneling = false;
                caster.setChanneling(false);
            }
        }
        currentTicks++;
        if (currentTickInterval >= tickInterval) {
            currentTickInterval = 0;
            onTick();
        } else {
            currentTickInterval++;
        }
        if (shouldRemove()) {
            onTickFinish();
            caster.getActiveSpells().remove(this);
        }
    }


    public boolean shouldRemove() {
        if (failed) {
            return true;
        }
        if (keepAliveTicks < 0) {
            return false;
        }
        return currentTicks >= keepAliveTicks;
    }

    public SpellData getData() {
        return data;
    }

    public LivingEntity getCaster() {
        return caster;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getId() {
        return data.getId();
    }
}
