package oopt.assignment.ui;

import java.util.Arrays;

/**
 * representing all modification actions available
 */
public enum TrainModifyOption {

    STANDARD_QTY(1, "Standard Seat Quantity"),
    PREMIUM_QTY(2, "Premium Seat Quantity"),
    STANDARD_PRICE(3, "Standard Seat Price"),
    PREMIUM_PRICE(4, "Premium Seat Price"),
    DISCONTINUE(5, "Discontinue Train"),
    EXIT(6, "Exit");

    private final int id;
    private final String description;

    /**
     * Constructs a TrainModifyOption entry.
     * @param id          unique number associated with this modification action
     * @param description label shown in the Modify-Train menu
     */
    TrainModifyOption(int id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Returns the numeric identifier for this modification action.
     * @return integer ID for menu selection
     */
    public int getId() { return id; }

    /**
     * Returns the label to display to the user.
     * @return option description for UI printing
     */
    public String getDescription() { return description; }

    /**
     * Converts a raw integer input to a TrainModifyOption enum value.
     * @param id numeric input entered by the user
     * @return matching TrainModifyOption or null if invalid
     */
    public static TrainModifyOption fromId(int id) {
        return Arrays.stream(values())
                .filter(option -> option.id == id)
                .findFirst()
                .orElse(null);
    }
}
