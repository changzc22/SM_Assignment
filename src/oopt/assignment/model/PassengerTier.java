package oopt.assignment.model;


/**
 * Enum representing the loyalty tier of a passenger.
 * Determines the discount rate applied to ticket fares.
 * <p>
 * Logic:
 * GOLD   : 25% Discount (Multiplier 0.75)
 * SILVER : 15% Discount (Multiplier 0.85)
 * NORMAL: No Discount (Multiplier 1.00)
 */
public enum PassengerTier {
    GOLD('G', 0.75),    // 25% discount (Pay 75%)
    SILVER('S', 0.85),  // 15% discount (Pay 85%)
    NORMAL('N', 1.00);  // No discount

    private final char code;
    private final double priceMultiplier;


    /**
     * Constructor for PassengerTier.
     *
     * @param code            The single-character code stored in the file (e.g., 'G').
     * @param priceMultiplier The factor used to calculate the final price (e.g., 0.75).
     */
    PassengerTier(char code, double priceMultiplier) {
        this.code = code;
        this.priceMultiplier = priceMultiplier;
    }

    /**
     * Gets the price multiplier for fare calculation.
     *
     * @return The multiplier (e.g., 0.75 for 25% discount).
     */
    public double getPriceMultiplier() {
        return priceMultiplier;
    }


    /**
     * Gets the character code used for data persistence.
     *
     * @return The character code (e.g., 'G').
     */
    public char getCode() {
        return code;
    }

    /**
     * Helper method to convert character code from file to Enum.
     * @param code The character code (e.g., 'S')
     * @return The corresponding SeatTier enum
     */
    public static PassengerTier fromCode(char code) {
        for (PassengerTier tier : values()) {
            if (Character.toUpperCase(code) == tier.code) {
                return tier;
            }
        }
        return NORMAL; // Default to Normal if not found
    }
}