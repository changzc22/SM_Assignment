package test.ui;

import ets.model.IStaffRepository;
import ets.model.Staff;
import ets.service.StaffService;
import ets.ui.StaffUI;
import ets.util.PasswordUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Staff UI Interaction Tests")
public class StaffUITest {

    @Test
    void testLoginInputFlow() {
        StaffService mockService = new StaffService(new MockRepo());
        String simulatedInput = "S001\nPass123\n";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);

        String resultId = StaffUI.handleLogin(mockService);
        assertEquals("S001", resultId);
    }

    /**
     * Comprehensive test for the Main Menu and Create Staff flow.
     * Simulates:
     * 1. Selecting '1' (Create Staff)
     * 2. Entering an INVALID name (triggers validation error loop)
     * 3. Entering a VALID name
     * 4. Entering valid Contact, IC, ID, Password
     * 5. Selecting '6' (Return) to exit the loop.
     */
    @Test
    void testMainFlowWithInvalidInputs() {
        // 1. Build the input stream FIRST
        StringBuilder input = new StringBuilder();

        // --- Step 1: Menu Selection (Create) ---
        input.append("1\n");

        // --- Step 2: Input Name (Invalid then Valid) ---
        input.append("InvalidName123\n"); // Triggers "Error: Invalid Name"
        input.append("Valid Name\n");     // Accepted

        // --- Step 3: Input Contact ---
        input.append("0123456789\n");

        // --- Step 4: Input IC ---
        input.append("990101015555\n");

        // --- Step 5: Input ID ---
        input.append("S002\n");

        // --- Step 6: Input Password ---
        input.append("Password123\n");

        // --- Step 7: Back to Menu -> Select '6' (Return) ---
        input.append("6\n");

        // 2. Inject Input BEFORE creating StaffUI
        // This ensures the Scanner inside StaffUI captures OUR stream, not the keyboard.
        InputStream in = new ByteArrayInputStream(input.toString().getBytes());
        System.setIn(in);

        // 3. Setup Service and UI
        StaffService mockService = new StaffService(new MockRepo());
        StaffUI ui = new StaffUI(mockService);

        // 4. Run the UI (Blocking call until '6' is selected)
        assertDoesNotThrow(() -> ui.start("S001"));

        // 5. Verify Side Effect: S002 should have been created in the repo
        assertNotNull(mockService.getStaffById("S002"), "New staff S002 should be created");
    }

    // Simple mock repository
    static class MockRepo implements IStaffRepository {
        private LinkedHashMap<String, Staff> db = new LinkedHashMap<>();

        public MockRepo() {
            String hash = PasswordUtil.hashPassword("Pass123");
            db.put("S001", new Staff("Test", "012", "999", "S001", hash, 0));
        }

        public LinkedHashMap<String, Staff> getAll() {
            return new LinkedHashMap<>(db);
        }
        public void saveAll(Collection<Staff> staffList) {
            for (Staff s : staffList) db.put(s.getId(), s);
        }
    }
}