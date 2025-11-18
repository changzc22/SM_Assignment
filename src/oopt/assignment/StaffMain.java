package oopt.assignment;

import oopt.assignment.model.StaffRepository;
import oopt.assignment.service.StaffService;
import oopt.assignment.ui.StaffUI;

public class StaffMain {

    /**
     * Entry point for the Staff Management module.
     * Sets up the necessary layers and starts the Staff UI.
     * @param staffID The ID of the staff member who is logged in.
     */
    public static void staffMain(String staffID) {
        // 1. Create Repository (Data Layer)
        StaffRepository repo = new StaffRepository();

        // 2. Create Service (Business Layer) - Injects Repository
        StaffService service = new StaffService(repo);

        // 3. Create UI (Presentation Layer) - Injects Service
        StaffUI ui = new StaffUI(service);

        // 4. Start Staff UI Application
        ui.start(staffID);
    }
}