package oopt.assignment;

import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.*;

public class PassengerMain {

    public static void passengerMain() {
        int selection = 0;
        boolean validInput;
        Passenger passenger = new Passenger();
        Booking booking = new Booking();
        Train train = new Train();
        Scanner sc = new Scanner(System.in).useDelimiter("\n");

        do {
            validInput = false;

            System.out.println("\nPPPPP     AAA     SSSSS    SSSSS   EEEEEE  N    N   GGGG   EEEEEE  RRRRR");
            System.out.println("P    P   A   A   S        S        E       NN   N  G       E       R    R");
            System.out.println("PPPPP   AAAAAAA   SSSSS    SSSSS   EEEE    N NN N  G  GG   EEEE    RRRRR");
            System.out.println("P       A     A        S        S  E       N   NN  G    G  E       RRR");
            System.out.println("P       A     A   SSSSS    SSSSS   EEEEEE  N    N   GGGG   EEEEEE  R  RRR");
            System.out.println("============================");
            System.out.println("PASSENGER INFORMATION MODULE");
            System.out.println("============================");
            System.out.println("1. New Passenger Registration");
            System.out.println("2. Search Passenger");
            System.out.println("3. Edit Passenger Details");
            System.out.println("4. Display All Passengers Information");
            System.out.println("5. Upgrade/Downgrade Passenger Tier");
            System.out.println("6. Back To Main Menu");

            do {
                try {
                    System.out.print("\nEnter a choice > ");
                    selection = sc.nextInt();

                    if (selection >= 1 && selection <= 6) {
                        validInput = true;
                    } else {
                        System.out.println("Invalid input. Please enter again.");
                    }
                } catch (Exception ex) {
                    System.out.println("Enter digits only! ");
                    sc.nextLine();
                }
            } while (!validInput);

            switch (selection) {
                case 1:
                    passenger.createNewPassenger();
                    break;
                case 2:
                    passenger.searchPerson();
                    break;
                case 3:
                    passenger.modifyPassenger();
                    break;
                case 4:
                    passenger.displayPerson();
                    break;
                case 5:
                    passenger.passengerTier();
                    break;
                case 6:
                    break;
                default:
                    System.out.println("Invalid input! Please enter again!");
            }
        } while (selection != 6);

    }

    public static ArrayList<Passenger> readPassengerFile() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        ArrayList<Passenger> passengerList = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("PassengerFile.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                String name = fields[0];
                String contactNo = fields[1];
                String ic = fields[2];
                String id = fields[3];
                char gender = fields[4].charAt(0);
                LocalDate joinedDate = LocalDate.parse(fields[5], dateFormatter);
                char tier = fields[6].charAt(0);
                Passenger passengerData = new Passenger(name, contactNo, ic, id, gender, joinedDate, tier);
                passengerList.add(passengerData);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return passengerList;
    }

    public static void updatePassengerFile(ArrayList<Passenger> passengerList) {
        try (FileWriter fileWriter = new FileWriter("PassengerFile.txt"); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            for (Passenger passenger : passengerList) {

                bufferedWriter.write(passenger.getName() + "|" + passenger.getContactNo() + "|" + passenger.getIc() + "|" + passenger.getId() + "|" + passenger.getGender() + "|" + passenger.getDateJoined() + "|" + passenger.getPassengerTier());
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
