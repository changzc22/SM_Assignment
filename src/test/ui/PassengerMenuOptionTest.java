package test.ui;

import ets.ui.PassengerMenuOption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PassengerMenuOptionTest - To test the passenger main menu UI
 */
class PassengerMenuOptionTest {

    /**
     * Verifies that valid numeric codes (1–6) correctly map to the
     * corresponding PassengerMenuOption enum values.
     * This ensures that the UI’s menu selection system aligns with the
     * intended menu structure and that mappings remain reliable.
     */
    @Test
    void fromCode_validCodes_returnEnum() {
        assertEquals(PassengerMenuOption.NEW_REGISTRATION,
                PassengerMenuOption.fromCode(1));
        assertEquals(PassengerMenuOption.SEARCH_PASSENGER,
                PassengerMenuOption.fromCode(2));
        assertEquals(PassengerMenuOption.EDIT_PASSENGER,
                PassengerMenuOption.fromCode(3));
        assertEquals(PassengerMenuOption.DISPLAY_ALL,
                PassengerMenuOption.fromCode(4));
        assertEquals(PassengerMenuOption.CHANGE_TIER,
                PassengerMenuOption.fromCode(5));
        assertEquals(PassengerMenuOption.BACK_TO_MAIN_MENU,
                PassengerMenuOption.fromCode(6));
    }

    /**
     * Ensures that invalid menu codes return null instead of causing errors.
     * This is important for defensive programming and prevents the UI from
     * crashing when a user or test environment provides unexpected input.
     */
    @Test
    void fromCode_invalidCode_returnsNull() {
        assertNull(PassengerMenuOption.fromCode(99));
    }

    /**
     * Confirms that the enum’s getter methods correctly expose the configured
     * menu code and description. This protects the integrity of user-facing text
     * and helps ensure consistency across the UI.
     */
    @Test
    void getters_returnExpectedValues() {
        PassengerMenuOption opt = PassengerMenuOption.CHANGE_TIER;
        assertEquals(5, opt.getCode());
        assertEquals("Upgrade/Downgrade Passenger Tier", opt.getDescription());
    }
}
