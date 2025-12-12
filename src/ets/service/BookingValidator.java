package ets.service;

import ets.model.SeatTier;
import ets.model.Train;


/**
 * Utility class to enforce validation rules for Bookings.
 */
public class BookingValidator {

    /**
     * Validates if the booking quantity is a positive integer.
     * @param quantity The number of seats requested
     * @return true if quantity > 0
     */
    public boolean isValidQuantity(int quantity) {
        return quantity > 0;
    }

    /**
     * Checks if the specific train has enough seats for the requested tier.
     *
     * @param train    The train object to check
     * @param tier     The seat tier (Standard/Premium)
     * @param quantity The number of seats requested
     * @return true if available seats >= quantity
     */
    public boolean hasEnoughSeats(Train train, SeatTier tier, int quantity) {
        int availableSeats = (tier == SeatTier.STANDARD)
                ? train.getStandardSeatQty()
                : train.getPremiumSeatQty();

        return quantity <= availableSeats;
    }
}