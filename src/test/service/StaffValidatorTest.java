package test.service;

import oopt.assignment.model.Staff;
import oopt.assignment.service.StaffValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Staff Validator Tests")
public class StaffValidatorTest {

    private StaffValidator validator;
    private LinkedHashMap<String, Staff> mockData;

    @BeforeEach
    void setup() {
        validator = new StaffValidator();
        mockData = new LinkedHashMap<>();
        // Add a dummy staff to test duplicate checks
        Staff existing = new Staff("Existing User", "0123456789", "990101015555", "S001", "hash", 0);
        mockData.put(existing.getId(), existing);
    }

    /**
     * Tests the name validation logic.
     * Ensures names with alphabets are accepted, while numbers or empty strings are rejected.
     */
    @Test
    void testNameValidation() {
        assertTrue(validator.isNameValid("John Doe"));
        assertFalse(validator.isNameValid("John123"), "Name with numbers should fail");
        assertFalse(validator.isNameValid(""), "Empty name should fail");
    }

    /**
     * Tests the contact number validation logic.
     * Ensures correct format (01xxxxxxxx) is accepted and duplicates are rejected.
     */
    @Test
    void testContactValidation() {
        assertTrue(validator.isCNValid("0123456788", mockData)); // Unique
        assertFalse(validator.isCNValid("0123456789", mockData), "Duplicate contact should fail");
        assertFalse(validator.isCNValid("123456", mockData), "Invalid format should fail");
    }

    /**
     * Tests the IC number validation logic.
     * Ensures correct 12-digit format is accepted and duplicates are rejected.
     */
    @Test
    void testICValidation() {
        assertTrue(validator.isICValid("990101015556", mockData)); // Unique
        assertFalse(validator.isICValid("990101015555", mockData), "Duplicate IC should fail");
        assertFalse(validator.isICValid("990101", mockData), "Short IC should fail");
    }

    /**
     * Tests the Staff ID validation logic.
     * Ensures the SXXX format is enforced and existing IDs are flagged as duplicates.
     */
    @Test
    void testIDValidation() {
        assertTrue(validator.isIDValid("S002", mockData)); // Unique
        assertFalse(validator.isIDValid("S001", mockData), "Duplicate ID should fail");
        assertFalse(validator.isIDValid("X999", mockData), "Wrong ID format should fail");
    }

    /**
     * Tests the password complexity rules.
     * Ensures passwords meet minimum length and character requirements.
     */
    @Test
    void testPasswordComplexity() {
        assertTrue(validator.isPasswordComplexityValid("Pass1234"));
        assertFalse(validator.isPasswordComplexityValid("weak"), "Short password should fail");
    }
}
