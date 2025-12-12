package test.service;
import ets.service.PassengerValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PassengerValidatorTest - To test the PassengerValidator class
 */
class PassengerValidatorTest {

    private final PassengerValidator validator = new PassengerValidator();

    // ===== validateName =====

    /**
     * Verifies that a properly formatted and non-empty name passes validation
     * without throwing an exception. Confirms the validator accepts valid input.
     */
    @Test
    void validateName_validName_passes() {
        assertDoesNotThrow(() -> validator.validateName("Alice Tan"));
    }

    /**
     * Ensures that passing a null value to validateName triggers the correct
     * IllegalArgumentException with the expected error message.
     */
    @Test
    void validateName_null_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateName(null));
        assertEquals("Name cannot be empty.", ex.getMessage());
    }

    /**
     * Ensures that a blank or whitespace-only name is rejected and produces
     * the appropriate validation error message.
     */
    @Test
    void validateName_blank_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateName("   "));
        assertEquals("Name cannot be empty.", ex.getMessage());
    }

    /**
     * Confirms that names exceeding the maximum length restriction (>= 30 chars)
     * are rejected with the correct validation message.
     */
    @Test
    void validateName_tooLong_throws() {
        String longName = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234"; // length >= 30
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateName(longName));
        assertEquals("Name must be less than 30 characters.", ex.getMessage());
    }

    // ===== validateContact =====

    /**
     * Confirms that a 10-digit phone number format is accepted as valid input.
     */
    @Test
    void validateContact_valid10Digits_passes() {
        assertDoesNotThrow(() -> validator.validateContact("0123456789"));
    }

    /**
     * Confirms that an 11-digit Malaysian contact number is accepted as valid input.
     */
    @Test
    void validateContact_valid11Digits_passes() {
        assertDoesNotThrow(() -> validator.validateContact("01234567890"));
    }

    /**
     * Ensures that null contact numbers trigger the correct validation failure.
     */
    @Test
    void validateContact_null_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateContact(null));
        assertEquals("Contact number cannot be empty.", ex.getMessage());
    }

    /**
     * Ensures that blank or whitespace-only contact numbers are rejected.
     */
    @Test
    void validateContact_blank_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateContact(" "));
        assertEquals("Contact number cannot be empty.", ex.getMessage());
    }

    /**
     * Confirms that any non-numeric or incorrectly formatted contact number
     * results in an appropriate validation exception.
     */
    @Test
    void validateContact_wrongFormat_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateContact("abc123"));
        assertEquals("Contact number must be 10-11 digits.", ex.getMessage());
    }

    // ===== validateIc =====

    /**
     * Verifies that a valid 12-digit Malaysian IC number is accepted.
     */
    @Test
    void validateIc_valid12Digits_passes() {
        assertDoesNotThrow(() -> validator.validateIc("010203040506"));
    }

    /**
     * Ensures that null IC values correctly trigger a validation exception.
     */
    @Test
    void validateIc_null_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateIc(null));
        assertEquals("IC cannot be empty.", ex.getMessage());
    }

    /**
     * Ensures that blank or whitespace-only IC values are rejected with the
     * correct validation error.
     */
    @Test
    void validateIc_blank_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateIc(" "));
        assertEquals("IC cannot be empty.", ex.getMessage());
    }

    /**
     * Confirms that IC values with the wrong length (not 12 digits)
     * fail validation and return the correct error message.
     */
    @Test
    void validateIc_wrongLength_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateIc("123456"));
        assertEquals("IC must be 12 digits.", ex.getMessage());
    }

    // ===== validateGender =====

    /**
     * Ensures that uppercase 'M' is accepted as a valid gender value.
     */
    @Test
    void validateGender_M_passes() {
        assertDoesNotThrow(() -> validator.validateGender('M'));
    }

    /**
     * Confirms that lowercase gender inputs are converted and accepted,
     * proving validation is case-insensitive.
     */
    @Test
    void validateGender_f_lowercase_passes() {
        assertDoesNotThrow(() -> validator.validateGender('f'));
    }

    /**
     * Ensures that invalid gender characters (not M/F) are rejected with a
     * meaningful validation error.
     */
    @Test
    void validateGender_invalid_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateGender('X'));
        assertEquals("Gender must be M or F.", ex.getMessage());
    }

    // ===== validateTier =====

    /**
     * Confirms that GOLD tier code 'G' is recognised as valid input.
     */
    @Test
    void validateTier_validGold_passes() {
        assertDoesNotThrow(() -> validator.validateTier('G'));
    }

    /**
     * Confirms that lowercase tier code input (e.g., 's') is handled correctly
     * by case-insensitive validation.
     */
    @Test
    void validateTier_validSilver_lowercase_passes() {
        assertDoesNotThrow(() -> validator.validateTier('s'));
    }

    /**
     * Ensures that invalid tier codes (anything outside G/S/N)
     * result in a correct and meaningful validation exception.
     */
    @Test
    void validateTier_invalid_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateTier('Z'));
        assertEquals("Tier must be G (Gold), S (Silver) or N (Normal).", ex.getMessage());
    }
}
