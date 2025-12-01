package oopt.assignment.service;

/**
 * Passenger Validator - Utility class to check the input validity
 */
public class PassengerValidator {

    /**
     * Method to validate name
     * @param name Passenger's name
     */
    public void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if(name.length() >= 30) {
            throw new IllegalArgumentException("Name must be less than 30 characters.");
        }
    }

    /**
     * Method to validate contact number
     * @param contactNo Passenger's contact number/phone number
     */
    public void validateContact(String contactNo) {
        if (contactNo == null || contactNo.isBlank()) {
            throw new IllegalArgumentException("Contact number cannot be empty.");
        }
        if (!contactNo.matches("\\d{10,11}")) {
            throw new IllegalArgumentException("Contact number must be 10-11 digits.");
        }
    }

    /**
     * Method to validate identity card number
     * @param ic Passenger's identity card number
     */
    public void validateIc(String ic) {
        if (ic == null || ic.isBlank()) {
            throw new IllegalArgumentException("IC cannot be empty.");
        }
        if (!ic.matches("\\d{12}")) {
            throw new IllegalArgumentException("IC must be 12 digits.");
        }
    }

    /**
     * Method to validate gender
     * @param gender Passenger's gender
     */
    public void validateGender(char gender) {
        char upper = Character.toUpperCase(gender);
        if (upper != 'M' && upper != 'F') {
            throw new IllegalArgumentException("Gender must be M or F.");
        }
    }

    /**
     * Method to validate passenger tier
     * @param tier Passenger's tier in the system
     */
    public void validateTier(char tier) {
        char upper = Character.toUpperCase(tier);
        if (upper != 'N' && upper != 'S' && upper != 'G') {
            throw new IllegalArgumentException("Tier must be N (Normal), S (Silver), G(Gold).");
        }
    }
}
