package test.service;

import oopt.assignment.Train;
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

        // Setup a dummy train for testing seats
        // Constructor: ID, Dest, Date, Time, StdQty, PrmQty, StdPrice, PrmPrice, Status
        testTrain = new Train("T001", "Test Dest", LocalDate.now(), LocalTime.now(),
                10, 5, 50.0, 80.0, true);
        // Standard Seats: 10
        // Premium Seats: 5
    }

    // =========================================================
    // 1. QUANTITY VALIDATION TESTS
    // =========================================================

    @Test
    void testIsValidQuantity_Positive() {
        assertTrue(validator.isValidQuantity(1), "Quantity of 1 should be valid");
        assertTrue(validator.isValidQuantity(100), "Quantity of 100 should be valid");
    }

    @Test
    void testIsValidQuantity_Zero() {
        assertFalse(validator.isValidQuantity(0), "Quantity of 0 should be invalid");
    }

    @Test
    void testIsValidQuantity_Negative() {
        assertFalse(validator.isValidQuantity(-1), "Negative quantity should be invalid");
        assertFalse(validator.isValidQuantity(-50), "Negative quantity should be invalid");
    }

    // =========================================================
    // 2. SEAT AVAILABILITY TESTS (Standard)
    // =========================================================

    @Test
    void testHasEnoughSeats_Standard_Success() {
        // Train has 10 Standard seats. Requesting 5.
        assertTrue(validator.hasEnoughSeats(testTrain, SeatTier.STANDARD, 5));
    }

    @Test
    void testHasEnoughSeats_Standard_Boundary() {
        // Train has 10 Standard seats. Requesting 10 (Exact match).
        assertTrue(validator.hasEnoughSeats(testTrain, SeatTier.STANDARD, 10),
                "Should allow booking typically exactly the remaining amount");
    }

    @Test
    void testHasEnoughSeats_Standard_Fail() {
        // Train has 10 Standard seats. Requesting 11.
        assertFalse(validator.hasEnoughSeats(testTrain, SeatTier.STANDARD, 11),
                "Should fail if requesting more than available");
    }

    // =========================================================
    // 3. SEAT AVAILABILITY TESTS (Premium)
    // =========================================================

    @Test
    void testHasEnoughSeats_Premium_Success() {
        // Train has 5 Premium seats. Requesting 2.
        assertTrue(validator.hasEnoughSeats(testTrain, SeatTier.PREMIUM, 2));
    }

    @Test
    void testHasEnoughSeats_Premium_Boundary() {
        // Train has 5 Premium seats. Requesting 5.
        assertTrue(validator.hasEnoughSeats(testTrain, SeatTier.PREMIUM, 5));
    }

    @Test
    void testHasEnoughSeats_Premium_Fail() {
        // Train has 5 Premium seats. Requesting 6.
        assertFalse(validator.hasEnoughSeats(testTrain, SeatTier.PREMIUM, 6));
    }
}