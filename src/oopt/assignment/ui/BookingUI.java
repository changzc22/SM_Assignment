package oopt.assignment.ui;

import oopt.assignment.model.*;
import oopt.assignment.service.BookingService;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * Presentation layer for the Booking Module.
 * Handles all console output and user input validation.
 * Delegates logic to BookingService.
 */
public class BookingUI {

    private final BookingService bookingService;
    private final IPassengerRepository passengerRepository;
    private final String currentStaffId;
    private final Scanner scanner;


    /**
     * Constructor injecting dependencies.
     *
     * @param bookingService Service to handle booking logic
     * @param staffId        ID of the currently logged-in staff
     * @param scanner        Shared scanner instance
     */
    public BookingUI(BookingService bookingService, String staffId, Scanner scanner) {
        this.bookingService = bookingService;
        this.currentStaffId = staffId;
        this.scanner = scanner;
        this.passengerRepository = new PassengerRepository();
    }


    /**
     * Main loop for the Booking Menu.
     */
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

    /**
     * Handles the flow for creating a new booking.
     * Displays trains with prices, selects passenger, and confirms details.
     */
    private void handleAddBooking() {
        System.out.println("\n--- Add New Booking ---");
        System.out.println("(Enter 'X' at any prompt to cancel operation)");

        // 1. Display Trains with PRICES (Detailed Table)
        System.out.println("\nAvailable Trains (Active & Has Seats):");
        List<Train> trains = bookingService.getAvailableTrains();
        boolean hasAvailableTrains = false;

        // Display table of the available train
        // ID | Dest | Departure | Std Qty | Std Price | Prm Qty | Prm Price
        String headerFmt = "%-6s %-15s %-25s %-8s %-12s %-8s %-12s\n";
        String rowFmt    = "%-6s %-15s %-25s %-8d RM %-9.2f %-8d RM %-9.2f\n";
        String divider   = "-------------------------------------------------------------------------------------------------------------";

        System.out.println(divider);
        System.out.printf(headerFmt, "ID", "Destination", "Departure", "Std Qty", "Std Price", "Prm Qty", "Prm Price");
        System.out.println(divider);

        for (Train t : trains) {
            if (t.getStatus() == TrainStatus.ACTIVE && (t.getStandardSeatQty() > 0 || t.getPremiumSeatQty() > 0)) {
                String departureInfo = t.getDepartureTime() + " (" + t.getDepartureDate() + ")";

                System.out.printf(rowFmt,
                        t.getTrainID(),
                        t.getDestination(),
                        departureInfo,
                        t.getStandardSeatQty(),
                        t.getStandardSeatPrice(),
                        t.getPremiumSeatQty(),
                        t.getPremiumSeatPrice());

                hasAvailableTrains = true;
            }
        }
        System.out.println(divider);

        if (!hasAvailableTrains) {
            System.out.println("No active trains with available seats.");
            return;
        }

        // 2. Select Train
        Train selectedTrain = null;
        while (selectedTrain == null) {
            String destInput = promptInput("Please input Train ID (e.g. T001)");
            if (destInput == null) return;

            String inputUpper = destInput.toUpperCase();
            selectedTrain = trains.stream()
                    .filter(t -> t.getStatus() == TrainStatus.ACTIVE && t.getTrainID().equals(inputUpper))
                    .findFirst().orElse(null);

            if (selectedTrain == null) {
                System.out.println("Error: Invalid Train ID. Please try again.");
            }
        }

        // 3. Seat Tier
        SeatTier tier = null;
        while (tier == null) {
            // Using helper without repetitive prompt text
            SeatTier temp = readSeatTierInput();
            if (temp == null) return;

            int avail = (temp == SeatTier.STANDARD) ? selectedTrain.getStandardSeatQty() : selectedTrain.getPremiumSeatQty();
            if (avail <= 0) {
                System.out.println("Error: No more " + temp.getLabel() + " seats available on this train.");
            } else {
                tier = temp;
            }
        }

        // 4. Quantity
        int qty = -1;
        while (true) {
            qty = readIntInput("Enter quantity for seat");

            // Returns -1 if user typed 'X'
            if (qty == -1) return;

            int avail = (tier == SeatTier.STANDARD) ? selectedTrain.getStandardSeatQty() : selectedTrain.getPremiumSeatQty();
            if (qty <= 0) System.out.println("Error: You cannot input 0 or negative values.");
            else if (qty > avail) System.out.println("Error: You should not input more than available quantity (" + avail + ").");
            else break;
        }

        // 5. Booking ID
        String id = bookingService.generateNewBookingId();

        // 6. Select Passenger (Table View)
        Passenger selectedPassenger = null;
        List<Passenger> allPassengers = new ArrayList<>(passengerRepository.getAll().values());

        if (allPassengers.isEmpty()) {
            System.out.println("Error: No registered passengers found. Please register a passenger first.");
            return;
        }

        // Display Passenger List for Easy Selection
        System.out.println("\n--- Select Passenger ---");
        System.out.println("---------------------------------------------------------");
        System.out.printf("%-6s %-20s %-15s %-10s\n", "ID", "Name", "IC Number", "Tier");
        System.out.println("---------------------------------------------------------");
        for (Passenger p : allPassengers) {
            System.out.printf("%-6s %-20s %-15s %-10s\n",
                    p.getId(),
                    p.getName(),
                    p.getIc(),
                    p.getPassengerTier());
        }
        System.out.println("---------------------------------------------------------");

        // Loop to get valid ID
        while (selectedPassenger == null) {
            String pIdInput = promptInput("Please input Passenger ID");
            if (pIdInput == null) return;

            String finalId = pIdInput.toUpperCase();
            selectedPassenger = allPassengers.stream()
                    .filter(p -> p.getId().equals(finalId))
                    .findFirst().orElse(null);

            if (selectedPassenger == null) {
                System.out.println("Error: Passenger ID not found. Please input a valid ID from the list.");
            }
        }

        // 7. Calculate Fare
        double basePrice = (tier == SeatTier.STANDARD) ? selectedTrain.getStandardSeatPrice() : selectedTrain.getPremiumSeatPrice();
        double fare = bookingService.calculateFare(selectedPassenger.getPassengerTier(), tier, qty, basePrice);

        // 8. Review Screen
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
        System.out.println("Note: SST & Discounts (if applicable) included in total fare.");

        // 9. Strict Confirmation
        String confirm = "";
        while (true) {
            confirm = promptInput("Do you want to add this Booking? [Y/N]");
            if (confirm == null) {
                System.out.println("Booking creation cancelled.");
                return;
            }
            if (confirm.equalsIgnoreCase("Y")) break;
            else if (confirm.equalsIgnoreCase("N")) {
                System.out.println("The new booking detail will not be added.");
                return;
            } else {
                System.out.println("Error: Invalid input. Please enter 'Y' for Yes or 'N' for No.");
            }
        }

        // 10. Execute Creation
        Booking newBooking = new Booking(id, selectedPassenger.getName(), tier, qty, 0.0, selectedTrain, currentStaffId);
        newBooking.setTotalFare(fare);

        if (bookingService.createBooking(newBooking, selectedTrain)) {
            System.out.println("A new Booking Details with the Booking ID of " + id + " has been added.");
        } else {
            System.out.println("Error creating booking (System Validation Failed).");
        }
    }

