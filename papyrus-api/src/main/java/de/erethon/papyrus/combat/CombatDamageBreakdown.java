package de.erethon.papyrus.combat;

/**
 * Immutable snapshots of each stage in Papyrus's elemental damage pipeline.
 */
public record CombatDamageBreakdown(
        double requestedDamage,
        double damageAfterCritical,
        double resistance,
        double penetration,
        double effectiveResistance,
        double damageAfterResistance,
        double damageAfterModifiers,
        double finalDamage,
        boolean critical
) {

    public CombatDamageBreakdown withFinalDamage(double finalDamage) {
        return new CombatDamageBreakdown(requestedDamage, damageAfterCritical, resistance, penetration,
                effectiveResistance, damageAfterResistance, damageAfterModifiers, finalDamage, critical);
    }
}
