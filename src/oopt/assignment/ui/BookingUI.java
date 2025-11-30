package oopt.assignment.ui;

import oopt.assignment.Train;
import oopt.assignment.model.*;
import oopt.assignment.service.BookingService;
import java.util.Scanner;
import java.util.ArrayList;

public class BookingUI {

    private final BookingService bookingService;
    private final Scanner scanner;

    public BookingUI() {
        // Initialize Service with Repository (Dependency Injection)
        this.bookingService = new BookingService(new BookingRepository());
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            MainUI.clearScreen(); // Optional: Clear screen if you have MainUI.clearScreen()
            displayLogo();        // <--- NEW: Display your logo here
            printMenu();

            int choice = 0;
            try {
                System.out.print("Your Selection > ");
                String input = scanner.nextLine();
                // Handle empty input gracefully
                if (input.trim().isEmpty()) continue;
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                waitForEnter(); // Pause so user sees the error
                continue;
            }

            switch (choice) {
                case 1 -> {
                    handleAddBooking();
                    waitForEnter();
                }
                case 2 -> {
                    handleDisplayBookings();
                    waitForEnter();
                }
                case 3 -> {
                    handleSearchBooking();
                    waitForEnter();
                }
                case 4 -> {
                    handleCancelBooking();
                    waitForEnter();
                }
                case 6 -> exit = true;
                default -> {
                    System.out.println("Invalid option.");
                    waitForEnter();
                }
            }
        }
    }

    // --- NEW: Your Logo Method ---
    private void displayLogo() {
        System.out.println("===========================================================");
        System.out.println("| BBBBB    OOO    OOO   KK   KK   III   NN    NN   GGGGG  |");
        System.out.println("| BB  BB  O   O  O   O  KK  KK    III   NNN   NN  GG   G  |");
        System.out.println("| BBBBB   O   O  O   O  KKKKK     III   NN N  NN  GG      |");
        System.out.println("| BB  BB  O   O  O   O  KK KK     III   NN  N NN  GG  GG  |");
        System.out.println("| BBBBB    OOO    OOO   KK  KK    III   NN   NNN   GGGGG  |");
        System.out.println("===========================================================");
    }

    private void handleAddBooking() {
        System.out.println("\n--- Add New Booking ---");

        // 1. Select Train
        System.out.println("Available Trains:");
        ArrayList<Train> trains = oopt.assignment.TrainMain.readTrainFile();
        boolean hasTrains = false;
        for (Train t : trains) {
            if (t.isTrainStatus()) {
                System.out.printf("[%s] %s - %s\n", t.getTrainID(), t.getDestination(), t.getDepartureTime());
                hasTrains = true;
            }
        }

        if (!hasTrains) {
            System.out.println("No active trains available.");
            return;
        }

        System.out.print("Enter Train ID: ");
        String trainId = scanner.nextLine().toUpperCase();

        Train selectedTrain = trains.stream()
                .filter(t -> t.getTrainID().equals(trainId))
                .findFirst()
                .orElse(null);

        if (selectedTrain == null) {
            System.out.println("Invalid Train ID!");
            return;
        }

        // 2. Enter Details
        System.out.print("Enter Booking ID: ");
        String id = scanner.nextLine().toUpperCase();

        System.out.print("Enter Passenger Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Seat Tier (S - Standard, P - Premium): ");
        String tierInput = scanner.nextLine();
        if (tierInput.isEmpty()) {
            System.out.println("Invalid input.");
            return;
        }

        char tierCode = tierInput.charAt(0);
        SeatTier tier;
        try {
            tier = SeatTier.fromCode(tierCode);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Seat Tier code.");
            return;
        }

        System.out.print("Enter Quantity: ");
        int qty = 0;
        try {
            qty = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
            return;
        }

        // 3. Create & Save
        Booking newBooking = new Booking(id, name, tier, qty, 0.0, selectedTrain);

        double basePrice = (tier == SeatTier.STANDARD) ? selectedTrain.getStandardSeatPrice() : selectedTrain.getPremiumSeatPrice();
        double fare = bookingService.calculateFare(PassengerTier.NORMAL, tier, qty, basePrice);
        newBooking.setTotalFare(fare);

        if (bookingService.createBooking(newBooking, selectedTrain)) {
            System.out.println("Booking Successful! Total Fare: RM " + String.format("%.2f", fare));
            oopt.assignment.TrainMain.writeTrainFile(trains); // Save train seat updates
        }
    }

    private void handleDisplayBookings() {
        System.out.println("\n--- All Bookings ---");
        System.out.printf("%-6s %-20s %-10s %-15s %-10s\n", "ID", "Name", "Tier", "Destination", "Fare");
        System.out.println("------------------------------------------------------------------");
        for (Booking b : bookingService.getAllBookings()) {
            String dest = (b.getTrain() != null) ? b.getTrain().getDestination() : "Unknown";
            System.out.printf("%-6s %-20s %-10s %-15s RM%.2f\n",
                    b.getBookingID(),
                    b.getName(),
                    b.getSeatTier().getLabel(),
                    dest,
                    b.getTotalFare());
        }
    }

    private void handleSearchBooking() {
        System.out.print("Enter Booking ID to search: ");
        String id = scanner.nextLine();
        Booking b = bookingService.getBookingById(id);
        if (b != null) {
            System.out.println("\n--- Booking Found ---");
            System.out.println("ID:          " + b.getBookingID());
            System.out.println("Passenger:   " + b.getName());
            System.out.println("Train:       " + (b.getTrain() != null ? b.getTrain().getDestination() : "N/A"));
            System.out.println("Seats:       " + b.getNumOfSeatBook() + " (" + b.getSeatTier().getLabel() + ")");
            System.out.println("Total Fare:  RM " + String.format("%.2f", b.getTotalFare()));
        } else {
            System.out.println("Booking not found.");
        }
    }

    private void handleCancelBooking() {
        System.out.print("Enter Booking ID to cancel: ");
        String id = scanner.nextLine();
        if (bookingService.deleteBooking(id)) {
            System.out.println("Booking cancelled successfully.");
        } else {
            System.out.println("Booking ID not found or could not be cancelled.");
        }
    }

    private void printMenu() {
        System.out.println("\n         --------------------------------------------");
        System.out.println("         |               Booking Menu               |");
        System.out.println("         --------------------------------------------");
        System.out.println("         |  1. Add New Booking                      |");
        System.out.println("         |  2. Display Booking Details              |");
        System.out.println("         |  3. Search Booking details               |");
        System.out.println("         |  4. Cancel Booking                       |");
        System.out.println("         |  6. Exit                                 |");
        System.out.println("         --------------------------------------------");
    }

    // Helper to pause screen so user can read output
    private void waitForEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}