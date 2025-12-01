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
        System.out.print("Enter name        : ");
        String name = scanner.nextLine();
        System.out.print("Enter contact no  : ");
        String contact = scanner.nextLine();
        System.out.print("Enter IC          : ");
        String ic = scanner.nextLine();
        System.out.print("Enter gender (M/F): ");
        char gender = scanner.nextLine().toUpperCase().charAt(0);

        try {
            Passenger created = service.registerNewPassenger(name, contact, ic, gender);
            System.out.println("Passenger registered successfully with ID: " + created.getId());
        } catch (IllegalArgumentException ex) {
            System.out.println("Failed to register passenger: " + ex.getMessage());
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
        System.out.println("Current details:");
        System.out.println(existing);

        System.out.print("New name (leave blank to keep): ");
        String newName = scanner.nextLine();
        if (!newName.isBlank()) {
            existing.setName(newName);
        }

        System.out.print("New contact no (leave blank to keep): ");
        String newContact = scanner.nextLine();
        if (!newContact.isBlank()) {
            existing.setContactNo(newContact);
        }

        System.out.print("New IC (leave blank to keep): ");
        String newIc = scanner.nextLine();
        if (!newIc.isBlank()) {
            existing.setIc(newIc);
        }

        System.out.print("New gender (M/F, leave blank to keep): ");
        String genderInput = scanner.nextLine();
        if (!genderInput.isBlank()) {
            existing.setGender(genderInput.toUpperCase().charAt(0));
        }

        try {
            service.updatePassenger(existing);
            System.out.println("Passenger updated successfully.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Failed to update passenger: " + ex.getMessage());
        }
    }

    /**
     * UI for Display All Passengers Information
     */
    private void handleDisplayAll() {
        Collection<Passenger> passengers = service.getAllPassengers();

        if (passengers.isEmpty()) {
            System.out.println("No passengers found.");
            return;
        }

        System.out.println("All passengers:");
        for (Passenger p : passengers) {
            System.out.println("-----------------------");
            System.out.println(p);
        }
    }

    /**
     * UI for Upgrade/Downgrade Passenger Tier
     */
    private void handleChangeTier() {
        System.out.print("Enter passenger ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter new tier (N - Normal | S - Silver | G - Gold): ");
        String tierInput = scanner.nextLine();

        if (tierInput.isBlank()) {
            System.out.println("Tier cannot be empty.");
            return;
        }

        char tier = tierInput.toUpperCase().charAt(0);

        try {
            service.changeTier(id, tier);
            System.out.println("Passenger tier updated.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Failed to update tier: " + ex.getMessage());
        }
    }
}
