package oopt.assignment;

import static java.lang.Character.toUpperCase;
import java.util.*;
import java.io.*;
import java.time.LocalDate;

public class Passenger extends Person {

    private char gender;
    private LocalDate dateJoined;
    private char passengerTier;

    //Constructor
    public Passenger() {

    }

    public Passenger(String name, String contactNo, String ic, String id, char gender, LocalDate dateJoined, char passengerTier) {
        super(name, contactNo, ic, id);
        this.gender = gender;
        this.dateJoined = dateJoined;
        this.passengerTier = passengerTier;
    }

    //Getter
    public char getGender() {
        return gender;
    }

    public LocalDate getDateJoined() {
        return dateJoined;
    }

    public char getPassengerTier() {
        return passengerTier;
    }

    //Setter
    public void setGender(char gender) {
        this.gender = gender;
    }

    public void setDateJoined(LocalDate dateJoined) {
        this.dateJoined = dateJoined;
    }

    public void setPassengerTier(char passengerTier) {
        this.passengerTier = passengerTier;
    }

    public void createNewPassenger() {
        ArrayList<Passenger> passengerList = PassengerMain.readPassengerFile();
        Scanner sc = new Scanner(System.in).useDelimiter("\n");
        String name, contactNo, ic, id;
        char gender, tier, selection;
        boolean nameValid = false, contactValid = false, icValid = false, genderValid = false;
        int invalidCount;
        LocalDate dateJoined;

        System.out.println("==========================");
        System.out.println("NEW PASSENGER REGISTRATION");
        System.out.println("==========================");
        System.out.println("\nKindly enter the passenger details below.");
        System.out.println("Press X to exit if you wish to exit.");

        do {
            invalidCount = 0;
            System.out.println("\n(1/4) Enter the passenger name without special characters.");
            System.out.print("Enter here > ");
            name = sc.nextLine().trim();

            if ((name.equals("X") || name.equals("x")) && name.length() == 1) {
                return;
            }

            if (name.isEmpty()) {
                invalidCount++;
            }

            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (!Character.isAlphabetic(c) && !Character.isWhitespace(c)) {
                    invalidCount++;
                }
            }
            nameValid = invalidCount == 0;

            if (!nameValid) {
                System.out.println("Enter alphabelts and spaces only!");
            }

        } while (!nameValid);

        do {
            invalidCount = 0;
            System.out.println("\n(2/4) Enter the passenger contact number.");
            System.out.println("Format example: 0123456789 or 01234567890");
            System.out.print("Enter here > ");
            contactNo = sc.nextLine().trim();

            if ((contactNo.equals("X") || contactNo.equals("x")) && contactNo.length() == 1) {
                return;
            }

            for (int i = 0; i < contactNo.length(); i++) {
                char c = contactNo.charAt(i);
                if (!Character.isDigit(c)) {
                    invalidCount++;
                }
            }

            System.out.println("Contact number length: " + contactNo.length());
            
            for (Passenger passenger : passengerList) {
                if (passenger.getContactNo().equals(contactNo)) {
                    invalidCount++; // IC already exists
                }
            }

            if (contactNo.length() != 10 && contactNo.length() != 11) {
                invalidCount++;
            }

            contactValid = invalidCount == 0;

            if (!contactValid) {
                System.out.println("Enter digits only or the length is not 10 or 11!");
            }

        } while (!contactValid);

        do {
            invalidCount = 0;
            System.out.println("\n(3/4) Enter the passenger IC.");
            System.out.println("Format example: 001122012345");
            System.out.print("Enter here > ");
            ic = sc.nextLine().trim();

            if ((ic.equals("X") || ic.equals("x")) && ic.length() == 1) {
                return;
            }

            for (int i = 0; i < ic.length(); i++) {
                char c = ic.charAt(i);
                if (!Character.isDigit(c)) {
                    invalidCount++;
                }
            }
            if (ic.length() != 12) {
                invalidCount++;
            }

            System.out.println("IC length: " + ic.length());

            for (Passenger passenger : passengerList) {
                if (passenger.getIc().equals(ic)) {
                    invalidCount++; // IC already exists
                }
            }

            icValid = invalidCount == 0;

            if (!icValid) {
                System.out.println("The entered IC is invalid or already exists. Please enter again!");
            }
        } while (!icValid);

