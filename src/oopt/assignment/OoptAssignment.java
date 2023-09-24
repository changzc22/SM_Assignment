package oopt.assignment;

import java.util.*;
import java.time.*;

public class OoptAssignment {

    public static void main(String[] args) {
        mainMenu();
    }

    public static void mainMenu() {

        Scanner input = new Scanner(System.in);
        boolean isValid = false, cont = true;
        int opt = 0;

        do {
            etsLogo();
            do {
                try {
                    System.out.print("Your selection > ");
                    opt = input.nextInt();

                    if (opt >= 1 && opt <= 5) {
                        isValid = true;
                    } else {
                        System.out.println("Invalid input. You should only select between 1 and 5");
                    }

                } catch (Exception e) {
                    System.out.println("You should not enter other characters");
                    input.nextLine();
                }
            } while (!isValid);

            switch (opt) {
                case 1 ->
                    StaffMain.staffMain();
                case 2 ->
                    PassengerMain.passengerMain();
                case 3 ->
                    BookingMain.bookingMain();
                case 4 ->
                    TrainMain.trainMain();
                default ->
                    cont = false;
            }

        } while (cont);
    }

    public static void etsLogo() {
        System.out.println("                   /EEEEEE\\\\    TTTTTT\\\\    SSSSSSS\\\\");
        System.out.println("                  //EEEEEEEE    TTTTTTTT    SSSSSSSS");
        System.out.println("                 ///EE          TTTTTTTT    SS");
        System.out.println("                0///EE            TTTT      SS");
        System.out.println("               00///EEEEEEEE      TTTT      SSSSSSSSS");
        System.out.println("              000///EEEEEEEE      TTTT       SSSSSSSSS");
        System.out.println("             0000///EE            TTTT              SS");
        System.out.println("            ////////EE            TTTT              SS");
        System.out.println("           /////////EEEEEEEE      TTTT       SSSSSSSSS");
        System.out.println("           \\\\///////EEEEEEEE      TTTT      SSSSSSSSS");
        System.out.println("");
        System.out.println("           E l e c t r i c   T r a i n   S e r v i c e\n");

        System.out.println("               =========================================");
        System.out.println("               |            **Modules Menu**           |");
        System.out.println("               =========================================");
        System.out.println("               |  1) Staff Information                 |");
        System.out.println("               |  2) Passenger Information             |");
        System.out.println("               |  3) Train Booking Information         |");
        System.out.println("               |  4) Train Information                 |");
        System.out.println("               |  5) Log out                           |");
        System.out.println("               =========================================");

    }

}
