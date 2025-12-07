package test.model;

import oopt.assignment.model.SeatTier;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SeatTierTest {

    @Test
    void testFromCode_ValidInput() {
        // Test Uppercase
        assertEquals(SeatTier.STANDARD, SeatTier.fromCode('S'));
        assertEquals(SeatTier.PREMIUM, SeatTier.fromCode('P'));
    }

    @Test
    void testFromCode_CaseInsensitivity() {
        // Test Lowercase (Logic uses Character.toUpperCase)
        assertEquals(SeatTier.STANDARD, SeatTier.fromCode('s'));
        assertEquals(SeatTier.PREMIUM, SeatTier.fromCode('p'));
    }

    @Test
    void testFromCode_InvalidInput() {
        // Test Exception Throwing
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            SeatTier.fromCode('X');
        });

        assertEquals("Invalid Seat Tier: X", exception.getMessage());
    }
}