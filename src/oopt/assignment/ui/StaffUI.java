package oopt.assignment.ui;

import oopt.assignment.model.Staff;
import oopt.assignment.service.StaffService;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Handles all console interactions for Staff Management.
 * Delegates business logic to StaffService.
 */
public class StaffUI {

    private final StaffService staffService;
    private final Scanner scanner;

    public StaffUI(StaffService service) {
        this.staffService = service;
        this.scanner = new Scanner(System.in);
    }

    /**
     * A generic helper method to handle user input loops.
     * This implements the DRY (Don't Repeat Yourself) principle by centralizing
     * the "Ask -> Validate -> Retry" logic used across multiple methods.
     *
     * @param message   The prompt to display to the user.
     * @param errorMsg  The error message if validation fails.
     * @param validator A functional interface (lambda) that defines valid input rules.
     * @return The valid string input from the user, or null if they chose to exit.
     */
    // --- Helper for DRY Input ---
    private String promptForInput(String message, String errorMsg, Predicate<String> validator) {
        String input;
        do {
            System.out.print(message + " (X to exit): ");
            input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("X")) return null;

            if (!validator.test(input)) {
                System.out.println(errorMsg);
            } else {
                return input.toUpperCase();
            }
        } while (true);
    }

    /**
     * Main entry point for the Staff Management UI.
     * Loops until the user chooses to return to the main menu.
     * @param loggedInStaffId Used to prevent the user from deleting themselves.
     */
    public void start(String loggedInStaffId) {
        while (true) {
            StaffMenuOption option = getMenuSelection();
            if (option == null) {
                System.out.println("Invalid choice. Please try again.");
                continue;
            }

            switch (option) {
                case CREATE -> handleCreateStaff();
                case MODIFY -> handleModifyStaff();
                case DELETE -> handleDeleteStaff(loggedInStaffId);
                case DISPLAY_ALL -> handleDisplayAllStaff();
                case SEARCH -> handleSearchStaff();
                case RETURN -> { return; } // Exit the method
            }
        }
    }

    /**
     * Displays the menu options and parses the user's integer choice.
     */
    private StaffMenuOption getMenuSelection() {
        System.out.println("\n--- Staff Management Menu ---");
        for (StaffMenuOption opt : StaffMenuOption.values()) {
            System.out.println(opt.getId() + ". " + opt.getDescription());
        }
        System.out.print("Your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            return StaffMenuOption.fromId(choice);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Orchestrates the creation of a new staff member.
     * Uses promptForInput to ensure all data is valid before calling the Service.
     */
    private void handleCreateStaff() {
        System.out.println("\n--- Create New Staff ---");

        String name = promptForInput("Enter Name", "Invalid Name", staffService::isNameValid);
        if (name == null) return;

        String cn = promptForInput("Enter Contact No", "Invalid/Duplicate Contact", staffService::isCNValid);
        if (cn == null) return;

        String ic = promptForInput("Enter IC (no dashes)", "Invalid/Duplicate IC", staffService::isICValid);
        if (ic == null) return;

        String id = promptForInput("Enter ID (e.g., S001)", "Invalid/Duplicate ID", staffService::isIDValid);
        if (id == null) return;

        String pw = promptForInput("Set Password", "Weak Password", staffService::isPWValid);
        if (pw == null) return;

        staffService.createStaff(name, cn, ic, id, pw);
        System.out.println("Staff created successfully!");
    }

    /**
     * Handles the modification of existing staff details.
     * Uses ModifyStaffOption Enum to handle sub-menu selection.
     */
    private void handleModifyStaff() {
        System.out.println("\n--- Modify Staff Details ---");
        System.out.print("Enter Staff ID to modify (X to return): ");
        String id = scanner.nextLine().toUpperCase();
        if (id.equals("X")) return;

        Staff staff = staffService.getStaffById(id);
        if (staff == null) {
            System.out.println("Staff not found.");
            return;
        }

        // Display Modify Options
        System.out.println("Select option to modify:");
        for (ModifyStaffOption opt : ModifyStaffOption.values()) {
            System.out.println(opt.getCode() + ") " + opt.getDescription());
        }
        System.out.print("Your choice: ");

        String input = scanner.nextLine();
        ModifyStaffOption choice = ModifyStaffOption.fromCode(input);

        if (choice == null) {
            System.out.println("Invalid option.");
            return;
        }

        String newValue;
        switch (choice) {
            case NAME -> {
                newValue = promptForInput("New Name", "Invalid Name", staffService::isNameValid);
                if (newValue != null) staffService.updateStaffField(id, "NAME", newValue);
            }
            case CONTACT -> {
                newValue = promptForInput("New Contact", "Invalid Contact", staffService::isCNValid);
                if (newValue != null) staffService.updateStaffField(id, "CONTACT", newValue);
            }
            case IC -> {
                newValue = promptForInput("New IC", "Invalid IC", staffService::isICValid);
                if (newValue != null) staffService.updateStaffField(id, "IC", newValue);
            }
            case PASSWORD -> {
                newValue = promptForInput("New Password", "Weak Password", staffService::isPWValid);
                if (newValue != null) staffService.updateStaffField(id, "PASSWORD", newValue);
            }
        }
        System.out.println(choice.getDescription() + " updated successfully.");
    }

    /**
     * Removes a staff member, ensuring the current user cannot delete themselves.
     */
    private void handleDeleteStaff(String loggedInStaffId) {
        System.out.print("Enter Staff ID to delete (X to exit): ");
        String id = scanner.nextLine().toUpperCase();
        if (id.equals("X")) return;

        if (staffService.deleteStaff(id, loggedInStaffId)) {
            System.out.println("Staff deleted.");
        } else {
            System.out.println("Deletion failed (Staff not found or you tried to delete yourself).");
        }
    }

    /**
     * Displays a formatted table of all staff members.
     */
    private void handleDisplayAllStaff() {
        ArrayList<Staff> list = staffService.getAllStaff();
        if (list.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("| %-10s | %-30s | %-15s |\n", "ID", "Name", "Bookings");
        for (Staff s : list) {
            System.out.printf("| %-10s | %-30s | %-15d |\n", s.getId(), s.getName(), s.getNoOfBookingHandle());
        }
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Searches for a specific staff member by ID and prints their details.
     */
    private void handleSearchStaff() {
        System.out.print("Enter ID to search: ");
        String id = scanner.nextLine().toUpperCase();
        Staff s = staffService.getStaffById(id);
        if (s != null) System.out.println(s);
        else System.out.println("Not found.");
    }

    /**
     * Static method to handle initial application login.
     * Catches security exceptions (e.g., Lockout) thrown by the Service.
     */
    public static String handleLogin(StaffService service) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("Staff ID (X to Exit): ");
            String id = sc.nextLine().toUpperCase();
            if (id.equals("X")) System.exit(0);

            System.out.print("Password: ");
            String pw = sc.nextLine();

            try {
                Staff staff = service.loginStaff(id, pw);
                if (staff != null) return id;
                System.out.println("Invalid ID or Password.");
            } catch (RuntimeException e) {
                System.out.println("Login Failed: " + e.getMessage());
            }
        }
    }
}