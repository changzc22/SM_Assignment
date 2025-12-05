package oopt.assignment.ui;

import oopt.assignment.Train;
import oopt.assignment.model.*;
import oopt.assignment.service.BookingService;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookingUI {

    private final BookingService bookingService;
    private final String currentStaffId;
    private final Scanner scanner;

    public BookingUI(String staffId) {
        this.bookingService = new BookingService(new BookingRepository());
        this.currentStaffId = staffId;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            MainUI.clearScreen();
            displayLogo();
            printMenu();

            int choice = 0;
            try {
                System.out.print("Your Selection > ");
                String input = scanner.nextLine();
                if (input.trim().isEmpty()) continue;
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                waitForEnter();
                continue;
            }

            switch (choice) {
                case 1 -> { handleAddBooking(); waitForEnter(); }
                case 2 -> { handleDisplayBookings(); waitForEnter(); }
                case 3 -> { handleSearchBooking(); waitForEnter(); }
                case 4 -> { handleCancelBooking(); waitForEnter(); }
                case 5 -> { handleGenerateReport(); waitForEnter(); }
                case 6 -> exit = true;
                default -> { System.out.println("Invalid option."); waitForEnter(); }
            }
        }
    }

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
        System.out.println("(Enter 'X' at any prompt to cancel)");

        // 1. Select Train (Looping until valid or X)
        System.out.println("\nAvailable Trains:");
        List<Train> trains = oopt.assignment.TrainMain.readTrainFile();
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

        Train selectedTrain = null;
        while (selectedTrain == null) {
            String trainId = promptInput("Enter Train ID"); // Use helper!
            if (trainId == null) return; // User entered X

            String finalId = trainId.toUpperCase();
            selectedTrain = trains.stream()
                    .filter(t -> t.getTrainID().equals(finalId))
                    .findFirst()
                    .orElse(null);

            if (selectedTrain == null) {
                System.out.println("Error: Invalid Train ID. Please try again.");
            }
        }

        // 2. Generate ID
        String id = bookingService.generateNewBookingId();

        // 3. Get Details using Helper Methods (Clean & Looping)
        String name = readStringInput("Enter Passenger Name");
        if (name == null) return;

        SeatTier tier = readSeatTierInput();
        if (tier == null) return;

        int qty = readIntInput("Enter Quantity");
        if (qty == -1) return;

        if (qty <= 0) {
            System.out.println("Error: Quantity must be positive.");
            return;
        }

        // 4. Create & Save
        Booking newBooking = new Booking(id, name, tier, qty, 0.0, selectedTrain, currentStaffId);

        double basePrice = (tier == SeatTier.STANDARD) ? selectedTrain.getStandardSeatPrice() : selectedTrain.getPremiumSeatPrice();
        double fare = bookingService.calculateFare(PassengerTier.NORMAL, tier, qty, basePrice);
        newBooking.setTotalFare(fare);

        if (bookingService.createBooking(newBooking, selectedTrain)) {
            // Success Summary
            System.out.println("\n==========================================");
            System.out.println("          BOOKING CONFIRMED               ");
            System.out.println("==========================================");
            System.out.println(" Booking ID   : " + newBooking.getBookingID());
            System.out.println(" Passenger    : " + newBooking.getName());
            System.out.println(" Train        : " + selectedTrain.getTrainID() + " (" + selectedTrain.getDestination() + ")");
            System.out.println(" Seat Info    : " + qty + " x " + tier.getLabel());
            System.out.printf(" Total Fare   : RM %.2f\n", fare);
            System.out.println("==========================================");

            // Save updates
            oopt.assignment.TrainMain.writeTrainFile((ArrayList<Train>) trains);
        } else {
            System.out.println("Booking Failed (Not enough seats available or system error).");
        }
    }

    private void handleDisplayBookings() {
        System.out.println("\n--- All Bookings ---");
        System.out.printf("%-6s %-20s %-10s %-15s %-10s\n", "ID", "Name", "Tier", "Destination", "Fare");
        System.out.println("------------------------------------------------------------------");
        for (Booking b : bookingService.getAllBookings()) {
            String dest = (b.getTrain() != null) ? b.getTrain().getDestination() : "Unknown";
            System.out.printf("%-6s %-20s %-10s %-15s RM%.2f\n",
                    b.getBookingID(), b.getName(), b.getSeatTier().getLabel(), dest, b.getTotalFare());
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

        Booking b = bookingService.getBookingById(id);
        if (b == null) {
            System.out.println("Error: Booking ID " + id + " does not exist.");
            return;
        }

        System.out.print("Are you sure you want to cancel booking " + id + "? (Y/N): ");
        String confirm = scanner.nextLine();
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Cancellation aborted.");
            return;
        }

        ArrayList<Train> allTrains = oopt.assignment.TrainMain.readTrainFile();

        if (bookingService.cancelBooking(id, allTrains)) {
            System.out.println("Booking cancelled successfully.");
            oopt.assignment.TrainMain.writeTrainFile(allTrains);
            System.out.println("Seats have been returned to the train.");
        } else {
            System.out.println("Error cancelling booking.");
        }
    }

    private void handleGenerateReport() {
        MainUI.clearScreen();
        ArrayList<Booking> bookingList = bookingService.getAllBookings();
        ArrayList<Train> trainList = oopt.assignment.TrainMain.readTrainFile();

        if (bookingList.isEmpty()) {
            System.out.println("Oops, the report cannot be generated due to insufficient Data.");
            return;
        }

        double totalDestinationFare = 0.0;
        double[] subtotal = {0.0, 0.0};

        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        System.out.println("--------------------------------------------------------");
        System.out.println("      **Seat Revenue Analysis by Destination Report** ");
        System.out.println("--------------------------------------------------------");
        System.out.println("Date: " + formattedDate);
        System.out.println("--------------------------------------------------------");

        System.out.println("Revenue by Destination (Standard Seat):");
        System.out.println("--------------------------------------------------------");
        for (Train t : trainList) {
            if (!t.isTrainStatus()) continue;

            for (Booking b : bookingList) {
                if (b.getTrain() != null && t.getDestination().equals(b.getTrain().getDestination())
                        && b.getSeatTier() == SeatTier.STANDARD) {
                    totalDestinationFare += b.getTotalFare();
                    subtotal[0] += b.getTotalFare();
                }
            }
            System.out.printf("%-15s  : RM %8.2f\n", t.getDestination(), totalDestinationFare);
            totalDestinationFare = 0.0;
        }

        System.out.println("--------------------------------------------------------");
        System.out.println("Revenue by Destination (Premium Seat):");
        System.out.println("--------------------------------------------------------");
        for (Train t : trainList) {
            if (!t.isTrainStatus()) continue;

            for (Booking b : bookingList) {
                if (b.getTrain() != null && t.getDestination().equals(b.getTrain().getDestination())
                        && b.getSeatTier() == SeatTier.PREMIUM) {
                    totalDestinationFare += b.getTotalFare();
                    subtotal[1] += b.getTotalFare();
                }
            }
            System.out.printf("%-15s  : RM %8.2f\n", t.getDestination(), totalDestinationFare);
            totalDestinationFare = 0.0;
        }

        System.out.println("--------------------------------------------------------");
        System.out.println("Total Revenue");
        System.out.println("--------------------------------------------------------");
        System.out.printf("Standard Seat : RM %8.2f\n", subtotal[0]);
        System.out.printf("Premium Seat  : RM %8.2f\n", subtotal[1]);
        System.out.println("--------------------------------------------------------");
        System.out.printf("Overall Total Revenue: RM %8.2f\n", subtotal[0] + subtotal[1]);
        System.out.println("--------------------------------------------------------");
        System.out.println("\nNote: Discontinued train will not be included in the report!\n");
    }

    private void printMenu() {
        System.out.println("\n         --------------------------------------------");
        System.out.println("         |               Booking Menu               |");
        System.out.println("         --------------------------------------------");
        System.out.println("         |  1. Add New Booking                      |");
        System.out.println("         |  2. Display Booking Details              |");
        System.out.println("         |  3. Search Booking details               |");
        System.out.println("         |  4. Cancel Booking                       |");
        System.out.println("         |  5. Generate Report                      |");
        System.out.println("         |  6. Exit                                 |");
        System.out.println("         --------------------------------------------");
    }

    private void waitForEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // --- MISSING METHOD ADDED HERE ---
    private String promptInput(String message) {
        System.out.print(message + " > ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("X")) {
            System.out.println("Operation cancelled by user.");
            return null;
        }
        return input;
    }

    private int readIntInput(String message) {
        while (true) {
            String input = promptInput(message);
            if (input == null) return -1;

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a valid number.");
            }
        }
    }

    private SeatTier readSeatTierInput() {
        while (true) {
            String input = promptInput("Enter Seat Tier (S - Standard, P - Premium)");
            if (input == null) return null;

            try {
                return SeatTier.fromCode(input.charAt(0));
            } catch (Exception e) {
                System.out.println("Error: Invalid Tier. Please enter 'S' or 'P'.");
            }
        }
    }

    private String readStringInput(String message) {
        while (true) {
            String input = promptInput(message);
            if (input == null) return null;

            if (!input.trim().isEmpty()) {
                return input;
            }
            System.out.println("Error: Input cannot be empty.");
        }
    }
}