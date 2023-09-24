package oopt.assignment;

import java.util.*;
import java.io.*;

public class StaffMain {


    public static void staffMain() {

        Staff staff = new Staff();
        ArrayList<Staff> staffList = staff.getStaffList();

        Scanner sc = new Scanner(System.in);
        boolean exit = false;
        

        do {
            System.out.println("  SSS  TTTTT AAAAA  FFFFF  FFFFF ");
            System.out.println(" S       T   A   A  F      F     ");
            System.out.println("  SSS    T   AAAAA  FFFF   FFFF  ");
            System.out.println("     S   T   A   A  F      F     ");
            System.out.println(" SSSS    T   A   A  F      F     ");
            System.out.println("|=======================================|");
            System.out.println("|     Staff Information Module          |");
            System.out.println("|=======================================|");
            System.out.println("|  1. Add new Staff                     |");
            System.out.println("|  2. Edit information                  |");
            System.out.println("|  3. Delete staff                      |");
            System.out.println("|  4. Display staff                     |");
            System.out.println("|  5. Search staff                      |");
            System.out.println("|  6. Exit to main menu                 |");
            System.out.println("|=======================================|");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();
            System.out.println();
            if (choice.equals("1")) {
                staff.createStaff();
            } else if (choice.equals("2")) {
                staff.modifyStaff();
            } else if (choice.equals("3")) {
                staff.deleteStaff();
            } else if (choice.equals("4")) {
                staff.displayPerson();
            } else if (choice.equals("5")) {
                staff.searchPerson();
            } else if (choice.equals("6")) {
                exit = true;
            } else {
                System.out.println("Invalid number. Please type again.");
                System.out.println();
            }

        } while (!exit);

    }

}
