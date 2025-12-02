package test.ui;

import oopt.assignment.ui.StaffMenuOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Staff Menu Option Enum Tests")
public class StaffMenuOptionTest {

    /**
     * verify that valid integer IDs (1, 2, 6) are correctly mapped
     * to their corresponding Enum constants (CREATE, MODIFY, RETURN).
     */
    @Test
    void testFromIdValid() {
        assertEquals(StaffMenuOption.CREATE, StaffMenuOption.fromId(1));
        assertEquals(StaffMenuOption.MODIFY, StaffMenuOption.fromId(2));
        assertEquals(StaffMenuOption.RETURN, StaffMenuOption.fromId(6));
    }

    /**
     * verify that invalid integer IDs (like 99 or -1) correctly return null,
     * ensuring the system handles bad input gracefully without crashing.
     */
    @Test
    void testFromIdInvalid() {
        assertNull(StaffMenuOption.fromId(99));
        assertNull(StaffMenuOption.fromId(-1));
    }

    /**
     * verify that the Enum constants contain the correct ID and description text
     * as defined in the Enum constructor.
     */
    @Test
    void testGetters() {
        StaffMenuOption opt = StaffMenuOption.CREATE;
        assertEquals(1, opt.getId());
        assertNotNull(opt.getDescription());
    }
}
