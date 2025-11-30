package oopt.assignment.service;

import oopt.assignment.Train;
import oopt.assignment.model.*;
import oopt.assignment.util.AppConstants;
import java.util.ArrayList;

public class BookingService {

    private final BookingInterface bookingRepository;

    public BookingService(BookingInterface bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Calculates the total fare based on business rules.
     */
    public double calculateFare(PassengerTier passengerTier, SeatTier seatTier, int quantity, double basePrice) {
        double discountMultiplier = passengerTier.getPriceMultiplier();
        double tax = AppConstants.SST_RATE;

        return basePrice * quantity * discountMultiplier * tax;
    }

    /**
     * Validates and creates a new booking.
     */
    public boolean createBooking(Booking newBooking, Train train) {
        // 1. Check for Duplicate ID
        if (getBookingById(newBooking.getBookingID()) != null) {
            System.out.println("Error: Duplicate Booking ID.");
            return false;
        }

        // 2. Check Seat Availability
        int availableSeats = (newBooking.getSeatTier() == SeatTier.STANDARD)
                ? train.getStandardSeatQty()
                : train.getPremiumSeatQty();

        if (newBooking.getNumOfSeatBook() > availableSeats) {
            System.out.println("Error: Not enough seats available.");
            return false;
        }

        // 3. Update Train Seats (In-Memory update)
        if (newBooking.getSeatTier() == SeatTier.STANDARD) {
            train.setStandardSeatQty(availableSeats - newBooking.getNumOfSeatBook());
        } else {
            train.setPremiumSeatQty(availableSeats - newBooking.getNumOfSeatBook());
        }

        // 4. Save Booking
        bookingRepository.add(newBooking);
        return true;
    }

    /**
     * Retrieves all bookings and links them to full Train details.
     */
    public ArrayList<Booking> getAllBookings() {
        ArrayList<Booking> bookings = bookingRepository.getAll();

        // Fetch all trains to hydrate the data
        // Uses legacy TrainMain because Train module is not yet refactored
        ArrayList<Train> allTrains = oopt.assignment.TrainMain.readTrainFile();

        for (Booking b : bookings) {
            String trainId = b.getTrain().getTrainID();

            // Find matching train
            Train fullTrain = allTrains.stream()
                    .filter(t -> t.getTrainID().equals(trainId))
                    .findFirst()
                    .orElse(null);

            if (fullTrain != null) {
                b.setTrain(fullTrain); // Link full object
            }
        }
        return bookings;
    }

    public Booking getBookingById(String id) {
        ArrayList<Booking> all = getAllBookings(); // Use the method that links trains
        return all.stream()
                .filter(b -> b.getBookingID().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public boolean deleteBooking(String id) {
        if (getBookingById(id) == null) return false;
        bookingRepository.delete(id);
        return true;
    }
}