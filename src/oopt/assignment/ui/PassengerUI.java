package oopt.assignment.ui;

import oopt.assignment.model.Passenger;
import oopt.assignment.model.PassengerTier;
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
        PassengerMenuOption option;

        do {
            printHeader();
            int selection = readInt("\nEnter a choice > ", 1, 6);

            option = PassengerMenuOption.fromCode(selection);
            if (option == null) {
                System.out.println("Invalid input!");
            } else {
                switch (option) {
                    case NEW_REGISTRATION -> handleNewPassenger();
                    case SEARCH_PASSENGER -> handleSearchPassenger();
                    case EDIT_PASSENGER -> handleEditPassenger();
                    case DISPLAY_ALL -> handleDisplayAll();
                    case CHANGE_TIER -> handleChangeTier();
                    case BACK_TO_MAIN_MENU -> System.out.println("Returning to main menu...");
                }
            }

            if (option != PassengerMenuOption.BACK_TO_MAIN_MENU) {
                System.out.println();
                System.out.print("Press Enter to continue...");
                scanner.nextLine();
            }

        } while (option != PassengerMenuOption.BACK_TO_MAIN_MENU);
    }


    /**
     * Display Passenger module's menu (UI)
     */
    private void printHeader() {
        System.out.println();
        System.out.println("--- Passenger Management Menu ---");
        for (PassengerMenuOption option : PassengerMenuOption.values()) {
            System.out.printf("%d. %s%n", option.getCode(), option.getDescription());
        }
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
            System.out.print("(1/4) Enter passenger name  > ");
            String name = scanner.nextLine().trim();

            try {
                service.validateName(name);
                return name;
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid name: " + ex.getMessage());
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

            try {
                service.validateContact(contact);
                return contact;
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid contact number: " + ex.getMessage());
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

            try {
                service.validateIc(ic);
                return ic;
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid IC number: " + ex.getMessage());
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
            try {
                service.validateGender(gender);
                return gender;
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid gender: " + ex.getMessage());
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
            for (PassengerEditOption option : PassengerEditOption.values()) {
                System.out.printf("%d. %s%n", option.getCode(), option.getDescription());
            }

            int choiceNumber = readInt("Your choice > ", 1, 6);
            PassengerEditOption choice = PassengerEditOption.fromCode(choiceNumber);

            if (choice == null) {
                System.out.println("Invalid option selected.");
                continue;
            }

            switch (choice) {
                case EDIT_NAME -> {
                    while (true) {
                        System.out.print("Enter new name           > ");
                        String newName = scanner.nextLine().trim();
                        try {
                            service.validateName(newName);
                            existing.setName(newName);
                            System.out.println("Name updated (pending save).");
                            break;
                        } catch (IllegalArgumentException ex) {
                            System.out.println("Invalid name: " + ex.getMessage());
                        }
                    }
                }
                case EDIT_CONTACT -> {
                    while (true) {
                        System.out.print("Enter new contact number > ");
                        String newContact = scanner.nextLine().trim();
                        try {
                            service.validateContact(newContact);
                            existing.setContactNo(newContact);
                            System.out.println("Contact number updated (pending save).");
                            break;
                        } catch (IllegalArgumentException ex) {
                            System.out.println("Invalid contact number: " + ex.getMessage());
                        }
                    }
                }
                case EDIT_IC -> {
                    while (true) {
                        System.out.print("Enter new IC             > ");
                        String newIc = scanner.nextLine().trim();
                        try {
                            service.validateIc(newIc);
                            existing.setIc(newIc);
                            System.out.println("IC updated (pending save).");
                            break;
                        } catch (IllegalArgumentException ex) {
                            System.out.println("Invalid IC: " + ex.getMessage());
                        }
                    }
                }
                case EDIT_GENDER -> {
                    while (true) {
                        System.out.print("Enter new gender (M - Male | F - Female) > ");
                        String genderInput = scanner.nextLine().trim().toUpperCase();
                        if (genderInput.isBlank()) {
                            System.out.println("Gender cannot be empty. Please try again.");
                            continue;
                        }
                        char newGender = genderInput.charAt(0);
                        try {
                            service.validateGender(newGender);
                            existing.setGender(newGender);
                            System.out.println("Gender updated (pending save).");
                            break;
                        } catch (IllegalArgumentException ex) {
                            System.out.println("Invalid gender: " + ex.getMessage());
                        }
                    }
                }
                case SAVE_AND_RETURN -> {
                    try {
                        service.updatePassenger(existing);
                        System.out.println("Passenger updated successfully.");
                    } catch (IllegalArgumentException ex) {
                        System.out.println("Failed to update passenger: " + ex.getMessage());
                    }
                    done = true;
                }
                case CANCEL -> {
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
                    String.valueOf(p.getPassengerTier().getCode())
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
            return;
        }

        Passenger p = result.get();

        System.out.println("\nPassenger found:");
        System.out.println("ID    : " + p.getId());
        System.out.println("Name  : " + p.getName());
        System.out.println("Tier  : " + p.getPassengerTier() + " (" + p.getPassengerTier().getCode() + ")");

        System.out.print("\nDo you want to change this passenger's tier? (Y/N) > ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        if (!confirm.equals("Y")) {
            System.out.println("Tier change cancelled.");
            return;
        }

        System.out.println("\nSelect new tier:");
        PassengerTier[] tiers = PassengerTier.values();
        for (int i = 0; i < tiers.length; i++) {
            PassengerTier tier = tiers[i];
            System.out.printf("%d. %s (%c, %.0f%% Discount)%n",
                    i + 1, tier.name(), tier.getCode(), (1 - tier.getPriceMultiplier())*100);
        }

        int selection = readInt("Your choice > ", 1, tiers.length);
        PassengerTier selectedTier = tiers[selection - 1];

        try {
            // service.changeTier still expects a char code
            service.changeTier(id, selectedTier.getCode());
            System.out.println("Passenger tier updated successfully to " + selectedTier.name() + ".");
        } catch (IllegalArgumentException ex) {
            System.out.println("Failed to update tier: " + ex.getMessage());
        }
    }


}
