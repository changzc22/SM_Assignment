package test.ui;

import ets.ui.PassengerEditOption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PassengerEditOptionTest - To test the passenger edit option menu UI
 */
class PassengerEditOptionTest {

    /**
     * Ensures that valid numeric codes (1–6) correctly map to
     * their corresponding PassengerEditOption enum values.
     * This guarantees that the edit menu logic inside the UI
     * behaves consistently and remains aligned with the enum definitions.
     */
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

    /**
     * Verifies that invalid edit codes return null instead of causing
     * errors or undefined behaviour. This helps ensure that the UI
     * can safely guard against unexpected input during user interaction.
     */
    @Test
    void fromCode_invalidCode_returnsNull() {
        assertNull(PassengerEditOption.fromCode(0));
    }

    /**
     * Confirms that the enum getter methods correctly expose each edit
     * option's numeric code and text description. Maintaining these values
     * ensures consistency between the UI display and the edit logic.
     */
    @Test
    void getters_returnExpectedValues() {
        PassengerEditOption opt = PassengerEditOption.EDIT_CONTACT;
        assertEquals(2, opt.getCode());
        assertEquals("Contact number", opt.getDescription());
    }
}
