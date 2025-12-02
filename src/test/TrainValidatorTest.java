package test;

import oopt.assignment.service.TrainValidator;
import oopt.assignment.util.AppConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TrainValidator.
 */
class TrainValidatorTest {

    private final TrainValidator validator = new TrainValidator();

    /**
     * Verifies valid and invalid Train ID formats.
     */
    @Test
    void testIsValidTrainIdFormat() {
        assertTrue(validator.isValidTrainIdFormat("T000"));
        assertTrue(validator.isValidTrainIdFormat("T999"));

        assertFalse(validator.isValidTrainIdFormat(null));
        assertFalse(validator.isValidTrainIdFormat(""));
        assertFalse(validator.isValidTrainIdFormat("A123"));
        assertFalse(validator.isValidTrainIdFormat("T12"));
        assertFalse(validator.isValidTrainIdFormat("T1234"));
        assertFalse(validator.isValidTrainIdFormat("TX12"));
    }

    /**
     * Verifies the destination format rules
     */
    @Test
    void testIsValidDestinationFormat() {
        assertTrue(validator.isValidDestinationFormat("Penang"));
        assertTrue(validator.isValidDestinationFormat("KL Sentral"));

        assertFalse(validator.isValidDestinationFormat(null));
        assertFalse(validator.isValidDestinationFormat(""));
        assertFalse(validator.isValidDestinationFormat("123Station"));
        assertFalse(validator.isValidDestinationFormat("KL-Sentral"));
        // longer than 15 characters should be invalid
        assertFalse(validator.isValidDestinationFormat("ThisIsVeryLongCityName"));
    }

    /**
     * Verifies that valid future dates are accepted and reject others
     */
    @Test
    void testIsValidFutureDate() {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate tomorrow = today.plusDays(1);
        java.time.LocalDate yesterday = today.minusDays(1);

        assertTrue(validator.isValidFutureDate(tomorrow.toString()));
        assertFalse(validator.isValidFutureDate(today.toString()));
        assertFalse(validator.isValidFutureDate(yesterday.toString()));

        assertFalse(validator.isValidFutureDate("not-a-date"));
        assertFalse(validator.isValidFutureDate("2024/01/01"));
    }

    /**
     * Verifies that valid times are accepted and invalid
     */
    @Test
    void testIsValidTime() {
        assertTrue(validator.isValidTime("00:00"));
        assertTrue(validator.isValidTime("23:59"));

        assertFalse(validator.isValidTime("24:00"));

        assertFalse(validator.isValidTime("12:60"));
        assertFalse(validator.isValidTime("ab:cd"));
        assertFalse(validator.isValidTime("12-00"));
        assertFalse(validator.isValidTime(""));
        assertFalse(validator.isValidTime(null));
    }

    /**
     * Verifies valid and invalid cases for standard seat quantity rule.
     */
    @Test
    void testValidateStandardSeatQuantity() {
        int minStd = AppConstants.MIN_STANDARD_SEATS;
        int maxSeats = AppConstants.MAX_SEATS;

        assertDoesNotThrow(() ->
                validator.validateStandardSeatQuantity(minStd + 10, minStd));

        assertThrows(IllegalArgumentException.class, () ->
                validator.validateStandardSeatQuantity(minStd - 1, 0));

        assertThrows(IllegalArgumentException.class, () ->
                validator.validateStandardSeatQuantity(maxSeats + 1, 0));

        assertThrows(IllegalArgumentException.class, () ->
                validator.validateStandardSeatQuantity(minStd, minStd));
    }

    /**
     * Verifies valid and invalid cases for premium seat quantity rule.
     */
    @Test
    void testValidatePremiumSeatQuantity() {
        int minPrem = AppConstants.MIN_PREMIUM_SEATS;
        int maxPrem = AppConstants.MAX_PREMIUM_SEATS;

        assertDoesNotThrow(() ->
                validator.validatePremiumSeatQuantity(minPrem + 5, minPrem + 10));

        assertThrows(IllegalArgumentException.class, () ->
                validator.validatePremiumSeatQuantity(minPrem - 1, minPrem + 10));

        assertThrows(IllegalArgumentException.class, () ->
                validator.validatePremiumSeatQuantity(maxPrem + 1, maxPrem + 10));

        assertThrows(IllegalArgumentException.class, () ->
                validator.validatePremiumSeatQuantity(minPrem + 10, minPrem + 10));
    }

    /**
     * Verifies valid and invalid cases for standard seat price.
     */
    @Test
    void testValidateStandardSeatPrice() {
        double minStd = AppConstants.MIN_STANDARD_PRICE;
        double maxStd = AppConstants.MAX_STANDARD_PRICE;

        assertDoesNotThrow(() ->
                validator.validateStandardSeatPrice(minStd + 1.0, maxStd + 10.0));

        assertThrows(IllegalArgumentException.class, () ->
                validator.validateStandardSeatPrice(minStd - 0.01, maxStd + 10.0));

        assertThrows(IllegalArgumentException.class, () ->
                validator.validateStandardSeatPrice(maxStd + 0.01, maxStd + 10.0));

        assertThrows(IllegalArgumentException.class, () ->
                validator.validateStandardSeatPrice(50.0, 50.0));
    }

    /**
     * Verifies valid and invalid cases for premium seat price.
     */
    @Test
    void testValidatePremiumSeatPrice() {
        double minPrem = AppConstants.MIN_PREMIUM_PRICE;
        double maxPrem = AppConstants.MAX_PREMIUM_PRICE;

        assertDoesNotThrow(() ->
                validator.validatePremiumSeatPrice(minPrem + 5.0, minPrem));

        assertThrows(IllegalArgumentException.class, () ->
                validator.validatePremiumSeatPrice(minPrem - 0.01, minPrem));

        assertThrows(IllegalArgumentException.class, () ->
                validator.validatePremiumSeatPrice(maxPrem + 0.01, minPrem));

        assertThrows(IllegalArgumentException.class, () ->
                validator.validatePremiumSeatPrice(80.0, 80.0));
    }
}
