package oopt.assignment.model;

public enum PassengerTier {
    GOLD('G', 0.75),    // 25% discount (Pay 75%)
    SILVER('S', 0.85),  // 15% discount (Pay 85%)
    NORMAL('N', 1.00);  // No discount

    private final char code;
    private final double priceMultiplier;

    PassengerTier(char code, double priceMultiplier) {
        this.code = code;
        this.priceMultiplier = priceMultiplier;
    }

    public double getPriceMultiplier() {
        return priceMultiplier;
    }

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