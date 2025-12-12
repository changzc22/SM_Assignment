package test.service;

import ets.model.Train;
import ets.model.TrainStatus;
import ets.model.SeatTier;
import ets.service.BookingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BookingValidator.
 */
public class BookingValidatorTest {

    private BookingValidator validator;
    private Train testTrain;

    @BeforeEach
    void setUp() {
        validator = new BookingValidator();
        // Train with 10 Standard, 5 Premium
        testTrain = new Train("T001", "Test Dest",
                LocalDate.now(), LocalTime.now(),
                10, 5,
                50.0, 80.0,
                TrainStatus.ACTIVE);
    }

    /**
     * Verifies that only positive integers are accepted for quantity.
     */
    @Test
    void testIsValidQuantity() {
        assertTrue(validator.isValidQuantity(1));
        assertFalse(validator.isValidQuantity(0));
        assertFalse(validator.isValidQuantity(-5));
    }

    /**
     * Verifies the logic that checks if the train has enough capacity.
     */
    @Test
    void testHasEnoughSeats() {
        assertTrue(validator.hasEnoughSeats(testTrain, SeatTier.STANDARD, 5));
        assertFalse(validator.hasEnoughSeats(testTrain, SeatTier.STANDARD, 11)); // Overflow
    }
}