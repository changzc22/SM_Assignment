package tests;

import oopt.assignment.model.IStaffRepository;
import oopt.assignment.model.Staff;
import oopt.assignment.service.StaffService;
import oopt.assignment.service.StaffValidator;
import oopt.assignment.util.AppConstants;
import oopt.assignment.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive Test Suite for the Staff Module.
 * Covers: Service Logic, Validation, Security Utils, and Model Integrity.
 * Target Coverage: >80%
 */
public class StaffModuleTest {

    // =========================================================================
    // 1. UNIT TESTS: UTILITIES (PasswordUtil)
    // =========================================================================
    @Nested
    @DisplayName("Password Utility Tests")
    class PasswordUtilTests {

        /**
         * Verifies that the hashing algorithm is deterministic.
         * The same input password must always produce the exact same hash output.
         */
        @Test
        void testHashConsistency() {
            String password = "Password123";
            String hash1 = PasswordUtil.hashPassword(password);
            String hash2 = PasswordUtil.hashPassword(password);

            assertNotNull(hash1);
            assertEquals(hash1, hash2, "Hashing the same password twice must result in the same hash");
        }

        /**
         * Verifies that the checkPassword method correctly identifies a match
         * when the provided plain text password corresponds to the stored hash.
         */
        @Test
        void testCheckPasswordSuccess() {
            String plain = "SecurePass1";
            String hash = PasswordUtil.hashPassword(plain);
            assertTrue(PasswordUtil.checkPassword(plain, hash), "Password verification should succeed for correct input");
        }

        /**
         * Verifies that the checkPassword method correctly rejects a mismatch,
         * ensuring that an incorrect password does not validate against the hash.
         */
        @Test
        void testCheckPasswordFailure() {
            String plain = "SecurePass1";
            String hash = PasswordUtil.hashPassword(plain);
            assertFalse(PasswordUtil.checkPassword("WrongPass", hash), "Password verification should fail for wrong input");
        }
    }

    // =========================================================================
    // 2. UNIT TESTS: VALIDATOR (StaffValidator)
    // =========================================================================
    @Nested
    @DisplayName("Staff Validator Tests")
    class ValidatorTests {

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

    // =========================================================================
    // 3. INTEGRATION TESTS: SERVICE LAYER (StaffService + Fake Repository)
    // =========================================================================
    @Nested
    @DisplayName("Staff Service Logic Tests")
    class StaffServiceTests {

        private StaffService service;
        private FakeStaffRepository fakeRepo;

        @BeforeEach
        void setup() {
            // Use the Fake Repository to avoid touching real files
            fakeRepo = new FakeStaffRepository();
            // Pre-seed with an admin user
            String adminHash = PasswordUtil.hashPassword("AdminPass1");
            Staff admin = new Staff("Admin", "0123456789", "900101141234", "S001", adminHash, 5);
            fakeRepo.save(admin);

            // Inject Fake Repo into Service
            service = new StaffService(fakeRepo);
        }

        /**
         * Tests retrieving all staff members.
         * Verifies that the service correctly fetches data from the repository.
         */
        @Test
        void testGetAllStaff() {
            assertEquals(1, service.getAllStaff().size());
            assertEquals("Admin", service.getAllStaff().get(0).getName());
        }

        /**
         * Tests the creation of a new staff member.
         * Verifies that the new staff is saved to the repository and the password is securely hashed.
         */
        @Test
        void testCreateStaff() {
            service.createStaff("New User", "0198765432", "950505101234", "S002", "UserPass1");

            Staff created = service.getStaffById("S002");
            assertNotNull(created);
            assertEquals("New User", created.getName());
            // Verify password was hashed
            assertNotEquals("UserPass1", created.getPassword());
            assertTrue(PasswordUtil.checkPassword("UserPass1", created.getPassword()));
        }

        /**
         * Tests a successful login attempt.
         * Verifies that correct credentials return the staff object and reset failed attempts.
         */
        @Test
        void testLoginSuccess() {
            Staff s = service.loginStaff("S001", "AdminPass1");
            assertNotNull(s);
            assertEquals("S001", s.getId());
            assertEquals(0, s.getFailedAttempts(), "Failed attempts should reset on success");
        }

