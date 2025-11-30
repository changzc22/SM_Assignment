package oopt.assignment.model;

public enum SeatTier {
    STANDARD('S', "Standard"),
    PREMIUM('P', "Premium");

    private final char code;
    private final String label;

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

    public static SeatTier fromCode(char code) {
        for (SeatTier st : SeatTier.values()) {
            if (st.code == Character.toUpperCase(code)) {
                return st;
            }
        }
        throw new IllegalArgumentException("Invalid Seat Tier: " + code);
    }
}