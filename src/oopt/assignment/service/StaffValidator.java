package oopt.assignment.service;

import oopt.assignment.model.Staff;
import java.util.Map;

/**
 * Validates input data for Staff operations.
 * Segregated from the Service to satisfy Single Responsibility Principle (SRP).
 */
public class StaffValidator {

    /**
     * Validates name contains only alphabets and spaces.
     * @param name from user input
     */
    public boolean isNameValid(String name) {
        return name != null && !name.trim().isEmpty() && name.matches("[a-zA-Z\\s]+");
    }

    /**
     * Validates contact number format (starts with 0, 10-11 digits) and checks for duplicates.
     * @param cn HandPhone number from user input
     * @param existingStaff from database
     */
    public boolean isCNValid(String cn, Map<String, Staff> existingStaff) {
        if (cn == null || !cn.matches("0\\d{9,10}")) return false;
        return existingStaff.values().stream().noneMatch(s -> s.getContactNo().equals(cn));
    }

    /**
     * Validates IC format (12 digits) and checks for duplicates.
     * @param ic from user input
     * @param existingStaff from database
     */
    public boolean isICValid(String ic, Map<String, Staff> existingStaff) {
        if (ic == null || !ic.matches("\\d{12}")) return false;
        return existingStaff.values().stream().noneMatch(s -> s.getIc().equals(ic));
    }

    /**
     * Validates Staff ID format (e.g., S001) and checks if ID already exists.
     * @param id Staff ID from user input
     * @param existingStaff from database
     */
    public boolean isIDValid(String id, Map<String, Staff> existingStaff) {
        if (id == null || !id.matches("S\\d{3}")) return false;
        return !existingStaff.containsKey(id);
    }

    /**
     * Enforces password complexity (Min 8 chars, must contain letters).
     * @param password from user input
     */
    public boolean isPasswordComplexityValid(String password) {
        return password != null && password.length() >= 8 && password.matches(".*[a-zA-Z].*");
    }
}