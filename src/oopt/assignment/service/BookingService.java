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

    public double calculateFare(PassengerTier passengerTier, SeatTier seatTier, int quantity, double basePrice) {
        double discountMultiplier = passengerTier.getPriceMultiplier();
        double tax = AppConstants.SST_RATE;
        return basePrice * quantity * discountMultiplier * tax;
    }

    public boolean createBooking(Booking newBooking, Train train) {
        // Logic largely handled in UI/Repo, but we validate here
        if (newBooking.getNumOfSeatBook() <= 0) return false;

        // Update Train Seats (In Memory)
        int currentSeats = (newBooking.getSeatTier() == SeatTier.STANDARD) ? train.getStandardSeatQty() : train.getPremiumSeatQty();
        if (currentSeats < newBooking.getNumOfSeatBook()) return false;

        if (newBooking.getSeatTier() == SeatTier.STANDARD) {
            train.setStandardSeatQty(currentSeats - newBooking.getNumOfSeatBook());
        } else {
            train.setPremiumSeatQty(currentSeats - newBooking.getNumOfSeatBook());
        }

        bookingRepository.add(newBooking);
        return true;
    }

    /**
     * Cancels a booking and restores seats to the provided Train List.
     */
    public boolean cancelBooking(String bookingID, ArrayList<Train> allTrains) {
        Booking booking = getBookingById(bookingID);
        if (booking == null) return false;

        // 1. Find the correct train in the list to update
        Train trainToUpdate = null;
        if (booking.getTrain() != null) {
            for (Train t : allTrains) {
                if (t.getTrainID().equals(booking.getTrain().getTrainID())) {
                    trainToUpdate = t;
                    break;
                }
            }
        }

        // 2. Restore Seats
        if (trainToUpdate != null) {
            if (booking.getSeatTier() == SeatTier.STANDARD) {
                trainToUpdate.setStandardSeatQty(trainToUpdate.getStandardSeatQty() + booking.getNumOfSeatBook());
            } else {
                trainToUpdate.setPremiumSeatQty(trainToUpdate.getPremiumSeatQty() + booking.getNumOfSeatBook());
            }
        }

        // 3. Delete Booking
        bookingRepository.delete(bookingID);
        return true;
    }

    public ArrayList<Booking> getAllBookings() {
        ArrayList<Booking> bookings = bookingRepository.getAll();
        ArrayList<Train> allTrains = oopt.assignment.TrainMain.readTrainFile();

        for (Booking b : bookings) {
            if (b.getTrain() == null) continue;
            String trainId = b.getTrain().getTrainID();
            Train fullTrain = allTrains.stream()
                    .filter(t -> t.getTrainID().equals(trainId))
                    .findFirst()
                    .orElse(null);
            if (fullTrain != null) {
                b.setTrain(fullTrain);
            }
        }
        return bookings;
    }

    public Booking getBookingById(String id) {
        return getAllBookings().stream()
                .filter(b -> b.getBookingID().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    // Simple delete for internal use
    public boolean deleteBooking(String id) {
        if (getBookingById(id) == null) return false;
        bookingRepository.delete(id);
        return true;
    }
}