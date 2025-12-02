package oopt.assignment;

import oopt.assignment.service.StaffService;
import oopt.assignment.ui.StaffUI;

/**
 * Controller/Bootstrapper for the Staff Management Module.
 * Refactored to accept dependencies rather than creating them,
 * ensuring data consistency across the application.
 */
public class StaffMain {
    /**
     * Entry point for the Staff Management module.
     * @param loggedInStaffId The ID of the currently logged-in user.
     * @param sharedService The shared StaffService instance (must be passed from Main).
     */
    public static void staffMain(String loggedInStaffId, StaffService sharedService) {
        // 1. Create UI (Presentation Layer) - Injecting the Shared Service
        StaffUI ui = new StaffUI(sharedService);

        // 2. Start Staff UI Application
        ui.start(loggedInStaffId);
    }
}