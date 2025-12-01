package oopt.assignment.ui;

import oopt.assignment.model.Passenger;
import oopt.assignment.service.PassengerService;

import java.util.Collection;
import java.util.Optional;
import java.util.Scanner;

/**
 * PassengerUI - Console-based user interface for the passenger module
 */
public class PassengerUI {

    private final PassengerService service;
    private final Scanner scanner;

    public PassengerUI(PassengerService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    /**
     * Display Passenger module's menu (Switch)
     */
    public void showMenu() {
        int selection;

        do {
            printHeader();
            selection = readInt("\nEnter a choice > ", 1, 6);

            switch (selection) {
                case 1 -> handleNewPassenger();
                case 2 -> handleSearchPassenger();
                case 3 -> handleEditPassenger();
                case 4 -> handleDisplayAll();
                case 5 -> handleChangeTier();
                case 6 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid input!");
            }

            if (selection != 6) {
                System.out.println();
                System.out.print("Press Enter to continue...");
                scanner.nextLine();
            }

        } while (selection != 6);
    }

    /**
     * Display Passenger module's menu (UI)
     */
    private void printHeader() {
        System.out.println();
        System.out.println("--- Passenger Management Menu ---");
        System.out.println("1. New Passenger Registration");
        System.out.println("2. Search Passenger");
        System.out.println("3. Edit Passenger Details");
        System.out.println("4. Display All Passengers Information");
        System.out.println("5. Upgrade/Downgrade Passenger Tier");
        System.out.println("6. Back To Main Menu");
    }

    /**
     *
     * @param prompt User input number
     * @param min Minimum option number
     * @param max Maximum option number
     * @return option number if valid
     */
    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Invalid input. Please enter between " + min + " and " + max + ".");
            } catch (NumberFormatException ex) {
                System.out.println("Enter digits only!");
            }
        }
    }

    /**
     * UI for New Passenger Registration
     */
    private void handleNewPassenger() {
        System.out.println("=== New Passenger Registration ===");

        String name    = readValidName();
        String contact = readValidContact();
        String ic      = readValidIc();
        char gender    = readValidGender();

        System.out.println("\nYou have entered the following details:");
        System.out.println("Name           : " + name);
        System.out.println("Contact Number : " + contact);
        System.out.println("IC Number      : " + ic);
        System.out.println("Gender         : " + gender);

        System.out.print("\nConfirm to add this passenger? (Y/N) > ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (!confirm.equals("Y")) {
            System.out.println("Registration cancelled. No passenger was added.");
            return;
        }

        try {
            Passenger created = service.registerNewPassenger(name, contact, ic, gender);
            System.out.println("Passenger registered successfully with ID: " + created.getId());
        } catch (IllegalArgumentException ex) {
            // This is a final safeguard in case service-level validation fails
            System.out.println("Failed to register passenger: " + ex.getMessage());
        }
    }

    /**
     * Handle UI for entering name
     * @return Passenger's name
     */
    private String readValidName() {
        while (true) {
            System.out.print("(1/4) Enter name           > ");
            String name = scanner.nextLine().trim();

            if (name.isBlank()) {
                System.out.println("Name cannot be empty. Please try again.");
            } else {
                return name;
            }
        }
    }

    /**
     * Handle UI for entering valid contact numver
     * @return Passenger's contact number/phone number
     */
    private String readValidContact() {
        while (true) {
            System.out.print("(2/4) Enter contact number > ");
            String contact = scanner.nextLine().trim();

            if (contact.isBlank()) {
                System.out.println("Contact number cannot be empty. Please try again.");
            } else if (!contact.matches("\\d{9,15}")) {
                System.out.println("Contact number must be 9–15 digits. Please try again.");
            } else {
                return contact;
            }
        }
    }

    /**
     * Handle UI for entering valid ic
     * @return Passenger's identity card number
     */
    private String readValidIc() {
        while (true) {
            System.out.print("(3/4) Enter IC number      > ");
            String ic = scanner.nextLine().trim();

            if (ic.isBlank()) {
                System.out.println("IC number cannot be empty. Please try again.");
            } else if (!ic.matches("\\d{12}")) {
                System.out.println("IC number must be exactly 12 digits. Please try again.");
            } else {
                return ic;
            }
        }
    }

    /**
     * Handle UI for entering valid gender
     * @return Passenger's gender
     */
    private char readValidGender() {
        while (true) {
            System.out.print("(4/4) Enter gender (M - Male | F - Female) > ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.isBlank()) {
                System.out.println("Gender cannot be empty. Please try again.");
                continue;
            }

            char gender = input.charAt(0);
            if (gender == 'M' || gender == 'F') {
                return gender;
            } else {
                System.out.println("Gender must be M (Male) or F (Female). Please try again.");
            }
        }
    }


    /**
     * UI for Search Passenger
     */
    private void handleSearchPassenger() {
        System.out.print("Enter passenger ID: ");
        String id = scanner.nextLine();
        Optional<Passenger> result = service.findById(id);

        if (result.isPresent()) {
            System.out.println("Passenger found:");
            System.out.println(result.get());
        } else {
            System.out.println("Passenger not found.");
        }
    }

    /**
     * UI for Edit Passenger Details
     */
    private void handleEditPassenger() {
        System.out.print("Enter passenger ID to edit: ");
        String id = scanner.nextLine();
        Optional<Passenger> result = service.findById(id);

        if (result.isEmpty()) {
            System.out.println("Passenger not found.");
            return;
        }

        Passenger existing = result.get();

        boolean done = false;
        while (!done) {
            System.out.println("\nCurrent details:");
            System.out.println(existing);

            System.out.println("\nWhich field would you like to edit?");
            System.out.println("1. Name");
            System.out.println("2. Contact number");
            System.out.println("3. IC");
            System.out.println("4. Gender");
            System.out.println("5. Save changes and return");
            System.out.println("6. Cancel without saving");

            int choice = readInt("Your choice > ", 1, 7);

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter new name           > ");
                    String newName = scanner.nextLine();
                    existing.setName(newName);
                }
                case 2 -> {
                    System.out.print("Enter new contact number > ");
                    String newContact = scanner.nextLine();
                    existing.setContactNo(newContact);
                }
                case 3 -> {
                    System.out.print("Enter new IC             > ");
                    String newIc = scanner.nextLine();
                    existing.setIc(newIc);
                }
                case 4 -> {
                    System.out.print("Enter new gender (M - Male | F - Female) > ");
                    String genderInput = scanner.nextLine();
                    if (!genderInput.isBlank()) {
                        existing.setGender(genderInput.toUpperCase().charAt(0));
                    }
                }
                case 5 -> {
                    try {
                        service.updatePassenger(existing);
                        System.out.println("Passenger updated successfully.");
                    } catch (IllegalArgumentException ex) {
                        System.out.println("Failed to update passenger: " + ex.getMessage());
                    }
                    done = true;
                }
                case 6 -> {
                    System.out.println("No changes saved.");
                    done = true;
                }
            }
        }
    }

    /**
     * UI for Display All Passengers Information
     */
    private void handleDisplayAll() {
        Collection<Passenger> passengers = service.getAllPassengers();

        if (passengers.isEmpty()) {
            System.out.println("No passengers found.");
            System.out.println("Total passengers registered: 0");
            return;
        }

        String border = "+--------+----------------------+--------------+--------------+--------+--------------+------+\n";
        String headerFormat = "| %-6s | %-20s | %-12s | %-12s | %-6s | %-12s | %-4s |\n";
        String rowFormat    = "| %-6s | %-20s | %-12s | %-12s | %-6s | %-12s | %-4s |\n";

        System.out.print(border);
        System.out.printf(headerFormat,
                "ID", "Name", "Contact No", "IC", "Gender", "Joined Date", "Tier");
        System.out.print(border);

        for (Passenger p : passengers) {
            System.out.printf(rowFormat,
                    p.getId(),
                    p.getName(),
                    p.getContactNo(),
                    p.getIc(),
                    String.valueOf(p.getGender()),
                    p.getDateJoined(),
                    String.valueOf(p.getPassengerTier())
            );
        }

        System.out.print(border);
        System.out.println("Total passengers registered: " + passengers.size());
    }


    /**
     * UI for Upgrade/Downgrade Passenger Tier
     */
    private void handleChangeTier() {
        System.out.print("Enter passenger ID: ");
        String id = scanner.nextLine();

        Optional<Passenger> result = service.findById(id);

        if (result.isEmpty()) {
            System.out.println("Passenger not found.");
            return; // nothing else to do
        }

        Passenger p = result.get();

        System.out.println("\nPassenger found:");
        System.out.println("ID    : " + p.getId());
        System.out.println("Name  : " + p.getName());
        System.out.println("Tier  : " + p.getPassengerTier());

        System.out.print("\nDo you want to change this passenger's tier? (Y/N) > ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        if (!confirm.equals("Y")) {
            System.out.println("Tier change cancelled.");
            return;
        }

        System.out.print("Enter new tier (N - Normal | S - Silver | G - Gold) > ");
        String tierInput = scanner.nextLine().trim().toUpperCase();
        if (tierInput.isBlank()) {
            System.out.println("Tier cannot be empty. Operation cancelled.");
            return;
        }
        char newTier = tierInput.charAt(0);

        try {
            service.changeTier(id, newTier);
            System.out.println("Passenger tier updated successfully.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Failed to update tier: " + ex.getMessage());
        }
    }

}
