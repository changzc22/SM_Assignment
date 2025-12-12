package test.model;

import oopt.assignment.model.SeatTier;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SeatTier Enum.
 * <p>
 * Purpose: Verifies that the helper method {@code fromCode(char)} correctly maps
 * characters to Enum constants, handling both valid and invalid inputs robustly.
 */
public class SeatTierTest {

    /**
     * Verifies that the standard uppercase codes defined in the requirement
     * map correctly to their respective Enums.
     */
    @Test
    void testFromCode_ValidInput() {
        // Test Uppercase
        assertEquals(SeatTier.STANDARD, SeatTier.fromCode('S'));
        assertEquals(SeatTier.PREMIUM, SeatTier.fromCode('P'));
    }

    /**
     * Verifies that the lookup logic is case-insensitive.
     * Use Case: Users might type 's' instead of 'S', and the system should accept it.
     */
    @Test
    void testFromCode_CaseInsensitivity() {
        // Test Lowercase (Logic uses Character.toUpperCase)
        assertEquals(SeatTier.STANDARD, SeatTier.fromCode('s'));
        assertEquals(SeatTier.PREMIUM, SeatTier.fromCode('p'));
    }

    /**
     * Verifies that invalid characters trigger an Exception rather than returning null.
     * This ensures the calling code is forced to handle bad data immediately (Fail Fast).
     */
    @Test
    void testFromCode_InvalidInput() {
        // Test Exception Throwing
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            SeatTier.fromCode('X');
        });

        assertEquals("Invalid Seat Tier: X", exception.getMessage());
    }
}