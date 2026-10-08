package de.erethon.papyrus.combat;

/**
 * Shared arithmetic for Papyrus combat. Attribute values are in points, while
 * critical damage is a bonus percentage on top of the normal critical hit.
 */
public final class CombatMath {

    private CombatMath() {
    }

    /**
     * Resistance has diminishing returns and cannot erase a landed hit.
     * Penetration can move resistance below zero, increasing damage by at most
     * 100%. Positive resistance can prevent at most 80% of incoming damage.
     */
    public static double applyResistance(double damage, double resistance, double penetration) {
        if (!Double.isFinite(damage) || damage <= 0) {
            return 0;
        }
        double effectiveResistance = resistance - penetration;
        if (!Double.isFinite(effectiveResistance)) {
            return damage;
        }
        if (effectiveResistance >= 0) {
            return damage * Math.max(0.2, 100.0 / (100.0 + effectiveResistance));
        }
        return damage * (1.0 + Math.min(100.0, -effectiveResistance) / 100.0);
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
