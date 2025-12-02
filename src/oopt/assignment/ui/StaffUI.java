package oopt.assignment.ui;

import oopt.assignment.model.Staff;
import oopt.assignment.service.StaffService;
import oopt.assignment.util.AppConstants;
import oopt.assignment.util.ErrorMessage;

import java.util.ArrayList;
import java.util.Arrays;
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
     * @param message   The prompt to display to the user.
     * @param errorMsg  The error message if validation fails.
     * @param validator A functional interface (lambda) that defines valid input rules.
     * @return The valid string input from the user, or null if they chose to exit.
     */
    private String promptForInput(String message, String errorMsg, Predicate<String> validator) {
        String input;
        do {
            System.out.print(message + " (X to exit): ");
            input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("X")) return null;

            if (!validator.test(input)) {
                // Requirement: Use ErrorMessage constant
                System.out.println("Error: " + errorMsg);
            } else {
                return input.toUpperCase();
            }
        } while (true);
    }

    /**
     * Main entry point for the Staff Management UI.
     * @param loggedInStaffId Used to prevent the user from deleting themselves.
     */
    public void start(String loggedInStaffId) {
        while (true) {
            displayStaffLogo();
            printStaffMenu();
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
                case RETURN -> { return; }
            }
        }
    }

    /**
     * Display Staff module Logo
     */
    private void displayStaffLogo() {
        System.out.println("         ================================================");
        System.out.println("         | SSSSS    TTTTTTT    AAA     FFFFFF   FFFFFF  |");
        System.out.println("         | SS         TTT     AA AA    FF       FF      |");
        System.out.println("         | SSSSS      TTT    AAAAAAA   FFFF     FFFF    |");
        System.out.println("         |     SS     TTT    AA   AA   FF       FF      |");
        System.out.println("         | SSSSS      TTT    AA   AA   FF       FF      |");
        System.out.println("         ================================================");
    }

    /**
     * Display Staff Menu
     */
    private void printStaffMenu() {
        System.out.println("\n           --------------------------------------------");
        System.out.println("           |               Staff Menu                 |");
        System.out.println("           --------------------------------------------");
        System.out.println("           |  1. Create New Staff                     |");
        System.out.println("           |  2. Modify Staff Details                 |");
        System.out.println("           |  3. Delete Staff                         |");
        System.out.println("           |  4. Display All Staff                    |");
        System.out.println("           |  5. Search for Staff                     |");
        System.out.println("           |  6. Exit                                 |");
        System.out.println("           --------------------------------------------");
    }

    /**
     * Displays the menu options and parses the user's integer choice.
     * @return user's integer choice mapped to Enum.
     */
    private StaffMenuOption getMenuSelection() {
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
     * Uses promptForInput with Method References for validation.
     */
    private void handleCreateStaff() {
        System.out.println("\n--- Create New Staff ---");

        String name = promptForInput("Enter Name", ErrorMessage.INVALID_NAME, staffService::isNameValid);
        if (name == null) return;

        String cn = promptForInput("Enter Contact No", ErrorMessage.INVALID_CONTACT + " or " + ErrorMessage.DUPLICATE_CONTACT, staffService::isCNValid);
        if (cn == null) return;

        String ic = promptForInput("Enter IC (no dashes)", ErrorMessage.INVALID_IC + " or " + ErrorMessage.DUPLICATE_IC, staffService::isICValid);
        if (ic == null) return;

        String id = promptForInput("Enter ID (e.g., S001)", ErrorMessage.INVALID_ID + " or " + ErrorMessage.DUPLICATE_ID, staffService::isIDValid);
        if (id == null) return;

        String pw = promptForInput("Set Password", ErrorMessage.WEAK_PASSWORD, staffService::isPWValid);
        if (pw == null) return;

        staffService.createStaff(name, cn, ic, id, pw);
        System.out.println("Staff created successfully!");
    }

    /**
     * Handles the modification of existing staff details.
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

        String newValue = null;
        String fieldKey = "";

        // Map Enum choice to AppConstants keys
        switch (choice) {
            case NAME -> {
                newValue = promptForInput("New Name", ErrorMessage.INVALID_NAME, staffService::isNameValid);
                fieldKey = AppConstants.FIELD_NAME;
            }
            case CONTACT -> {
                newValue = promptForInput("New Contact", ErrorMessage.INVALID_CONTACT, staffService::isCNValid);
                fieldKey = AppConstants.FIELD_CONTACT;
            }
            case IC -> {
                newValue = promptForInput("New IC", ErrorMessage.INVALID_IC, staffService::isICValid);
                fieldKey = AppConstants.FIELD_IC;
            }
            case PASSWORD -> {
                newValue = promptForInput("New Password", ErrorMessage.WEAK_PASSWORD, staffService::isPWValid);
                fieldKey = AppConstants.FIELD_PASSWORD;
            }
        }

        if (newValue != null) {
            staffService.updateStaffField(id, fieldKey, newValue);
            System.out.println(choice.getDescription() + " updated successfully.");
        }
    }

    /**
     * Removes a staff member.
     * @param loggedInStaffId staff id
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
        // Requirement: Professional formatting
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("| %-10s | %-30s | %-15s |\n", "ID", "Name", "Bookings");
        System.out.println("-----------------------------------------------------------------");
        // Use stream to loop
        list.forEach(s ->
                System.out.printf("| %-10s | %-30s | %-15d |\n",
                        s.getId(), s.getName(), s.getNoOfBookingHandle())
        );
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Searches for a specific staff member by ID.
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
     * @param service StaffService instance
     * @return Logged in staff ID
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
                // Catches the 'Locked' exception from Service
                System.out.println("Login Failed: " + e.getMessage());
            }
        }
    }
}