package oopt.assignment.service;

import oopt.assignment.model.Booking;
import oopt.assignment.Train;
import oopt.assignment.model.SeatTier;

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

    /**
     * Check if ID format is correct (e.g., starts with 'B')
     */
    public boolean isValidIdFormat(String bookingId) {
        return bookingId.matches("^B\\d{3}$"); // Example Regex: B001
    }
}