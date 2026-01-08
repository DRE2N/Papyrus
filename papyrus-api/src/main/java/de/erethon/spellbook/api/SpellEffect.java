package de.erethon.spellbook.api;

import de.erethon.papyrus.PDamageType;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpellEffect {

    protected LivingEntity target;
    protected final LivingEntity caster;
    public final EffectData data;

    protected int ticksLeft;
    protected int stacks;
    protected final List<Integer> stackDurations = new ArrayList<>();

    public SpellEffect(EffectData data, LivingEntity caster, LivingEntity target, int duration, int stacks) {
        this.data = data;
        this.caster = caster;
        this.target = target;
        this.stacks = stacks;

        for (int i = 0; i < stacks; i++) {
            this.stackDurations.add(duration);
        }
        this.ticksLeft = duration;
    }

    public void tick() {
        if (!stackDurations.isEmpty()) {
            stackDurations.replaceAll(t -> t - 1);
            stackDurations.removeIf(t -> t <= 0);
            this.stacks = stackDurations.size();
            this.ticksLeft = stackDurations.isEmpty() ? 0 : Collections.max(stackDurations);
        } else {
            ticksLeft--;
            stacks = 0;
        }

        onTick();
    }

    public void add(int duration, int stacksToAdd) {
        if (data.getStackMode() == EffectData.StackMode.PROLONG) {
            stackDurations.replaceAll(t -> t + duration);
            if (stackDurations.isEmpty()) {
                stackDurations.add(duration);
            }

        } else if (data.getStackMode() == EffectData.StackMode.INTENSIFY) {
            for (int i = 0; i < stacksToAdd; i++) {
                if (this.stackDurations.size() < data.getMaxStacks()) {
                    this.stackDurations.add(duration);
                }
            }
        }

        this.stacks = stackDurations.size();
        this.ticksLeft = stackDurations.isEmpty() ? 0 : Collections.max(stackDurations);

        onApply();
    }

    public boolean canAdd(int duration, int newStacks) {
        if (data.getStackMode() == EffectData.StackMode.INTENSIFY) {
            return this.stacks + newStacks <= data.getMaxStacks();
        }
        if (data.getStackMode() == EffectData.StackMode.PROLONG) {
            return this.ticksLeft + duration <= data.getMaxDuration();
        }

        return true;
    }

    public boolean shouldRemove() {
        return stackDurations.isEmpty() || ticksLeft <= 0;
    }

    public void onTick() {
    }

    public void onApply() {
    }

    public void onRemove() {
    }

    public double onDamage(LivingEntity damager, double damage, PDamageType type) {
        return damage;
    }

    public double onAttack(LivingEntity target, double damage, PDamageType type) {
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

    public int getStacks() {
        return stacks;
    }

    public void setStacks(int stacks) {

        int diff = stacks - this.stackDurations.size();

        if (diff > 0) {
            int duration = this.ticksLeft > 0 ? this.ticksLeft : 20; // Default or current
            for (int i = 0; i < diff; i++) {
                this.stackDurations.add(duration);
            }
        } else if (diff < 0) {
            for (int i = 0; i < Math.abs(diff); i++) {
                if (!stackDurations.isEmpty()) {
                    Integer min = Collections.min(stackDurations);
                    stackDurations.remove(min);
                }
            }
        }
        this.stacks = stackDurations.size();
    }

    public int getTicksLeft() {
        return ticksLeft; // Returns the duration of the stack that lasts the longest
    }

    public void setTicksLeft(int ticksLeft) {
        this.ticksLeft = ticksLeft;
        this.stackDurations.replaceAll(t -> ticksLeft);
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