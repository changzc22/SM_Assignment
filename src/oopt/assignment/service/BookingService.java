package oopt.assignment.service;

import oopt.assignment.Train;
import oopt.assignment.model.*;
import oopt.assignment.util.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class BookingService {

    private final IBookingRepository bookingRepository;
    private final BookingValidator validator;

    // Constructor removed TrainInterface to avoid conflict
    public BookingService(IBookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
        this.validator = new BookingValidator();
    }

    public double calculateFare(PassengerTier passengerTier, SeatTier seatTier, int quantity, double basePrice) {
        double discountMultiplier = passengerTier.getPriceMultiplier();
        double tax = AppConstants.SST_RATE;
        return basePrice * quantity * discountMultiplier * tax;
    }

    public boolean createBooking(Booking newBooking, Train train) {
        if (!validator.isValidQuantity(newBooking.getNumOfSeatBook())) return false;
        if (!validator.hasEnoughSeats(train, newBooking.getSeatTier(), newBooking.getNumOfSeatBook())) return false;

        // Update Legacy Train Seats
        int currentSeats = (newBooking.getSeatTier() == SeatTier.STANDARD)
                ? train.getStandardSeatQty() : train.getPremiumSeatQty();

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

    public boolean cancelBooking(String bookingID, List<Train> allTrains) {
        Booking booking = getBookingById(bookingID);
        if (booking == null) return false;

        if (booking.getTrain() != null) {
            for (Train t : allTrains) {
                if (t.getTrainID().equals(booking.getTrain().getTrainID())) {
                    if (booking.getSeatTier() == SeatTier.STANDARD) {
                        t.setStandardSeatQty(t.getStandardSeatQty() + booking.getNumOfSeatBook());
                    } else {
                        t.setPremiumSeatQty(t.getPremiumSeatQty() + booking.getNumOfSeatBook());
                    }
                    break;
                }
            }
        }
        bookingRepository.delete(bookingID);
        return true;
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = bookingRepository.getAll();
        // Use Legacy TrainMain to fetch trains (compatible with your UI)
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

    public String generateNewBookingId() {
        List<Booking> allBookings = bookingRepository.getAll();
        if (allBookings.isEmpty()) return "B001";

        int maxId = 0;
        for (Booking b : allBookings) {
            try {
                String numberPart = b.getBookingID().substring(1);
                int currentId = Integer.parseInt(numberPart);
                if (currentId > maxId) maxId = currentId;
            } catch (Exception e) { continue; }
        }
        return String.format("B%03d", maxId + 1);
    }
}