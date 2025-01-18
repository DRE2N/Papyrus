package de.erethon.spellbook.api;

import de.erethon.papyrus.PDamageType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A SpellTrait is a passive ability that can be used to enhance or change existing Spells.
 */
public class SpellTrait {

    protected final TraitData data;
    protected final LivingEntity caster;
    protected boolean active = true;

    public SpellTrait(TraitData data, LivingEntity caster) {
        this.data = data;
        this.caster = caster;
    }

    /**
     * This method is called every tick.
     * Traits currently tick every server tick.
     */
    protected void onTick() {
    }

    /**
     * This method is called when a Spell uses its triggerTraits() methods.
     * Can be used to change Spell behaviour.
     * @param trigger the TraitTrigger object
     */
    protected void onTrigger(TraitTrigger trigger) {
    }

    /**
     * This method is called when a Spell is cast
     * @param casted the Spell that was cast
     * @return the Spell. If a different spell is returned, the casting of the existing spell will be cancelled.
     */
    protected SpellbookSpell onSpellCast(SpellbookSpell casted) {
        return casted;
    }

    /**
     * This method is called when a SpellEffect is applied on the caster
     * @param effect the SpellEffect
     * @param isNew if the effect is new
     * @return if the effect should apply
     */
    protected boolean onAddEffect(SpellEffect effect, boolean isNew) {
        return true;
    }

    /**
     * This method is called when a SpellEffect is removed from the caster
     * @param effect the SpelLEffect
     * @return if the effect should be removed
     */
    protected boolean onRemoveEffect(SpellEffect effect) {
        return true;
    }

    protected void onAdd() {
    }

    protected void onRemove() {
    }

    protected void onOtherTraitAdd(TraitData other) {
    }

    protected void onOtherTraitRemove(TraitData other) {
    }

    protected void onSpellPreCast(SpellbookSpell spell) {
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

    public TraitData getData() {
        return data;
    }

    /**
     * @return the LivingEntity this SpellTrait belongs to.
     */
    public LivingEntity getCaster() {
        return caster;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean bool) {
        active = bool;
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
     * Returns a list of description placeholders for this trait.
     */
    public List<Component> getPlaceholders(SpellCaster caster) {
        return new ArrayList<>();
    }



}
