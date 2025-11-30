package oopt.assignment.model;

import oopt.assignment.util.AppConstants;
import oopt.assignment.util.ErrorMessage;

import java.io.*;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles file I/O for Staff data.
 * Implements logging and exception handling standards.
 */
public class StaffRepository implements IStaffRepository {

    private static final Logger logger = Logger.getLogger(StaffRepository.class.getName());

    /**
     * Reads the file and returns a Map of Staff objects.
     * Uses LinkedHashMap to preserve the insertion order of the file.
     * @return a Map of staff objects
     */
    @Override
    public LinkedHashMap<String, Staff> getAll() {
        LinkedHashMap<String, Staff> staffMap = new LinkedHashMap<>();
        File file = new File(AppConstants.STAFF_FILE_PATH);

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
            // Requirement: Use Logger instead of System.err
            logger.log(Level.SEVERE, ErrorMessage.FILE_READ_ERROR, e);
        }
        return staffMap;
    }

    /**
     * Overwrites the file with the current list of staff.
     * @param staffList current list of staff.
     */
    @Override
    public void saveAll(Collection<Staff> staffList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(AppConstants.STAFF_FILE_PATH, false))) {
            for (Staff staff : staffList) {
                writer.write(formatStaffForFile(staff));
                writer.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, ErrorMessage.FILE_WRITE_ERROR, e);
        }
    }

    // --- Private Helpers for Clean Code ---

    /**
     * Create new file if the file does not exists from the public class
     * @param file instance
     */
    private void createNewFile(File file) {
        try {
            boolean created = file.createNewFile();
            if (created) {
                logger.info("New data file created: " + AppConstants.STAFF_FILE_PATH);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, ErrorMessage.FILE_CREATE_ERROR, e);
        }
    }

    /**
     * Parses a pipe-separated string into a Staff object.
     * Includes Try-Catch for data corruption safety.
     * @param line pipe-separated string
     * @return Staff object, or null if parsing fails
     */
    private Staff parseLineToStaff(String line) {
        try {
            String[] fields = line.split("\\|");
            return new Staff(fields[0], fields[1], fields[2], fields[3], fields[4], Integer.parseInt(fields[5]));
        } catch (Exception e) {
            // Log specific data corruption errors
            logger.log(Level.WARNING, ErrorMessage.CORRUPTED_DATA + line);
            return null;
        }
    }

    /**
     * Format the staff object for storing into the file
     * @param s staff object
     * @return formatted staff details in String
     */
    private String formatStaffForFile(Staff s) {
        return String.join("|", s.getName(), s.getContactNo(), s.getIc(), s.getId(), s.getPassword(), String.valueOf(s.getNoOfBookingHandle()));
    }
}