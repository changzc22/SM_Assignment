package test.model;

import ets.model.Booking;
import ets.model.SeatTier;
import ets.model.Train;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Booking Data Model (POJO).
 * Verifies object identity methods (equals, hashCode) and string representation.
 */
public class BookingTest {

    /**
     * Ensures two booking objects with the same ID are considered equal.
     * Crucial for ArrayList.remove() and contains().
     */
    @Test
    void testEquality() {
        Booking b1 = new Booking();
        b1.setBookingID("B001");

        Booking b2 = new Booking();
        b2.setBookingID("B001");

        assertEquals(b1, b2, "Bookings with same ID should be equal");
    }

    @Test
    void testInequality() {
        Booking b1 = new Booking();
        b1.setBookingID("B001");

        Booking b2 = new Booking();
        b2.setBookingID("B002");

        assertNotEquals(b1, b2, "Bookings with different IDs should not be equal");
    }

    @Test
    void testHashCode() {
        Booking b1 = new Booking();
        b1.setBookingID("B001");

        Booking b2 = new Booking();
        b2.setBookingID("b001"); // Case-insensitive check

        assertEquals(b1.hashCode(), b2.hashCode(), "HashCodes should be consistent");
    }

    @Test
    void testToString() {
        // Setup Dummy Train
        Train train = new Train();
        train.setTrainID("T001");

        Booking b = new Booking("B001", "Ali", SeatTier.STANDARD, 1, 50.0, train, "S001");

        String result = b.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("B001"), "Should contain ID");
        assertTrue(result.contains("Ali"), "Should contain Name");
        assertTrue(result.contains("T001"), "Should contain Train ID");
        assertTrue(result.contains("S001"), "Should contain Staff ID");
    }
}