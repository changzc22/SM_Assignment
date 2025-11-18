package oopt.assignment.ui;

import oopt.assignment.model.Staff;
import oopt.assignment.service.StaffService;
import java.util.ArrayList;
import java.util.Scanner;

// This class handles all console input and output for the Staff module.
public class StaffUI {

    private final StaffService staffService;
    private final Scanner scanner;

    public StaffUI(StaffService service) {
        this.staffService = service;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Static method to handle the initial staff login.
     * Called from OoptAssignment (main app).
     */
    public static String handleLogin(StaffService service) {
        Scanner staticScanner = new Scanner(System.in);
        String id;
        while (true) {
            System.out.print("Enter your staff ID [X to Exit]:");
            id = staticScanner.nextLine().toUpperCase();
            if (id.equals("X")) {
                System.out.println("Thank you and have a nice day.");
                System.exit(0);
            }

            System.out.print("Enter your password [Z - forget password] [X to Exit]:");
            String pw = staticScanner.nextLine();

            if (pw.equals("X")) {
                System.out.println("Thank you and have a nice day.");
                System.exit(0);
            } else if (pw.equals("Z") || pw.equals("z")) {
                handleForgotPassword(service, staticScanner);
            } else {
                // --- UPDATED LOGIC FOR LOGIN AND LOCKOUT ---
                try {
                    Staff staff = service.loginStaff(id, pw);
                    if (staff != null) {
                        System.out.println("Welcome " + staff.getName() + "\n");
                        System.out.print("Press 'Enter' key to continue ...");
                        staticScanner.nextLine();
                        return id; // Login successful
                    } else {
                        System.out.println("Invalid Staff ID/Password");
                    }
                } catch (RuntimeException e) {
                    // Catch the specific error message thrown by the service when account is locked
                    System.out.println("Login Failed: " + e.getMessage());
                }
                // -------------------------------------------
            }
        }
    }

    /**
     * Static method to handle the "forgot password" process during login.
     */
    private static void handleForgotPassword(StaffService service, Scanner scanner) {
        while (true) {
            System.out.print("Enter your staff ID to change password (X to return):");
            String id = scanner.nextLine().toUpperCase();
            if (id.equals("X")) {
                return;
            }

            Staff staff = service.getStaffById(id);
            if (staff == null) {
                System.out.println("Staff ID not Found!");
                continue;
            }

            System.out.print("Enter your IC (X to return):");
            String ic = scanner.nextLine();
            if (ic.equals("X")) {
                return;
            }

            // Note: We can keep this check for User Experience (immediate feedback),
            // but the actual security check is now handled inside changePassword().
            if (!staff.getIc().equals(ic)) {
                System.out.println("Invalid IC");
                continue;
            }

            System.out.println("IC correct!");
            String newPw;
            boolean pwValid;
            do {
                System.out.print("Enter your new password here (Password must be >=8 characters with at least 1 alphabet/symbol):");
                newPw = scanner.nextLine();
                pwValid = service.isPWValid(newPw);

                if (pwValid) {
                    // --- UPDATED: Utilizing changePassword() ---
                    // We pass ID, IC, and New Password. The Service layer verifies them and updates.
                    boolean success = service.changePassword(id, ic, newPw);

                    if (success) {
                        System.out.println("Password updated successfully.");
                        return; // Success
                    } else {
                        // This happens if the ID/IC check failed inside the service
                        System.out.println("Error: Verification failed. Password not updated.");
                    }
                } else {
                    System.out.println("Invalid Password.");
                }
            } while (!pwValid);
        }
    }

    public void start(String loggedInStaffId) {
        boolean exitStaffMenu = false;
        while (!exitStaffMenu) {
            showStaffMenu();
            int choice = getMenuChoice();
            switch (choice) {
                case 1:
                    handleCreateStaff();
                    break;
                case 2:
                    handleModifyStaff();
                    break;
                case 3:
                    handleDeleteStaff(loggedInStaffId);
                    break;
                case 4:
                    handleDisplayAllStaff();
                    break;
                case 5:
                    handleSearchStaff();
                    break;
                case 6:
                    exitStaffMenu = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
            }
        }
    }

    private void showStaffMenu() {
        System.out.println("\n--- Staff Management Menu ---");
        System.out.println("1. Create New Staff");
        System.out.println("2. Modify Staff Details");
        System.out.println("3. Delete Staff");
        System.out.println("4. Display All Staff");
        System.out.println("5. Search for Staff");
        System.out.println("6. Return to Main Menu");
        System.out.print("Your choice: ");
    }

    private int getMenuChoice() {
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // Invalid choice
        }
    }

