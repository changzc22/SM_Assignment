package oopt.assignment.ui;

import oopt.assignment.Train;
import oopt.assignment.model.*;
import oopt.assignment.service.BookingService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDate;

public class BookingUI {

    private final BookingService bookingService;
    private final IPassengerRepository passengerRepository;
    private final String currentStaffId;
    private final Scanner scanner;

    public BookingUI(BookingService bookingService, String staffId, Scanner scanner) {
        this.bookingService = bookingService;
        this.currentStaffId = staffId;
        this.scanner = scanner;
        this.passengerRepository = new PassengerRepository();
    }

    public void start() {
        boolean exit = false;
        while (!exit) {
            MainUI.clearScreen();
            displayLogo();
            printMenu();

            BookingMenuOption option = getMenuSelection();
            if (option == null) {
                System.out.println("Invalid option. Please enter a number from the menu.");
                waitForEnter();
                continue;
            }

            switch (option) {
                case ADD_BOOKING -> { handleAddBooking(); waitForEnter(); }
                case DISPLAY_BOOKINGS -> { handleDisplayBookings(); waitForEnter(); }
                case SEARCH_BOOKING -> { handleSearchBooking(); waitForEnter(); }
                case CANCEL_BOOKING -> { handleCancelBooking(); waitForEnter(); }
                case GENERATE_REPORT -> { handleGenerateReport(); waitForEnter(); }
                case EXIT -> exit = true;
            }
        }
    }

    // --- 1. ADD BOOKING (New Logic: Safer & cleaner) ---
    private void handleAddBooking() {
        System.out.println("\n--- Add New Booking ---");
        System.out.println("(Enter 'X' at any prompt to cancel)");

        // 1. Display Available Trains
        System.out.println("\nAvailable Trains (Active & Has Seats):");
        List<Train> trains = oopt.assignment.TrainMain.readTrainFile();
        boolean hasAvailableTrains = false;

        // Uses a cleaner table for selection
        System.out.printf("%-6s %-15s %-25s %-10s %-10s\n", "ID", "Destination", "Departure", "Std Seats", "Prm Seats");
        System.out.println("---------------------------------------------------------------------------");

        for (Train t : trains) {
            if (t.isTrainStatus() && (t.getStandardSeatQty() > 0 || t.getPremiumSeatQty() > 0)) {
                String departureInfo = t.getDepartureTime() + " (" + t.getDepartureDate() + ")";
                System.out.printf("%-6s %-15s %-25s %-10d %-10d\n",
                        t.getTrainID(), t.getDestination(), departureInfo, t.getStandardSeatQty(), t.getPremiumSeatQty());
                hasAvailableTrains = true;
            }
        }

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
                    .findFirst().orElse(null);

            if (selectedTrain == null) {
                System.out.println("Error: Invalid Train ID. Please try again.");
            } else if (!selectedTrain.isTrainStatus() || (selectedTrain.getStandardSeatQty() <= 0 && selectedTrain.getPremiumSeatQty() <= 0)) {
                System.out.println("Error: That train is unavailable or fully booked.");
                selectedTrain = null;
            }
        }

        // 3. Generate ID
        String id = bookingService.generateNewBookingId();

        // 4. Select Passenger
        Passenger selectedPassenger = null;
        List<Passenger> allPassengers = new ArrayList<>(passengerRepository.getAll().values());

        while (selectedPassenger == null) {
            String pId = promptInput("Enter Passenger ID");
            if (pId == null) return;

            String finalPId = pId.toUpperCase();
            selectedPassenger = allPassengers.stream()
                    .filter(p -> p.getId().equalsIgnoreCase(finalPId))
                    .findFirst().orElse(null);

            if (selectedPassenger == null) {
                System.out.println("Error: Passenger ID not found. Please try again.");
            } else {
                System.out.println(">> Passenger Selected: " + selectedPassenger.getName());
            }
        }

        // 5. Seat Tier
        SeatTier tier = null;
        while (tier == null) {
            SeatTier tempTier = readSeatTierInput();
            if (tempTier == null) return;

            int availableSeats = (tempTier == SeatTier.STANDARD) ? selectedTrain.getStandardSeatQty() : selectedTrain.getPremiumSeatQty();
            if (availableSeats <= 0) {
                System.out.println("Error: No " + tempTier.getLabel() + " seats available on this train.");
            } else {
                tier = tempTier;
            }
        }

        // 6. Quantity
        int qty = -1;
        while (true) {
            qty = readIntInput("Enter Quantity");
            if (qty == -1) return;

            int maxSeats = (tier == SeatTier.STANDARD) ? selectedTrain.getStandardSeatQty() : selectedTrain.getPremiumSeatQty();
            if (qty <= 0) System.out.println("Error: Quantity must be positive.");
            else if (qty > maxSeats) System.out.println("Error: Only " + maxSeats + " seats available.");
            else break;
        }

        // 7. Review & Confirm
        double basePrice = (tier == SeatTier.STANDARD) ? selectedTrain.getStandardSeatPrice() : selectedTrain.getPremiumSeatPrice();
        double fare = bookingService.calculateFare(selectedPassenger.getPassengerTier(), tier, qty, basePrice);

