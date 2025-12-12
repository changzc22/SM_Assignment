package oopt.assignment.ui;

import java.util.Arrays;

/**
 * Enum representing the available menu actions in the Booking Module.
 */
public enum BookingMenuOption {
    ADD_BOOKING(1, "Add New Booking"),
    DISPLAY_BOOKINGS(2, "Display Booking Details"),
    SEARCH_BOOKING(3, "Search Booking Details"),
    CANCEL_BOOKING(4, "Cancel Booking"),
    GENERATE_REPORT(5, "Generate Revenue Report"),
    EXIT(6, "Exit");

    private final int id;
    private final String description;

    BookingMenuOption(int id, String description) {
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
     * Converts a raw integer input from the user into a type-safe Enum option.
     *
     * @param id The number entered by the user.
     * @return The corresponding BookingMenuOption, or null if the ID is invalid.
     */
    public static BookingMenuOption fromId(int id) {
        return Arrays.stream(values())
                .filter(option -> option.id == id)
                .findFirst()
                .orElse(null);
    }
}