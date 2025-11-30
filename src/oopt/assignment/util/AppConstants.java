package oopt.assignment.util;

/**
 * Centralizes all application constants (Magic Numbers, Regex, File Paths).
 * This makes the system easier to maintain and configure.
 */
public class AppConstants {
    // File Configurations
    public static final String STAFF_FILE_PATH = "StaffFile.txt";
    public static final String BOOKING_FILE_PATH = "BookingFile.txt";

    // Business Rules
    public static final int MAX_LOGIN_ATTEMPTS = 4;
    public static final int LOCKOUT_MINUTES = 5;
    public static final int MIN_PASSWORD_LENGTH = 8;

    // Regex Patterns
    public static final String REGEX_NAME = "[a-zA-Z\\s]+";
    public static final String REGEX_CONTACT = "0\\d{9,10}";
    public static final String REGEX_IC = "\\d{12}";
    public static final String REGEX_ID = "S\\d{3}";
    public static final String REGEX_PASSWORD = ".*[a-zA-Z].*";

    // Modification Keys (Links UI Enum to Service Logic)
    public static final String FIELD_NAME = "NAME";
    public static final String FIELD_CONTACT = "CONTACT";
    public static final String FIELD_IC = "IC";
    public static final String FIELD_PASSWORD = "PASSWORD";

    // Tax Rate
    public static final double SST_RATE = 1.06;
}