    /**
     * Displays a formatted table of all bookings in the system.
     */
    private void handleDisplayBookings() {
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("|                                                     All Booking Details                                                                                    |");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("  Booking      Passenger Name                  Destination          Departure     Departure      Seat Tier         Seat          Total        Train");
        System.out.println("    ID                                                                Date          Time                         Quantity      Fare (RM)      Status\n");

        for (Booking b : bookingService.getAllBookings()) {
            String dest = (b.getTrain() != null) ? b.getTrain().getDestination() : "Unknown";
            LocalDate date = (b.getTrain() != null) ? b.getTrain().getDepartureDate() : null;
            Object time = (b.getTrain() != null) ? b.getTrain().getDepartureTime() : "N/A";

            String status = (b.getTrain() != null && b.getTrain().getStatus() == TrainStatus.ACTIVE) ? "Active" : "Discontinued";

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

    /**
     * Prompts for Booking ID and displays details if found.
     */
    private void handleSearchBooking() {
        System.out.println("\n--- Search Booking ---");
        Booking b = null;
        while (b == null) {
            String id = promptInput("Please enter the booking ID");
            if (id == null) return;

            b = bookingService.getBookingById(id);
            if (b == null) {
                System.out.println("Booking ID was not found.");
            }
        }
        displayBookingDetail(b);
    }

    /**
     * Handles the cancellation of a booking and refunds the seats.
     */
    private void handleCancelBooking() {
        System.out.println("\n--- Cancel Booking ---");
        Booking b = null;
        while (b == null) {
            String id = promptInput("Please enter the booking ID");
            if (id == null) return;

            b = bookingService.getBookingById(id);
            if (b == null) {
                System.out.println("Booking ID was not found.");
            }
        }

        displayBookingDetail(b);

        String confirm = "";
        while (true) {
            confirm = promptInput("Are you sure you want to cancel this booking? (Y/N)");
            if (confirm == null) return;
            if (confirm.equalsIgnoreCase("Y")) break;
            else if (confirm.equalsIgnoreCase("N")) {
                System.out.println("The Booking ID of " + b.getBookingID() + " will not be cancelled");
                return;
            } else {
                System.out.println("Error: Invalid input. Please enter 'Y' or 'N'.");
            }
        }

        if (bookingService.cancelBooking(b.getBookingID())) {
            System.out.println("Booking " + b.getBookingID() + " has been successfully cancelled.");
        } else {
            System.out.println("Error cancelling booking.");
        }
    }

    /**
     * Generates a revenue report based on destination and seat tier.
     */
    private void handleGenerateReport() {
        MainUI.clearScreen();
        List<Booking> bookingList = bookingService.getAllBookings();
        List<Train> trainList = bookingService.getAvailableTrains();

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
            if (t.getStatus() != TrainStatus.ACTIVE) continue;

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
            if (t.getStatus() != TrainStatus.ACTIVE) continue;

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


    /**
     * Display the Booking Detail based on the given booking id
     * @param b Booking id
     */
    private void displayBookingDetail(Booking b) {
        System.out.println("--------------------------------------------");
        System.out.println("|               Booking Detail             |");
        System.out.println("--------------------------------------------");
        System.out.println("Booking ID      : " + b.getBookingID());
        System.out.println("Name            : " + b.getName());
        System.out.println("Destination     : " + (b.getTrain() != null ? b.getTrain().getDestination() : "N/A"));
        System.out.println("Departure Date  : " + (b.getTrain() != null ? b.getTrain().getDepartureDate() : "N/A"));
        System.out.println("Departure Time  : " + (b.getTrain() != null ? b.getTrain().getDepartureTime() : "N/A"));
        System.out.println("Seat Tier       : " + b.getSeatTier().getLabel());
        System.out.println("Seat Quantity   : " + b.getNumOfSeatBook());
        System.out.printf("Total Fare      : RM %.2f\n", b.getTotalFare());
        System.out.println("Train Status    : " + (b.getTrain() != null && b.getTrain().getStatus() == TrainStatus.ACTIVE ? "Active" : "Discontinued"));
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
            return null;
        }
        return input;
    }

    private int readIntInput(String message) {
        while (true) {
            String input = promptInput(message);

            // If promptInput returns null, it means user typed 'X'.
            // Return -1 to signal cancellation to the caller.
            if (input == null) return -1;

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private SeatTier readSeatTierInput() {
        while (true) {
            String input = promptInput("Please enter seat tier (S-Standard, P-Premium)");
            if (input == null) return null;
            try {
                return SeatTier.fromCode(input.charAt(0));
            } catch (Exception e) {
                System.out.println("Invalid Tier. Please enter 'S' or 'P'.");
            }
        }
    }
}