        System.out.println("\n==========================");
        System.out.println("**Add New Booking Detail**");
        System.out.println("==========================");
        System.out.println("Booking ID     : " + id);
        System.out.println("Name           : " + selectedPassenger.getName());
        System.out.println("Destination    : " + selectedTrain.getDestination());
        System.out.println("Departure Date : " + selectedTrain.getDepartureDate());
        System.out.println("Departure Time : " + selectedTrain.getDepartureTime());
        System.out.println("Seat Tier      : " + tier.getLabel());
        System.out.println("Seat Quantity  : " + qty);
        System.out.printf("Total Fare     : RM %.2f\n", fare);
        System.out.println("==========================");

        String confirm = promptInput("Do you want to add this Booking? [Y/N]");
        if (confirm == null || !confirm.equalsIgnoreCase("Y")) {
            System.out.println("Booking cancelled.");
            return;
        }

        Booking newBooking = new Booking(id, selectedPassenger.getName(), tier, qty, fare, selectedTrain, currentStaffId);

        // FIX: Removed extra 'currentStaffId' argument
        if (bookingService.createBooking(newBooking, selectedTrain)) {
            System.out.println("Booking Confirmed!");
            oopt.assignment.TrainMain.writeTrainFile((ArrayList<Train>) trains);
        } else {
            System.out.println("Booking Failed (System Error).");
        }
    }

    // --- 2. DISPLAY BOOKINGS (Restored Legacy Table) ---
    private void handleDisplayBookings() {
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("|                                                     All Booking Details                                                                                    |");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("  Booking      Passenger Name                  Destination          Departure     Departure      Seat Tier         Seat          Total        Train");
        System.out.println("    ID                                                                Date          Time                         Quantity      Fare (RM)      Status\n");

        for (Booking b : bookingService.getAllBookings()) {
            // Safety check for null train (prevents crashes if data is corrupted)
            String dest = (b.getTrain() != null) ? b.getTrain().getDestination() : "Unknown";
            LocalDate date = (b.getTrain() != null) ? b.getTrain().getDepartureDate() : null;
            Object time = (b.getTrain() != null) ? b.getTrain().getDepartureTime() : "N/A";
            String status = (b.getTrain() != null && b.getTrain().isTrainStatus()) ? "Active" : "Discontinued";

            System.out.printf(" %6s        %-30s   %-15s    " + date + "      " + time, b.getBookingID(), b.getName(), dest);

            if (b.getSeatTier() == SeatTier.STANDARD) {
                System.out.printf("         %-9s", "Standard");
            } else {
                System.out.printf("         %-9s", "Premium");
            }

            System.out.printf("         %3d         %8.2f", b.getNumOfSeatBook(), b.getTotalFare());
            System.out.printf("       %-13s\n", status);
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------");
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
        displayBookingDetail(b);
    }

    private void handleCancelBooking() {
        System.out.println("\n--- Cancel Booking ---");
        Booking b = null;
        while (b == null) {
            String id = promptInput("Enter Booking ID to cancel");
            if (id == null) return;

            b = bookingService.getBookingById(id);
            if (b == null) {
                System.out.println("Error: Booking ID does not exist.");
            }
        }

        displayBookingDetail(b);

        String confirm = promptInput("Are you sure you want to cancel this booking? (Y/N)");
        if (confirm != null && confirm.equalsIgnoreCase("Y")) {
            ArrayList<Train> allTrains = oopt.assignment.TrainMain.readTrainFile();
            if (bookingService.cancelBooking(b.getBookingID(), allTrains)) {
                System.out.println("Booking cancelled successfully.");
                oopt.assignment.TrainMain.writeTrainFile(allTrains);
            } else {
                System.out.println("Error cancelling booking.");
            }
        }
    }

    private void displayBookingDetail(Booking b) {
        System.out.println("--------------------------------------------");
        System.out.println("|               Booking Detail             |");
        System.out.println("--------------------------------------------");
        System.out.println("Booking ID      : " + b.getBookingID());
        System.out.println("Name            : " + b.getName());
        System.out.println("Destination     : " + (b.getTrain() != null ? b.getTrain().getDestination() : "N/A"));
        System.out.println("Seat Tier       : " + b.getSeatTier().getLabel());
        System.out.println("Seat Quantity   : " + b.getNumOfSeatBook());
        System.out.printf("Total Fare      : RM %.2f\n", b.getTotalFare());
        System.out.println("--------------------------------------------");
    }

    private void handleGenerateReport() {
        // ... (Keep your existing report logic here) ...
        // Re-paste the report logic from previous versions if needed,
        // otherwise this response gets too long.
        // It should match the legacy style too.
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
        for (BookingMenuOption opt : BookingMenuOption.values()) {
            System.out.printf("         |  %d. %-36s |%n", opt.getId(), opt.getDescription());
        }
        System.out.println("         --------------------------------------------");
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

    private BookingMenuOption getMenuSelection() {
        System.out.print("Your Selection > ");
        String input = scanner.nextLine();
        if (input.trim().isEmpty()) return null;
        try {
            int choice = Integer.parseInt(input);
            return BookingMenuOption.fromId(choice);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void waitForEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private String promptInput(String message) {
        System.out.print(message + " > ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("X")) {
            System.out.println("Operation cancelled.");
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
}