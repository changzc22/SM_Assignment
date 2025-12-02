package test.model;

import oopt.assignment.model.TrainStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TrainStatus
 */
class TrainStatusTest {

    /**
     * Verifies converting from boolean to enum.
     */
    @Test
    void testFromBoolean() {
        assertEquals(TrainStatus.ACTIVE, TrainStatus.fromBoolean(true));
        assertEquals(TrainStatus.DISCONTINUED, TrainStatus.fromBoolean(false));
    }

    /**
     * Verifies converting from enum to boolean.
     */
    @Test
    void testToBoolean() {
        assertTrue(TrainStatus.ACTIVE.toBoolean());
        assertFalse(TrainStatus.DISCONTINUED.toBoolean());
    }
}