    private void handleCreateStaff() {
        System.out.println("\n--- Create New Staff ---");
        String name, cn, ic, id, pw;

        do {
            System.out.print("Enter the staff name (X to exit): ");
            name = scanner.nextLine().toUpperCase();
            if (name.equals("X")) return;
            if (!staffService.isNameValid(name)) {
                System.out.println("Invalid name! Please try again.");
            }
        } while (!staffService.isNameValid(name));

        do {
            System.out.print("Enter your contact number (X to exit): ");
            cn = scanner.nextLine();
            if (cn.equalsIgnoreCase("X")) return;
            if (!staffService.isCNValid(cn)) {
                System.out.println("Invalid/duplicate contact number! Please try again.");
            }
        } while (!staffService.isCNValid(cn));

        do {
            System.out.print("Enter your IC number (X to exit/without -): ");
            ic = scanner.nextLine();
            if (ic.equalsIgnoreCase("X")) return;
            if (!staffService.isICValid(ic)) {
                System.out.println("Invalid/duplicate IC number! Please try again.");
            }
        } while (!staffService.isICValid(ic));

        do {
            System.out.print("Enter your ID (S001 format/X to exit): ");
            id = scanner.nextLine().toUpperCase();
            if (id.equals("X")) return;
            if (!staffService.isIDValid(id)) {
                System.out.println("Invalid/duplicate Staff ID! Please try again.");
            }
        } while (!staffService.isIDValid(id));

        do {
            System.out.print("Please set your password (Password must be >=8 characters with at least 1 alphabet/symbol)/(X to exit): ");
            pw = scanner.nextLine();
            if (pw.equalsIgnoreCase("X")) return;
            if (!staffService.isPWValid(pw)) {
                System.out.println("Invalid password! Please try again.");
            }
        } while (!staffService.isPWValid(pw));

        staffService.createStaff(name, cn, ic, id, pw);
        System.out.print("Staff added! Press 'enter' key to exit to staff menu....");
        scanner.nextLine();
    }

