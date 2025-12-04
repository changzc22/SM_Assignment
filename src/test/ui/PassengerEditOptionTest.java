package test.ui;

import oopt.assignment.ui.PassengerEditOption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PassengerEditOptionTest {

    @Test
    void fromCode_validCodes_returnEnum() {
        assertEquals(PassengerEditOption.EDIT_NAME,
                PassengerEditOption.fromCode(1));
        assertEquals(PassengerEditOption.EDIT_CONTACT,
                PassengerEditOption.fromCode(2));
        assertEquals(PassengerEditOption.EDIT_IC,
                PassengerEditOption.fromCode(3));
        assertEquals(PassengerEditOption.EDIT_GENDER,
                PassengerEditOption.fromCode(4));
        assertEquals(PassengerEditOption.SAVE_AND_RETURN,
                PassengerEditOption.fromCode(5));
        assertEquals(PassengerEditOption.CANCEL,
                PassengerEditOption.fromCode(6));
    }

    @Test
    void fromCode_invalidCode_returnsNull() {
        assertNull(PassengerEditOption.fromCode(0));
    }

    @Test
    void getters_returnExpectedValues() {
        PassengerEditOption opt = PassengerEditOption.EDIT_CONTACT;
        assertEquals(2, opt.getCode());
        assertEquals("Contact number", opt.getDescription());
    }
}
