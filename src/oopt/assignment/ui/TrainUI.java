package oopt.assignment.ui;

import oopt.assignment.model.Train;
import oopt.assignment.model.TrainCreationRequest;
import oopt.assignment.model.TrainStatus;
import oopt.assignment.service.TrainService;
import oopt.assignment.util.ErrorMessage;
import oopt.assignment.util.AppConstants;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.logging.LogRecord;

/**
 * TrainUI presentation layer
 */
public class TrainUI {
    private static final Logger LOGGER = Logger.getLogger(TrainUI.class.getName());

    static {
        LOGGER.setUseParentHandlers(false);
        Handler handler = new StreamHandler(System.out, new SimpleFormatter()) {
            @Override
            public synchronized void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        LOGGER.addHandler(handler);
    }

    private final TrainService trainService;
    private final Scanner scanner;

    /**
     * Creates a TrainUI instance.
     * @param trainService the TrainService handling business logic and persistence
     * @param scanner      shared Scanner instance for reading user input
     */
    public TrainUI(TrainService trainService, Scanner scanner) {
        this.trainService = trainService;
        this.scanner = scanner;
    }

    /**
     * Displays the menu and dispatches the chosen action until user returns.
     */
    public void showMenu() {
        while (true) {
            MainUI.clearScreen();
            printModuleHeader("Train Information Module");
            displayTrainMenu();

            String choiceStr = readLine("Enter your choice: ");
            int raw;

            try {
                raw = Integer.parseInt(choiceStr.trim());
            } catch (NumberFormatException e) {
                System.out.println();
                LOGGER.log(Level.WARNING,
                        ErrorMessage.INPUT_NOT_NUMBER + " Value: " + choiceStr);
                System.out.println();
                continue;
            }

            TrainMenuOption option = TrainMenuOption.fromId(raw);
            if (option == null) {
                System.out.println();
                LOGGER.log(Level.WARNING,
                        ErrorMessage.INVALID_MENU_SELECTION + " Value: " + raw);
                System.out.println();
                continue;
            }

            switch (option) {
                case ADD_TRAIN -> handleAddNewTrain();
                case SEARCH_TRAIN -> handleSearchTrain();
                case MODIFY_TRAIN -> handleModifyTrain();
                case DISPLAY_TRAIN -> handleDisplayTrain();
                case RETURN -> {
                    return;
                }
            }
        }
    }

    /**
     * Prints the module header.
     * @param title module title text
     */
    private void printModuleHeader(String title) {
        System.out.println("=========================================");
        System.out.printf("|  %-37s|%n", title);
        System.out.println("=========================================");
    }

    /**
     * Prints the menu options for the Train module.
     */
    private void displayTrainMenu() {
        for (TrainMenuOption opt : TrainMenuOption.values()) {
            System.out.printf("|  %d. %-33s |%n", opt.getId(), opt.getDescription());
        }
        System.out.println("=========================================");
    }

    /**
     * Prints the table header for the "Display Train" listing.
     */
    private void printTableHeader() {
        System.out.println("=======================================================================================================================================================================");
        System.out.println("|| Train ID || ||   Destination   || ||  Departure  || || Departure || || Standard Seat || || Standard Seat || || Premium Seat || || Premium Seat || || Train Status ||");
        System.out.println("||          || ||                 || ||    Date     || ||    Time   || ||   Quantity    || ||     Price     || ||   Quantity   || ||     Price    || ||              ||");
        System.out.println("=======================================================================================================================================================================");
    }

    /**
     * Prints the table footer for the "Display Train" listing.
     */
    private void printTableFooter() {
        System.out.println("=======================================================================================================================================================================");
    }

    /**
     * Read an integer, re-prompting on invalid input.
     * @param prompt prompt text to show to user
     * @return parsed integer value
     */
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println();
                LOGGER.log(Level.WARNING, ErrorMessage.INPUT_NOT_NUMBER + " Value: " + line);
                System.out.println();
            }
        }
    }

    /**
     * Read a double, re-prompting on invalid input.
     * @param prompt prompt text shown to user
     * @return parsed double value
     */
    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println();
                LOGGER.log(Level.WARNING, ErrorMessage.INPUT_NOT_NUMBER + " Value: " + line);
                System.out.println();
            }
        }
    }

    /**
     * Variant that accepts a sentinel integer value (e.g. -1) to return to caller.
     * @param prompt   prompt text shown to user
     * @param sentinel sentinel integer that indicates "return/cancel"
     * @return parsed Integer, or null when sentinel entered
     */
    private Integer readIntWithSentinel(String prompt, int sentinel) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v == sentinel) return null;
                return v;
            } catch (NumberFormatException e) {
                System.out.println();
                LOGGER.log(Level.WARNING, ErrorMessage.INPUT_NOT_NUMBER + " Value: " + line);
                System.out.println();
            }
        }
    }

    /**
     * Variant that accepts a sentinel double value (e.g. -1.0) to return to caller.
     * @param prompt   prompt text shown to user
     * @param sentinel sentinel double indicating "return/cancel"
     * @return parsed Double, or null when sentinel entered
     */
    private Double readDoubleWithSentinel(String prompt, double sentinel) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                double v = Double.parseDouble(line);
                if (Double.compare(v, sentinel) == 0) return null;
                return v;
            } catch (NumberFormatException e) {
                System.out.println();
                LOGGER.log(Level.WARNING, ErrorMessage.INPUT_NOT_NUMBER + " Value: " + line);
                System.out.println();
            }
        }
    }

    /**
     * Read a raw string line (trimmed).
     * @param prompt prompt shown to user
     * @return trimmed user input line
     */
    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Simple Y/N prompt. Keeps re-prompting until user enters Y or N.
     * @param prompt prompt shown to user
     * @return true when user enters Y/y, false when N/n
     */
    private boolean askYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/N): ");
            String ans = scanner.nextLine().trim().toUpperCase();
            if ("Y".equals(ans)) return true;
            if ("N".equals(ans)) return false;
            System.out.println();
            LOGGER.log(Level.WARNING, "Please enter only Y or N. Value: " + ans);
            System.out.println();
        }
    }

    /**
     * Display a compact before/after confirmation and ask for Y/N from user.
     * @param fieldName field being changed for display
     * @param from      original value as string
     * @param to        new value as string
     * @return true when user confirms change
     */
    private boolean confirmChange(String fieldName, String from, String to) {
        System.out.println();
        System.out.println(fieldName + " From : " + from);
        System.out.println("               To   : " + to);
        System.out.println();
        return askYesNo("Are you sure that you want to change?");
    }

    /**
     * Prints the Add New Train context — header plus only fields that have been filled so far.
     * @param req current TrainCreationRequest being constructed
     */
    private void displayAddContext(TrainCreationRequest req) {
        printModuleHeader("Add New Train");
        if (req.trainId != null) {
            System.out.println("Train ID        : " + req.trainId);
        }
        if (req.destination != null) {
            System.out.println("Destination     : " + req.destination);
        }
        if (req.departureDate != null) {
            System.out.println("Departure Date  : " + req.departureDate);
        }
        if (req.departureTime != null) {
            System.out.println("Departure Time  : " + req.departureTime);
        }
        if (req.standardSeatQty != 0) { // zero means not set (assumes creation default 0)
            System.out.println("Standard Seat Quantity  : " + req.standardSeatQty);
        }
        if (req.premiumSeatQty != 0) {
            System.out.println("Premium Seat Quantity   : " + req.premiumSeatQty);
        }
        if (req.standardSeatPrice != 0.0) {
            System.out.printf("Standard Seat Price     : RM %.2f%n", req.standardSeatPrice);
        }
        if (req.premiumSeatPrice != 0.0) {
            System.out.printf("Premium Seat Price      : RM %.2f%n", req.premiumSeatPrice);
        }
        System.out.println(); // spacing before prompt
    }


    /**
     * UI flow to collect data for a new train.
     */
    private void handleAddNewTrain() {
        TrainCreationRequest req = new TrainCreationRequest();

        while (true) {
            MainUI.clearScreen();
            printModuleHeader("Add New Train");
            System.out.println();

            String trainId = readLine("Enter New Train ID (TXXX) (Press X/x to return) : ").toUpperCase();
            if ("X".equalsIgnoreCase(trainId)) {
                return;
            }

            if (!trainService.isValidTrainIdFormat(trainId)) {
                System.out.println();
                LOGGER.log(Level.WARNING, ErrorMessage.INVALID_TRAIN_ID_FORMAT + " Value: " + trainId);
                System.out.println();
                continue;
            }
            if (trainService.isDuplicateTrainId(trainId)) {
                System.out.println();
                LOGGER.log(Level.WARNING, ErrorMessage.DUPLICATE_TRAIN_ID + " Value: " + trainId);
                System.out.println();
                continue;
            }

            req.trainId = trainId;
            break;
        }

        while (true) {
            MainUI.clearScreen();
            displayAddContext(req);
            String destination = readLine("Enter New Destination (Press X/x to return) : ");
            if ("X".equalsIgnoreCase(destination)) return;

            if (!trainService.isValidDestinationFormat(destination)) {
                System.out.println();
                LOGGER.log(Level.WARNING, ErrorMessage.DESTINATION_FORMAT_ERROR + " Value: " + destination);
                System.out.println();
                continue;
            }
            if (trainService.isDuplicateDestination(destination)) {
                System.out.println();
                LOGGER.log(Level.WARNING, ErrorMessage.DUPLICATE_DESTINATION + " Value: " + destination);
                System.out.println();
                continue;
            }
            req.destination = destination;
            break;
        }

        while (true) {
            MainUI.clearScreen();
            displayAddContext(req);
            String inputDate = readLine("Enter the date in yyyy-MM-dd format (Press X/x to return) : ");
            if ("X".equalsIgnoreCase(inputDate)) return;

            if (!trainService.isValidFutureDate(inputDate)) {
                System.out.println();
                continue;
            }
            try {
                req.departureDate = trainService.parseDate(inputDate);
            } catch (DateTimeParseException ex) {
                System.out.println();
                LOGGER.log(Level.WARNING, ErrorMessage.INVALID_DATE_FORMAT + " Value: " + inputDate);
                System.out.println();
                continue;
            }
            break;
        }

        while (true) {
            MainUI.clearScreen();
            displayAddContext(req);
            String inputTime = readLine("Enter the time in HH:mm format (Press X/x to return) : ");
            if ("X".equalsIgnoreCase(inputTime)) return;

            if (!trainService.isValidTime(inputTime)) {
                System.out.println();
                continue;
            }
            try {
                req.departureTime = trainService.parseTime(inputTime);
            } catch (DateTimeParseException ex) {
                System.out.println();
                LOGGER.log(Level.WARNING, ErrorMessage.INVALID_TIME_FORMAT + " Value: " + inputTime);
                System.out.println();
                continue;
            }
            break;
        }

        while (true) {
            MainUI.clearScreen();
            displayAddContext(req);
            Integer qty = readIntWithSentinel("Enter quantity for Standard Seat (Press -1 to return) : ", -1);
            if (qty == null) return;

            if (qty < AppConstants.MIN_STANDARD_SEATS || qty > AppConstants.MAX_SEATS) {
                System.out.println();
                LOGGER.log(
                        Level.WARNING,
                        String.format(
                                "Invalid Quantity %d-%d! Value: %d",
                                AppConstants.MIN_STANDARD_SEATS,
                                AppConstants.MAX_SEATS,
                                qty
                        )
                );
                System.out.println();
                continue;
            }
            req.standardSeatQty = qty;
            break;
        }

        while (true) {
            MainUI.clearScreen();
            displayAddContext(req);
            Integer qty = readIntWithSentinel("Enter quantity for Premium Seat (Press -1 to return) : ", -1);
            if (qty == null) return;

            try {
                trainService.validatePremiumSeatQuantity(qty, req.standardSeatQty);
                req.premiumSeatQty = qty;
                break;
            } catch (IllegalArgumentException ex) {
                System.out.println();
                LOGGER.log(Level.WARNING, ex.getMessage());
                System.out.println();
            }
        }

        while (true) {
            MainUI.clearScreen();
            displayAddContext(req);
            Double price = readDoubleWithSentinel("Enter pricing for Standard Seat (Press -1 to return) : ", -1.0);
            if (price == null) return;

            if (price < AppConstants.MIN_STANDARD_PRICE || price > AppConstants.MAX_STANDARD_PRICE) {
                System.out.println();
                LOGGER.log(
                        Level.WARNING,
                        String.format(
                                "Invalid Pricing %.2f-%.2f Value: %.2f",
                                AppConstants.MIN_STANDARD_PRICE,
                                AppConstants.MAX_STANDARD_PRICE,
                                price
                        )
                );
                System.out.println();
                continue;
            }
            req.standardSeatPrice = price;
            break;
        }

        while (true) {
            MainUI.clearScreen();
            displayAddContext(req);
            System.out.printf("Standard Seat Price     : RM %.2f%n", req.standardSeatPrice);
            Double price = readDoubleWithSentinel("Enter the pricing for Premium Seat (Press -1 to return) : ", -1.0);
            if (price == null) return;

            try {
                trainService.validatePremiumSeatPrice(price, req.standardSeatPrice);
                req.premiumSeatPrice = price;
                break;
            } catch (IllegalArgumentException ex) {
                System.out.println();
                LOGGER.log(Level.WARNING, ex.getMessage());
                System.out.println();
            }
        }

        MainUI.clearScreen();
        printModuleHeader("Add New Train - Review");
        Train preview = new Train(
                req.trainId,
                req.destination,
                req.departureDate,
                req.departureTime,
                req.standardSeatQty,
                req.premiumSeatQty,
                req.standardSeatPrice,
                req.premiumSeatPrice,
                TrainStatus.ACTIVE
        );

        System.out.println("---------------------------------------------");
        System.out.println("New Train Detail");
        System.out.println("---------------------------------------------");
        System.out.println(preview);

        if (askYesNo("Confirm Add Train ?")) {
            trainService.createTrain(req);
            MainUI.clearScreen();
            System.out.println("Added successfully!");
            MainUI.systemPause();
        } else {
            MainUI.clearScreen();
            System.out.println("New information discarded!");
            MainUI.systemPause();
        }
    }

    /**
     * Prompts the user for a Train ID and displays details if found.
     */
    private void handleSearchTrain() {
        while (true) {
            MainUI.clearScreen();
            printModuleHeader("Search Train");
            String searchTrain = readLine("Enter Train ID to search (Press X/x to return): ").toUpperCase();
            if ("X".equalsIgnoreCase(searchTrain)) return;

            if (!trainService.isValidTrainIdFormat(searchTrain)) {
                System.out.println();
                LOGGER.log(Level.WARNING, ErrorMessage.INVALID_TRAIN_ID_FORMAT + " Value: " + searchTrain);
                System.out.println();
                continue;
            }

            Optional<Train> opt = trainService.findById(searchTrain);
            if (opt.isEmpty()) {
                System.out.println();
                LOGGER.log(Level.WARNING, "Data not found for Train ID: " + searchTrain);
                System.out.println();
                continue;
            }

            Train t = opt.get();
            System.out.println("==============================================");
            System.out.println("|| Train Detail                             ||");
            System.out.println("==============================================");
            System.out.printf("|| Train ID               : %-6s          ||%n", t.getTrainID());
            System.out.printf("|| Destination            : %-15s ||%n", t.getDestination());
            System.out.printf("|| Departure Date         : %-11s     ||%n", t.getDepartureDate());
            System.out.printf("|| Departure Time         : %-7s         ||%n", t.getDepartureTime());
            System.out.printf("|| Standard Seat Quantity : %5d           ||%n", t.getStandardSeatQty());
            System.out.printf("|| Premium Seat Quantity  : %5d           ||%n", t.getPremiumSeatQty());
            System.out.printf("|| Standard Seat Price    : RM %10.2f   ||%n", t.getStandardSeatPrice());
            System.out.printf("|| Premium Seat Price     : RM %10.2f   ||%n", t.getPremiumSeatPrice());
            System.out.printf("|| Train Status           : %-14s  ||%n",
                    (t.getStatus() == TrainStatus.ACTIVE ? "Active" : "Discontinued"));
            System.out.println("==============================================");
            MainUI.systemPause();
            return;
        }
    }

    /**
     * Prompts for a Train ID and then allows modifications via TrainService.
     */
    private void handleModifyTrain() {
        while (true) {
            MainUI.clearScreen();
            printModuleHeader("Modify Train");
            String modifyTrainId = readLine("Enter Train ID to modify (Press X/x to return): ").toUpperCase();
            if ("X".equalsIgnoreCase(modifyTrainId)) return;

            if (!trainService.isValidTrainIdFormat(modifyTrainId)) {
                System.out.println();
                LOGGER.log(Level.WARNING, ErrorMessage.INVALID_TRAIN_ID_FORMAT + " Value: " + modifyTrainId);
                System.out.println();
                continue;
            }

            Optional<Train> opt = trainService.findById(modifyTrainId);
            if (opt.isEmpty()) {
                System.out.println();
                LOGGER.log(Level.WARNING, "Data not found for Train ID: " + modifyTrainId);
                System.out.println();
                continue;
            }

            Train t = opt.get();
            boolean backToSearch = false;

            while (!backToSearch) {
                MainUI.clearScreen();
                System.out.println("---------------------------------------------");
                System.out.println("Train Detail");
                System.out.println("---------------------------------------------");
                System.out.println(t);
                System.out.println("=============================================\n");
                System.out.println("What do you want to do with this information?");
                System.out.println("---------------------------------------------");
                for (TrainModifyOption optMenu : TrainModifyOption.values()) {
                    System.out.printf("%d. %s%n", optMenu.getId(), optMenu.getDescription());
                }
                System.out.println("---------------------------------------------");

                String rawStr = readLine("Choice : ");
                int raw;
                try {
                    raw = Integer.parseInt(rawStr);
                } catch (NumberFormatException e) {
                    System.out.println();
                    LOGGER.log(Level.WARNING, ErrorMessage.INPUT_NOT_NUMBER + " Value: " + rawStr);
                    System.out.println();
                    continue;
                }

                TrainModifyOption modifyOption = TrainModifyOption.fromId(raw);
                if (modifyOption == null) {
                    System.out.println();
                    LOGGER.log(Level.WARNING, ErrorMessage.INVALID_MENU_SELECTION + " Value: " + raw);
                    System.out.println();
                    continue;
                }

                switch (modifyOption) {
                    case STANDARD_QTY -> modifyStandardSeatQuantity(t);
                    case PREMIUM_QTY -> modifyPremiumSeatQuantity(t);
                    case STANDARD_PRICE -> modifyStandardSeatPrice(t);
                    case PREMIUM_PRICE -> modifyPremiumSeatPrice(t);
                    case DISCONTINUE -> discontinueTrain(t);
                    case EXIT -> backToSearch = true;
                }

                trainService.save();
            }
        }
    }

    /**
     * UI flow to modify standard seat quantity of a train.
     * @param t train to be updated
     */
    private void modifyStandardSeatQuantity(Train t) {
        while (true) {
            MainUI.clearScreen();
            System.out.printf("Current Standard Seat Quantity : %d seats%n", t.getStandardSeatQty());
            System.out.printf("Current Premium Seat Quantity  : %d seats%n", t.getPremiumSeatQty());

            int qty = readInt("Enter New Standard Seat Quantity (Press -1 to return) : ");
            if (qty == -1) return;

            try {
                trainService.validateStandardSeatQuantity(qty, t.getPremiumSeatQty());
                if (confirmChange("Standard Seat Quantity",
                        String.valueOf(t.getStandardSeatQty()),
                        String.valueOf(qty))) {
                    t.setStandardSeatQty(qty);
                    System.out.println("Changed successfully!");
                    MainUI.systemPause();
                }
                return;
            } catch (IllegalArgumentException ex) {
                System.out.println();
                LOGGER.log(Level.WARNING, ex.getMessage());
                System.out.println();
            }
        }
    }

    /**
     * UI flow to modify premium seat quantity of a train.
     * @param t train to be updated
     */
    private void modifyPremiumSeatQuantity(Train t) {
        while (true) {
            MainUI.clearScreen();
            System.out.printf("Current Standard Seat Quantity : %d seats%n", t.getStandardSeatQty());
            System.out.printf("Current Premium Seat Quantity  : %d seats%n", t.getPremiumSeatQty());

            int qty = readInt("Enter New Premium Seat Quantity (Press -1 to return) : ");
            if (qty == -1) return;

            try {
                trainService.validatePremiumSeatQuantity(qty, t.getStandardSeatQty());
                if (confirmChange("Premium Seat Quantity",
                        String.valueOf(t.getPremiumSeatQty()),
                        String.valueOf(qty))) {
                    t.setPremiumSeatQty(qty);
                    System.out.println("Changed successfully!");
                    MainUI.systemPause();
                }
                return;
            } catch (IllegalArgumentException ex) {
                System.out.println();
                LOGGER.log(Level.WARNING, ex.getMessage());
                System.out.println();
            }
        }
    }

    /**
     * UI flow to modify standard seat price of a train.
     * @param t train to be updated
     */
    private void modifyStandardSeatPrice(Train t) {
        while (true) {
            MainUI.clearScreen();
            System.out.printf("Current Standard Seat Price : RM %.2f%n", t.getStandardSeatPrice());
            System.out.printf("Current Premium Seat Price  : RM %.2f%n", t.getPremiumSeatPrice());

            double price = readDouble("Enter New Standard Seat Price (Press -1 to return) : RM ");
            if (price == -1.0) return;

            try {
                trainService.validateStandardSeatPrice(price, t.getPremiumSeatPrice());
                if (confirmChange(
                        "Standard Seat Price",
                        String.format("RM %.2f", t.getStandardSeatPrice()),
                        String.format("RM %.2f", price))) {
                    t.setStandardSeatPrice(price);
                    System.out.println("Changed successfully!");
                    MainUI.systemPause();
                }
                return;
            } catch (IllegalArgumentException ex) {
                System.out.println();
                LOGGER.log(Level.WARNING, ex.getMessage());
                System.out.println();
            }
        }
    }

    /**
     * UI flow to modify premium seat price of a train.
     * @param t train to be updated
     */
    private void modifyPremiumSeatPrice(Train t) {
        while (true) {
            MainUI.clearScreen();
            System.out.printf("Current Standard Seat Price : RM %.2f%n", t.getStandardSeatPrice());
            System.out.printf("Current Premium Seat Price  : RM %.2f%n", t.getPremiumSeatPrice());

            double price = readDouble("Enter New Premium Seat Price (Press -1 to return) : RM ");
            if (price == -1.0) return;

            try {
                trainService.validatePremiumSeatPrice(price, t.getStandardSeatPrice());
                if (confirmChange(
                        "Premium Seat Price",
                        String.format("RM %.2f", t.getPremiumSeatPrice()),
                        String.format("RM %.2f", price))) {
                    t.setPremiumSeatPrice(price);
                    System.out.println("Changed successfully!");
                    MainUI.systemPause();
                }
                return;
            } catch (IllegalArgumentException ex) {
                System.out.println();
                LOGGER.log(Level.WARNING, ex.getMessage());
                System.out.println();
            }
        }
    }

    /**
     * UI flow to discontinue a train via TrainService.
     * @param t train to be discontinued
     */
    private void discontinueTrain(Train t) {
        if (t.getStatus() == TrainStatus.DISCONTINUED) {
            System.out.println();
            LOGGER.log(Level.WARNING, ErrorMessage.TRAIN_ALREADY_DISCONTINUED);
            System.out.println();
            return;
        }

        if (confirmChange("Train Status", "Active", "Discontinued")) {
            trainService.discontinueTrain(t);
            System.out.println("Changed successfully!");
            MainUI.systemPause();
        }
    }

    /**
     * Displays all trains in a formatted table.
     */
    private void handleDisplayTrain() {
        List<Train> trainList = trainService.getAllTrains();
        MainUI.clearScreen();
        printTableHeader();

        trainList.forEach(a -> System.out.printf(
                "|| %-8s || || %-15s || || %-11s || || %-9s || || %4d Seats    || || RM %8.2f   || || %4d Seats   || || RM %8.2f  || || %-12s ||%n",
                a.getTrainID(),
                a.getDestination(),
                a.getDepartureDate(),
                a.getDepartureTime(),
                a.getStandardSeatQty(),
                a.getStandardSeatPrice(),
                a.getPremiumSeatQty(),
                a.getPremiumSeatPrice(),
                a.getStatus() == TrainStatus.ACTIVE ? "Active" : "Discontinued"
        ));

        printTableFooter();
        MainUI.systemPause();
    }
}
