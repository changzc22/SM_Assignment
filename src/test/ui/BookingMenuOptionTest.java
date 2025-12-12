package test.ui;

import oopt.assignment.ui.BookingMenuOption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingMenuOptionTest {

    @Test
    void fromId_validIds_returnEnum() {
        assertEquals(BookingMenuOption.ADD_BOOKING, BookingMenuOption.fromId(1));
        assertEquals(BookingMenuOption.DISPLAY_BOOKINGS, BookingMenuOption.fromId(2));
        assertEquals(BookingMenuOption.SEARCH_BOOKING, BookingMenuOption.fromId(3));
        assertEquals(BookingMenuOption.CANCEL_BOOKING, BookingMenuOption.fromId(4));
        assertEquals(BookingMenuOption.GENERATE_REPORT, BookingMenuOption.fromId(5));
        assertEquals(BookingMenuOption.EXIT, BookingMenuOption.fromId(6));
    }

    @Test
    void fromId_invalidId_returnsNull() {
        assertNull(BookingMenuOption.fromId(0));
        assertNull(BookingMenuOption.fromId(99));
        assertNull(BookingMenuOption.fromId(-1));
    }

    @Test
    void getters_returnExpectedValues() {
        BookingMenuOption opt = BookingMenuOption.ADD_BOOKING;
        assertEquals(1, opt.getId());
        assertEquals("Add New Booking", opt.getDescription());
    }
}