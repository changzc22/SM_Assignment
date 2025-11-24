package oopt.assignment.ui;

import java.util.Arrays;

/**
 * Represents the main menu options for the Staff Management module.
 * Usage of Enum eliminates "Magic Numbers" in the UI switch statements.
 */
public enum StaffMenuOption {
    CREATE(1, "Create New Staff"),
    MODIFY(2, "Modify Staff Details"),
    DELETE(3, "Delete Staff"),
    DISPLAY_ALL(4, "Display All Staff"),
    SEARCH(5, "Search for Staff"),
    RETURN(6, "Return to Main Menu");

    private final int id;
    private final String description;

    StaffMenuOption(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Converts a raw integer input from the user into a type-safe Enum.
     * @param id The number entered by the user.
     * @return The corresponding Enum option, or null if not found.
     */
    // Static lookup method to convert user input (int) to Enum
    public static StaffMenuOption fromId(int id) {
        return Arrays.stream(values())
                .filter(option -> option.id == id)
                .findFirst()
                .orElse(null);
    }
}