        do {
            invalidCount = 0;
            System.out.println("\n(4/4) Enter the passenger gender.");
            System.out.println("M = Male or F = Female");
            System.out.print("Enter here > ");
            gender = sc.next().charAt(0);
            gender = toUpperCase(gender);

            if (gender == 'X') {
                return;
            }

            if (gender != 'M' && gender != 'F') {
                invalidCount++;
            }

            genderValid = invalidCount == 0;

            if (!genderValid) {
                System.out.println("Invalid input. Please enter again!");
            }

        } while (!genderValid);

        //Randomly generate passenger ID
        do {
            invalidCount = 0;
            id = "P" + (100 + (int) (Math.random() * 900));
            for (Passenger passenger : passengerList) {
                if (passenger.getId().equals(id)) {
                    invalidCount++; // IC already exists
                }
            }
        } while (invalidCount != 0);

        //Initialise tier to N (Without tier)
        tier = 'N';

        //Generate current system date
        dateJoined = LocalDate.now();

        System.out.println("PASSENGER DETAILS");
        System.out.println("=================");
        System.out.println("Passenger ID             : " + id);
        System.out.println("\nPassenger name           : " + name);
        System.out.println("Passenger contact number : " + contactNo);
        System.out.println("Passenger IC             : " + ic);
        System.out.println("Passenger gender         : " + gender);
        System.out.println("Passenger tier           : " + tier);
        System.out.println("\nJoined Date              : " + dateJoined);

        do {
            System.out.println("\nAre you sure you want to register passsenger with the information above (Y/N)?");
            System.out.print("Enter here > ");
            selection = sc.next().charAt(0);
            selection = toUpperCase(selection);

            if (selection == 'N') {
                return;
            } else if (selection != 'N' && selection != 'Y') {
                System.out.println("Invalid input! Please enter again!");
            }

        } while (selection != 'N' && selection != 'Y');

