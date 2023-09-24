package oopt.assignment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.TemporalQueries.localDate;
import static java.time.temporal.TemporalQueries.localTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Train {

    private ArrayList<Train> trainList = new ArrayList<>();
    private String trainID;
    private String destination;
    private LocalDate depatureDate;
    private LocalTime depatureTime;
    private int standardSeatQty;
    private int premiumSeatQty;
    private double standardSeatPrice;
    private double premiumSeatPrice;
    private boolean trainStatus;
    Scanner scanner = new Scanner(System.in);

    public Train() {
    }

    public Train(String trainID, String destination, LocalDate depatureDate, LocalTime depatureTime, int standardSeatQty, int premiumSeatQty, double standardSeatPrice, double premiumSeatPrice, boolean trainStatus) {
        this.trainID = trainID;
        this.destination = destination;
        this.depatureDate = depatureDate;
        this.depatureTime = depatureTime;
        this.standardSeatQty = standardSeatQty;
        this.premiumSeatQty = premiumSeatQty;
        this.standardSeatPrice = standardSeatPrice;
        this.premiumSeatPrice = premiumSeatPrice;
        this.trainStatus = trainStatus;
    }

    public ArrayList<Train> getTrainList() {
        return trainList;
    }

    public String getTrainID() {
        return trainID;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getDepartureDate() {
        return depatureDate;
    }

    public LocalTime getDepartureTime() {
        return depatureTime;
    }

    public int getStandardSeatQty() {
        return standardSeatQty;
    }

    public int getPremiumSeatQty() {
        return premiumSeatQty;
    }

    public double getStandardSeatPrice() {
        return standardSeatPrice;
    }

    public double getPremiumSeatPrice() {
        return premiumSeatPrice;
    }

    public boolean isTrainStatus() {
        return trainStatus;
    }

    //setter
    public void setTrainList(ArrayList<Train> trainList) {
        this.trainList = trainList;
    }

    public void setTrainID(String trainID) {
        this.trainID = trainID;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDepatureDate(LocalDate depatureDate) {
        this.depatureDate = depatureDate;
    }

    public void setDepatureTime(LocalTime depatureTime) {
        this.depatureTime = depatureTime;
    }

    public void setStandardSeatQty(int standardSeatQty) {
        this.standardSeatQty = standardSeatQty;
    }

    public void setPremiumSeatQty(int premiumSeatQty) {
        this.premiumSeatQty = premiumSeatQty;
    }

    public void setStandardSeatPrice(double standardSeatPrice) {
        this.standardSeatPrice = standardSeatPrice;
    }

    public void setPremiumSeatPrice(double premiumSeatPrice) {
        this.premiumSeatPrice = premiumSeatPrice;
    }

    public void setTrainStatus(boolean trainStatus) {
        this.trainStatus = trainStatus;
    }

    public String toString() {
        return "Train ID                : " + trainID
                + "\nDestination             : " + destination
                + "\nDeparture Date          : " + depatureDate
                + "\nDeparture Time          : " + depatureTime
                + "\nStandard Seat Quantity  : " + standardSeatQty
                + "\nPremium Seat Quantity   : " + premiumSeatQty
                + "\nStandard Seat Price     : RM " + standardSeatPrice
                + "\nPremium Seat Price      : RM " + premiumSeatPrice
                + "\nTrain Status            : " + (trainStatus ? "Active" : "Discontinued");
    }

    public static void displayTrainMenu() {
        OoptAssignment.clearScreen();
        System.out.println("=========================================");
        System.out.println("|     Train Information Module          |");
        System.out.println("=========================================");
        System.out.println("|  1. Add New Train                     |");
        System.out.println("|  2. Search Train                      |");
        System.out.println("|  3. Modify Train                      |");
        System.out.println("|  4. Display Train                     |");
        System.out.println("|  5. Exit to main menu                 |");
        System.out.println("|=======================================|");
    }

    public static void addNewTrain() {
        ArrayList<Train> trainList = TrainMain.readTrainFile();

        String newTrainID, newDestination, inputNewDate, inputNewTime, confirmAddString;
        int newStandardSeatQty = 0, newPremiumSeatQty = 0;
        double newStandardSeatPrice = 0, newPremiumSeatPrice = 0;
        char confirmAdd, exitChoice;
        boolean valid;
        Scanner scanner = new Scanner(System.in);
        OoptAssignment.clearScreen();
        do {
            valid = false;
            System.out.print("Enter New Train ID (Press X/x to return) : ");
            newTrainID = scanner.nextLine().toUpperCase();
            if (newTrainID.matches("[T]+[0-9]{3}")) {

                System.out.println("Press Any Key To Continue...");
                new java.util.Scanner(System.in).nextLine();
                valid = Train.checkDuplicate(newTrainID);

            } else {
                OoptAssignment.clearScreen();
                System.out.println("Invalid Train ID format(TXXX)!");
            }
        } while (!valid);
        OoptAssignment.clearScreen();
        do {
            valid = false;
            System.out.println("Train ID        : " + newTrainID);
            System.out.print("Enter New Destination (Press X to return) : ");
            newDestination = scanner.nextLine();
            
            if (newDestination.equals("X")){
                return;
            }
            
            else if (!newDestination.isEmpty() && newDestination.length() <= 15) {
                valid = Train.checkDuplicate(newDestination);
            }
            
            else if (newDestination.length() > 15) {
                valid = false;
                TrainMain.clearJavaConsoleScreen();
                System.out.println("Please input less than 15 characters!");
            }
            
            else {
                valid = false;
                OoptAssignment.clearScreen();
                System.out.println("Please input a destination!");
            }

        } while (!valid);
        OoptAssignment.clearScreen();
        do {
            valid = false;
            System.out.println("Train ID        : " + newTrainID);
            System.out.println("Destination     : " + newDestination);
            System.out.print("Enter the date in yyyy-MM-dd format (Press X to return) : ");
            inputNewDate = scanner.nextLine();
            
            if (inputNewDate.equals("X")){
                return;
            }
            
            valid = Train.checkDate(inputNewDate);

        } while (!valid);
        OoptAssignment.clearScreen();
        do {
            valid = false;
            System.out.println("Train ID        : " + newTrainID);
            System.out.println("Destination     : " + newDestination);
            System.out.println("Departure Date  : " + inputNewDate);
            System.out.print("Enter the date in HH:mm format (Press X to return) : ");
            inputNewTime = scanner.nextLine();
            
            if (inputNewTime.equals("X")){
                return;
            }
            
            valid = Train.checkTime(inputNewTime);

        } while (!valid);
        OoptAssignment.clearScreen();
        do {
            try {
                valid = false;
                System.out.println("Train ID        : " + newTrainID);
                System.out.println("Destination     : " + newDestination);
                System.out.println("Departure Date  : " + inputNewDate);
                System.out.println("Departure Time  : " + inputNewTime);
                System.out.print("Enter quantity for Standard Seat (Press -1 to return) : ");

                newStandardSeatQty = scanner.nextInt();
                if (newStandardSeatQty >= 1 && newStandardSeatQty < 999) {
                    valid = true;
                } else {
                    OoptAssignment.clearScreen();
                    System.out.println("Invalid Quantity 1-998!");
                }
            } catch (Exception e) {
                OoptAssignment.clearScreen();
                System.err.println("You should not enter other characters!");
                scanner.nextLine();
            }

        } while (!valid);
        OoptAssignment.clearScreen();
        do {
            try {
                valid = false;
                System.out.println("Train ID        : " + newTrainID);
                System.out.println("Destination     : " + newDestination);
                System.out.println("Departure Date  : " + inputNewDate);
                System.out.println("Departure Time  : " + inputNewTime);
                System.out.println("Standard Seat Quantity  : " + newStandardSeatQty);
                System.out.print("Enter quantity for Premium Seat (Press -1 to return) : ");
                newPremiumSeatQty = scanner.nextInt();
                if (newPremiumSeatQty >= 0 && newPremiumSeatQty < 1000) {
                    valid = newPremiumSeatQty < newStandardSeatQty;
                    TrainMain.clearJavaConsoleScreen();
                    if (!valid) {
                        System.out.println("Premium Seat Quantity must be lower than Standard!!");
                    }
                } else {
                    OoptAssignment.clearScreen();
                    System.out.println("Invalid Quantity 0-999");
                }
            } catch (Exception e) {
                OoptAssignment.clearScreen();
                System.err.println("You should not enter other characters!");
                scanner.nextLine();
            }

        } while (!valid);
        OoptAssignment.clearScreen();
        do {
            try{
                valid = false;
                System.out.println("Train ID        : " + newTrainID);
                System.out.println("Destination     : " + newDestination);
                System.out.println("Departure Date  : " + inputNewDate);
                System.out.println("Departure Time  : " + inputNewTime);
                System.out.println("Standard Seat Quantity  : " + newStandardSeatQty);
                System.out.println("Premium Seat Quantity  : " + newPremiumSeatQty);
                System.out.print("Enter pricing for Standard Seat (Press -1 to return) : ");
                newStandardSeatPrice = scanner.nextDouble();
                if (newStandardSeatPrice >= 1.00 && newStandardSeatPrice < 999.99) {
                    valid = true;
                } 
                
                else if (newStandardSeatPrice == -1){
                    return;
                }
                
                else {
                    TrainMain.clearJavaConsoleScreen();
                    System.out.println("Invalid Pricing 1.00-999.98");
                }
            } catch (Exception e) {
                OoptAssignment.clearScreen();
                System.err.println("You should not enter other characters!");
                scanner.nextLine();
            }
            

        } while (!valid);
        OoptAssignment.clearScreen();
        do {
            try{
                valid = false;
                System.out.println("Train ID        : " + newTrainID);
                System.out.println("Destination     : " + newDestination);
                System.out.println("Departure Date  : " + inputNewDate);
                System.out.println("Departure Time  : " + inputNewTime);
                System.out.println("Standard Seat Quantity  : " + newStandardSeatQty);
                System.out.println("Premium Seat Quantity  : " + newPremiumSeatQty);
                System.out.printf("Standard Seat Price  : RM %.2f\n", newStandardSeatPrice);
                System.out.print("Enter the pricing for Premium Seat (Press -1 to return) : ");
                newPremiumSeatPrice = scanner.nextDouble();
                
                if (newPremiumSeatPrice >= 1.01 && newPremiumSeatPrice < 1000.00) {
                    valid = newPremiumSeatPrice > newStandardSeatPrice;
                    if (!valid) {
                        OoptAssignment.clearScreen();
                        System.out.println("Premium Price must be higher than Standard!");
                    }
                } 
                
                else if (newPremiumSeatPrice == -1){
                    return;
                }
                
                else {
                    TrainMain.clearJavaConsoleScreen();
                    System.out.println("Invalid Pricing 1.01-999.99");
                }
            } catch (Exception e) {
                OoptAssignment.clearScreen();
                System.err.println("You should not enter other characters!");
                scanner.nextLine();
            }
            
        } while (!valid);
        scanner.nextLine();
        OoptAssignment.clearScreen();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(inputNewDate, dateFormatter);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localTime = LocalTime.parse(inputNewTime, timeFormatter);

        Train train = new Train(newTrainID, newDestination, localDate, localTime, newStandardSeatQty, newPremiumSeatQty, newStandardSeatPrice, newPremiumSeatPrice, true);

        valid = false;
        while (!valid) {
            System.out.println("---------------------------------------------");
            System.out.println("New Train Detail");
            System.out.println("---------------------------------------------");
            System.out.println(train.toString());
            System.out.print("\nConfirm Add Train ? (Y/N) : ");
            confirmAddString = scanner.nextLine();

            if (confirmAddString.length() == 1) {
                confirmAdd = confirmAddString.charAt(0);

                if (confirmAdd == 'Y') {
                    valid = true;
                    trainList.add(train);
                    OoptAssignment.clearScreen();
                    System.out.println("Added successfully!");
                    break;
                } else if (confirmAdd == 'N') {
                    valid = true;
                    OoptAssignment.clearScreen();
                    System.out.println("New information discarded!");
                    break;
                }
            }
            OoptAssignment.clearScreen();
            System.out.println("Please enter only Y/N");
        }
        TrainMain.writeTrainFile(trainList);

    }

    public static boolean checkDuplicate(String value) {

        ArrayList<Train> trainList = TrainMain.readTrainFile();

        // Check if the user input matches any token
        for (Train a : trainList) {
            OoptAssignment.clearScreen();
            if (a.getTrainID().equals(value)) {
                System.out.println("Duplicate ID : " + value);
                System.out.println("Please Enter Again!\n");
                return false; // Exit loop when a match is found in this line
            } else if (a.getDestination().equals(value) && a.isTrainStatus() == true) {
                System.out.println("Duplicate Destination : " + value);
                System.out.println("Please Enter Again!\n");
                return false;
            }
        }
        return true;
    }

    public static boolean checkDate(String valueDate) {

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // Parse the input string into a LocalTime object
            LocalDate localDate = LocalDate.parse(valueDate, dateFormatter);
            LocalDate today = LocalDate.now();
            
            if (localDate.isAfter(today)) {
                return true;
            } else if (localDate.isEqual(today)) {
                OoptAssignment.clearScreen();
                System.out.println("Please input date after today");
                return false;
            } else {
                OoptAssignment.clearScreen();
                System.out.println("Please input date after today");
                return false;
            }

        } catch (Exception e) {
            OoptAssignment.clearScreen();
            System.err.println("Invalid input format. Please use yyyy-MM-dd format.");
            return false;
        }

    }

    public static boolean checkTime(String valueTime) {
        try {
            // Parse the input string into a LocalTime object
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime localTime = LocalTime.parse(valueTime, timeFormatter);

            // Debugging
            System.out.println("Time: " + localTime);
            return true;

        } catch (Exception e) {
            OoptAssignment.clearScreen();
            System.err.println("Invalid input format. Please use HH:mm format.");
            return false;
        }
    }

    public static void searchTrain() {
        ArrayList<Train> trainList = TrainMain.readTrainFile();
        String searchTrain;
        int searchChoice;
        boolean validTrainSearch, trainFound = false;
        Scanner scanner = new Scanner(System.in);
        int i;

        do {
            System.out.println("Search : Train");
            do {
                validTrainSearch = false;
                System.out.print("Enter Train ID to search : ");
                searchTrain = scanner.nextLine().toUpperCase();
                if (searchTrain.matches("[T]+[0-9]{3}")) {

                    System.out.println("Press Any Key To Continue...");
                    new java.util.Scanner(System.in).nextLine();
                    validTrainSearch = true;
                } else {
                    System.out.println("Invalid Search Train ID format(TXXX)!");
                }
            } while (!validTrainSearch);

            for (i = 0; i < trainList.size(); i++) {
                if (trainList.get(i).getTrainID().equals(searchTrain)) {
                    trainFound = true;
                    break;
                }
            }

            if (trainFound) {
                TrainMain.clearJavaConsoleScreen();
                System.out.println(   "======================================");
                System.out.println(   "|| Train Detail                      ||");
                System.out.println(   "======================================");
                System.out.println(     "|| Train ID               : " + trainList.get(i).getTrainID());
                System.out.println(     "|| Destination            : " + trainList.get(i).getDestination());
                System.out.println(     "|| Departure Date         : " + trainList.get(i).getDepartureDate());
                System.out.println(     "|| Departure Time         : " + trainList.get(i).getDepartureTime());
                System.out.println(     "|| Standard Seat Quantity : " + trainList.get(i).getDepartureTime());
                System.out.println(     "|| Premium Seat Quantity  : " + trainList.get(i).getDepartureTime());
                System.out.printf("|| Standard Seat Price    : RM %.2f\n", trainList.get(i).getStandardSeatPrice());
                System.out.printf("|| Standard Seat Price    : RM %.2f\n", trainList.get(i).getPremiumSeatPrice());
                System.out.println(     "|| Train Status  : " + (trainList.get(i).isTrainStatus() ? "Active" : "Discontinued"));
            }

            System.out.println("What do you want to do with this information?");

            searchChoice = scanner.nextInt();

        } while (trainFound);

    }

    public static void modifyTrain() {
        ArrayList<Train> trainList = TrainMain.readTrainFile();
        Scanner scanner = new Scanner(System.in);
        String modifyTrain;
        int modifyChoice = 0, i, temporaryTrainQuantity;
        double temporaryTrainPrice;
        String confirmModifyString;
        char confirmModify;
        boolean validTrainSearch, validModifyChoice, trainFound = false, validTrainInput;

        do {
            System.out.println("Modify : Train");
            do {
                validTrainSearch = false;
                System.out.print("Enter Train ID to modify (Press X to return): ");
                modifyTrain = scanner.nextLine().toUpperCase();
                if (modifyTrain.matches("[T]+[0-9]{3}")) {

                    System.out.println("Press Any Key To Continue...");
                    new java.util.Scanner(System.in).nextLine();
                    validTrainSearch = true;
                } 
                
                else if (modifyTrain.equals("X")){
                    return;
                }
                
                else {
                    TrainMain.clearJavaConsoleScreen();
                    System.out.println("Invalid Search Train ID format(TXXX)!");
                }
            } while (!validTrainSearch);

            for (i = 0; i < trainList.size(); i++) {
                if (trainList.get(i).getTrainID().equals(modifyTrain)) {
                    trainFound = true;
                    break;
                }
            }

            do {
                if (trainFound) {
                    System.out.println("---------------------------------------------");
                    System.out.println("Train Detail");
                    System.out.println("---------------------------------------------");
                    System.out.println("Train ID                : " + trainList.get(i).getTrainID());
                    System.out.println("Destination             : " + trainList.get(i).getDestination());
                    System.out.println("Departure Date          : " + trainList.get(i).getDepartureDate());
                    System.out.println("Departure Time          : " + trainList.get(i).getDepartureTime());
                    System.out.println("Standard Seat Quantity  : " + trainList.get(i).getStandardSeatQty());
                    System.out.println("Premium Seat Quantity   : " + trainList.get(i).getPremiumSeatQty());
                    System.out.printf("Standard Seat Price     : RM %.2f\n", trainList.get(i).getStandardSeatPrice());
                    System.out.printf("Premium Seat Price      : RM %.2f\n", trainList.get(i).getPremiumSeatPrice());
                    System.out.println("Train Status            : " + (trainList.get(i).isTrainStatus() ? "Active" : "Discontinued"));
                    System.out.println("=============================================\n");
                    try {
                        // Sleep for 2 seconds (2000 milliseconds)
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                validTrainInput = false;
                while (!validTrainInput) {
                    try {
                        System.out.println("---------------------------------------------");
                        System.out.println("What do you want to do with this information?");
                        System.out.println("---------------------------------------------");
                        System.out.println("1. Standard Seat Quantity");
                        System.out.println("2. Premium Seat Quantity");
                        System.out.println("3. Standard Seat Price");
                        System.out.println("4. Premium Seat Price");
                        System.out.println("5. Discontinue Train");
                        System.out.println("6. Exit");
                        System.out.println("---------------------------------------------");
                        System.out.print("Choice : ");
                        modifyChoice = scanner.nextInt();
                        scanner.nextLine();

                        if (modifyChoice < 1 || modifyChoice > 6) {
                            OoptAssignment.clearScreen();
                            System.err.println("Please enter within 1-5");
                            scanner.nextLine();
                        } else {
                            validTrainInput = true;
                        }
                    } catch (Exception e) {
                        OoptAssignment.clearScreen();
                        System.err.println("Please enter only numbers");
                        scanner.nextLine();
                    }
                }
                validTrainInput = false;
                if (modifyChoice == 1) {
                    OoptAssignment.clearScreen();
                    while (!validTrainInput) {
                        try {
                            System.out.printf("          Standard Seat Quantity : %d seats\n", trainList.get(i).standardSeatQty);
                            System.out.printf("           Premium Seat Quantity : %d seats\n", trainList.get(i).premiumSeatQty);
                            System.out.print("Enter New Standard Seat Quantity (Press -1 to return) : ");
                            temporaryTrainQuantity = scanner.nextInt();
                            
                            if (temporaryTrainQuantity == -1){
                                break;
                            }
                            
                            else if (temporaryTrainQuantity > trainList.get(i).premiumSeatQty && (temporaryTrainQuantity >= 0 && temporaryTrainQuantity < 999)) {
                                validTrainInput = true;
                                validModifyChoice = false;
                                scanner.nextLine();
                                while (!validModifyChoice) {
                                    System.out.printf("\nStandard Seat Quantity From : %d seats\n", trainList.get(i).standardSeatQty);
                                    System.out.printf("                         To : %d seats\n\n", temporaryTrainQuantity);
                                    System.out.print("Are you sure that you want to change？(Y/N) : ");
                                    confirmModifyString = scanner.nextLine();

                                    if (confirmModifyString.length() == 1) {
                                        confirmModify = confirmModifyString.charAt(0);

                                        if (confirmModify == 'Y') {
                                            trainList.get(i).standardSeatQty = temporaryTrainQuantity;
                                            validModifyChoice = true;
                                            OoptAssignment.clearScreen();
                                            System.out.println("Changed successfully!");
                                            break;
                                        } else if (confirmModify == 'N') {
                                            validModifyChoice = true;
                                            OoptAssignment.clearScreen();
                                            System.out.println("Changes discarded!");
                                            break;
                                        }
                                    }
                                   OoptAssignment.clearScreen();
                                    System.out.println("Please enter only Y/N");
                                }
                            } else if (temporaryTrainQuantity < 1 || temporaryTrainQuantity >= 999) {
                                OoptAssignment.clearScreen();
                                System.out.println("Number too small or large! Enter within 1-998");
                                System.out.printf("Enter number larger than %d but lower than 998\n\n", trainList.get(i).premiumSeatQty);
                            } else {
                                OoptAssignment.clearScreen();
                                System.out.printf("Please enter number larger than %d\n\n", trainList.get(i).premiumSeatQty);
                            }
                        } catch (Exception e) {
                            OoptAssignment.clearScreen();
                            System.err.println("Please enter only numbers");
                            scanner.nextLine();
                        }
                    }
                } else if (modifyChoice == 2) {
                    OoptAssignment.clearScreen();
                    while (!validTrainInput) {
                        try {
                            System.out.printf("          Standard Seat Quantity : %d\n", trainList.get(i).standardSeatQty);
                            System.out.printf("           Premium Seat Quantity : %d\n", trainList.get(i).premiumSeatQty);
                            System.out.print(" Enter New Premium Seat Quantity (Press -1 to return) : ");
                            temporaryTrainQuantity = scanner.nextInt();
                            
                            if (temporaryTrainQuantity == -1){
                                break;
                            }
                            
                            else if (temporaryTrainQuantity < trainList.get(i).standardSeatQty && (temporaryTrainQuantity >= 0 && temporaryTrainQuantity <= 999)) {
                                validTrainInput = true;
                                validModifyChoice = false;
                                scanner.nextLine();
                                while (!validModifyChoice) {
                                    System.out.printf("\n  Premium Seat Quantity From : %d seats\n", trainList.get(i).premiumSeatQty);
                                    System.out.printf("                          To : %d seats\n\n", temporaryTrainQuantity);
                                    System.out.print("Are you sure that you want to change？(Y/N) : ");
                                    confirmModifyString = scanner.nextLine();

                                    if (confirmModifyString.length() == 1) {
                                        confirmModify = confirmModifyString.charAt(0);

                                        if (confirmModify == 'Y') {
                                            trainList.get(i).premiumSeatQty = temporaryTrainQuantity;
                                            validModifyChoice = true;
                                            OoptAssignment.clearScreen();
                                            System.out.println("Changed successfully!");
                                            break;
                                        } else if (confirmModify == 'N') {
                                            validModifyChoice = true;
                                            OoptAssignment.clearScreen();
                                            System.out.println("Changes discarded!");
                                            break;
                                        }
                                    }
                                    OoptAssignment.clearScreen();
                                    System.out.println("Please enter only Y/N");
                                }
                            } else if (temporaryTrainQuantity < 0 || temporaryTrainQuantity >= 1000) {
                                OoptAssignment.clearScreen();
                                System.out.println("Number too small or large! Enter within 0-999");
                                System.out.printf("Enter number larger than 0 but lower than %d\n\n", trainList.get(i).standardSeatQty);
                            } else {
                                OoptAssignment.clearScreen();
                                System.out.printf("Please enter number less than %d\n\n", trainList.get(i).standardSeatQty);
                            }
                        } catch (Exception e) {
                            OoptAssignment.clearScreen();
                            System.err.println("Please enter only numbers");
                            scanner.nextLine();
                        }
                    }
                } else if (modifyChoice == 3) {
                    OoptAssignment.clearScreen();
                    while (!validTrainInput) {
                        try {
                            System.out.printf("          Standard Seat Price : RM %.2f\n", trainList.get(i).standardSeatPrice);
                            System.out.printf("           Premium Seat Price : RM %.2f\n", trainList.get(i).premiumSeatPrice);
                            System.out.print("Enter New Standard Seat Price (Press -1 to return) : RM ");
                            temporaryTrainPrice = scanner.nextDouble();
                            
                            if (temporaryTrainPrice == -1){
                                break;
                            }
                            
                            else if (temporaryTrainPrice < trainList.get(i).premiumSeatPrice && (temporaryTrainPrice >= 0 && temporaryTrainPrice < 999)) {
                                validTrainInput = true;
                                validModifyChoice = false;
                                scanner.nextLine();
                                while (!validModifyChoice) {
                                    System.out.printf("\n   Standard Seat Price From : RM %.2f\n", trainList.get(i).standardSeatPrice);
                                    System.out.printf("                         To : RM %.2f\n\n", temporaryTrainPrice);
                                    System.out.print("Are you sure that you want to change？(Y/N) : ");
                                    confirmModifyString = scanner.nextLine();

                                    if (confirmModifyString.length() == 1) {
                                        confirmModify = confirmModifyString.charAt(0);

                                        if (confirmModify == 'Y') {
                                            trainList.get(i).standardSeatPrice = temporaryTrainPrice;
                                            validModifyChoice = true;
                                            OoptAssignment.clearScreen();
                                            System.out.println("Changed successfully!");
                                            break;
                                        } else if (confirmModify == 'N') {
                                            validModifyChoice = true;
                                           OoptAssignment.clearScreen();
                                            System.out.println("Changes discarded!");
                                            break;
                                        }
                                    }
                                    OoptAssignment.clearScreen();
                                    System.out.println("Please enter only Y/N");
                                }
                            } else if (temporaryTrainPrice < 1.00 || temporaryTrainPrice >= 999.99) {
                               OoptAssignment.clearScreen();
                                System.out.println("Pricing too small or large! Enter within 1.00-999.98");
                                System.out.printf("Enter price larger than RM 1.01 but lower than RM %.2f\n\n", trainList.get(i).premiumSeatPrice);
                            } else {
                                OoptAssignment.clearScreen();
                                System.out.printf("Please enter price less than RM %.2f\n\n", trainList.get(i).premiumSeatPrice);
                            }
                        } catch (Exception e) {
                            OoptAssignment.clearScreen();
                            System.err.println("Please enter only numbers");
                            scanner.nextLine();
                        }
                    }
                } else if (modifyChoice == 4) {
                    OoptAssignment.clearScreen();
                    while (!validTrainInput) {
                        try {
                            System.out.printf("         Standard Seat Price : RM %.2f\n", trainList.get(i).standardSeatPrice);
                            System.out.printf("          Premium Seat Price : RM %.2f\n", trainList.get(i).premiumSeatPrice);
                            System.out.print("Enter New Premium Seat Price (Press -1 to return) : RM ");
                            temporaryTrainPrice = scanner.nextDouble();
                            
                            if (temporaryTrainPrice == -1){
                                break;
                            }
                            
                            else if (temporaryTrainPrice > trainList.get(i).standardSeatPrice && (temporaryTrainPrice >= 0 && temporaryTrainPrice < 999)) {
                                validTrainInput = true;
                                validModifyChoice = false;
                                scanner.nextLine();
                                while (!validModifyChoice) {
                                    System.out.printf("\nPremium Seat Price From : RM %.2f\n", trainList.get(i).premiumSeatPrice);
                                    System.out.printf("                     To : RM %.2f\n\n", temporaryTrainPrice);
                                    System.out.print("Are you sure that you want to change？(Y/N) : ");
                                    confirmModifyString = scanner.nextLine();

                                    if (confirmModifyString.length() == 1) {
                                        confirmModify = confirmModifyString.charAt(0);

                                        if (confirmModify == 'Y') {
                                            trainList.get(i).premiumSeatPrice = temporaryTrainPrice;
                                            validModifyChoice = true;
                                            OoptAssignment.clearScreen();
                                            System.out.println("Changed successfully!");
                                            break;
                                        } else if (confirmModify == 'N') {
                                            validModifyChoice = true;
                                            OoptAssignment.clearScreen();
                                            System.out.println("Changes discarded!");
                                            break;
                                        }
                                    }
                                    OoptAssignment.clearScreen();
                                    System.out.println("Please enter only Y/N");
                                }
                            } else if (temporaryTrainPrice < 1.01 || temporaryTrainPrice >= 1000.00) {
                               OoptAssignment.clearScreen();
                                System.out.println("Pricing too small or large! Enter within 1.01-999.99");
                                System.out.printf("Enter price larger than RM %.2f but lower than RM 999.99\n\n", trainList.get(i).standardSeatPrice);
                            } else {
                                OoptAssignment.clearScreen();
                                System.out.printf("Please enter price larger than RM %.2f\n\n", trainList.get(i).standardSeatPrice);
                            }
                        } catch (Exception e) {
                            OoptAssignment.clearScreen();
                            System.err.println("Please enter only numbers");
                            scanner.nextLine();
                        }
                    }
                } else if (modifyChoice == 5) {
                    validModifyChoice = false;
                    if (trainList.get(i).trainStatus == true) {
                        while (!validModifyChoice) {
                            System.out.print("Are you sure that you want to discontinue the train？(Y/N) : ");
                            confirmModifyString = scanner.nextLine();

                            if (confirmModifyString.length() == 1) {
                                confirmModify = confirmModifyString.charAt(0);

                                if (confirmModify == 'Y') {
                                    trainList.get(i).trainStatus = false;
                                    validModifyChoice = true;
                                    OoptAssignment.clearScreen();
                                    System.out.println("Changed successfully!");
                                    break;
                                } else if (confirmModify == 'N') {
                                    validModifyChoice = true;
                                    OoptAssignment.clearScreen();
                                    System.out.println("Changes discarded!");
                                    break;
                                }
                            }
                            OoptAssignment.clearScreen();
                            System.out.println("Please enter only Y/N");
                        }
                    } else {
                        System.out.println("Train already discontinued!");
                    }
                }
                TrainMain.writeTrainFile(trainList);
            } while (modifyChoice != 6);
        } while (trainFound);

    }
    
    public static void displayTrain(){
        ArrayList<Train> trainList = TrainMain.readTrainFile();
        Scanner scanner = new Scanner(System.in);
        String modifyTrain;
        OoptAssignment.clearScreen();
        System.out.println(   "=======================================================================================================================================================================");
        System.out.println(   "|| Train ID || ||   Destination   || ||  Departure  || || Departure || || Standard Seat || || Standard Seat || || Premium Seat || || Premium Seat || || Train Status ||");
        System.out.println(   "||          || ||                 || ||    Date     || ||    Time   || ||   Quantity    || ||     Price     || ||   Quantity   || ||     Price    || ||              ||");
        System.out.println(   "=======================================================================================================================================================================");
        
        for (Train a : trainList){
            System.out.printf("|| %4s     || || %-15s || || %10s  || ||   %5s   || || %4d Seats    || || RM %8.2f   || || %4d Seats   || || RM %8.2f  || || %-12s ||\n"
                    , a.trainID, a.destination, a.depatureDate, a.depatureTime, a.standardSeatQty, a.standardSeatPrice, a.premiumSeatQty, a.premiumSeatPrice, a.trainStatus ? "Active" : "Discontinued");
        }
        System.out.println(   "=======================================================================================================================================================================");
        System.out.print("Press any key to continue...");
        modifyTrain = scanner.nextLine();
    }
    
}
