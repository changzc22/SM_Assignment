package test.service;

import oopt.assignment.model.Train;
import oopt.assignment.model.TrainStatus;
import oopt.assignment.model.SeatTier;
import oopt.assignment.service.BookingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testIsValidQuantity() {
        assertTrue(validator.isValidQuantity(1));
        assertFalse(validator.isValidQuantity(0));
        assertFalse(validator.isValidQuantity(-5));
    }

    @Test
    void testHasEnoughSeats() {
        assertTrue(validator.hasEnoughSeats(testTrain, SeatTier.STANDARD, 5));
        assertFalse(validator.hasEnoughSeats(testTrain, SeatTier.STANDARD, 11)); // Overflow
    }
}