package de.erethon.papyrus.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombatMathTest {

    @Test
    void resistanceNeverErasesAValidHit() {
        assertEquals(10, CombatMath.applyResistance(10, 0, 0), 0.0001);
        assertEquals(8, CombatMath.applyResistance(10, 25, 0), 0.0001);
        assertEquals(2, CombatMath.applyResistance(10, 1000, 0), 0.0001);
        assertEquals(0, CombatMath.applyResistance(0, 25, 0), 0.0001);
    }

    @Test
    void penetrationCanAmplifyButNotExplodeDamage() {
        assertEquals(10, CombatMath.applyResistance(10, 25, 25), 0.0001);
        assertEquals(12.5, CombatMath.applyResistance(10, 25, 50), 0.0001);
        assertEquals(20, CombatMath.applyResistance(10, 0, 1000), 0.0001);
    }

    @Test
    void lowLevelCritDamageIsABonusNotAMultiplierBelowOne() {
        assertEquals(1.5, CombatMath.criticalMultiplier(0), 0.0001);
        assertEquals(1.6055, CombatMath.criticalMultiplier(10.55), 0.0001);
        assertEquals(4.0, CombatMath.criticalMultiplier(1000), 0.0001);
    }

    @Test
    void invalidAndOutOfRangeInputsAreContained() {
        assertEquals(10, CombatMath.applyResistance(10, Double.NaN, 0), 0.0001);
        assertEquals(0, CombatMath.applyResistance(Double.NaN, 0, 0), 0.0001);
        assertEquals(0, CombatMath.chancePercent(-5), 0.0001);
        assertEquals(100, CombatMath.chancePercent(500), 0.0001);
    }
}
