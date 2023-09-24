package oopt.assignment;

import java.util.*;
import java.io.*;

public class Staff extends Person {

    private static ArrayList<Staff> staffList = new ArrayList<>();
    private String password;
    private int noOfBookingHandle = 0;

    public int getNumberOfStaff() {
        return staffList.size();
    }

    public ArrayList<Staff> getStaffList() {
        return staffList;
    }
   
    public Staff() {
    }

    ;
    public Staff(String name, String contactNo, String ic,String id) {
        super(name, contactNo, ic,id);
    }

    public Staff(String name, String contactNo, String ic,String id,String password, int noOfBookingHandle) {
        super(name, contactNo, ic,id);
        
        this.password = password;
        this.noOfBookingHandle = noOfBookingHandle;
    }

    

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNoOfBookingHandle(int noOfBookingHandle) {
        this.noOfBookingHandle = noOfBookingHandle;
    }

    
    

    public String getPassword() {
        return password;
    }

    public int getNoOfBookingHandle() {
        return noOfBookingHandle;
    }

    @Override
    public String toString() {
        return super.toString() + "\nNumber Of Booking Handle: " + noOfBookingHandle;
    }
    
    public static void updateStaffFile() {
    try (FileWriter fileWriter = new FileWriter("StaffFile.txt");
         BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

        for (Staff staff : staffList) {
            
            bufferedWriter.write(staff.getName() + "|" + staff.getContactNo() + "|" + staff.getIc() + "|" + staff.getId() + "|" + staff.getPassword() + "|" + staff.getNoOfBookingHandle());
            bufferedWriter.newLine(); 
            
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    //method overloading 
    public static void updateStaffFile(ArrayList<Staff>staffList) {
    try (FileWriter fileWriter = new FileWriter("StaffFile.txt");
         BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

        for (Staff staff : staffList) {
            
            bufferedWriter.write(staff.getName() + "|" + staff.getContactNo() + "|" + staff.getIc() + "|" + staff.getId() + "|" + staff.getPassword() + "|" + staff.getNoOfBookingHandle());
            bufferedWriter.newLine(); 
            
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    public String toString2() {
        return "\nEnter the option that you want to modify " + "\n" + "A)Name B)Contact Number C)IC Number D)Password E)Exit" + "\n" + "Your choice:";
    }

    public boolean isNameValid(String name) {
        boolean isValidName = true;

        name = name.toUpperCase();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            if (!Character.isAlphabetic(c) && !Character.isWhitespace(c)) {
                isValidName = false;
                break;
            }
        }

        if (name.isEmpty()) {
            isValidName = false;
        }
        name = name.trim();
        return isValidName;
    }

    public boolean isCNValid(String cn) {
        boolean isValid = true;

        if (cn.isEmpty()) {
            isValid = false;
        } else {
            for (int i = 0; i < cn.length(); i++) {
                char c = cn.charAt(i);
                if (!Character.isDigit(c)) {
                    isValid = false;
                    break;
                }
            }

            if (cn.length() != 10 && cn.length() != 11) {
                isValid = false;
            }

            if (cn.charAt(0) != '0') {
                isValid = false;
            }
            for (Staff staff : staffList) {
                if (staff.getContactNo().equals(cn)) {
                    isValid = false; // Contact number already exists
                    break;
                }
            }
        }

        return isValid;
    }

    public boolean isICValid(String ic) {
        boolean isValid = true;

        if (ic.isEmpty()) {
            isValid = false;
        } else {
            for (int i = 0; i < ic.length(); i++) {
                char c = ic.charAt(i);
                if (!Character.isDigit(c)) {
                    isValid = false;
                    break;
                }
            }

            if (ic.length() != 12) {
                isValid = false;
            }
            for (Staff staff : staffList) {
                if (staff.getIc().equals(ic)) {
                    isValid = false; // IC already exists
                    break;
                }
            }
        }

        return isValid;
    }

    public boolean isIDValid(String ID) {
        ID = ID.toUpperCase();

        if (ID.length() != 4 || ID.trim().isEmpty() || ID.charAt(0) != 'S') {
            return false;
        }

        for (int i = 1; i < ID.length(); i++) {
            char c = ID.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        for (Staff staff : staffList) {
            if (staff.getId().equals(ID)) {
                return false;
            }
        }

        return true;
    }

    public boolean isPWValid(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasAlphabetOrSymbol = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isLetter(c) || !Character.isLetterOrDigit(c)) {
                hasAlphabetOrSymbol = true;
                break;
            }
        }

        return hasAlphabetOrSymbol;
    }

    public ArrayList<Staff> modifyStaff() {
    ArrayList<Staff> staffList = readStaffFile();
    Scanner scanner = new Scanner(System.in);

    boolean isValidName, isCNtrue, isValidIC, isValidpw;
    String name, cn, ic, pw;

    while (true) {
        System.out.print("Enter staff ID (X to return): ");
        String ID = scanner.nextLine().toUpperCase();

        if (ID.equals("X")) {
            return staffList;
        }

        boolean staffFound = false;

        for (Staff s : staffList) {
            if (s.getId().equals(ID)) {
                staffFound = true;

                System.out.print(toString2());
                String choice = scanner.nextLine().toUpperCase();
                if (choice.equals("A")) {
                    do {
                        System.out.print("Update staff name (X to exit): ");
                        name = scanner.nextLine().toUpperCase();
                        if (name.equals("X")) {
                            return staffList;
                        }
                        isValidName = isNameValid(name);

                        if (!isValidName) {
                            System.out.println("Invalid name! Please try again.");
                        }
                    } while (!isValidName);

                    s.setName(name);
                    updateStaffFile(staffList);
                    System.out.println("Name updated successfully.");
                    return staffList;
                } else if (choice.equals("B")) {
                    do {
                        System.out.print("Modify your contact number (X to exit): ");
                        cn = scanner.nextLine().toUpperCase();
                        if (cn.equals("X")) {
                            return staffList;
                        }
                        isCNtrue = isCNValid(cn);

                        if (!isCNtrue) {
                            System.out.println("Invalid/duplicate contact number! Please try again.");
                        }
                    } while (!isCNtrue);

                    s.setContactNo(cn);
                    updateStaffFile(staffList);
                    System.out.println("Contact number updated successfully.");
                    return staffList;
                } else if (choice.equals("C")) {
                    do {
                        System.out.print("Update IC number (X to exit/without -): ");
                        ic = scanner.nextLine().toUpperCase();
                        if (ic.equals("X")) {
                            return staffList;
                        }
                        isValidIC = isICValid(ic);

                        if (!isValidIC) {
                            System.out.println("Invalid/duplicate IC number! Please try again.");
                        }
                    } while (!isValidIC);

                    s.setIc(ic);
                    updateStaffFile(staffList);
                    System.out.println("IC number updated successfully.");
                    return staffList;
                } else if (choice.equals("D")) {
                    do {
                        System.out.print("Please set password (Password must be >=8 characters with at least 1 alphabet/symbol) (X to exit): ");
                        pw = scanner.nextLine();
                        if (pw.equals("X") || pw.equals("x")) {
                            return staffList;
                        }
                        isValidpw = isPWValid(pw);

                        if (!isValidpw) {
                            System.out.println("Invalid password! Please try again.");
                        }
                    } while (!isValidpw);

                    s.setPassword(pw);
                    updateStaffFile(staffList);
                    System.out.println("Password updated successfully.");
                    return staffList;
                } else if (choice.equals("E")) {
                    return staffList;
                } else {
                    System.out.println("Invalid input!");
                }
            }
        }

        if (!staffFound) {
            System.out.println("Invalid staff ID!");
        }
    }
}


    public ArrayList<Staff> createStaff() {
        String name, cn, ic, ID, pw;
        Scanner scanner = new Scanner(System.in);
        boolean isValidName, isCNtrue, isValidIC, isValidStaffID, isValidpw;
          ArrayList<Staff>staffList=readStaffFile();
        do {
            System.out.print("Enter the staff name(X to exit): ");
            name = scanner.nextLine();
            name = name.toUpperCase();
            if (name.equals("X")) {
                return staffList;
            }
            isValidName = isNameValid(name);

            if (!isValidName) {
                System.out.println("Invalid name! Please try again.");
            }
        } while (!isValidName);

        do {
            System.out.print("Enter your contact number(X to exit): ");
            cn = scanner.nextLine().toUpperCase();
            if (cn.equals("X")) {
                return staffList;
            }
            isCNtrue = isCNValid(cn);

            if (!isCNtrue) {
                System.out.println("Invalid/duplicate contact number! Please try again.");
            }

        } while (!isCNtrue);

        do {
            System.out.print("Enter your IC number (X to exit/without -): ");
            ic = scanner.nextLine().toUpperCase();
            if (ic.equals("X")) {
                return staffList;
            }
            isValidIC = isICValid(ic);

            if (!isValidIC) {
                System.out.println("Invalid/duplicate IC number! Please try again.");
            }

        } while (!isValidIC);

        do {
            System.out.print("Enter your ID (S001 format/X to exit): ");
            ID = scanner.nextLine();
            ID = ID.toUpperCase();
            if (ID.equals("X")) {
                return staffList;
            }
            isValidStaffID = isIDValid(ID);

            if (!isValidStaffID) {
                System.out.println("Invalid/duplicate Staff ID! Please try again.");
            }

        } while (!isValidStaffID);

        do {
            System.out.print("Please set your password (Password must be >=8 characters with at least 1 alphabet/symbol)/(X to exit): ");
            pw = scanner.nextLine();
            if (pw.equals("X") || pw.equals("x")) {
                return staffList;
            }
            isValidpw = isPWValid(pw);

            if (!isValidpw) {
                System.out.println("Invalid password! Please try again.");
            }

        } while (!isValidpw);

        Staff staff = new Staff(name, cn, ic, ID, pw, 0);
        staffList.add(staff);
        try (FileWriter fileWriter = new FileWriter("StaffFile.txt", true);
         BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

        
        bufferedWriter.write(name + "|" + cn + "|" + ic + "|" + ID + "|" + pw + "|0");
        bufferedWriter.newLine(); 

        
        bufferedWriter.close();
        
    } catch (IOException e) {
        e.printStackTrace();
    }
        System.out.print("Staff added ! Press 'enter' key to exit to staff menu....");
        String c = scanner.nextLine();

        return staffList;
    }

    public void displayPerson() {
        Scanner scanner = new Scanner(System.in);
         ArrayList<Staff>staffList=readStaffFile();
        String c;
        if (staffList.isEmpty()) {
            System.out.println("No staff members to display.");
            System.out.print("Press 'enter' key to exit to staff menu....");
            c = scanner.nextLine();
            return;
        }

        System.out.println("List of Staff Members:");
        for (int i = 0; i < staffList.size(); i++) {
            Staff staff = staffList.get(i);
            System.out.println("Staff " + (i + 1) + ":");
            System.out.println(staff.toString());
            System.out.println("-----------");
        }
        System.out.print("Press 'Enter' to exit to staff menu....");
        c = scanner.nextLine();
    }

    public ArrayList<Staff> deleteStaff() {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Staff>staffList=readStaffFile();
        if (staffList.isEmpty()) {
            System.out.println("No staff members to delete.");
            System.out.print("Press 'Enter' key to exit to staff menu....");
            String c = scanner.nextLine();
            return staffList;
        }
        while (true) {
            System.out.print("Please type the Staff ID to remove staff(Press X to exit to main menu):");
            String ID = scanner.nextLine().toUpperCase();
            System.out.println("");
            boolean isValidInput = false;

            if (ID.equals("X")) {
                return staffList;
            }

            for (Staff s : staffList) {
                if (ID.equals(s.getId())) {
                    System.out.println(s.toString() + "\n");
                    while (true) {
                        System.out.print("Do you sure you want to delete this Staff?(Y/N):");
                        String YN = scanner.nextLine().toUpperCase();
                        if (YN.equals("Y")) {
                            staffList.remove(s);
                            System.out.println("Staff deleted.");
                            updateStaffFile(staffList);
                            isValidInput = true;
                            break;
                        } else if (YN.equals("N")) {
                            System.out.println("Deletion canceled.");
                            isValidInput = true;
                            break;
                        } else {
                            System.out.println("Invalid input! Please enter 'Y' or 'N'.");
                        }
                    }
                    break;
                }
            }

            if (!isValidInput) {
                System.out.println("Staff ID not found,please try again.");
            } else {
                break;
            }
        }
        return staffList;
    }

    public void searchPerson() {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Staff>staffList=readStaffFile();
        while (true) {
            System.out.print("Enter the staff ID that you want to search(X to exit):");
            String ID = scanner.nextLine().toUpperCase();
            boolean staffFound = false;
            if (ID.equals("X")) {
                return;
            }
            for (Staff s : staffList) {
                if (s.getId().equals(ID)) {
                    System.out.println("\n");
                    staffFound = true;
                    System.out.println(s.toString());
                    System.out.println("");
                    System.out.print("Press 'Enter' key to exit to staff menu....");
                    String c = scanner.nextLine();
                    return;
                }
            }
            if (!staffFound) {
                System.out.println("Staff not found!");
            }
        }

    }

    public void loginStaff() {
        Scanner scanner = new Scanner(System.in);
          ArrayList<Staff>staffList=readStaffFile();
        while (true) {
            
            System.out.print("Enter your staff ID (X to end program):");
            String ID = scanner.nextLine().toUpperCase();
            if (ID.equals("X")) {
                System.exit(0);
            }
            System.out.print("Enter your password(Z to forget password):");
            String pw = scanner.nextLine();
            if(pw.equals("X")){
                System.exit(0);
            }  else if (pw.equals("Z")) {
               modifyPassword();
                
            }
            else {
                boolean staffFound = false;

                for (Staff s : staffList) {
                    if (s.getId().equals(ID)) {
                        while (true) {
                            
                            if (s.getPassword().equals(pw)) {
                                System.out.println("Welcome " + s.getName() + "\n");
                                System.out.print("Press 'Enter' key to continue ...");
                                String c = scanner.nextLine();
                                staffFound = true;
                                StaffMain staff=new StaffMain();
                                staff.ID=ID;
                                break; // Exit the password input loop
                            } else {
                                break;
                            }
                        }

                        if (staffFound) {
                            break; // Exit the staff loop
                        }
                    }
                }

                if (!staffFound) {
                    System.out.println("invalid Staff ID/Password");
                }
                if (staffFound) {
                    break;
                }
            }
        }
    }

    public void modifyPassword() {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Staff>staffList=readStaffFile();
        while (true) {
            boolean isValidpw = false;
            System.out.print("Enter your staff ID to change password(X to return):");
            String ID = scanner.nextLine().toUpperCase();

            if (ID.equals("X")) {
                return;
            } else {
                boolean staffFound = false;

                for (Staff s : staffList) {
                    if (s.getId().equals(ID)) {
                        while (true) {
                            System.out.print("Enter your IC(X to end program):");
                            String ic = scanner.nextLine();
                            if (ic.equals("X")) {
                                return;
                            }

                            if (s.getIc().equals(ic)) {
                                System.out.println("IC correct!");
                                while (true) {
                                    System.out.print("Enter your new password here:");
                                    String pw = scanner.nextLine();
                                    isValidpw = isPWValid(pw);
                                    if (isValidpw) {
                                        s.setPassword(pw);
                                        System.out.println("Password updated successfully.");
                                        staffFound = true;
                                        updateStaffFile(staffList);
                                        break;
                                        
                                    } else {
                                        System.out.println("Invalid Password. Password must be at least 8 characters long and at least 1 digit/symbol.");
                                    }
                                }
                            } else {
                                System.out.println("Invalid IC");
                            }

                            if (staffFound) {
                                break; // Exit the staff loop
                            }
                        }
                    }
                }

                if (!staffFound) {
                    System.out.println("Staff ID not Found!");
                }
                if (staffFound) {
                    break;
                }
            }
        }
    }
    public static ArrayList<Staff>readStaffFile(){
    ArrayList <Staff>staffList=new ArrayList<>();
     try {
        BufferedReader reader = new BufferedReader(new FileReader("StaffFile.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split("\\|");
            String name = fields[0];
            String contactNo = fields[1];
            String ic = fields[2];
            String ID = fields[3];
            String password = fields[4];
            int noOfBookingHandle = Integer.parseInt(fields[5]);
            Staff staff1 = new Staff(name, contactNo, ic, ID, password, noOfBookingHandle);
            staffList.add(staff1);
        }
        reader.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return staffList;
    }
    public static ArrayList<Staff>updateNo(ArrayList<Staff>staffList,String ID){
        for(Staff s:staffList){
        if(ID.equals(s.getId())){
        s.setNoOfBookingHandle(s.getNoOfBookingHandle()+1);
        updateStaffFile(staffList);
        }
        
        }
        return staffList;
    }
}

