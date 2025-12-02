package test.service;

import oopt.assignment.model.Staff;
import oopt.assignment.model.StaffRepository;
import oopt.assignment.service.StaffService;
import oopt.assignment.util.AppConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Staff Service Integration Tests (Real File I/O)")
public class StaffServiceIntegrationTest {

    private StaffService service;
    private final File dataFile = new File(AppConstants.STAFF_FILE_PATH);
    private final File backupFile = new File(AppConstants.STAFF_FILE_PATH + ".bak");

    /**
     * Setup:
     * 1. Backup any existing real data file to avoid corrupting it.
     * 2. Initialize the Service with a REAL Repository (not a fake/mock).
     * 3. Ensure we start with a clean state (empty file) for predictable tests.
     */
    @BeforeEach
    void setup() {
        // REFACTOR: Removed "if (exists)" check to ensure 100% branch coverage.
        // renameTo() returns false if dataFile is missing, which is safe to ignore here.
        dataFile.renameTo(backupFile);

        // Initialize REAL repository logic (writing to disk)
        StaffRepository realRepo = new StaffRepository();

        // Initialize REAL service with REAL repository
        service = new StaffService(realRepo);
    }

    /**
     * Cleanup:
     * 1. Delete the test data file created during the test.
     * 2. Restore the original data file from backup.
     */
    @AfterEach
    void cleanup() {
        // REFACTOR: Removed "if (exists)" checks to ensure 100% branch coverage.
        // delete() returns false if file is missing (safe).
        dataFile.delete();

        // renameTo() returns false if backupFile is missing (safe).
        backupFile.renameTo(dataFile);
    }

    /**
     * Integration Scenario: Full Lifecycle
     * 1. Create a staff member via Service.
     * 2. Restart the Service (simulate app restart) to force reloading from disk.
     * 3. Verify the staff member still exists and can log in.
     * * Value: Proves that data persistence works across application restarts.
     */
    @Test
    void testCreatePersistAndLogin() {
        // 1. Create User
        service.createStaff("Integration User", "0123456789", "990101015555", "S999", "Pass123");

        // 2. Simulate App Restart (Re-initialize Service/Repo)
        // This forces the Repository to read from the actual "StaffFile.txt" again.
        StaffRepository newRepoInstance = new StaffRepository();
        StaffService newServiceInstance = new StaffService(newRepoInstance);

        // 3. Verify Persistence
        Staff loadedStaff = newServiceInstance.getStaffById("S999");
        assertNotNull(loadedStaff, "Staff should exist after reloading from file");
        assertEquals("Integration User", loadedStaff.getName());

        // 4. Verify Login Logic on Reloaded Data
        Staff loggedIn = newServiceInstance.loginStaff("S999", "Pass123");
        assertNotNull(loggedIn, "Should be able to login with original password");
    }

    /**
     * Integration Scenario: Modification Persistence
     * 1. Create a user.
     * 2. Modify their password via Service.
     * 3. Restart Service.
     * 4. Verify OLD password fails and NEW password works.
     * * Value: Proves that updates are committed to disk immediately.
     */
    @Test
    void testModifyPasswordPersistence() {
        // 1. Create
        service.createStaff("User Mod", "0111222333", "880808088888", "S888", "OldPass");

        // 2. Modify
        service.updateStaffField("S888", AppConstants.FIELD_PASSWORD, "NewPass");

        // 3. Restart
        StaffService restartedService = new StaffService(new StaffRepository());

        // 4. Verify
        assertNull(restartedService.loginStaff("S888", "OldPass"), "Old password should fail");
        assertNotNull(restartedService.loginStaff("S888", "NewPass"), "New password should succeed");
    }

    /**
     * Integration Scenario: Data Integrity on Bulk Operations
     * 1. Create multiple users.
     * 2. Verify all are present in the file.
     * * Value: Ensures the file writing loop handles multiple records correctly without overwriting/corruption.
     */
    @Test
    void testBulkCreationPersistence() {
        service.createStaff("User A", "0111111111", "111111111111", "S001", "Pass");
        service.createStaff("User B", "0122222222", "222222222222", "S002", "Pass");
        service.createStaff("User C", "0133333333", "333333333333", "S003", "Pass");

        // Restart
        StaffService restartedService = new StaffService(new StaffRepository());
        ArrayList<Staff> allStaff = restartedService.getAllStaff();

        assertEquals(3, allStaff.size(), "Should have 3 records persisted");
        assertNotNull(restartedService.getStaffById("S001"));
        assertNotNull(restartedService.getStaffById("S002"));
        assertNotNull(restartedService.getStaffById("S003"));
    }
}