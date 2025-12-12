package ets.ui;

import java.util.Arrays;

/**
 * Represents the specific fields a user can modify on a Staff profile.
 * Replaces "Magic Strings" like "A", "B", "C".
 */
public enum ModifyStaffOption {
    NAME("A", "Name"),
    CONTACT("B", "Contact Number"),
    IC("C", "IC Number"),
    PASSWORD("D", "Password");

    private final String code;
    private final String description;

    ModifyStaffOption(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * static lookup to find the Enum based on the letter input (e.g., "A" -> NAME).
     * @param code The letter entered by the user.
     * @return The corresponding Enum, or null if invalid.
     */
    // Static lookup method to convert user input (String) to Enum
    public static ModifyStaffOption fromCode(String code) {
        return Arrays.stream(values())
                .filter(option -> option.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }
}