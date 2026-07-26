package de.erethon.spellbook.api;

import de.erethon.papyrus.PDamageType;
import de.erethon.papyrus.combat.CombatContext;
import de.erethon.papyrus.combat.CombatSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public interface SpellCaster {
    default void cast(SpellData spellData) {
        if (!canCast(spellData)) {
            return;
        }
        spellData.queue((LivingEntity) this);
    }

    /**
     * Shortcut method
     */
    Location getLocation();

    /**
     * @return A map of SpellData and long timestamps.
     * The timestamp is the time when a Spell was last cast successfully.
     */
    Map<SpellData, Long> getUsedSpells();

    /**
     * @return the SpelLEffects currently active on this SpellCaster
     */
    Set<SpellEffect> getEffects();

    /**
     * @return a Set of SpellbookSpells that were added using addPassiveSpell()
     * @deprecated PassiveSpells should be replaced by SpellTraits if possible
     */
    @Deprecated
    Set<SpellbookSpell> getPassiveSpells();

    /**
     * @return a Set of SpellbookSpells that are currently active (being ticked and not expired) on this SpellCaster
     */
    Set<SpellbookSpell> getActiveSpells();

    /**
     * @return a Set of Spells this SpellCaster could use. Spell can be added using addSpell() and removeSpell()
     */
    Set<SpellData> getUnlockedSpells();
    @Deprecated
    Set<SpellData> getUnlockedPassives();

    /**
     * @return a Set of SpellTraits that are currently on the SpelLCaster.
     * All traits in this set will receive triggers.
     */
    Set<SpellTrait> getActiveTraits();

    /**
     * @return a Set of Tags that are currently on the SpellCaster.
     */
    Set<String> getTags();

    boolean isChanneling();

    void setChanneling(boolean channeling);

    default void tick() {
        for (SpellEffect effect : new ArrayList<>(getEffects())) {
            if (!getEffects().contains(effect)) {
                continue;
            }
            if (effect.shouldRemove()) {
                SpellEffectRemoveEvent event = new SpellEffectRemoveEvent(effect.caster, effect, effect.data, RemovalReason.EXPIRED);
                event.callEvent();
                if (event.isCancelled()) {
                    return;
                }
                try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.effect(effect))) {
                    effect.onRemove();
                }
                getEffects().remove(effect);
                for (SpellEffect eventEffect: new ArrayList<>(getEffects())) {
                    try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.effect(eventEffect))) {
                        eventEffect.onRemoveEffect(effect);
                    }
                }} else {
                effect.tick();
            }
        }
        for (SpellTrait trait : new ArrayList<>(getActiveTraits())) {
            if (!trait.active) {
                continue;
            }
            try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.trait(trait))) {
                trait.onTick();
            }
        }
    }

    /**
     * Called when this entity takes damage
     */
    default double onDamage(LivingEntity damager, double damage, PDamageType type) {
        for (SpellbookSpell spell : new ArrayList<>(getActiveSpells())) {
            try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.spell(spell))) {
                damage = spell.onDamage(damager, damage, type);
            }
        }
        for (SpellEffect effect : new ArrayList<>(getEffects())) {
            try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.effect(effect))) {
                damage = effect.onDamage(damager, damage, type);
            }
        }
        for (SpellTrait trait : new ArrayList<>(getActiveTraits())) {
            try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.trait(trait))) {
                damage = trait.onDamage(damager, damage, type);
            }
        }
        return Math.max(damage, 0);
    }


    /**
     * Called when this entity deals damage
     */
    default double onAttack(LivingEntity target, double damage, PDamageType type) {
        for (SpellbookSpell spell : new ArrayList<>(getActiveSpells())) {
            try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.spell(spell))) {
                damage = spell.onAttack(target, damage, type);
            }
        }
        for (SpellEffect effect : new ArrayList<>(getEffects())) {
            try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.effect(effect))) {
                damage = effect.onAttack(target, damage, type);
            }
        }
        for (SpellTrait trait : new ArrayList<>(getActiveTraits())) {
            try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.trait(trait))) {
                damage = trait.onAttack(target, damage, type);
            }
        }
        return Math.max(damage, 0);
    }

    default void addPassiveSpell(SpellbookSpell spell) {
        getPassiveSpells().add(spell);
        spell.ready();
        addActiveSpell(spell);
    }

    default void removePassiveSpell(SpellbookSpell spell) {
        removeActiveSpell(spell);
        getPassiveSpells().remove(spell);
    }

    default void addSpell(SpellData spell) {
        getUnlockedSpells().add(spell);
    }

    default void removeSpell(SpellData spell) {
        getUnlockedSpells().remove(spell);
    }

    default void addActiveSpell(SpellbookSpell spell) {
        getActiveSpells().add(spell);
    }

    default void removeActiveSpell(SpellbookSpell spell) {
        getActiveSpells().remove(spell);
    }

    default void addEffect(LivingEntity caster, EffectData data, int duration, int stacks) {
        if (hasEffect(data)) {
            SpellEffect oldEffect = getEffects().stream().filter(e -> e.data.equals(data)).findFirst().get();
            if (oldEffect.canAdd(duration, stacks)) {
                SpellEffectAddEvent event = new SpellEffectAddEvent(this, oldEffect, data, true);
                event.callEvent();
                if (event.isCancelled()) {
                    return;
                }
                if (!onEffectAdd(oldEffect, false)) {
                    return;
                }
                oldEffect.add(duration, stacks);
            }
        } else {
            SpellEffect effect = data.getActiveEffect(caster, (LivingEntity) this, duration, stacks);
            SpellEffectAddEvent event = new SpellEffectAddEvent(this, effect, data, false);
            event.callEvent();
            if (event.isCancelled()) {
                return;
            }
            if (!onEffectAdd(effect, true)) {
                return;
            }
            getEffects().add(effect);
            try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.effect(effect))) {
                effect.onApply();
            }}
    }

    default void removeEffect(EffectData effect) {
        for (SpellEffect itr : new ArrayList<>(getEffects())) {
            if (itr.data == effect) {
                SpellEffectRemoveEvent event = new SpellEffectRemoveEvent(this, itr, effect, RemovalReason.CLEANSED);
                event.callEvent();
                if (event.isCancelled()) {
                    continue;
                }
                if (!onEffectRemove(itr)) {
                    continue;
                }
                getEffects().remove(itr);
                onEffectRemove(itr);
            }
        }
    }

    default boolean hasEffect(EffectData effect) {
        return getEffects().stream().anyMatch(e -> e.data.equals(effect));
    }

    default void addTrait(TraitData traitData) {
        SpellTrait trait = traitData.getActiveTrait((LivingEntity) this);
        getActiveTraits().add(trait);
        try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.trait(trait))) {
            trait.onAdd();
        }
        onTraitAdd(traitData);
    }

    default void removeTrait(TraitData traitData) {
        for (SpellTrait itr : new ArrayList<>(getActiveTraits())) {
            if (itr.data == traitData) {
                try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.trait(itr))) {
                    itr.onRemove();
                }
                onTraitRemove(traitData);
                getActiveTraits().remove(itr);
            }
        }
    }

    default boolean hasTrait(TraitData data) {
        return getActiveTraits().stream().anyMatch(t -> t.data.equals(data));
    }

    default int calculateCooldown(SpellData spellData) {
        long timestamp = getCooldown(spellData);
        long now = System.currentTimeMillis();
        int cooldown = (int) (now - timestamp) / 1000;
        return Math.max(spellData.getCooldown() - cooldown, 1); // 1 because amount = 0 removes the item.
    }

    default boolean canCast(SpellData spellData) {
        long time = getUsedSpells().getOrDefault(spellData, 0L);
        double skillCD = spellData.getCooldown();
        long now = System.currentTimeMillis();
        return now - time > (skillCD * 1000);
    }

    default long getCooldown(SpellData spellData) {
        return getUsedSpells().getOrDefault(spellData, 0L);
    }

    default void setCooldown(SpellData skill) {
        getUsedSpells().put(skill, System.currentTimeMillis());
    }

    int getEnergy();
    int setEnergy(int energy);
    int getMaxEnergy();
    int setMaxEnergy(int maxEnergy);
    int addEnergy(int energy);
    int removeEnergy(int energy);
    
    private boolean onEffectAdd(SpellEffect effect, boolean isNew) {
        for (SpellbookSpell eventSpell : new ArrayList<>(getActiveSpells())) {
            try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.spell(eventSpell))) {
                if (!eventSpell.onAddEffect(effect, isNew)) {
                    return false;
                }
            }
        }
        for (SpellEffect eventEffect : new ArrayList<>(getEffects())) {
            try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.effect(eventEffect))) {
                if (!eventEffect.onAddEffect(effect, isNew)) {
                    return false;
                }
            }
        }
        for (SpellTrait eventTrait : new ArrayList<>(getActiveTraits())) {
            try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.trait(eventTrait))) {
                if (!eventTrait.onAddEffect(effect, isNew)) {
                    if (!eventTrait.active) {
                        continue;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean onEffectRemove(SpellEffect effect) {
        for (SpellbookSpell eventSpell : new ArrayList<>(getActiveSpells())) {
            try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.spell(eventSpell))) {
                if (!eventSpell.onRemoveEffect(effect)) {
                    return false;
                }
            }
        }
        for (SpellEffect eventEffect : new ArrayList<>(getEffects())) {
            try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.effect(eventEffect))) {
                if (!eventEffect.onRemoveEffect(effect)) {
                    return false;
                }
            }
        }
        for (SpellTrait eventTrait : new ArrayList<>(getActiveTraits())) {
            try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.trait(eventTrait))) {
                if (!eventTrait.onRemoveEffect(effect)) {
                    if (!eventTrait.active) {
                        continue;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    default void onSpellCast(SpellbookSpell spell) {
        for (SpellTrait trait : new ArrayList<>(getActiveTraits())) {
            if (!trait.active) {
                continue;
            }
            if (trait.data.affectedBySpell(spell.data)) {
                try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.trait(trait))) {
                    if (trait.onSpellCast(spell) == null) {
                        spell.cancel();
                    }
                    SpellData afterTraitData = trait.onSpellCast(spell).data;
                    if (afterTraitData != spell.data) {
                        spell.cancel();
                        cast(afterTraitData);
                    }
                }
            }
        }
    }

    default void triggerTraits(TraitTrigger trigger) {
        for (SpellTrait trait : new ArrayList<>(getActiveTraits())) {
            if (!trait.active) {
                continue;
            }
            if (trigger.getSpell() != null && trait.data.affectedBySpell(trigger.getSpell().data)) {
                try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.trait(trait))) {
                    trait.onTrigger(trigger);
                }
            }
            if (trigger.getTrait() != null && trait.data.affectedByTrait(trigger.getTrait().data)) {
                try (CombatContext.Scope ignored = CombatContext.enter(CombatSource.trait(trait))) {
                    trait.onTrigger(trigger);
                }
            }
        }
    }

    default void onTraitAdd(TraitData traitData) {
        for (SpellTrait trait : new ArrayList<>(getActiveTraits())) {
           trait.onOtherTraitAdd(traitData);
        }
    }

    default void onTraitRemove(TraitData traitData) {
        for (SpellTrait trait : new ArrayList<>(getActiveTraits())) {
            trait.onOtherTraitRemove(traitData);
        }
    }

    /**
     * This will only interrupt channeling spells for now.
     */
    default void interrupt() {
        for (SpellbookSpell spell : new ArrayList<>(getActiveSpells())) {
            if (spell.isChanneling) {
                spell.interrupt();
                setChanneling(false);
            }
        }
    }

    default void interruptFromMovement() {
        for (SpellbookSpell spell : new ArrayList<>(getActiveSpells())) {
            if (spell.isChanneling && spell.isMovementInterrupted) {
                spell.interrupt();
                setChanneling(false);
            }
        }
    }

}
