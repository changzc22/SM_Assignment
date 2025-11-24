package oopt.assignment.model;

import java.io.*;
import java.util.Collection;
import java.util.LinkedHashMap;

public class StaffRepository implements IStaffRepository {

    private static final String STAFF_FILE_PATH = "StaffFile.txt";

    /**
     * Reads the file and returns a Map of Staff objects.
     * Uses LinkedHashMap to preserve the insertion order of the file.
     */
    @Override
    public LinkedHashMap<String, Staff> getAll() {
        LinkedHashMap<String, Staff> staffMap = new LinkedHashMap<>();
        File file = new File(STAFF_FILE_PATH);

        if (!file.exists()) {
            createNewFile(file);
            return staffMap;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Staff staff = parseLineToStaff(line);
                if (staff != null) {
                    staffMap.put(staff.getId(), staff);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading staff file: " + e.getMessage());
        }
        return staffMap;
    }

    /**
     * Overwrites the file with the current list of staff.
     */
    @Override
    public void saveAll(Collection<Staff> staffList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STAFF_FILE_PATH, false))) {
            for (Staff staff : staffList) {
                writer.write(formatStaffForFile(staff));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to staff file: " + e.getMessage());
        }
    }

    // --- Private Helpers for Clean Code ---

    private void createNewFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.err.println("Error creating staff file: " + e.getMessage());
        }
    }

    /**
     * Parses a pipe-separated string into a Staff object.
     */
    private Staff parseLineToStaff(String line) {
        try {
            String[] fields = line.split("\\|");
            return new Staff(fields[0], fields[1], fields[2], fields[3], fields[4], Integer.parseInt(fields[5]));
        } catch (Exception e) {
            System.err.println("Skipping corrupted line: " + line);
            return null;
        }
    }

    private String formatStaffForFile(Staff s) {
        return String.join("|", s.getName(), s.getContactNo(), s.getIc(), s.getId(), s.getPassword(), String.valueOf(s.getNoOfBookingHandle()));
    }
}