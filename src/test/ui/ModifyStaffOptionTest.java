package test.ui;

import oopt.assignment.ui.ModifyStaffOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Modify Staff Option Enum Tests")
public class ModifyStaffOptionTest {

    /**
     * verify that valid code strings ("A", "b") are correctly mapped to their
     * corresponding Enum constants. This also tests case-insensitivity.
     */
    @Test
    void testFromCodeValid() {
        // Test exact match
        assertEquals(ModifyStaffOption.NAME, ModifyStaffOption.fromCode("A"));
        // Test case insensitivity
        assertEquals(ModifyStaffOption.CONTACT, ModifyStaffOption.fromCode("b"));
    }

    /**
     * verify that invalid code strings (like "Z" or "1") correctly return null,
     * ensuring that invalid user choices are handled safely.
     */
    @Test
    void testFromCodeInvalid() {
        assertNull(ModifyStaffOption.fromCode("Z"));
        assertNull(ModifyStaffOption.fromCode("1"));
    }

    /**
     * verify that the Enum constants store the correct code and description
     * as defined in the Enum constructor.
     */
    @Test
    void testGetters() {
        ModifyStaffOption opt = ModifyStaffOption.IC;
        assertEquals("C", opt.getCode());
        assertNotNull(opt.getDescription());
    }
}
