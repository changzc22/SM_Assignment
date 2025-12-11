package oopt.assignment.service;

import oopt.assignment.model.*;
import oopt.assignment.util.AppConstants;

import java.util.List;

public class BookingService {

    private final IBookingRepository bookingRepository;
    private final TrainInterface trainRepository;
    private final StaffService staffService;
    private final BookingValidator validator;

    // Updated Constructor: Injects all necessary dependencies to avoid static calls
    public BookingService(IBookingRepository bookingRepository,
                          TrainInterface trainRepository,
                          StaffService staffService) {
        this.bookingRepository = bookingRepository;
        this.trainRepository = trainRepository;
        this.staffService = staffService;
        this.validator = new BookingValidator();
    }

    public double calculateFare(PassengerTier passengerTier, SeatTier seatTier, int quantity, double basePrice) {
        double discountMultiplier = passengerTier.getPriceMultiplier();
        double tax = AppConstants.SST_RATE;
        return basePrice * quantity * discountMultiplier * tax;
    }

    /**
     * Fetch trains directly from repository (Replaces TrainMain usage)
     */
    public List<Train> getAvailableTrains() {
        return trainRepository.loadAll();
    }

    public boolean createBooking(Booking newBooking, Train uiSelectedTrain) {
        // 1. Validate Quantity
        if (!validator.isValidQuantity(newBooking.getNumOfSeatBook())) {
            return false;
        }

        // 2. Load fresh train data to ensure seat counts are accurate
        // (We don't trust the UI copy because it might be stale)
        List<Train> allTrains = trainRepository.loadAll();
        Train dbTrain = allTrains.stream()
                .filter(t -> t.getTrainID().equals(uiSelectedTrain.getTrainID()))
                .findFirst()
                .orElse(null);

        if (dbTrain == null) return false; // Train not found in file

        // 3. Validate Seat Availability on the FRESH object
        if (!validator.hasEnoughSeats(dbTrain, newBooking.getSeatTier(), newBooking.getNumOfSeatBook())) {
            return false;
        }

        // 4. Logic: Update Train Seats in Memory
        int currentSeats = (newBooking.getSeatTier() == SeatTier.STANDARD)
                ? dbTrain.getStandardSeatQty() : dbTrain.getPremiumSeatQty();

        if (newBooking.getSeatTier() == SeatTier.STANDARD) {
            dbTrain.setStandardSeatQty(currentSeats - newBooking.getNumOfSeatBook());
        } else {
            dbTrain.setPremiumSeatQty(currentSeats - newBooking.getNumOfSeatBook());
        }

        // 5. Transaction: Save Everything
        bookingRepository.add(newBooking);       // Save Booking
        trainRepository.saveAll(allTrains);      // Save Updated Train Seats

        // 6. Update Staff Stats (Using instance method, not static)
        if (newBooking.getStaffId() != null) {
            staffService.incrementBookingHandle(newBooking.getStaffId());
        }

        return true;
    }

    public boolean cancelBooking(String bookingID) {
        Booking booking = getBookingById(bookingID);
        if (booking == null) return false;

        // Load all trains to find the one we need to restore seats to
        List<Train> allTrains = trainRepository.loadAll();

        if (booking.getTrain() != null) {
            for (Train t : allTrains) {
                if (t.getTrainID().equals(booking.getTrain().getTrainID())) {
                    // Restore Seats logic
                    if (booking.getSeatTier() == SeatTier.STANDARD) {
                        t.setStandardSeatQty(t.getStandardSeatQty() + booking.getNumOfSeatBook());
                    } else {
                        t.setPremiumSeatQty(t.getPremiumSeatQty() + booking.getNumOfSeatBook());
                    }
                    break;
                }
            }
        }

        // Commit changes
        bookingRepository.delete(bookingID);
        trainRepository.saveAll(allTrains); // Save restored seats
        return true;
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = bookingRepository.getAll();
        List<Train> allTrains = trainRepository.loadAll(); // Use Repo directly

        // Hydrate Bookings with full Train objects
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

        if (allBookings.isEmpty()) {
            return "B001";
        }

        int maxId = 0;
        for (Booking b : allBookings) {
            try {
                // Extract number (B005 -> 5)
                String numberPart = b.getBookingID().substring(1);
                int currentId = Integer.parseInt(numberPart);

                if (currentId > maxId) {
                    maxId = currentId;
                }
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                continue;
            }
        }

        return String.format("B%03d", maxId + 1);
    }
}