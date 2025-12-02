package test;

import oopt.assignment.ui.TrainModifyOption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TrainModifyOption
 */
class TrainModifyOptionTest {

    /**
     * Verifies that getId and getDescription expose correct values.
     */
    @Test
    void testBasicGetters() {
        TrainModifyOption opt = TrainModifyOption.STANDARD_QTY;
        assertEquals(1, opt.getId());
        assertEquals("Standard Seat Quantity", opt.getDescription());
    }

    /**
     * Verifies that fromId maps valid IDs and returns null for invalid ones.
     */
    @Test
    void testFromId() {
        assertEquals(TrainModifyOption.STANDARD_QTY, TrainModifyOption.fromId(1));
        assertEquals(TrainModifyOption.PREMIUM_QTY, TrainModifyOption.fromId(2));
        assertEquals(TrainModifyOption.STANDARD_PRICE, TrainModifyOption.fromId(3));
        assertEquals(TrainModifyOption.PREMIUM_PRICE, TrainModifyOption.fromId(4));
        assertEquals(TrainModifyOption.DISCONTINUE, TrainModifyOption.fromId(5));
        assertEquals(TrainModifyOption.EXIT, TrainModifyOption.fromId(6));

        assertNull(TrainModifyOption.fromId(0));
        assertNull(TrainModifyOption.fromId(99));
    }
}
