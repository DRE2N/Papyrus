package de.erethon.papyrus.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombatMathTest {

    @Test
    void resistanceSubtractsFlatDamageAndCanBlockAHit() {
        assertEquals(10, CombatMath.applyResistance(10, 0, 0), 0.0001);
        assertEquals(38.3, CombatMath.applyResistance(63.8, 25.5, 0), 0.0001);
        assertEquals(0, CombatMath.applyResistance(10, 25, 0), 0.0001);
        assertEquals(0, CombatMath.applyResistance(0, 25, 0), 0.0001);
    }

    @Test
    void penetrationOffsetsResistanceInFlatPoints() {
        assertEquals(10, CombatMath.applyResistance(10, 25, 25), 0.0001);
        assertEquals(35, CombatMath.applyResistance(10, 25, 50), 0.0001);
        assertEquals(15, CombatMath.applyResistance(10, 0, 5), 0.0001);
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
