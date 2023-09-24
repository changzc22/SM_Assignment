package oopt.assignment;

import java.util.*;
import java.time.*;
import java.io.*;
import java.time.format.DateTimeFormatter;

public class BookingMain {

    public static void bookingMain(String staffID) {
        Scanner input = new Scanner(System.in);
        OoptAssignment.clearScreen();

        int selectionInput = 0;
        boolean isValidInput, exitBookingModule = false, printLogo = true;
        
        do {

            if (printLogo) {
                bookingMenu();
            }

            printLogo = false;

            isValidInput = false;
            while (!isValidInput) {
                try {
                    System.out.print("Your Selection > ");
                    selectionInput = input.nextInt();

                    if (selectionInput >= 1 && selectionInput <= 6) {
                        isValidInput = true;
                    } else {
                        System.out.println("Invalid Input. You should only enter between 1 and 6 options!");
                    }

                } catch (Exception e) {
                    System.out.println("You should not enter other characters!");
                    input.nextLine();
                }

            }

            printLogo = true;

            switch (selectionInput) {
                case 1:
                    Booking.addBooking(staffID);
                    break;
                case 2:
                    Booking.displayBooking();
                    break;
                case 3:
                    Booking.searchBooking();
                    break;
                case 4:
                    Booking.cancelBooking();
                    break;
                case 5:
                    Booking.bookingReport();
                    break;
                default:
                    exitBookingModule = true;
            }

        } while (!exitBookingModule);

    }

    public static ArrayList<Booking> readBookingFile() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        ArrayList<Booking> bookingList = new ArrayList<>();
        Map<String, Train> trainMap = new HashMap<>();

        try {
            // Read TrainFile.txt and populate trainMap
            BufferedReader trainReader = new BufferedReader(new FileReader("TrainFile.txt"));
            String trainLine;

            while ((trainLine = trainReader.readLine()) != null) {
                String[] trainFields = trainLine.split("\\|");
                String trainNo = trainFields[0];
                String destination = trainFields[1];
                LocalDate departureDate = LocalDate.parse(trainFields[2], dateFormatter);
                LocalTime departureTime = LocalTime.parse(trainFields[3], timeFormatter);
                int standardSeatQty = Integer.parseInt(trainFields[4]);
                int premiumSeatQty = Integer.parseInt(trainFields[5]);
                double standardSeatPrice = Double.parseDouble(trainFields[6]);
                double premiumSeatPrice = Double.parseDouble(trainFields[7]);
                boolean trainStatus = Boolean.parseBoolean(trainFields[8]);

                Train train = new Train(trainNo, destination, departureDate, departureTime, standardSeatQty, premiumSeatQty, standardSeatPrice, premiumSeatPrice, trainStatus);
                trainMap.put(trainNo, train);
            }

            trainReader.close();

            // Read BookingFile.txt and associate Bookings with Trains
            BufferedReader bookingReader = new BufferedReader(new FileReader("BookingFile.txt"));
            String bookingLine;

            while ((bookingLine = bookingReader.readLine()) != null) {
                String[] bookingFields = bookingLine.split("\\|");
                String bookingID = bookingFields[0];
                String name = bookingFields[1];
                char seatTier = bookingFields[2].charAt(0);
                int numOfSeat = Integer.parseInt(bookingFields[3]);
                double totalFare = Double.parseDouble(bookingFields[4]);
                String trainNo = bookingFields[5];

                // Retrieve the corresponding Train object from trainMap using TrainNo
                Train train = trainMap.get(trainNo);
                Booking booking = new Booking(bookingID, name, seatTier, numOfSeat, totalFare, train);
                bookingList.add(booking);

            }

            bookingReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookingList;
    }

    public static void writeBookingFile(ArrayList<Booking> bookingList) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("BookingFile.txt"));
            for (Booking b : bookingList) {
                String line
                        = b.getBookingID() + "|"
                        + b.getName() + "|"
                        + b.getSeatTier() + "|"
                        + b.getNumOfSeatBook() + "|"
                        + b.getTotalFare() + "|"
                        + b.getTrain().getTrainID();

                writer.write(line);
                writer.newLine();
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void bookingMenu() {
        System.out.println("===========================================================");
        System.out.println("| BBBBB    OOO    OOO   KK   KK   III   NN    NN   GGGGG  |");
        System.out.println("| BB  BB  O   O  O   O  KK  KK    III   NNN   NN  GG   G  |");
        System.out.println("| BBBBB   O   O  O   O  KKKKK     III   NN N  NN  GG      |");
        System.out.println("| BB  BB  O   O  O   O  KK KK     III   NN  N NN  GG  GG  |");
        System.out.println("| BBBBB    OOO    OOO   KK  KK    III   NN   NNN   GGGGG  |");
        System.out.println("===========================================================");

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
    
}
