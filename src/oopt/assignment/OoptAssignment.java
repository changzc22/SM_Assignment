package oopt.assignment;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.*;
import oopt.assignment.model.StaffRepository;
import oopt.assignment.service.StaffService;
import oopt.assignment.ui.StaffUI;

public class OoptAssignment {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        boolean isValid = false, cont = true, exit = false;
        int opt = 0;
        String loginStaffID;

        // --- Setup Staff Layers for Login ---
        // The login process needs the service layer, so we instantiate it here.
        StaffRepository staffRepo = new StaffRepository();
        StaffService staffService = new StaffService(staffRepo);
        // --- End Setup ---

        do {
            OoptAssignment.clearScreen();
            etsLogo();

            // --- Updated Login Call ---
            // Call the static handleLogin method from StaffUI
            loginStaffID = StaffUI.handleLogin(staffService);
            // --- End Updated Login Call ---

            do {

                OoptAssignment.clearScreen();
                etsLogo();
                mainMenu();

                isValid = false;
                cont = true;
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
                            StaffMain.staffMain(loginStaffID);
                    case 2 ->
                            PassengerMain.passengerMain(); // Assuming this class exists
                    case 3 ->
                            BookingMain.bookingMain(loginStaffID); // Assuming this class exists
                    case 4 ->
                            TrainMain.trainMain(); // Assuming this class exists
                    default ->
                            cont = false;
                }

            } while (cont);
        } while (!exit);

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

    }

    public static void mainMenu(){
        System.out.println("               =========================================");
        System.out.println("               |            **Modules Menu**           |");
        System.out.println("               =========================================");
        System.out.println("               |  1) Staff                             |");
        System.out.println("               |  2) Passenger                         |");
        System.out.println("               |  3) Booking                           |");
        System.out.println("               |  4) Train                             |");
        System.out.println("               |  5) Log out                           |");
        System.out.println("               =========================================");

    }

    public static void systemPause() {
        System.out.print("Press any key to continue...");
        try {
            System.in.read();
        } catch (Exception e) {
            System.err.println("Error system pause: " + e.getMessage());
        }
    }

    public static void clearScreen() {
        try {
            Robot rob = new Robot();
            try {
                rob.keyPress(KeyEvent.VK_CONTROL); // press "CTRL"
                rob.keyPress(KeyEvent.VK_L); // press "L"
                rob.keyRelease(KeyEvent.VK_L); // unpress "L"
                rob.keyRelease(KeyEvent.VK_CONTROL); // unpress "CTRL"
                Thread.sleep(10); // add delay in milisecond, if not there will automatically stop after clear
            } catch (InterruptedException e) {
                System.err.println("Error clear screen: " + e.getMessage());
            }
        } catch (AWTException e) {
            System.err.println("Error clear screen: " + e.getMessage());
        }
    }

}