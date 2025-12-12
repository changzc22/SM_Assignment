package test.model;

import oopt.assignment.model.PassengerTier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PassengerTierTest - To test the PassengerTier enum class
 */
class PassengerTierTest {

    /**
     * Ensures that fromCode('G') and fromCode('g') correctly map to GOLD.
     * This verifies that the enum's lookup method is case-insensitive and
     * consistently returns the expected tier for valid inputs.
     */
    @Test
    void fromCode_gold_returnsGold() {
        assertEquals(PassengerTier.GOLD, PassengerTier.fromCode('G'));
        assertEquals(PassengerTier.GOLD, PassengerTier.fromCode('g')); // case-insensitive
    }

    /**
     * Confirms that invalid or unrecognised tier codes default to NORMAL.
     * This ensures system robustness by preventing unexpected nulls or crashes,
     * and enforces the fallback logic defined in the enum.
     */
    @Test
    void fromCode_invalid_returnsNormal() {
        assertEquals(PassengerTier.NORMAL, PassengerTier.fromCode('X'));
    }

    /**
     * Validates that each enum constant returns the correct code and price
     * multiplier. This ensures that tier-related fare calculations and UI
     * display logic remain accurate and consistent with system specifications.
     */
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
