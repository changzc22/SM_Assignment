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
    private final IPassengerRepository passengerRepository;
    private final String currentStaffId;
    private final Scanner scanner;

    public BookingUI(String staffId) {
        this.bookingService = new BookingService(new BookingRepository());
        this.passengerRepository = new PassengerRepository();
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

        // 1. Display Available Trains (Table Form)
        System.out.println("\nAvailable Trains (Active & Has Seats):");
        List<Train> trains = oopt.assignment.TrainMain.readTrainFile();
        boolean hasAvailableTrains = false;

        // Define Table Format
        String border = "+--------+-----------------+---------------------------+-------------+-------------+";
        String format = "| %-6s | %-15s | %-25s | %-11s | %-11s |%n";

        // Print Header
        System.out.println(border);
        System.out.printf(format, "ID", "Destination", "Departure", "Std Seats", "Prm Seats");
        System.out.println(border);

        for (Train t : trains) {
            // Check if active AND has at least one seat in either tier
            if (t.isTrainStatus() && (t.getStandardSeatQty() > 0 || t.getPremiumSeatQty() > 0)) {
                String departureInfo = t.getDepartureTime() + " (" + t.getDepartureDate() + ")";
                System.out.printf(format,
                        t.getTrainID(),
                        t.getDestination(),
                        departureInfo,
                        t.getStandardSeatQty(),
                        t.getPremiumSeatQty());
                hasAvailableTrains = true;
            }
        }
        System.out.println(border);

        if (!hasAvailableTrains) {
            System.out.println("No active trains with available seats.");
            return;
        }

        // 2. Select Train
        Train selectedTrain = null;
        while (selectedTrain == null) {
            String trainId = promptInput("Enter Train ID");
            if (trainId == null) return;

            String finalId = trainId.toUpperCase();
            selectedTrain = trains.stream()
                    .filter(t -> t.getTrainID().equals(finalId))
                    .findFirst()
                    .orElse(null);

            if (selectedTrain == null) {
                System.out.println("Error: Invalid Train ID. Please try again.");
            } else if (!selectedTrain.isTrainStatus() || (selectedTrain.getStandardSeatQty() <= 0 && selectedTrain.getPremiumSeatQty() <= 0)) {
                System.out.println("Error: That train is unavailable or fully booked.");
                selectedTrain = null;
            }
        }

        // 3. Generate Booking ID
        String id = bookingService.generateNewBookingId();

        // 4. Select Passenger (Validate via ID)
        Passenger selectedPassenger = null;
        List<Passenger> allPassengers = new ArrayList<>(passengerRepository.getAll().values());

        while (selectedPassenger == null) {
            String pId = promptInput("Enter Passenger ID");
            if (pId == null) return;

            String finalPId = pId.toUpperCase();
            selectedPassenger = allPassengers.stream()
                    .filter(p -> p.getId().equalsIgnoreCase(finalPId))
                    .findFirst()
                    .orElse(null);

            if (selectedPassenger == null) {
                System.out.println("Error: Passenger ID not found. Please try again.");
            } else {
                System.out.println(">> Passenger Selected: " + selectedPassenger.getName());
            }
        }

        String name = selectedPassenger.getName();

        // 5. Seat Tier
        SeatTier tier = null;
        while (tier == null) {
            SeatTier tempTier = readSeatTierInput();
            if (tempTier == null) return;

            int availableSeats = (tempTier == SeatTier.STANDARD)
                    ? selectedTrain.getStandardSeatQty()
                    : selectedTrain.getPremiumSeatQty();

            if (availableSeats <= 0) {
                System.out.println("Error: No " + tempTier.getLabel() + " seats available on this train.");
            } else {
                tier = tempTier;
            }
        }

        // 6. Enter Quantity
        int qty = -1;
        while (true) {
            qty = readIntInput("Enter Quantity");
            if (qty == -1) return;

            if (qty <= 0) {
                System.out.println("Error: Quantity must be positive.");
                continue;
            }

            int maxSeats = (tier == SeatTier.STANDARD) ? selectedTrain.getStandardSeatQty() : selectedTrain.getPremiumSeatQty();
            if (qty > maxSeats) {
                System.out.println("Error: Only " + maxSeats + " seats available.");
            } else {
                break;
            }
        }

        // 7. Process Booking
        Booking newBooking = new Booking(id, name, tier, qty, 0.0, selectedTrain, currentStaffId);

        double basePrice = (tier == SeatTier.STANDARD) ? selectedTrain.getStandardSeatPrice() : selectedTrain.getPremiumSeatPrice();
        double fare = bookingService.calculateFare(selectedPassenger.getPassengerTier(), tier, qty, basePrice);
        newBooking.setTotalFare(fare);

        if (bookingService.createBooking(newBooking, selectedTrain)) {
            System.out.println("\n==========================================");
            System.out.println("          BOOKING CONFIRMED               ");
            System.out.println("==========================================");
            System.out.printf(" %-12s : %s\n", "Booking ID", newBooking.getBookingID());
            System.out.printf(" %-12s : %s (ID: %s)\n", "Passenger", newBooking.getName(), selectedPassenger.getId());
            System.out.printf(" %-12s : %s -> %s\n", "Train", selectedTrain.getTrainID(), selectedTrain.getDestination());
            System.out.printf(" %-12s : %s x %s Seat(s)\n", "Seat Info", qty, tier.getLabel());
            System.out.printf(" %-12s : RM %.2f\n", "Total Fare", fare);
            System.out.println("==========================================");

            oopt.assignment.TrainMain.writeTrainFile((ArrayList<Train>) trains);
        } else {
            System.out.println("Booking Failed (System Error).");
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
        System.out.println("\n--- Search Booking ---");

        Booking b = null;
        while (b == null) {
            String id = promptInput("Enter Booking ID to search");
            if (id == null) return;

            b = bookingService.getBookingById(id);
            if (b == null) {
                System.out.println("Booking not found. Please try again.");
            }
        }

        System.out.println("\n--- Booking Found ---");
        System.out.println("ID:          " + b.getBookingID());
        System.out.println("Passenger:   " + b.getName());
        System.out.println("Train:       " + (b.getTrain() != null ? b.getTrain().getDestination() : "N/A"));
        System.out.println("Seats:       " + b.getNumOfSeatBook() + " (" + b.getSeatTier().getLabel() + ")");
        System.out.println("Total Fare:  RM " + String.format("%.2f", b.getTotalFare()));
    }

    private void handleCancelBooking() {
        System.out.println("\n--- Cancel Booking ---");
        System.out.println("(Enter 'X' to cancel)");

        Booking b = null;
        while (b == null) {
            String id = promptInput("Enter Booking ID to cancel");
            if (id == null) return;

            b = bookingService.getBookingById(id);
            if (b == null) {
                System.out.println("Error: Booking ID " + id + " does not exist. Please try again.");
            }
        }

        System.out.println("\n--- Booking Details ---");
        System.out.println("ID:          " + b.getBookingID());
        System.out.println("Passenger:   " + b.getName());
        System.out.println("Destination: " + (b.getTrain() != null ? b.getTrain().getDestination() : "N/A"));
        System.out.println("Total Fare:  RM " + String.format("%.2f", b.getTotalFare()));
        System.out.println("-----------------------");

        String confirm = promptInput("Are you sure you want to cancel this booking? (Y/N)");
        if (confirm == null || !confirm.equalsIgnoreCase("Y")) {
            System.out.println("Cancellation aborted.");
            return;
        }

        ArrayList<Train> allTrains = oopt.assignment.TrainMain.readTrainFile();
        if (bookingService.cancelBooking(b.getBookingID(), allTrains)) {
            System.out.println("Booking cancelled successfully.");
            oopt.assignment.TrainMain.writeTrainFile(allTrains);
            System.out.println("Seats have been returned to the train.");
        } else {
            System.out.println("Error cancelling booking.");
        }
    }

    private void handleGenerateReport() {
        MainUI.clearScreen();
        List<Booking> bookingList = bookingService.getAllBookings();
        List<Train> trainList = oopt.assignment.TrainMain.readTrainFile();

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