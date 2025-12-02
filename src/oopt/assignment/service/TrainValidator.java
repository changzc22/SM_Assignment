package oopt.assignment.service;

import oopt.assignment.util.AppConstants;
import oopt.assignment.util.ErrorMessage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.logging.LogRecord;

/**
 * TrainValidator
 * validation logic for Train-related fields
 */
public class TrainValidator {

    private static final Logger LOGGER = Logger.getLogger(TrainValidator.class.getName());

    static {
        LOGGER.setUseParentHandlers(false);

        Handler handler = new StreamHandler(System.out, new SimpleFormatter()) {
            @Override
            public synchronized void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        LOGGER.addHandler(handler);
    }


    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Checks if a Train ID follows format TXXX
     * @param trainId Train ID to validate
     * @return true if matches T followed by 3 digits, otherwise {@code false}
     */
    public boolean isValidTrainIdFormat(String trainId) {
        return trainId != null && trainId.matches("T\\d{3}");
    }

    /**
     * Validates destination format.
     * @param destination destination string to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidDestinationFormat(String destination) {
        if (destination == null || destination.isEmpty() || destination.length() > 15) {
            return false;
        }
        return destination.chars().allMatch(c -> Character.isLetter(c) || Character.isWhitespace(c));
    }

    /**
     * Validates that the date is in yyyy-MM-dd format and strictly after today.
     * @param valueDate raw string entered by user
     * @return true if the date is valid and strictly after today, false otherwise
     */
    public boolean isValidFutureDate(String valueDate) {
        try {
            LocalDate date = LocalDate.parse(valueDate, DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            return date.isAfter(today);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,
                    ErrorMessage.INVALID_DATE_FORMAT + " Value: " + valueDate);
            return false;
        }
    }

    /**
     * Validates time format as  HH:mm
     * @param valueTime raw string entered by user
     * @return true if the time is valid,  false otherwise
     */
    public boolean isValidTime(String valueTime) {
        try {
            LocalTime.parse(valueTime, TIME_FORMATTER);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,
                    ErrorMessage.INVALID_TIME_FORMAT + " Value: " + valueTime);
            return false;
        }
    }

    /**
     * Validates a new standard seat quantity.
     * @param newQty       proposed new standard seat quantity
     * @param premiumSeats current premium seat quantity
     * @throws IllegalArgumentException if any rule is violated
     */
    public void validateStandardSeatQuantity(int newQty, int premiumSeats) {
        if (newQty < AppConstants.MIN_STANDARD_SEATS
                || newQty > AppConstants.MAX_SEATS
                || newQty <= premiumSeats) {
            throw new IllegalArgumentException(ErrorMessage.STANDARD_QTY_RULE);
        }
    }

    /**
     * Validates a new premium seat quantity.
     * @param newQty        proposed new premium seat quantity
     * @param standardSeats current standard seat quantity
     * @throws IllegalArgumentException if any rule is violated
     */
    public void validatePremiumSeatQuantity(int newQty, int standardSeats) {
        if (newQty < AppConstants.MIN_PREMIUM_SEATS
                || newQty > AppConstants.MAX_PREMIUM_SEATS
                || newQty >= standardSeats) {
            throw new IllegalArgumentException(ErrorMessage.PREMIUM_QTY_RULE);
        }
    }

    /**
     * Validates a new standard seat price.
     * @param newPrice     proposed new standard seat price
     * @param premiumPrice current premium seat price
     * @throws IllegalArgumentException if any rule is violated
     */
    public void validateStandardSeatPrice(double newPrice, double premiumPrice) {
        if (newPrice < AppConstants.MIN_STANDARD_PRICE
                || newPrice > AppConstants.MAX_STANDARD_PRICE
                || newPrice >= premiumPrice) {
            throw new IllegalArgumentException(ErrorMessage.STANDARD_PRICE_RULE);
        }
    }

    /**
     * Validates a new premium seat price.
     * @param newPrice      proposed new premium seat price
     * @param standardPrice current standard seat price
     * @throws IllegalArgumentException if any rule is violated
     */
    public void validatePremiumSeatPrice(double newPrice, double standardPrice) {
        if (newPrice < AppConstants.MIN_PREMIUM_PRICE
                || newPrice > AppConstants.MAX_PREMIUM_PRICE
                || newPrice <= standardPrice) {
            throw new IllegalArgumentException(ErrorMessage.PREMIUM_PRICE_RULE);
        }
    }
}
