package test.model;

import oopt.assignment.model.PassengerTier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PassengerTierTest {

    @Test
    void fromCode_gold_returnsGold() {
        assertEquals(PassengerTier.GOLD, PassengerTier.fromCode('G'));
        assertEquals(PassengerTier.GOLD, PassengerTier.fromCode('g')); // case-insensitive
    }

    @Test
    void fromCode_invalid_returnsNormal() {
        assertEquals(PassengerTier.NORMAL, PassengerTier.fromCode('X'));
    }

    @Test
    void getters_returnExpectedValues() {
        assertEquals('G', PassengerTier.GOLD.getCode());
        assertEquals(0.75, PassengerTier.GOLD.getPriceMultiplier(), 0.0001);

        assertEquals('S', PassengerTier.SILVER.getCode());
        assertEquals(0.85, PassengerTier.SILVER.getPriceMultiplier(), 0.0001);

        assertEquals('N', PassengerTier.NORMAL.getCode());
        assertEquals(1.00, PassengerTier.NORMAL.getPriceMultiplier(), 0.0001);
    }
}
