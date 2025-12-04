package oopt.assignment.ui;

/**
 * Enum representing the available edit options for a passenger.
 */
public enum PassengerEditOption {

    EDIT_NAME(1, "Name"),
    EDIT_CONTACT(2, "Contact number"),
    EDIT_IC(3, "IC"),
    EDIT_GENDER(4, "Gender"),
    SAVE_AND_RETURN(5, "Save changes and return"),
    CANCEL(6, "Cancel without saving");

    private final int code;
    private final String description;

    PassengerEditOption(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PassengerEditOption fromCode(int code) {
        for (PassengerEditOption option : values()) {
            if (option.code == code) {
                return option;
            }
        }
        return null;
    }
}
