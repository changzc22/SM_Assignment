package ets.service;

import ets.model.Staff;
import ets.util.AppConstants;

import java.util.Map;

/**
 * Validates input data for Staff operations.
 * Segregated from the Service to satisfy Single Responsibility Principle (SRP).
 */
public class StaffValidator {

    /**
     * Validates name contains only alphabets and spaces.
     * @param name from user input
     * @return true if valid
     */
    public boolean isNameValid(String name) {
        return name != null && !name.trim().isEmpty() && name.matches(AppConstants.REGEX_NAME);
    }

    /**
     * Validates contact number format and checks for duplicates via Stream.
     * @param cn HandPhone number from user input
     * @param existingStaff from database
     * @return true if valid and unique
     */
    public boolean isCNValid(String cn, Map<String, Staff> existingStaff) {
        if (cn == null || !cn.matches(AppConstants.REGEX_CONTACT)) return false;
        // Requirement: Use stream to loop
        return existingStaff.values().stream()
                .noneMatch(s -> s.getContactNo().equals(cn));
    }

    /**
     * Validates IC format and checks for duplicates via Stream.
     * @param ic from user input
     * @param existingStaff from database
     * @return true if valid and unique
     */
    public boolean isICValid(String ic, Map<String, Staff> existingStaff) {
        if (ic == null || !ic.matches(AppConstants.REGEX_IC)) return false;
        return existingStaff.values().stream()
                .noneMatch(s -> s.getIc().equals(ic));
    }

    /**
     * Validates Staff ID format and checks for existence.
     * @param id Staff ID from user input
     * @param existingStaff from database
     * @return true if valid format and unique
     */
    public boolean isIDValid(String id, Map<String, Staff> existingStaff) {
        if (id == null || !id.matches(AppConstants.REGEX_ID)) return false;
        return !existingStaff.containsKey(id);
    }

    /**
     * Enforces password complexity.
     * @param password from user input
     * @return true if valid
     */
    public boolean isPasswordComplexityValid(String password) {
        return password != null
                && password.length() >= AppConstants.MIN_PASSWORD_LENGTH
                && password.matches(AppConstants.REGEX_PASSWORD);
    }
}