        /**
         * Tests a failed login attempt due to wrong password.
         * Verifies that login returns null and the failed attempts counter is incremented.
         */
        @Test
        void testLoginWrongPassword() {
            Staff s = service.loginStaff("S001", "WrongPass");
            assertNull(s);

            Staff actual = service.getStaffById("S001");
            assertEquals(1, actual.getFailedAttempts(), "Failed attempts should increment");
        }

        /**
         * Tests the account lockout security mechanism.
         * Verifies that exceeding the max failed attempts throws a RuntimeException and sets a lock time.
         */
        @Test
        void testAccountLockoutLogic() {
            Staff target = service.getStaffById("S001");

            // Simulate 3 failed attempts
            target.setFailedAttempts(3);

            // 4th failed attempt should trigger lockout exception
            Exception exception = assertThrows(RuntimeException.class, () -> {
                service.loginStaff("S001", "WrongPass");
            });

            // Expecting lowercase 'locked' based on ErrorMessage.java
            assertTrue(exception.getMessage().contains("locked"), "Exception should mention locked");
            assertNotNull(target.getLockTime(), "Lock time should be set");
        }

        /**
         * Tests that a locked account cannot log in even with the correct password.
         * Verifies that the security lock is enforced until the timer expires.
         */
        @Test
        void testLoginWhileLocked() {
            Staff target = service.getStaffById("S001");
            target.setLockTime(LocalDateTime.now().plusMinutes(5)); // Manually lock

            Exception exception = assertThrows(RuntimeException.class, () -> {
                service.loginStaff("S001", "AdminPass1"); // Even correct pass should fail
            });

            assertTrue(exception.getMessage().contains("Account locked"));
        }

        /**
         * Tests the update functionality for staff details.
         * Verifies that specific fields (Name, Password) are updated correctly in the repository.
         */
        @Test
        void testUpdateStaffField() {
            // Update Name
            service.updateStaffField("S001", AppConstants.FIELD_NAME, "Super Admin");
            assertEquals("Super Admin", service.getStaffById("S001").getName());

            // Update Password
            service.updateStaffField("S001", AppConstants.FIELD_PASSWORD, "NewPass99");
            assertTrue(PasswordUtil.checkPassword("NewPass99", service.getStaffById("S001").getPassword()));
        }

        /**
         * Tests the deletion of staff members.
         * Verifies that self-deletion is prevented but valid deletion of other staff succeeds.
         */
        @Test
        void testDeleteStaff() {
            // Create a dummy user to delete
            service.createStaff("To Delete", "0111111111", "888888888888", "S999", "Pass");

            // Try to delete self (S001 deleting S001) - Should Fail
            boolean selfDelete = service.deleteStaff("S001", "S001");
            assertFalse(selfDelete, "Should not be able to delete yourself");

            // Try to delete valid user (S001 deletes S999) - Should Succeed
            boolean validDelete = service.deleteStaff("S999", "S001");
            assertTrue(validDelete, "Should delete successfully");
            assertNull(service.getStaffById("S999"));
        }
    }

    // =========================================================================
    // 4. TEST UTILITY: FAKE REPOSITORY (MOCK OBJECT)
    // =========================================================================
    /**
     * An In-Memory implementation of IStaffRepository.
     * Use this for testing to avoid reading/writing to the actual hard drive.
     */
    static class FakeStaffRepository implements IStaffRepository {

        // Simulates the "File" in memory
        private LinkedHashMap<String, Staff> db = new LinkedHashMap<>();

        // Helper to preload data
        public void save(Staff s) {
            db.put(s.getId(), s);
        }

        @Override
        public LinkedHashMap<String, Staff> getAll() {
            // Return a copy to mimic reading from disk
            return new LinkedHashMap<>(db);
        }

        @Override
        public void saveAll(Collection<Staff> staffList) {
            // Simulates "Overwriting" the file
            db.clear();
            for (Staff s : staffList) {
                db.put(s.getId(), s);
            }
        }
    }
}