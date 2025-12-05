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
        if (newBooking.getNumOfSeatBook() <= 0) return false;

        int currentSeats = (newBooking.getSeatTier() == SeatTier.STANDARD)
                ? train.getStandardSeatQty()
                : train.getPremiumSeatQty();

        if (currentSeats < newBooking.getNumOfSeatBook()) return false;

        if (newBooking.getSeatTier() == SeatTier.STANDARD) {
            train.setStandardSeatQty(currentSeats - newBooking.getNumOfSeatBook());
        } else {
            train.setPremiumSeatQty(currentSeats - newBooking.getNumOfSeatBook());
        }
        bookingRepository.add(newBooking);

        if (newBooking.getStaffId() != null) {
            oopt.assignment.service.StaffService.incrementBookingHandleStatic(newBooking.getStaffId());
        }

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

    public String generateNewBookingId() {
        ArrayList<Booking> allBookings = bookingRepository.getAll();

        // Default if no bookings exist
        if (allBookings.isEmpty()) {
            return "B001";
        }

        int maxId = 0;
        for (Booking b : allBookings) {
            try {
                // Extract the number part (e.g., "B005" -> "005" -> 5)
                String numberPart = b.getBookingID().substring(1);
                int currentId = Integer.parseInt(numberPart);

                if (currentId > maxId) {
                    maxId = currentId;
                }
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                // Skip malformed IDs just in case
                continue;
            }
        }

        // Increment and Format (e.g., 6 -> "B006")
        return String.format("B%03d", maxId + 1);
    }
}