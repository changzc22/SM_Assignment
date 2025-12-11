package oopt.assignment.service;

import oopt.assignment.model.SeatTier;
import oopt.assignment.model.Train;

public class BookingValidator {

    /**
     * Validates if the booking quantity is valid (positive integer).
     */
    public boolean isValidQuantity(int quantity) {
        return quantity > 0;
    }

    /**
     * Validates if the train has enough seats for the requested quantity.
     */
    public boolean hasEnoughSeats(Train train, SeatTier tier, int quantity) {
        int availableSeats = (tier == SeatTier.STANDARD)
                ? train.getStandardSeatQty()
                : train.getPremiumSeatQty();

        return quantity <= availableSeats;
    }
}