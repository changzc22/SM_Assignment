package test.service;

import ets.model.IStaffRepository;
import ets.model.Staff;
import ets.service.StaffService;
import ets.util.AppConstants;
import ets.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Staff Service Logic Tests")
public class StaffServiceTest {

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

    // =========================================================================
    // TEST HELPER: FAKE REPOSITORY
    // =========================================================================
    /**
     * An In-Memory implementation of IStaffRepository.
     * Use this for testing to avoid reading/writing to the actual hard drive.
     */
    static class FakeStaffRepository implements IStaffRepository {

        // Simulates the "File" in memory
        private LinkedHashMap<String, Staff> db = new LinkedHashMap<>();

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
