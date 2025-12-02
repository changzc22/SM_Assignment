package test.model;

import oopt.assignment.model.Staff;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Staff Model Tests")
public class StaffTest {

    /**
     * verify that the parameterized constructor correctly initializes all fields
     * and that all getters return the expected values.
     */
    @Test
    void testConstructorAndGetters() {
        Staff staff = new Staff("John", "0123456789", "990101015555", "S001", "hashedPass", 5);

        assertEquals("John", staff.getName());
        assertEquals("0123456789", staff.getContactNo());
        assertEquals("990101015555", staff.getIc());
        assertEquals("S001", staff.getId());
        assertEquals("hashedPass", staff.getPassword());
        assertEquals(5, staff.getNoOfBookingHandle());
    }

    /**
     * verify that the setter methods correctly update the object's state
     * and that the changes are reflected in the getters.
     */
    @Test
    void testSetters() {
        Staff staff = new Staff();
        staff.setName("Jane");
        staff.setFailedAttempts(2);
        staff.setLockTime(LocalDateTime.MAX);

        assertEquals("Jane", staff.getName());
        assertEquals(2, staff.getFailedAttempts());
        assertNotNull(staff.getLockTime());
    }

    /**
     * verify that the toString method returns a string containing key
     * identification details of the staff member (Name, ID, Bookings).
     */
    @Test
    void testToString() {
        Staff staff = new Staff("John", "012", "999", "S1", "pass", 10);
        String output = staff.toString();

        assertTrue(output.contains("John"));
        assertTrue(output.contains("S1"));
        assertTrue(output.contains("10")); // Bookings handled
    }
}
