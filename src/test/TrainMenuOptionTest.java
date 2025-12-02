package test;

import oopt.assignment.ui.TrainMenuOption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TrainMenuOption
 */
class TrainMenuOptionTest {

    /**
     * Verifies that getId and getDescription return expected values.
     */
    @Test
    void testBasicGetters() {
        TrainMenuOption opt = TrainMenuOption.ADD_TRAIN;
        assertEquals(1, opt.getId());
        assertEquals("Add New Train", opt.getDescription());
    }

    /**
     * Verifies that fromId maps valid IDs and returns null for invalid ones.
     */
    @Test
    void testFromId() {
        assertEquals(TrainMenuOption.ADD_TRAIN, TrainMenuOption.fromId(1));
        assertEquals(TrainMenuOption.SEARCH_TRAIN, TrainMenuOption.fromId(2));
        assertEquals(TrainMenuOption.MODIFY_TRAIN, TrainMenuOption.fromId(3));
        assertEquals(TrainMenuOption.DISPLAY_TRAIN, TrainMenuOption.fromId(4));
        assertEquals(TrainMenuOption.RETURN, TrainMenuOption.fromId(5));

        assertNull(TrainMenuOption.fromId(0));
        assertNull(TrainMenuOption.fromId(999));
    }
}
