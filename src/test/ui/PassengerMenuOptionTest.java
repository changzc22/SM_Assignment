package test.ui;

import oopt.assignment.ui.PassengerMenuOption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PassengerMenuOptionTest {

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

    @Test
    void fromCode_invalidCode_returnsNull() {
        assertNull(PassengerMenuOption.fromCode(99));
    }

    @Test
    void getters_returnExpectedValues() {
        PassengerMenuOption opt = PassengerMenuOption.CHANGE_TIER;
        assertEquals(5, opt.getCode());
        assertEquals("Upgrade/Downgrade Passenger Tier", opt.getDescription());
    }
}
