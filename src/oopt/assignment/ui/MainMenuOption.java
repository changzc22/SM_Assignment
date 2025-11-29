package oopt.assignment.ui;

import java.util.Arrays;

/**
 * Defines the top-level menu options for the application.
 * Removes "Magic Numbers" from the main switch statement.
 */
public enum MainMenuOption {
    STAFF(1, "Staff"),
    PASSENGER(2, "Passenger"),
    BOOKING(3, "Booking"),
    TRAIN(4, "Train"),
    LOGOUT(5, "Log Out");

    private final int id;
    private final String label;

    MainMenuOption(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() { return id; }
    public String getLabel() { return label; }

    /**
     * Helper to find the Enum from the user's integer input.
     * @param id input from scanner
     * @return The matching Enum option, or null if invalid.
     */
    public static MainMenuOption fromId(int id) {
        return Arrays.stream(values())
                .filter(opt -> opt.id == id)
                .findFirst()
                .orElse(null);
    }
}