        System.out.println("The information has been added into the array list.");
        Passenger passenger = new Passenger(name, contactNo, ic, id, gender, dateJoined, tier);
        passengerList.add(passenger);
        try (FileWriter fileWriter = new FileWriter("PassengerFile.txt", true); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            bufferedWriter.write(name + "|" + contactNo + "|" + ic + "|" + id + "|" + gender + "|" + dateJoined + "|" + tier);
            bufferedWriter.newLine();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void searchPerson() {
        ArrayList<Passenger> passengerList = PassengerMain.readPassengerFile();
        Scanner sc = new Scanner(System.in).useDelimiter("\n");
        String searchInput;
        char selection = 'N';
        boolean isValid = false;
        System.out.println("================");
        System.out.println("SEARCH PASSENGER");
        System.out.println("================");
        do {
            searchInput = "";
            System.out.println("\nEnter passenger details by entering passenger name, IC or ID.");
            System.out.println("Press X to exit if you wish to exit.");
            System.out.print("Enter here > ");
            searchInput = sc.nextLine().trim();

            if ((searchInput.equals("X") || searchInput.equals("x")) && searchInput.length() == 1) {
                return;
            }

            for (int i = 0; i < passengerList.size(); i++) {
                Passenger passenger = passengerList.get(i);
                String name = passenger.getName();
                String ic = passenger.getIc();
                String id = passenger.getId();

                if (searchInput.compareTo(name) == 0 || searchInput.compareTo(ic) == 0 || searchInput.compareTo(id) == 0) {
                    isValid = true;
                    System.out.println("Results found. Here are the following passenger details.\n");
                    System.out.println(passenger.toString());
                    System.out.println("\n");

                    do {
                        System.out.println("Do you want to search another?");
                        System.out.print("Enter here (Y/N) > ");
                        selection = sc.next().charAt(0);
                        selection = toUpperCase(selection);

                        if (selection == 'Y') {
                            System.out.println("You have selected to search another.\n\n");
                        } else if (selection == 'N') {
                            return;
                        } else {
                            System.out.println("Invalid input. Please enter again.");
                        }
                    } while (selection != 'Y' && selection != 'N');
                }
            }
            if (!isValid) {
                System.out.println("Results not found. Please search again.");
            }

        } while (!isValid || selection == 'Y');
    }

    public void modifyPassenger() {
        ArrayList<Passenger> passengerList = PassengerMain.readPassengerFile();
        ArrayList<Booking> bookingList = BookingMain.readBookingFile();
        Scanner sc = new Scanner(System.in).useDelimiter("\n");
        String name, contactNo, ic, id, searchInput;
        char gender, tier, selectionC = 'N', selectionC2, selectionC3;
        boolean isValid = false, nameValid, contactValid, icValid, genderValid;
        int invalidCount, selectionI = 0, j;

        System.out.println("======================");
        System.out.println("EDIT PASSENGER DETAILS");
        System.out.println("======================");
        do {
            System.out.println("\nEnter passenger details by entering passenger name, IC or ID.");
            System.out.println("Press X to exit if you wish to exit.");
            System.out.print("Enter here > ");
            searchInput = sc.next().trim();

            if ((searchInput.equals("X") || searchInput.equals("x")) && searchInput.length() == 1) {
                return;
            }

            for (int i = 0; i < passengerList.size(); i++) {
                Passenger passenger = passengerList.get(i);
                String compareName = passenger.getName();
                String compareIc = passenger.getIc();
                String compareId = passenger.getId();

                if (searchInput.compareTo(compareName) == 0 || searchInput.compareTo(compareIc) == 0 || searchInput.compareTo(compareId) == 0) {
                    isValid = true;
                    System.out.println("Results found. Here are the following passenger details.\n");

                    do {
                        nameValid = false;
                        contactValid = false;
                        icValid = false;
                        genderValid = false;

                        System.out.println(passenger.toString());
                        System.out.println("\n");
                        System.out.println("Please select the area that you wish to modify.");
                        System.out.println("(A) Passenger Name   (B) Passenger Contact Number");
                        System.out.println("(C) Passenger IC     (D) passenger Gender");
                        System.out.println("(E) Exit/Finish Edit Passenger Details");
                        System.out.print("Enter here > ");
                        selectionC2 = sc.next().charAt(0);
                        selectionC2 = toUpperCase(selectionC2);

                        switch (selectionC2) {
                            case 'A':
                                do {
                                    invalidCount = 0;
                                    System.out.println("\nEnter the passenger name without special characters.");
                                    System.out.print("Enter here > ");
                                    name = sc.next().trim();

                                    if (name.equals("X") || name.equals("x")) {
                                        nameValid = true;
                                    } 
                                    else if(name.isEmpty()){
                                        System.out.println("Enter alphabelts and spaces only!");
                                    }
                                    else {
                                        for (j = 0; j < name.length(); j++) {
                                            char c = name.charAt(j);
                                            if (!Character.isAlphabetic(c) && !Character.isWhitespace(c)) {
                                                invalidCount++;
                                            }
                                        }
                                        nameValid = invalidCount == 0;

                                        if (!nameValid) {
                                            System.out.println("Enter alphabelts and spaces only!");
                                        } 
                                        else {
                                            //for (j = 0; j < name.length(); j++) {
                                            //bookingList.get(j).setName(name);
                                        //}
                                            passenger.setName(name);
                                            PassengerMain.updatePassengerFile(passengerList);
                                            BookingMain.writeBookingFile(bookingList);
                                            System.out.println("Passenger name has been updated.");
                                        }
                                    }

                                } while (!nameValid);
                                break;
                            case 'B':
                                do {
                                    invalidCount = 0;
                                    System.out.println("Enter the passenger contact number.");
                                    System.out.println("Format example: 0123456789 or 01234567890");
                                    System.out.print("Enter here > ");
                                    contactNo = sc.next();

                                    if ((contactNo.equals("X") || contactNo.equals("x")) && contactNo.length() == 1) {
                                        contactValid = true;
                                    } else {
                                        for (int k = 0; k < contactNo.length(); k++) {
                                            char c = contactNo.charAt(k);
                                            if (!Character.isDigit(c)) {
                                                invalidCount++;
                                            }
                                        }

                                        System.out.println("Contact number length: " + contactNo.length());

                                        if (contactNo.length() != 10 && contactNo.length() != 11) {
                                            invalidCount++;
                                        }

                                        contactValid = invalidCount == 0;

                                        if (!contactValid) {
                                            System.out.println("Enter digits only or the length is not 10 or 11!");
                                        } else {
                                            passenger.setContactNo(contactNo);
                                            PassengerMain.updatePassengerFile(passengerList);
                                            System.out.println("Passenger contact number has been updated.");
                                        }
                                    }

                                } while (!contactValid);
                                break;
                            case 'C':
                                do {
                                    invalidCount = 0;
                                    System.out.println("Enter the passenger IC.");
                                    System.out.println("Format example: 001122012345");
                                    System.out.print("Enter here > ");
                                    ic = sc.next();

                                    if ((ic.equals("X") || ic.equals("x")) && ic.length() == 1) {
                                        icValid = true;
                                    } else {
                                        for (int z = 0; z < ic.length(); z++) {
                                            char c = ic.charAt(z);
                                            if (!Character.isDigit(c)) {
                                                invalidCount++;
                                            }
                                        }

                                        if (ic.length() != 12) {
                                            invalidCount++;
                                        }

                                        System.out.println("IC length: " + ic.length());

                                        for (Passenger passenger2 : passengerList) {
                                            if (passenger2.getIc().equals(ic)) {
                                                invalidCount++; // IC already exists
                                            }
                                        }

                                        icValid = invalidCount == 0;

                                        if (!icValid) {
                                            System.out.println("The entered IC is invalid or already exists. Please enter again!");
                                        } else {
                                            passenger.setIc(ic);
                                            PassengerMain.updatePassengerFile(passengerList);
                                            System.out.println("Passenger IC has been updated.");
                                        }
                                    }
                                } while (!icValid);
                                break;
                            case 'D':
                                do {
                                    gender = passenger.getGender();
                                    if (gender == 'M') {
                                        System.out.println("Do you want to change the passenger gender from male to female?");
                                        System.out.print("Enter here (Y/N) > ");
                                        selectionC3 = sc.next().charAt(0);
                                        selectionC3 = toUpperCase(selectionC3);
                                        if (selectionC3 == 'Y') {
                                            genderValid = true;
                                            passenger.setGender('F');
                                            PassengerMain.updatePassengerFile(passengerList);
                                            System.out.println("Passenger gender has been updated.");
                                        } else if (selectionC3 == 'N') {
                                            genderValid = true;
                                        } else {
                                            System.out.println("Invalid input. Please enter again.");
                                        }
                                    } else if (gender == 'F') {
                                        System.out.println("Do you want to change the passenger gender from female to male?");
                                        System.out.print("Enter here (Y/N) > ");
                                        selectionC3 = sc.next().charAt(0);
                                        selectionC3 = toUpperCase(selectionC3);
                                        if (selectionC3 == 'Y') {
                                            genderValid = true;
                                            passenger.setGender('M');
                                            PassengerMain.updatePassengerFile(passengerList);
                                            System.out.println("Passenger gender has been updated.");
                                        } else if (selectionC3 == 'N') {
                                            genderValid = true;
                                        } else {
                                            System.out.println("Invalid input. Please enter again.");
                                        }
                                    }
                                } while (!genderValid);
                                break;
                            case 'E':
                                do {
                                    System.out.println("Do you want to back to the passenger information module menu?");
                                    System.out.print("Enter here (Y/N) > ");
                                    selectionC = sc.next().charAt(0);
                                    selectionC = toUpperCase(selectionC);
                                    if (selectionC == 'Y') {
                                        System.out.println("Now you will redirect back to the passenger information module menu.\n");
                                    } else if (selectionC == 'N') {
                                        System.out.println("Now you can continue edit the passenger details.\n");
                                    } else {
                                        System.out.println("Invalid input. Please enter again.");
                                    }
                                } while (selectionC != 'Y' && selectionC != 'N');
                                break;
                            default:
                                System.out.println("Invalid input. Please enter again.");

                        }
                    } while (selectionC2 != 'E' || selectionC != 'Y');

                }
            }

            if (!isValid) {
                System.out.println("Results not found. Please search again.");
            }

        } while (!isValid);

    }

    @Override
    public void displayPerson() {
        ArrayList<Passenger> passengerList = PassengerMain.readPassengerFile();
        int i;
        System.out.println("==================================");
        System.out.println("DISPLAY ALL PASSENGERS INFORMATION");
        System.out.println("==================================");
        System.out.printf("%-6s %-28s %-14s %-15s %-7s %-9s %-14s %-7s", "NO.", "PASSENGER NAME", "CONTACT NO.", "IC NO.", "ID", "GENDER", "JOINED DATE", "TIER");
        System.out.println("\n");
        for (i = 0; i < passengerList.size(); i++) {
            Passenger passenger = passengerList.get(i);
            System.out.printf("%-6d %-28s %-14s %-15s %-7s %-9c %-14s %-7c\n", i + 1, passenger.getName(), passenger.getContactNo(), passenger.getIc(), passenger.getId(), passenger.gender, passenger.dateJoined, passenger.passengerTier);
        }
        System.out.println("\nLEGEND:                 M = Male   F = Female   N = No Tier   S = Silver Tier   G = Gold Tier");
        System.out.println("\nTotal of " + i + " passenger details has been displayed.\n\n");

    }

    public void passengerTier() {
        ArrayList<Passenger> passengerList = PassengerMain.readPassengerFile();
        Scanner sc = new Scanner(System.in).useDelimiter("\n");
        String searchInput;
        double inputAmount = 0.0, price = 0.0, changes;
        int selectionI = 0;
        char selectionC = 0, selectionC2;
        boolean isValid = false, isValid2 = false;
        System.out.println("================================");
        System.out.println("UPGRADE/DOWNGRADE PASSENGER TIER");
        System.out.println("================================");

        do {
            System.out.println("\nEnter passenger that you wish to upgrade/downgrade the tier by entering passenger name, IC or ID.");
            System.out.println("Press X to exit if you wish to exit.");
            System.out.print("Enter here > ");
            searchInput = sc.next().trim();

            if ((searchInput.equals("X") || searchInput.equals("x")) && searchInput.length() == 1) {
                return;
            }

            for (int i = 0; i < passengerList.size(); i++) {
                Passenger passenger = passengerList.get(i);
                String name = passenger.getName();
                String ic = passenger.getIc();
                String id = passenger.getId();

                if (searchInput.compareTo(name) == 0 || searchInput.compareTo(ic) == 0 || searchInput.compareTo(id) == 0) {
                    isValid = true;
                    System.out.println("Results found. Here are the following passenger details.");
                    System.out.println(passenger.toString());
                    System.out.print("\n\nThe current passenger tier is ");
                    if (passenger.passengerTier == 'N') {
                        System.out.println("none.");
                    } else if (passenger.passengerTier == 'S') {
                        System.out.println("silver.");
                    } else if (passenger.passengerTier == 'G') {
                        System.out.println("gold.");
                    }

                    do {
                        try {
                            System.out.println("Please select the option below.");
                            System.out.println("(1) Upgrade Tier");
                            System.out.println("(2) Downgrade Tier");
                            System.out.println("(3) Back");
                            System.out.print("Enter your choice > ");
                            selectionI = sc.nextInt();
                        } catch (Exception ex) {
                            System.out.println("Enter digits only.");
                            sc.nextLine();
                        }

                        switch (selectionI) {
                            case 1:
                                if (passenger.passengerTier == 'G') {
                                    System.out.println("Currently you are on the highest tier.\n");
                                } else {
                                    System.out.println("Please select the tier that you want to upgrade. ");
                                    System.out.println("S - Silver Tier (15% discount)");
                                    System.out.println("Price: RM 125.00");
                                    System.out.println("G - Gold Tier (25% discount)");
                                    System.out.println("Price: RM 175.00");
                                    System.out.println("(Others) - Back");
                                    System.out.print("Enter your choice > ");
                                    selectionC = sc.next().charAt(0);
                                    selectionC = toUpperCase(selectionC);
                                    if (selectionC == 'S' || selectionC == 'G') {
                                        if (passenger.passengerTier == 'S' && selectionC == 'S') {
                                            System.out.println("Currently you are on the silver tier.\n");
                                        } else {
                                            do {
                                                System.out.print("Do you want to upgrade the tier to ");
                                                if (selectionC == 'S') {
                                                    System.out.println("silver?");
                                                } else if (selectionC == 'G') {
                                                    System.out.println("gold?");
                                                }
                                                System.out.print("Enter your selection (Y/N) > ");
                                                selectionC2 = sc.next().charAt(0);
                                                selectionC2 = toUpperCase(selectionC2);
                                                if (selectionC2 == 'Y') {
                                                    isValid2 = true;
                                                    System.out.println("You select to upgrade the tier.\n");
                                                    if (selectionC == 'S') {
                                                        price = 125.0;
                                                    } else if (selectionC == 'G') {
                                                        price = 175.0;
                                                    }

                                                    do {
                                                        try {
                                                            System.out.println("The total price is RM " + String.format("%.2f", price));
                                                            System.out.print("Enter input amount: RM ");
                                                            inputAmount = sc.nextDouble();
                                                        } catch (Exception ex) {
                                                            System.out.println("Enter digits only! ");
                                                            sc.nextLine();
                                                        }
                                                        if (inputAmount < price) {
                                                            System.out.println("The input amount is less than the price. Please enter again.");
                                                        } else {
                                                            changes = inputAmount - price;
                                                            System.out.println("Changes: RM" + String.format("%.2f", changes));
                                                            System.out.print("\nYour passenger tier has been successfully upgraded to ");
                                                            if (selectionC == 'S') {
                                                                System.out.println("silver.");
                                                                passenger.setPassengerTier('S');
                                                                PassengerMain.updatePassengerFile(passengerList);
                                                            } else if (selectionC == 'G') {
                                                                System.out.println("gold.");
                                                                passenger.setPassengerTier('G');
                                                                PassengerMain.updatePassengerFile(passengerList);
                                                            }
                                                        }
                                                    } while (inputAmount < price);

                                                } else if (selectionC2 == 'N') {
                                                    System.out.println("You select not to upgrade the tier.\n");
                                                    isValid2 = true;
                                                } else {
                                                    System.out.println("Invalid input. Please enter again.");
                                                }
                                            } while (selectionC2 != 'Y' && selectionC2 != 'N');
                                        }
                                    }
                                }
                                break;
                            case 2:
                                if (passenger.passengerTier == 'N') {
                                    System.out.println("Currently you are on the lowest tier.\n");
                                } else {
                                    System.out.println("Please select the tier that you want to downgrade. ");
                                    System.out.println("S - Silver Tier (15% discount)");
                                    System.out.println("N - Without tier/None");
                                    System.out.println("(Others) - Back");
                                    System.out.print("Enter your choice > ");
                                    selectionC = sc.next().charAt(0);
                                    selectionC = toUpperCase(selectionC);
                                    if (selectionC == 'S' || selectionC == 'N') {
                                        if (passenger.passengerTier == 'S' && selectionC == 'S') {
                                            System.out.println("Currently you are on the silver tier.\n");
                                        } else {
                                            do {
                                                System.out.print("Do you want to downgrade the tier to ");
                                                if (selectionC == 'S') {
                                                    System.out.println("silver?");
                                                } else if (selectionC == 'N') {
                                                    System.out.println("none?");
                                                }
                                                System.out.print("Enter your selection (Y/N) > ");
                                                selectionC2 = sc.next().charAt(0);
                                                selectionC2 = toUpperCase(selectionC2);
                                                if (selectionC2 == 'Y') {
                                                    isValid2 = true;
                                                    System.out.println("You select to downgrade the tier.\n");
                                                    System.out.print("\nYour passenger tier has been successfully downgraded to ");
                                                    if (selectionC == 'S') {
                                                        System.out.println("silver.");
                                                        passenger.setPassengerTier('S');
                                                        PassengerMain.updatePassengerFile(passengerList);
                                                    } else if (selectionC == 'N') {
                                                        System.out.println("none.");
                                                        passenger.setPassengerTier('N');
                                                        PassengerMain.updatePassengerFile(passengerList);
                                                    }
                                                } else if (selectionC2 == 'N') {
                                                    System.out.println("You select not to dwongrade the tier.\n");
                                                    isValid2 = true;
                                                } else {
                                                    System.out.println("Invalid input. Please enter again.");
                                                }
                                            } while (selectionC2 != 'Y' && selectionC2 != 'N');
                                        }
                                    }
                                }
                                break;
                            case 3:
                                return;
                            default:

                        }

                    } while (!isValid2);
                }
            }
            if (!isValid) {
                System.out.println("Results not found. Please search again.");
            }

        } while (!isValid);

    }

    @Override
    public String toString() {
        return super.toString()
                + "\nGender          : " + gender
                + "\nDate Joined     : " + dateJoined
                + "\nPassenger Tier  : " + passengerTier;
    }

}
