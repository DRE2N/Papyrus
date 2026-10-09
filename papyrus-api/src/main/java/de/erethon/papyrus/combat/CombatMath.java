package de.erethon.papyrus.combat;

/**
 * Shared arithmetic for Papyrus combat. Attribute values are in points, while
 * critical damage is a bonus percentage on top of the normal critical hit.
 */
public final class CombatMath {

    private CombatMath() {
    }

    /**
     * Resistance and penetration are flat damage points. Penetration subtracts
     * from resistance; excess penetration adds the remaining points to damage.
     * Resistance can reduce a hit to zero, but never below zero.
     */
    public static double applyResistance(double damage, double resistance, double penetration) {
        if (!Double.isFinite(damage) || damage <= 0) {
            return 0;
        }
        double effectiveResistance = resistance - penetration;
        if (!Double.isFinite(effectiveResistance)) {
            return damage;
        }
        return Math.max(0.0, damage - effectiveResistance);
    }

    /** Normal critical hits deal 150% plus the critical damage attribute. */
    public static double criticalMultiplier(double criticalDamageBonus) {
        double bonus = Double.isFinite(criticalDamageBonus) ? criticalDamageBonus : 0;
        return 1.5 + Math.clamp(bonus, 0, 250) / 100.0;
    }

    public static double chancePercent(double chance) {
        return Double.isFinite(chance) ? Math.clamp(chance, 0, 100) : 0;
    }
}
