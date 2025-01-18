package de.erethon.spellbook.api;

import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;

public class TraitTrigger {

    private SpellbookSpell spell;
    private SpellTrait trait;
    private LivingEntity target = null;
    private final Set<LivingEntity> targets = new HashSet<>();
    private int id = 0;
    private Object[] optionals = null;

    TraitTrigger(SpellbookSpell spell) {
        this.spell = spell;
    }

    TraitTrigger(SpellTrait trait) {
        this.trait = trait;
    }

    TraitTrigger(SpellbookSpell spell, int id) {
        this.spell = spell;
        this.id = id;
    }

    TraitTrigger(SpellTrait trait, int id) {
        this.trait = trait;
        this.id = id;
    }

    TraitTrigger(SpellbookSpell spell, LivingEntity target) {
        this.spell = spell;
        this.target = target;
    }

    TraitTrigger(SpellTrait trait, LivingEntity target) {
        this.trait = trait;
        this.target = target;
    }

    TraitTrigger(SpellbookSpell spell, Set<LivingEntity> targets) {
        this.spell = spell;
        this.targets.addAll(targets);
    }

    TraitTrigger(SpellTrait trait, Set<LivingEntity> targets) {
        this.trait = trait;
        this.targets.addAll(targets);
    }

    TraitTrigger(SpellbookSpell spell, LivingEntity target, int id) {
        this.spell = spell;
        this.target = target;
        this.id = id;
    }

    TraitTrigger(SpellTrait trait, LivingEntity target, int id) {
        this.trait = trait;
        this.target = target;
        this.id = id;
    }

    TraitTrigger(SpellbookSpell spell, Set<LivingEntity> targets, int id) {
        this.spell = spell;
        this.targets.addAll(targets);
        this.id = id;
    }

    TraitTrigger(SpellTrait trait, Set<LivingEntity> targets, int id) {
        this.trait = trait;
        this.targets.addAll(targets);
        this.id = id;
    }

    TraitTrigger(SpellbookSpell spell, LivingEntity target, int id, Object... optional) {
        this.spell = spell;
        this.target = target;
        this.id = id;
        this.optionals = optional;
    }

    TraitTrigger(SpellTrait trait, LivingEntity target, int id, Object... optional) {
        this.trait = trait;
        this.target = target;
        this.id = id;
        this.optionals = optional;
    }

    TraitTrigger(SpellbookSpell spell, Set<LivingEntity> targets, int id, Object... optional) {
        this.spell = spell;
        this.targets.addAll(targets);
        this.id = id;
        this.optionals = optional;
    }

    TraitTrigger(SpellTrait trait, Set<LivingEntity> targets, int id, Object... optional) {
        this.trait = trait;
        this.targets.addAll(targets);
        this.id = id;
        this.optionals = optional;
    }

    public SpellbookSpell getSpell() {
        return spell;
    }

    public SpellTrait getTrait() {
        return trait;
    }

    public Set<LivingEntity> getTargets() {
        return targets;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public int getId() {
        return id;
    }

    public Object[] getOptionals() {
        return optionals;
    }
}
