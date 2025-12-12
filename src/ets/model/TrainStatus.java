package ets.model;

/**
 * Represents the lifecycle status of a Train.
 */
public enum TrainStatus {
    ACTIVE,
    DISCONTINUED;

    /**
     * Converts a boolean flag to a TrainStatus enum.
     * @param status true = active, false = discontinued
     */
    public static TrainStatus fromBoolean(boolean status) {
        return status ? ACTIVE : DISCONTINUED;
    }

    /**
     * Converts this enum back to boolean for file storage.
     * @return true if active, false otherwise
     */
    public boolean toBoolean() {
        return this == ACTIVE;
    }
}
