package oopt.assignment.model;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Collection;

public class StaffRepository {

    private static final String STAFF_FILE_PATH = "StaffFile.txt";

    /**
     * Reads staff records directly into a LinkedHashMap for fast access.
     * Key: Staff ID, Value: Staff Object
     */
    public LinkedHashMap<String, Staff> readStaffFile() {
        // We use LinkedHashMap to maintain the order from the file while allowing O(1) access
        LinkedHashMap<String, Staff> staffMap = new LinkedHashMap<>();

        File file = new File(STAFF_FILE_PATH);

        if (!file.exists()) {
            System.out.println("Staff file not found, creating a new one: " + STAFF_FILE_PATH);
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating staff file: " + e.getMessage());
            }
            return staffMap;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] fields = line.split("\\|");
                String name = fields[0];
                String contactNo = fields[1];
                String ic = fields[2];
                String id = fields[3];
                String password = fields[4];
                int noOfBookingHandle = Integer.parseInt(fields[5]);

                Staff staff = new Staff(name, contactNo, ic, id, password, noOfBookingHandle);
                // Put directly into the map. Key is ID.
                staffMap.put(staff.getId(), staff);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading staff file: " + e.getMessage());
        }
        return staffMap;
    }

    /**
     * Writes a collection of staff records to the file.
     * Changed to accept 'Collection' so it handles both Lists and Map Values.
     */
    public void updateStaffFile(Collection<Staff> staffList) {
        try (FileWriter fileWriter = new FileWriter(STAFF_FILE_PATH, false);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            for (Staff staff : staffList) {
                bufferedWriter.write(staff.getName() + "|"
                        + staff.getContactNo() + "|"
                        + staff.getIc() + "|"
                        + staff.getId() + "|"
                        + staff.getPassword() + "|"
                        + staff.getNoOfBookingHandle());
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to staff file: " + e.getMessage());
        }
    }
}