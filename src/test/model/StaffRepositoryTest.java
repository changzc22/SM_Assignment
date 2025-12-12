package test.model;

import ets.model.Staff;
import ets.model.StaffRepository;
import ets.util.AppConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Staff Repository I/O Tests")
public class StaffRepositoryTest {

    private StaffRepository repository;
    private final File dataFile = new File(AppConstants.STAFF_FILE_PATH);
    private final File backupFile = new File(AppConstants.STAFF_FILE_PATH + ".bak");

    /**
     * Setup method to prepare the test environment.
     * Backs up the actual "StaffFile.txt" to prevent data loss during testing.
     * Initializes a fresh repository instance.
     */
    @BeforeEach
    void setup() {
        // REFACTOR: Removed "if (exists)" check to improve branch coverage.
        // renameTo() returns false if dataFile is missing, which is safe to ignore here.
        dataFile.renameTo(backupFile);

        repository = new StaffRepository();
    }

    /**
     * Cleanup method to restore the environment after tests.
     * Deletes the test data and restores the original "StaffFile.txt".
     */
    @AfterEach
    void cleanup() {
        // REFACTOR: Removed "if (exists)" checks.
        // delete() returns false if file is missing (safe).
        dataFile.delete();

        // renameTo() returns false if backupFile is missing (safe).
        backupFile.renameTo(dataFile);
    }

    /**
     * BRANCH TEST: File Creation
     * Scenario: The data file does not exist.
     * Action: Call getAll().
     * Expected: The code should enter the 'if (!file.exists())' branch and create a new file.
     */
    @Test
    void testFileCreationOnMissing() {
        // Ensure file is definitely gone before test
        // REFACTOR: Removed "if (exists)" check.
        dataFile.delete();

        // This triggers the branch: if (!file.exists()) inside Repository
        LinkedHashMap<String, Staff> result = repository.getAll();

        assertTrue(result.isEmpty(), "New repository should be empty");
        assertTrue(dataFile.exists(), "File should be automatically created if missing");
    }

    /**
     * HAPPY PATH TEST: Save and Load
     * Scenario: Normal operation.
     * Action: Save valid data and read it back.
     * Expected: Data integrity is maintained.
     */
    @Test
    void testSaveAndLoad() {
        ArrayList<Staff> list = new ArrayList<>();
        list.add(new Staff("TestUser", "0123456789", "990101015555", "S001", "Hash", 0));

        repository.saveAll(list);

        LinkedHashMap<String, Staff> loaded = repository.getAll();

        assertTrue(loaded.containsKey("S001"));
        assertEquals("TestUser", loaded.get("S001").getName());
    }

    /**
     * BRANCH TEST: Edge Cases (Empty Lines & Corruption)
     * Scenario: The file contains blank lines and garbage data.
     * Action: Call getAll().
     * Expected:
     * 1. 'if (line.trim().isEmpty())' branch is triggered (skips blank lines).
     * 2. 'catch' block in parseLineToStaff is triggered (returns null).
     * 3. 'if (staff != null)' branch is evaluated as false.
     */
    @Test
    void testLoadEdgeCases() throws IOException {
        // 1. Manually write a file with specific messy content
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            // Valid Line
            writer.write("ValidUser|01234|999|S001|PassHash|0");
            writer.newLine();

            // Branch: Empty Line -> if (line.trim().isEmpty())
            writer.write("");
            writer.newLine();

            // Branch: Whitespace Line -> if (line.trim().isEmpty())
            writer.write("   ");
            writer.newLine();

            // Branch: Corrupted Data -> returns null -> if (staff != null) is false
            writer.write("This|Line|Is|Garbage");
            writer.newLine();
        }

        // 2. Load the messy file
        LinkedHashMap<String, Staff> loaded = repository.getAll();

        // 3. Verify resilience
        assertEquals(1, loaded.size(), "Should only load the 1 valid staff member");
        assertTrue(loaded.containsKey("S001"), "Valid staff S001 should be present");
    }
}