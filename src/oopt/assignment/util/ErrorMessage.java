package oopt.assignment.util;

/**
 * Centralizes all error messages.
 * Allows for easy modification of system text without touching logic code.
 */
public class ErrorMessage {
    // ===FOR FILE LOGIC ===
    public static final String FILE_READ_ERROR = "Critical: Unable to read data file.";
    public static final String FILE_WRITE_ERROR = "Critical: Unable to save data file.";
    public static final String FILE_CREATE_ERROR = "Error creating new data file.";
    public static final String CORRUPTED_DATA = "Skipping corrupted data line: ";

    // ===FOR VALIDATION ===
    public static final String INVALID_NAME = "Invalid Name: Must contain alphabets only.";
    public static final String INVALID_CONTACT = "Invalid Contact: Must start with 0 and be 10-11 digits.";
    public static final String DUPLICATE_CONTACT = "Contact number already exists.";
    public static final String INVALID_IC = "Invalid IC: Must be 12 digits.";
    public static final String DUPLICATE_IC = "IC number already exists.";
    public static final String INVALID_ID = "Invalid ID: Must follow format S001.";
    public static final String DUPLICATE_ID = "ID already exists.";
    public static final String WEAK_PASSWORD = "Password too weak: Min 8 chars, must contain letters.";

    // ===FOR LOGIN ===
    public static final String LOGIN_LOCKED = "Account locked. Try again in %d minutes.";
    public static final String LOGIN_MAX_ATTEMPTS = "Max attempts reached. Account locked for %d minutes.";

    // ===FOR MAIN MENU ===
    public static final String INPUT_NOT_NUMBER = "Invalid input. Please enter numbers only.";
    public static final String INVALID_MENU_SELECTION = "Invalid option. Please select a number from the menu.";
    public static final String SCREEN_CLEAR_ERROR = "System Warning: Unable to clear console screen.";
}