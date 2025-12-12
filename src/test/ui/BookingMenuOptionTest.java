package test.ui;

import ets.ui.BookingMenuOption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BookingMenuOption Enum.
 * Ensures menu selection logic maps integers to the correct actions.
 */
class BookingMenuOptionTest {

    /**
     * Verifies that valid menu numbers return the correct Enum constant.
     * This confirms the menu mapping is aligned with the requirement specification.
     */
    @Test
    void fromId_validIds_returnEnum() {
        assertEquals(BookingMenuOption.ADD_BOOKING, BookingMenuOption.fromId(1));
        assertEquals(BookingMenuOption.DISPLAY_BOOKINGS, BookingMenuOption.fromId(2));
        assertEquals(BookingMenuOption.SEARCH_BOOKING, BookingMenuOption.fromId(3));
        assertEquals(BookingMenuOption.CANCEL_BOOKING, BookingMenuOption.fromId(4));
        assertEquals(BookingMenuOption.GENERATE_REPORT, BookingMenuOption.fromId(5));
        assertEquals(BookingMenuOption.EXIT, BookingMenuOption.fromId(6));
    }

    /**
     * Verifies that invalid numbers (out of range) return null.
     * The UI layer depends on this null return to show "Invalid Option" messages
     * instead of throwing a RuntimeException.
     */
    @Test
    void fromId_invalidId_returnsNull() {
        assertNull(BookingMenuOption.fromId(0));
        assertNull(BookingMenuOption.fromId(99));
        assertNull(BookingMenuOption.fromId(-1));
    }

    /**
     * Verifies that the Enum stores the correct display description.
     * Ensures the UI prints user-friendly text (e.g., "Add New Booking") and not raw variable names.
     */
    @Test
    void getters_returnExpectedValues() {
        BookingMenuOption opt = BookingMenuOption.ADD_BOOKING;
        assertEquals(1, opt.getId());
        assertEquals("Add New Booking", opt.getDescription());
    }
}