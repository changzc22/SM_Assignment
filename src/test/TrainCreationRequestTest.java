package test;

import oopt.assignment.model.TrainCreationRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TrainCreationRequest
 */
class TrainCreationRequestTest {

    /**
     * Verifies that all public fields can be assigned and read back correctly.
     */
    @Test
    void testFieldAssignments() {
        TrainCreationRequest req = new TrainCreationRequest();

        req.trainId = "T010";
        req.destination = "Ipoh";
        req.departureDate = LocalDate.of(2030, 6, 1);
        req.departureTime = LocalTime.of(11, 30);
        req.standardSeatQty = 90;
        req.premiumSeatQty = 15;
        req.standardSeatPrice = 40.0;
        req.premiumSeatPrice = 90.0;

        assertEquals("T010", req.trainId);
        assertEquals("Ipoh", req.destination);
        assertEquals(LocalDate.of(2030, 6, 1), req.departureDate);
        assertEquals(LocalTime.of(11, 30), req.departureTime);
        assertEquals(90, req.standardSeatQty);
        assertEquals(15, req.premiumSeatQty);
        assertEquals(40.0, req.standardSeatPrice);
        assertEquals(90.0, req.premiumSeatPrice);
    }
}