    private void handleModifyStaff() {
        System.out.println("\n--- Modify Staff Details ---");
        while (true) {
            System.out.print("Enter staff ID (X to return): ");
            String id = scanner.nextLine().toUpperCase();
            if (id.equals("X")) return;

            Staff staff = staffService.getStaffById(id);
            if (staff == null) {
                System.out.println("Invalid staff ID!");
                continue;
            }

            System.out.print("""
                    Enter the option that you want to modify\s
                    A)Name B)Contact Number C)IC Number D)Password E)Exit
                    Your choice:\s""");
            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "A":
                    modifyStaffName(id);
                    return;
                case "B":
                    modifyStaffContact(id);
                    return;
                case "C":
                    modifyStaffIC(id);
                    return;
                case "D":
                    modifyStaffPassword(id);
                    return;
                case "E":
                    return;
                default:
                    System.out.println("Invalid input!");
            }
        }
    }

    private void modifyStaffName(String id) {
        String name;
        do {
            System.out.print("Update staff name (X to exit): ");
            name = scanner.nextLine().toUpperCase();
            if (name.equals("X")) return;
            if (!staffService.isNameValid(name)) {
                System.out.println("Invalid name! Please try again.");
            }
        } while (!staffService.isNameValid(name));
        staffService.updateStaffName(id, name);
        System.out.println("Name updated successfully.");
    }

    private void modifyStaffContact(String id) {
        String cn;
        do {
            System.out.print("Modify your contact number (X to exit): ");
            cn = scanner.nextLine();
            if (cn.equalsIgnoreCase("X")) return;
            if (!staffService.isCNValid(cn)) {
                System.out.println("Invalid/duplicate contact number! Please try again.");
            }
        } while (!staffService.isCNValid(cn));
        staffService.updateStaffContact(id, cn);
        System.out.println("Contact number updated successfully.");
    }

    private void modifyStaffIC(String id) {
        String ic;
        do {
            System.out.print("Update IC number (X to exit/without -): ");
            ic = scanner.nextLine();
            if (ic.equalsIgnoreCase("X")) return;
            if (!staffService.isICValid(ic)) {
                System.out.println("Invalid/duplicate IC number! Please try again.");
            }
        } while (!staffService.isICValid(ic));
        staffService.updateStaffIC(id, ic);
        System.out.println("IC number updated successfully.");
    }

    private void modifyStaffPassword(String id) {
        String pw;
        do {
            System.out.print("Please set password (Password must be >=8 characters with at least 1 alphabet/symbol) (X to exit): ");
            pw = scanner.nextLine();
            if (pw.equalsIgnoreCase("X")) return;
            if (!staffService.isPWValid(pw)) {
                System.out.println("Invalid password! Please try again.");
            }
        } while (!staffService.isPWValid(pw));
        staffService.updateStaffPassword(id, pw);
        System.out.println("Password updated successfully.");
    }


    private void handleDeleteStaff(String loggedInStaffId) {
        System.out.println("\n--- Delete Staff ---");
        System.out.println("Mention: You are not allowed to delete yourself!");
        ArrayList<Staff> staffList = staffService.getAllStaff();

        if (staffList.isEmpty()) {
            System.out.println("No staff members to delete.");
            System.out.print("Press 'Enter' key to exit to staff menu....");
            scanner.nextLine();
            return;
        }

        while (true) {
            System.out.print("Please type the Staff ID to remove staff (Press X to exit):");
            String idToDelete = scanner.nextLine().toUpperCase();
            if (idToDelete.equals("X")) return;

            if (idToDelete.equals(loggedInStaffId)) {
                System.out.println("You cannot delete yourself! Please try again.");
                continue;
            }

            Staff staff = staffService.getStaffById(idToDelete);
            if (staff == null) {
                System.out.println("Staff ID not found, please try again.");
                continue;
            }

            System.out.println(staff + "\n");
            System.out.print("Do you sure you want to delete this Staff?(Y/N):");
            String confirm = scanner.nextLine().toUpperCase();

            if (confirm.equals("Y")) {
                if (staffService.deleteStaff(idToDelete, loggedInStaffId)) {
                    System.out.println("Staff deleted.");
                } else {
                    System.out.println("Error deleting staff (maybe you tried to delete yourself).");
                }
                return;
            } else if (confirm.equals("N")) {
                System.out.println("Deletion canceled.");
                return;
            } else {
                System.out.println("Invalid input! Please enter 'Y' or 'N'.");
            }
        }
    }

    // --- UPDATED TABLE DISPLAY METHOD ---
    private void handleDisplayAllStaff() {
        System.out.println("\n--- List of Staff Members ---");
        ArrayList<Staff> staffList = staffService.getAllStaff();

        if (staffList.isEmpty()) {
            System.out.println("No staff members to display.");
        } else {
            // Print Table Header
            System.out.println("-------------------------------------------------------------------------------------------------------");
            System.out.printf("| %-10s | %-30s | %-15s | %-15s | %-17s |\n",
                    "ID", "Name", "Contact No", "IC Number", "Bookings Handled");
            System.out.println("-------------------------------------------------------------------------------------------------------");

            // Print Table Rows
            for (Staff s : staffList) {
                System.out.printf("| %-10s | %-30s | %-15s | %-15s | %-17d |\n",
                        s.getId(),
                        s.getName(),
                        s.getContactNo(),
                        s.getIc(),
                        s.getNoOfBookingHandle());
            }
            System.out.println("-------------------------------------------------------------------------------------------------------");
        }
        System.out.print("Press 'Enter' to exit to staff menu....");
        scanner.nextLine();
    }
    // ------------------------------------

    private void handleSearchStaff() {
        System.out.println("\n--- Search for Staff ---");
        while (true) {
            System.out.print("Enter the staff ID that you want to search (X to exit):");
            String id = scanner.nextLine().toUpperCase();
            if (id.equals("X")) return;

            Staff staff = staffService.getStaffById(id);
            if (staff != null) {
                System.out.println("\n" + staff + "\n");
                System.out.print("Press 'Enter' key to exit to staff menu....");
                scanner.nextLine();
                return;
            } else {
                System.out.println("Staff not found!");
            }
        }
    }
}