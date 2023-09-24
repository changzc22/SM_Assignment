package oopt.assignment;

import java.util.*;
import java.util.regex.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class Booking {

    private String bookingID;
    private String name;
    private char seatTier;
    private int numOfSeatBook;
    private double totalFare;
    private Train train;
    private final static double SST_RATE = 1.05; // SST 5%
    private final static double DISCOUNT_GOLD = 0.75; // 25% discount for gold passenger tier
    private final static double DISCOUNT_SILVER = 0.85; // 15% discount for silver passenger tier

    public Booking() {

    }

    public Booking(String bookingID, String name, char seatTier, int numOfSeatBook, double totalFare, Train train) {
        this.bookingID = bookingID;
        this.name = name;
        this.seatTier = seatTier;
        this.numOfSeatBook = numOfSeatBook;
        this.totalFare = totalFare;
        this.train = train;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getSeatTier() {
        return seatTier;
    }

    public void setSeatTier(char seatTier) {
        this.seatTier = seatTier;
    }

    public int getNumOfSeatBook() {
        return numOfSeatBook;
    }

    public void setNumOfSeatBook(int numOfSeatBook) {
        this.numOfSeatBook = numOfSeatBook;
    }

    public double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(double totalFare) {
        this.totalFare = totalFare;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    @Override
    public String toString() {
        LocalDate todayDate = LocalDate.now();
        return "A new Booking Details with the Booking ID of " + getBookingID() + " has been added on " + todayDate;
    }

    public static double calculateFare(char passengerTier, int numOfSeatBook, double price) {
        double discount = 0.0;

        switch (passengerTier) {
            case 'S' ->
                discount = DISCOUNT_SILVER;
            case 'G' ->
                discount = DISCOUNT_GOLD;
        }

        return price * numOfSeatBook * discount * SST_RATE;
    }

    public static void addBooking(String staffID) {
        ArrayList<Staff> s = Staff.readStaffFile();
        ArrayList<Booking> b = BookingMain.readBookingFile();
        ArrayList<Train> t = TrainMain.readTrainFile();
        ArrayList<Passenger> p = PassengerMain.readPassengerFile();
        Scanner input = new Scanner(System.in);

        LocalDate date = LocalDate.of(0, 1, 1);
        LocalTime time = LocalTime.of(0, 0);

        String bookingID = "", name = "", destination = "", inputDetail = "", trainNo = "", regex = "B\\d{3}";
        char seatTier, passengerTier = '\0';
        int numOfSeatBook = 0, i, j = 0, currentQty = 0;
        double price = 0.0, totalFare = 0.0;
        boolean isValid = false;

        System.out.println("                                            **List Of Available Train**");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        System.out.println("  Destination         Departure      Departure    Standard Seat    Premium Seat      Standard Seat       Premium Seat");
        System.out.println("                        Date           Time         Quantity         Quantity          Price(RM)           Price(RM)");
        System.out.println("------------------------------------------------------------------------------------------------------------------------");

        for (i = 0; i < t.size(); i++) {
            if (t.get(i).isTrainStatus() && (!(t.get(i).getStandardSeatQty() == 0 && t.get(i).getPremiumSeatQty() == 0))) {
                System.out.printf("  %-15s     " + t.get(i).getDepartureDate() + "       " + t.get(i).getDepartureTime() + "         %3d              %3d            %8.2f             %8.2f\n", t.get(i).getDestination(), t.get(i).getStandardSeatQty(), t.get(i).getPremiumSeatQty(), t.get(i).getStandardSeatPrice(), t.get(i).getPremiumSeatPrice());
            }
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------------");

        do {
            System.out.print("Please input destination [Enter x to exit]: ");
            inputDetail = input.nextLine();

            if (inputDetail.equalsIgnoreCase("x")) {
                return;
            } else if (inputDetail.isBlank()) {
                System.out.println("You cannot leave it blank!");
            } else {
                for (i = 0; i < t.size(); i++) {
                    if ((inputDetail.equalsIgnoreCase(t.get(i).getDestination()) && t.get(i).isTrainStatus() && (!(t.get(i).getStandardSeatQty() == 0 && t.get(i).getPremiumSeatQty() == 0)))) {
                        isValid = true;
                        j = i;
                        destination = t.get(j).getDestination();
                        date = t.get(j).getDepartureDate();
                        time = t.get(j).getDepartureTime();
                        trainNo = t.get(j).getTrainID();
                    }
                }

                if (!isValid) {
                    System.out.println("Destination Unavailable. Please input again");
                }

            }

        } while (!isValid);

        // Input Seat Tier
        isValid = false;
        do {
            System.out.print("Please enter seat tier (S-Standard, P-Premium) [Enter x to return]: ");
            inputDetail = input.nextLine().toUpperCase();

            if (inputDetail.equals("X")) {
                return;
            } else if (inputDetail.equals("S") || inputDetail.equals("P")) {
                isValid = true;
            } else {
                System.out.println("Invalid Input. Please re-enter again!!");
            }

        } while (!isValid);

        seatTier = inputDetail.charAt(0);

        currentQty = (seatTier == 'S') ? t.get(j).getStandardSeatQty() : t.get(j).getPremiumSeatQty();
        price = (seatTier == 'S') ? t.get(j).getStandardSeatPrice() : t.get(j).getPremiumSeatPrice();

        // Input Seat Quantity
        isValid = false;
        do {

            try {
                System.out.print("Enter quantity for seat [Enter -999 to return]: ");
                numOfSeatBook = input.nextInt();

                if (numOfSeatBook == -999) {
                    return;
                } else if (numOfSeatBook == 0) {
                    System.out.println("You cannot input 0");
                } else if (numOfSeatBook > currentQty) {
                    System.out.println("You shoudl not input more than available quantity.");
                } else {
                    isValid = true;
                }
                input.nextLine();
            } catch (Exception e) {
                System.out.println("You should only enter integer number!");
                input.nextLine();
            }
        } while (!isValid);

        // Input Booking ID
        isValid = false;
        do {
            System.out.print("Please input Booking ID (e.g. B001) [Enter x to return]: ");
            bookingID = input.nextLine().toUpperCase();

            if (bookingID.equalsIgnoreCase("X")) {
                return;
            }
            if (!Pattern.matches(regex, bookingID)) {
                System.out.println("Incorrect Format. Please input again!");
                isValid = false;
            } else {
                boolean isDuplicate = false;

                // Check for duplicate Booking IDs in your list 'b'
                for (Booking booking : b) {
                    if (bookingID.equals(booking.getBookingID())) {
                        isDuplicate = true;
                        break;
                    }
                }

                if (isDuplicate) {
                    System.out.println("The Booking ID is duplicated!");
                    isValid = false;
                } else {
                    isValid = true;
                }
            }

        } while (!isValid);

        //Input Name
        isValid = false;
        do {
            System.out.print("Please input passenger name [Enter x to exit]: ");
            name = input.nextLine();

            if (name.equalsIgnoreCase("X")) {
                return;
            }

            for (Passenger passengerList : p) {
                if (name.equalsIgnoreCase(passengerList.getName())) {
                    isValid = true;
                    name = passengerList.getName();
                    passengerTier = passengerList.getPassengerTier();
                    break;
                }
            }
            if (!isValid) {
                System.out.println("Passenger name not found. Please input again.");
            }
        } while (!isValid);

        // Calculate Total Fare
        totalFare = calculateFare(passengerTier, numOfSeatBook, price);

        // Display Booking Detail
        System.out.println("**Add New Booking Detail**");
        System.out.println("==========================");
        System.out.println("Booking ID     : " + bookingID);
        System.out.println("Name           : " + name);
        System.out.println("Destination    : " + destination);
        System.out.println("Departure Date : " + date);
        System.out.println("Departure Time : " + time);
        System.out.println("Seat Tier      : " + (seatTier == 'S' ? "Standard" : "Premium"));
        System.out.println("Seat Quantity  : " + numOfSeatBook);
        System.out.printf("Total Fare     : RM %.2f\n", totalFare);
        System.out.println("==========================\n");

        // Confirm to add new Booking
        isValid = false;
        do {
            System.out.print("Do you want to add this Booking ? [Y - Yes, N - No] : ");
            inputDetail = input.nextLine().toUpperCase();

            if (!(inputDetail.equals("Y") || inputDetail.equals("N"))) {
                System.out.println("Invalid Input. You should only enter Y or N.");
            } else {
                isValid = true;
            }

        } while (!isValid);

        if (inputDetail.equals("N")) {
            System.out.println("The new booking detail will not be added.");
            return;
        } else {
            if (seatTier == 'S') {
                t.get(j).setStandardSeatQty(currentQty - numOfSeatBook);
            } else {
                t.get(j).setPremiumSeatQty(currentQty - numOfSeatBook);
            }

            Train newTrain = t.get(j);
            Booking newBooking = new Booking(bookingID, name, seatTier, numOfSeatBook, totalFare, newTrain);
            b.add(newBooking);
            TrainMain.writeTrainFile(t);
            BookingMain.writeBookingFile(b);
            s = Staff.updateNo(s,staffID);
            System.out.println(newBooking.toString());
        }
    }

    public static void cancelBooking() {

        ArrayList<Booking> bookingList = BookingMain.readBookingFile();
        ArrayList<Train> trainList = TrainMain.readTrainFile();
        Scanner input = new Scanner(System.in);

        String inputDetail = null;
        char opt;
        boolean isValid = false;
        boolean found = false;
        boolean cont = true;
        int i, j, currentQty = 0;

        // Search Booking Logo
        System.out.println("===================================");
        System.out.println("|          Cancel Booking         |");
        System.out.println("===================================");

        do {
            while (!isValid) {
                System.out.print("Please enter the booking ID [Enter X to quit] > ");
                inputDetail = input.nextLine().toUpperCase();

                if (inputDetail.equals("X")) {
                    return;
                } else if (inputDetail.length() != 4) {
                    System.out.println("Invalid input. The Booking ID should be exactly 4 characters.");
                } else {
                    isValid = true;
                }
            }

            for (i = 0; i < bookingList.size(); i++) {
                if (bookingList.get(i).getBookingID().equals(inputDetail)) {
                    found = true;
                    break;
                }

            }

            if (found) {
                System.out.println("--------------------------------------------");
                System.out.println("|               Booking Detail             |");
                System.out.println("--------------------------------------------");
                System.out.println("Booking ID      : " + bookingList.get(i).getBookingID());
                System.out.println("Name            : " + bookingList.get(i).getName());
                System.out.println("Destination     : " + bookingList.get(i).train.getDestination());
                System.out.println("Departure Date  : " + bookingList.get(i).train.getDepartureDate());
                System.out.println("Departure Time  : " + bookingList.get(i).train.getDepartureTime());
                System.out.println("Seat Tier       : " + (bookingList.get(i).getSeatTier() == 'S' ? "Standard" : "Premium"));
                System.out.println("Seat Quantity   : " + bookingList.get(i).getNumOfSeatBook());
                System.out.printf("Total Fare      : RM %.2f\n", bookingList.get(i).getTotalFare());
                System.out.println("Train Status    : " + (bookingList.get(i).getTrain().isTrainStatus() ? "Active" : "Discontinued"));

                //Determine the Index for the particular train
                for (j = 0; i < trainList.size(); j++) {
                    if (trainList.get(j).getTrainID().equals(bookingList.get(i).getTrain().getTrainID())) {
                        break;
                    }
                }

                currentQty = bookingList.get(i).getSeatTier() == 'S' ? trainList.get(j).getStandardSeatQty() : trainList.get(j).getPremiumSeatQty();
                isValid = false;

                do {
                    System.out.print("Do you want to cancel this booking? [Y - Yes. N - No] > ");
                    opt = input.next().charAt(0);
                    opt = Character.toUpperCase(opt);

                    if (!(opt == 'Y' || opt == 'N')) {
                        System.out.println("Invalid Input. You should only enter either \'Y\' or \'N\'!");
                    } else {
                        isValid = true;
                    }

                } while (!isValid);

                if (opt == 'Y') {
                    if (bookingList.get(i).getSeatTier() == 'S') {
                        trainList.get(j).setStandardSeatQty(currentQty + bookingList.get(i).getNumOfSeatBook());
                    } else {
                        trainList.get(j).setPremiumSeatQty(currentQty + bookingList.get(i).getNumOfSeatBook());
                    }

                    System.out.println("Booking " + bookingList.get(i).getBookingID() + " has been successfully cancelled.");
                    System.out.println("Seat Quantity for the train " + trainList.get(j).getTrainID() + " will be retunred back.");
                    bookingList.remove(i);
                    BookingMain.writeBookingFile(bookingList);
                    TrainMain.writeTrainFile(trainList);
                } else {
                    System.out.println("The Booking ID of " + bookingList.get(i).getBookingID() + " will not be canceeld");
                }

            } else {
                System.out.println("Booking ID was not found.");
            }

            isValid = false;
            found = false;

            do {
                System.out.print("Do you still want to continue [Y - Yes. N - No] > ");
                opt = input.next().charAt(0);
                opt = Character.toUpperCase(opt);

                if (!(opt == 'Y' || opt == 'N')) {
                    System.out.println("Invalid Input. You should only enter either \'Y\' or \'N\'!");
                } else {
                    isValid = true;
                }

            } while (!isValid);

            if (opt == 'N') {
                cont = false;
            } else {
                isValid = false;
            }

            input.nextLine();

        } while (cont);

    }

    public static void searchBooking() {

        ArrayList<Booking> bookingList = BookingMain.readBookingFile();

        Scanner input = new Scanner(System.in).useDelimiter("\n");

        String inputDetail = null;
        char opt;
        boolean isValid = false;
        boolean cont = true;
        boolean found = false;
        int i;

        // Search Booking Logo
        System.out.println("===================================");
        System.out.println("|         Search Booking          |");
        System.out.println("===================================");

        do {
            while (!isValid) {
                System.out.print("Please enter the booking ID [Enter X to quit] > ");
                inputDetail = input.nextLine().toUpperCase();

                if (inputDetail.equals("X")) {
                    return;
                } else if (inputDetail.length() != 4) {
                    System.out.println("Invalid input. The Booking ID should be exactly 4 characters.");
                } else {
                    isValid = true;
                }
            }

            for (i = 0; i < bookingList.size(); i++) {
                if (bookingList.get(i).getBookingID().equals(inputDetail)) {
                    found = true;
                    break;
                }

            }

            if (found) {
                System.out.println("--------------------------------------------");
                System.out.println("|               Booking Detail             |");
                System.out.println("--------------------------------------------");
                System.out.println("Booking ID      : " + bookingList.get(i).getBookingID());
                System.out.println("Name            : " + bookingList.get(i).getName());
                System.out.println("Destination     : " + bookingList.get(i).train.getDestination());
                System.out.println("Departure Date  : " + bookingList.get(i).train.getDepartureDate());
                System.out.println("Departure Time  : " + bookingList.get(i).train.getDepartureTime());
                System.out.println("Seat Tier       : " + (bookingList.get(i).getSeatTier() == 'S' ? "Standard" : "Premium"));
                System.out.println("Seat Quantity   : " + bookingList.get(i).getNumOfSeatBook());
                System.out.printf("Total Fare      : RM %.2f\n", bookingList.get(i).getTotalFare());
                System.out.println("Train Status    : " + (bookingList.get(i).getTrain().isTrainStatus() ? "Active" : "Discontinued"));

            } else {
                System.out.println("Booking ID was not found.");
            }

            found = false;
            isValid = false;

            do {
                System.out.print("Do you still want to continue [Y - Yes. N - No] > ");
                opt = input.next().charAt(0);
                opt = Character.toUpperCase(opt);

                if (!(opt == 'Y' || opt == 'N')) {
                    System.out.println("Invalid Input. You should only enter either \'Y\' or \'N\'!");
                } else {
                    isValid = true;
                }

            } while (!isValid);

            if (opt == 'N') {
                cont = false;
            } else {
                isValid = false;
            }

            input.nextLine();

        } while (cont);

    }

    public static void displayBooking() {

        ArrayList<Booking> bookingList = BookingMain.readBookingFile();

        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("|                                                     All Booking Details                                                                                    |");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("  Booking      Passenger Name                  Destination          Departure     Departure      Seat Tier         Seat          Total        Train");
        System.out.println("    ID                                                                Date          Time                         Quantity      Fare (RM)      Status\n");

        for (Booking b : bookingList) {
            System.out.printf(" %6s        %-30s   %-15s    " + b.train.getDepartureDate() + "      " + b.train.getDepartureTime(), b.getBookingID(), b.getName(), b.train.getDestination());

            if (b.getSeatTier() == 'S') {
                System.out.printf("         %-9s", "Standard");
            } else {
                System.out.printf("         %-9s", "Premium");
            }

            System.out.printf("         %3d         %8.2f", b.getNumOfSeatBook(), b.getTotalFare());
            System.out.printf("       %-13s\n", (b.train.isTrainStatus() ? "Active" : "Discontinued"));
        }

        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    public static void bookingReport() {

        ArrayList<Booking> bookingList = BookingMain.readBookingFile();
        if (bookingList.isEmpty()) {
            System.out.println("Oops, the report cannot be generated due to insufficient Data.");
            return;
        }

        ArrayList<Train> trainList = TrainMain.readTrainFile();
        double destinationFare = 0.0, totalDestinationFare = 0.0;
        double[] subtotal = {0.0, 0.0};

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);

        System.out.println("--------------------------------------------------------");
        System.out.println("      **Seat Revenue Analysis by Destination Report**   ");
        System.out.println("--------------------------------------------------------");
        System.out.println("Date: " + formattedDate);
        System.out.println("--------------------------------------------------------");

        System.out.println("Revenue by Destination (Standard Seat):");
        System.out.println("--------------------------------------------------------");
        
        for (Train t : trainList) {
            for (Booking b : bookingList) {
                if (t.getDestination().equals(b.getTrain().getDestination()) && b.getSeatTier() == 'S' && b.getTrain().isTrainStatus()) {
                    destinationFare = b.getTotalFare();
                    totalDestinationFare += b.getTotalFare();
                    subtotal[0] += destinationFare;
                }
            }
            System.out.printf("%-12s  : RM %8.2f\n", t.getDestination(), totalDestinationFare);
            totalDestinationFare = 0.0;
        }
        
        destinationFare = 0.0;

        System.out.println("--------------------------------------------------------");
        System.out.println("Revenue by Destination (Premium Seat):");
        System.out.println("--------------------------------------------------------");

        for (Train t : trainList) {
            for (Booking b : bookingList) {
                if (t.getDestination().equals(b.getTrain().getDestination()) && b.getSeatTier() == 'P' && b.getTrain().isTrainStatus()) {
                    destinationFare = b.getTotalFare();
                    totalDestinationFare += b.getTotalFare();
                    subtotal[1] += destinationFare;
                }
            }
            System.out.printf("%-12s  : RM %8.2f\n", t.getDestination(), totalDestinationFare);
            totalDestinationFare = 0.0;
        }

        //BookingMain.systemPause();
        System.out.println("--------------------------------------------------------");
        System.out.println("Total Revenue");
        System.out.println("--------------------------------------------------------");
        System.out.printf("Standard Seat : RM %8.2f\n", subtotal[0]);
        System.out.printf("Premium Seat  : RM %8.2f\n", subtotal[1]);
        System.out.println("--------------------------------------------------------");
        System.out.printf("Overall Total Revenue: RM %8.2f\n", subtotal[0] + subtotal[1]);
        System.out.println("--------------------------------------------------------");

        System.out.println("\nNote: Discontinued train will not be included in the report!\n\n");

    }
}
