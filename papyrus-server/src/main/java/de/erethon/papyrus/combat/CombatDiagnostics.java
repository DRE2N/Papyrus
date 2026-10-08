package de.erethon.papyrus.combat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Optional, low overhead trace for tuning the live Papyrus damage pipeline. */
public final class CombatDiagnostics {

    private static final Logger LOGGER = LoggerFactory.getLogger(CombatDiagnostics.class);
    private static final boolean ENABLED = Boolean.getBoolean("papyrus.traceCombat");

    private CombatDiagnostics() {
    }

    public static void rejected(int targetId, String reason, Object damageSource, double requestedDamage) {
        if (!ENABLED) {
            return;
        }
        CombatSource source = CombatContext.capture();
        LOGGER.info("combat rejected target={} reason={} source={} ability={} requested={}",
                targetId, reason, damageSource, source == null ? "unknown" : source.attributionId(), requestedDamage);
    }

    public static void hit(int targetId, int attackerId, Object damageSource, CombatSource source,
                           String element, CombatDamageBreakdown breakdown, double healthBefore,
                           boolean contactKnockback) {
        if (!ENABLED) {
            return;
        }
        LOGGER.info("combat hit target={} attacker={} source={} ability={} element={} requested={} afterCrit={} "
                        + "resistance={} penetration={} afterResistance={} afterModifiers={} final={} applied={} "
                        + "critical={} contactKnockback={}",
                targetId, attackerId, damageSource, source.attributionId(), element,
                breakdown.requestedDamage(), breakdown.damageAfterCritical(), breakdown.resistance(),
                breakdown.penetration(), breakdown.damageAfterResistance(), breakdown.damageAfterModifiers(),
                breakdown.finalDamage(), Math.min(healthBefore, Math.max(0, breakdown.finalDamage())),
                breakdown.critical(), contactKnockback);
    }
}
