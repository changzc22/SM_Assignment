package oopt.assignment.ui;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles common visual elements and console utilities used across the entire app.
 * This separates "Visuals" (Logo/Clearing Screen) from "Logic".
 */
public class MainUI {

    private static final Logger logger = Logger.getLogger(MainUI.class.getName());
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * ETS logo display
     */
    public static void displayLogo() {
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

    /**
     * Display main menu of the system
     */
    public static void displayMainMenu() {
        System.out.println("             =========================================");
        System.out.println("             |            **Modules Menu**           |");
        System.out.println("             =========================================");
        Arrays.stream(MainMenuOption.values())
                .forEach(opt -> System.out.printf("             |  %d) %-33s |\n", opt.getId(), opt.getLabel()));
        System.out.println("             =========================================");
    }

    /**
     * Clear screen for clean display
     */
    public static void clearScreen() {
        try {
            Robot rob = new Robot();
            rob.keyPress(KeyEvent.VK_CONTROL);
            rob.keyPress(KeyEvent.VK_L);
            rob.keyRelease(KeyEvent.VK_L);
            rob.keyRelease(KeyEvent.VK_CONTROL);
            Thread.sleep(10);
        } catch (AWTException | InterruptedException e) {
            logger.log(Level.WARNING, "Clear screen failed: " + e.getMessage());
        }
    }

    /**
     * System pause to allow the user see the current status before proceed to next
     */
    public static void systemPause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}
