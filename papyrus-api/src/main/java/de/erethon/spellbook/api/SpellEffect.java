package de.erethon.spellbook.api;

import de.erethon.papyrus.PDamageType;
import org.bukkit.entity.LivingEntity;

public class SpellEffect {

    protected LivingEntity target;
    protected final LivingEntity caster;
    public final EffectData data;

    protected int ticksLeft;
    protected int stacks;

    public SpellEffect(EffectData data, LivingEntity caster, LivingEntity target, int duration, int stacks) {
        this.data = data;
        this.caster = caster;
        this.target = target;
        this.ticksLeft = duration;
        this.stacks = stacks;
    }

    public void tick() {
        ticksLeft--;
        onTick();
    }

    public void add(int duration, int stacks) {
        if (data.getStackMode() == EffectData.StackMode.PROLONG) {
            this.ticksLeft += duration;
        } else if (data.getStackMode() == EffectData.StackMode.INTENSIFY) {
            this.stacks += stacks;
        }
        onApply();
    }

    public boolean canAdd(int duration, int newStacks) {
        if (data.getStackMode() == EffectData.StackMode.PROLONG) {
            return ticksLeft + duration <= data.getMaxDuration();
        } else if (data.getStackMode() == EffectData.StackMode.INTENSIFY) {
            return stacks + newStacks <= data.getMaxStacks();
        }
        // Default: check both (or return false for unknown modes)
        return ticksLeft + duration <= data.getMaxDuration() && stacks + newStacks <= data.getMaxStacks();
    }

    public void onTick() {
    }

    public void onApply() { // Mostly useful for stat buffs/debuffs
    }

    public void onRemove() { // Mostly useful for stat buffs/debuffs
    }

    public double onDamage(LivingEntity damager, double damage, PDamageType type) { // For effects that block 1 attack and get removed after that
        return damage;
    }

    public double onAttack(LivingEntity target, double damage, PDamageType type) { // For effects that add something to the attack
        return damage;
    }

    public boolean onCast(SpellbookSpell spell) {
        return true;
    }

    public boolean onSpellTick(SpellbookSpell spell) {
        return true;
    }

    public boolean onRemoveEffect(SpellEffect effect) {
        return true;
    }

    public boolean onAddEffect(SpellEffect effect, boolean isNew) {
        return true;
    }

    public boolean shouldRemove() {
        return ticksLeft <= 0;
    }

    public int getStacks() {
        return stacks;
    }

    public void setStacks(int stacks) {
        this.stacks = stacks;
    }

    public int getTicksLeft() {
        return ticksLeft;
    }

    public void setTicksLeft(int ticksLeft) {
        this.ticksLeft = ticksLeft;
    }

    public LivingEntity getCaster() {
        return caster;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SpellEffect other) {
            return other.data.equals(data);
        }
        return false;
    }
}
