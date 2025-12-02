package oopt.assignment.ui;

import java.util.Arrays;

/**
 * representing all menu options available in train module
 */
public enum TrainMenuOption {

    ADD_TRAIN(1, "Add New Train"),
    SEARCH_TRAIN(2, "Search Train"),
    MODIFY_TRAIN(3, "Modify Train"),
    DISPLAY_TRAIN(4, "Display Train"),
    RETURN(5, "Return to Main Menu");

    private final int id;
    private final String description;

    /**
     * Constructs a TrainMenuOption entry.
     * @param id          unique menu number associated with this option
     * @param description description shown in the menu
     */
    TrainMenuOption(int id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Returns the numeric ID associated with the menu option.
     * @return integer representing the menu selection number
     */
    public int getId() { return id; }

    /**
     * Returns the  description of this option.
     * @return description string for UI menu printing
     */
    public String getDescription() { return description; }

    /**
     * Converts a raw integer input into the corresponding
     * @param id numeric selection
     * @return matching TrainMenuOption or null if invalid
     */
    public static TrainMenuOption fromId(int id) {
        return Arrays.stream(values())
                .filter(option -> option.id == id)
                .findFirst()
                .orElse(null);
    }
}
