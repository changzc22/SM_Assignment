package ets.model;


/**
 * Enum representing the class/tier of a seat.
 */
public enum SeatTier {
    STANDARD('S', "Standard"),
    PREMIUM('P', "Premium");

    private final char code;
    private final String label;


    /**
     * Constructor for SeatTier.
     *
     * @param code  The single-character code used for file storage
     * @param label The human-readable name used for UI display.
     */
    SeatTier(char code, String label) {
        this.code = code;
        this.label = label;
    }

    public char getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }


    /**
     * Lookup helper to convert a character from a file/input into the corresponding Enum.
     *
     * @param code The character code to look up (case-insensitive).
     * @return The matching SeatTier enum.
     * @throws IllegalArgumentException if the code does not match any tier.
     */
    public static SeatTier fromCode(char code) {
        for (SeatTier st : SeatTier.values()) {
            if (st.code == Character.toUpperCase(code)) {
                return st;
            }
        }
        throw new IllegalArgumentException("Invalid Seat Tier: " + code);
    }
}