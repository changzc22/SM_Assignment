package ets.ui;

/**
 * Enum representing the available options in the Passenger Management Menu.
 */
public enum PassengerMenuOption {

    NEW_REGISTRATION(1, "New Passenger Registration"),
    SEARCH_PASSENGER(2, "Search Passenger"),
    EDIT_PASSENGER(3, "Edit Passenger Details"),
    DISPLAY_ALL(4, "Display All Passengers Information"),
    CHANGE_TIER(5, "Upgrade/Downgrade Passenger Tier"),
    BACK_TO_MAIN_MENU(6, "Back To Main Menu");

    private final int code;
    private final String description;

    PassengerMenuOption(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Find enum by numeric code (1–6). Returns null if not found.
     */
    public static PassengerMenuOption fromCode(int code) {
        for (PassengerMenuOption option : values()) {
            if (option.code == code) {
                return option;
            }
        }
        return null;
    }
}
