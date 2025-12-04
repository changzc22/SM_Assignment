package test.service;
import oopt.assignment.service.PassengerValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PassengerValidatorTest {

    private final PassengerValidator validator = new PassengerValidator();

    // ===== validateName =====

    @Test
    void validateName_validName_passes() {
        assertDoesNotThrow(() -> validator.validateName("Alice Tan"));
    }

    @Test
    void validateName_null_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateName(null));
        assertEquals("Name cannot be empty.", ex.getMessage());
    }

    @Test
    void validateName_blank_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateName("   "));
        assertEquals("Name cannot be empty.", ex.getMessage());
    }

    @Test
    void validateName_tooLong_throws() {
        String longName = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234"; // length >= 30
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateName(longName));
        assertEquals("Name must be less than 30 characters.", ex.getMessage());
    }

    // ===== validateContact =====

    @Test
    void validateContact_valid10Digits_passes() {
        assertDoesNotThrow(() -> validator.validateContact("0123456789"));
    }

    @Test
    void validateContact_valid11Digits_passes() {
        assertDoesNotThrow(() -> validator.validateContact("01234567890"));
    }

    @Test
    void validateContact_null_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateContact(null));
        assertEquals("Contact number cannot be empty.", ex.getMessage());
    }

    @Test
    void validateContact_blank_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateContact(" "));
        assertEquals("Contact number cannot be empty.", ex.getMessage());
    }

    @Test
    void validateContact_wrongFormat_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateContact("abc123"));
        assertEquals("Contact number must be 10-11 digits.", ex.getMessage());
    }

    // ===== validateIc =====

    @Test
    void validateIc_valid12Digits_passes() {
        assertDoesNotThrow(() -> validator.validateIc("010203040506"));
    }

    @Test
    void validateIc_null_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateIc(null));
        assertEquals("IC cannot be empty.", ex.getMessage());
    }

    @Test
    void validateIc_blank_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateIc(" "));
        assertEquals("IC cannot be empty.", ex.getMessage());
    }

    @Test
    void validateIc_wrongLength_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateIc("123456"));
        assertEquals("IC must be 12 digits.", ex.getMessage());
    }

    // ===== validateGender =====

    @Test
    void validateGender_M_passes() {
        assertDoesNotThrow(() -> validator.validateGender('M'));
    }

    @Test
    void validateGender_f_lowercase_passes() {
        assertDoesNotThrow(() -> validator.validateGender('f'));
    }

    @Test
    void validateGender_invalid_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateGender('X'));
        assertEquals("Gender must be M or F.", ex.getMessage());
    }

    // ===== validateTier =====

    @Test
    void validateTier_validGold_passes() {
        assertDoesNotThrow(() -> validator.validateTier('G'));
    }

    @Test
    void validateTier_validSilver_lowercase_passes() {
        assertDoesNotThrow(() -> validator.validateTier('s'));
    }

    @Test
    void validateTier_invalid_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> validator.validateTier('Z'));
        assertEquals("Tier must be G (Gold), S (Silver) or N (Normal).", ex.getMessage());
    }
}
