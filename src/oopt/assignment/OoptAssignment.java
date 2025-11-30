package oopt.assignment;

import oopt.assignment.model.StaffRepository;
import oopt.assignment.service.StaffService;
import oopt.assignment.ui.BookingUI;
import oopt.assignment.ui.MainMenuOption;
import oopt.assignment.ui.MainUI;
import oopt.assignment.ui.StaffUI;
import oopt.assignment.util.ErrorMessage; // Import the utility

import java.util.Scanner;

public class OoptAssignment {

    public static void main(String[] args) {

        StaffRepository staffRepo = new StaffRepository();
        StaffService staffService = new StaffService(staffRepo);

        boolean exitApp = false;
        Scanner scanner = new Scanner(System.in);

        do {
            MainUI.clearScreen();
            MainUI.displayLogo();

            String loginStaffID = StaffUI.handleLogin(staffService);

            boolean stayInMenu = true;
            do {
                MainUI.clearScreen();
                MainUI.displayLogo();
                MainUI.displayMainMenu();

                MainMenuOption option = getSelection(scanner);

                if (option == null) {
                    // CONSTANT : Generic invalid selection
                    System.out.println(ErrorMessage.INVALID_MENU_SELECTION);
                    MainUI.systemPause();
                    continue;
                }

                switch (option) {
                    case STAFF -> StaffMain.staffMain(loginStaffID, staffService);

                    case PASSENGER -> PassengerMain.passengerMain();

                    case BOOKING -> new BookingUI().start();

                    case TRAIN -> TrainMain.trainMain();

                    case LOGOUT -> {
                        System.out.println("Logging out...");
                        stayInMenu = false;
                    }
                }

            } while (stayInMenu);

        } while (!exitApp);
    }

    /**
     * Helpers specifically for getting valid ENUM input.
     * @param scanner input
     * @return user main menu selection
     */
    private static MainMenuOption getSelection(Scanner scanner) {
        System.out.print("Your selection > ");
        try {
            int val = Integer.parseInt(scanner.nextLine());
            return MainMenuOption.fromId(val);
        } catch (NumberFormatException e) {
            // CONSTANT : Specific number format error
            System.out.println(ErrorMessage.INPUT_NOT_NUMBER);
            return null;
        }
    }
}