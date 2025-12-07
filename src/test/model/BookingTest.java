package test.model;

import oopt.assignment.model.Booking;
import oopt.assignment.model.SeatTier;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookingTest {

    @Test
    void testEquality() {
        // Two separate objects with the same ID should be equal
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
        b2.setBookingID("B002"); // Different ID

        assertNotEquals(b1, b2, "Bookings with different IDs should not be equal");
    }

    @Test
    void testHashCode() {
        // If objects are equal, their hashCodes MUST be the same
        Booking b1 = new Booking();
        b1.setBookingID("B001");

        Booking b2 = new Booking();
        b2.setBookingID("b001"); // Lowercase check (logic uses toUpperCase)

        assertEquals(b1.hashCode(), b2.hashCode(), "HashCodes should be consistent and case-insensitive");
    }

    @Test
    void testToString() {
        // Basic check to ensure toString doesn't crash
        Booking b = new Booking("B001", "Ali", SeatTier.STANDARD, 1, 50.0, null, "S001");
        assertNotNull(b.toString());
        assertTrue(b.toString().contains("B001"));
    }
}