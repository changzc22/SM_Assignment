package oopt.assignment.service;

import oopt.assignment.model.*;
import oopt.assignment.util.AppConstants;

import java.util.List;

/**
 * Handles the core business logic for the Booking Module.
 * Responsible for fare calculations, seat inventory management,
 * and coordinating data persistence between Booking, Train, and Staff modules.
 */
public class BookingService {

    private final IBookingRepository bookingRepository;
    private final TrainInterface trainRepository;
    private final StaffService staffService;
    private final BookingValidator validator;


    /**
     * Parameterized constructor for Dependency Injection.
     * Allows for easier testing by injecting Mock repositories.
     *
     * @param bookingRepository Repository for handling Booking I/O
     * @param trainRepository   Repository for handling Train I/O (seat updates)
     * @param staffService      Service for handling Staff performance tracking
     */
    public BookingService(IBookingRepository bookingRepository,
                          TrainInterface trainRepository,
                          StaffService staffService) {
        this.bookingRepository = bookingRepository;
        this.trainRepository = trainRepository;
        this.staffService = staffService;
        this.validator = new BookingValidator();
    }


    /**
     * Calculates the total ticket price based on multiple factors.
     * Formula: Base Price * Quantity * Tier Discount * Tax Rate.
     *
     * @param passengerTier The tier of the passenger (Gold/Silver/Normal)
     * @param seatTier      The class of seat (Standard/Premium)
     * @param quantity      Number of seats being booked
     * @param basePrice     The base price of the specific train seat
     * @return The final total fare (double)
     */
    public double calculateFare(PassengerTier passengerTier, SeatTier seatTier, int quantity, double basePrice) {
        double discountMultiplier = passengerTier.getPriceMultiplier();
        double tax = AppConstants.SST_RATE;
        return basePrice * quantity * discountMultiplier * tax;
    }

    /**
     * Retrieves the latest list of trains directly from the repository.
     * Used by the UI to display available options.
     * @return List of all Train objects
     */
    public List<Train> getAvailableTrains() {
        return trainRepository.loadAll();
    }


    /**
     * Create a new booking.
     * This method performs a transaction:
     * 1. Validates input.
     * 2. Refreshes train data to prevent stale seat counts.
     * 3. Deducts seats from the Train.
     * 4. Saves the Booking AND the updated Train file.
     * 5. Updates the Staff performance counter.
     *
     * @param newBooking      The booking object containing user inputs
     * @param uiSelectedTrain The train object selected in the UI (used for ID reference)
     * @return true if the booking was created successfully, false if validation failed
     */
    public boolean createBooking(Booking newBooking, Train uiSelectedTrain) {
        // 1. Validate Quantity
        if (!validator.isValidQuantity(newBooking.getNumOfSeatBook())) {
            return false;
        }

        // 2. Load fresh train data to ensure seat counts are accurate
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

        // 6. Update Staff Stats
        if (newBooking.getStaffId() != null) {
            staffService.incrementBookingHandle(newBooking.getStaffId());
        }

        return true;
    }


    /**
     * Cancels an existing booking and restores the seats to the Train.
     *
     * @param bookingID The unique ID of the booking to cancel
     * @return true if successful, false if booking ID not found
     */
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


    /**
     * Retrieves all bookings and "hydrates" them with full Train details.
     * The file only stores Train ID, so this method links the actual Train object.
     *
     * @return List of fully populated Booking objects
     */
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


    /**
     * Helper to find a specific booking by ID.
     *
     * @param id The Booking ID (e.g. "B001")
     * @return The Booking object, or null if not found
     */
    public Booking getBookingById(String id) {
        return getAllBookings().stream()
                .filter(b -> b.getBookingID().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }


    /**
     * Generates a new unique Booking ID (e.g., B001, B002).
     * Scans existing bookings to find the highest number and increments it.
     *
     * @return A formatted String ID
     */
    public String generateNewBookingId() {
        int maxId = bookingRepository.getAll().stream()
                .map(Booking::getBookingID)
                .filter(id -> id != null && id.matches("B\\d+"))
                .mapToInt(id -> Integer.parseInt(id.substring(1)))
                .max()
                .orElse(0);

        return String.format("B%03d", maxId + 1);
    }
}