package test.service;

import ets.model.*;
import ets.service.BookingService;
import ets.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for BookingService.
 */
public class BookingServiceTest {

    private BookingService bookingService;
    private MockBookingRepository mockBookingRepo;
    private MockTrainRepository mockTrainRepo;

    /**
     * Setup runs before every single test.
     * It creates fresh instances of the Mocks and Service to ensure no data "leaks" between tests.
     */
    @BeforeEach
    void setUp() {
        // 1. Create Mocks
        mockBookingRepo = new MockBookingRepository();
        mockTrainRepo = new MockTrainRepository();
        MockStaffRepository mockStaffRepo = new MockStaffRepository();

        // 2. Setup Dependencies
        StaffService staffService = new StaffService(mockStaffRepo);
        mockStaffRepo.save(new Staff("Admin", "0123456789", "999999010101", "S001", "pass", 0));

        // 3. Initialize BookingService
        bookingService = new BookingService(mockBookingRepo, mockTrainRepo, staffService);
    }

    // --- 1. FARE CALCULATION ---
    /**
     * Verifies the financial logic for ticket pricing.
     * Scenario: Different Passenger Tiers (Gold/Normal) and Seat Tiers.
     * Expected: Base Price * Quantity * Tier Multiplier * Tax Rate (1.06).
     */
    @Test
    void testCalculateFare() {
        // Gold (0.75) * Standard (100) * 1.06 tax = 79.50
        double goldFare = bookingService.calculateFare(PassengerTier.GOLD, SeatTier.STANDARD, 1, 100.00);
        assertEquals(79.50, goldFare, 0.01);

        // Normal (1.00) * Premium (200) * 1.06 tax = 424.00
        double normFare = bookingService.calculateFare(PassengerTier.NORMAL, SeatTier.PREMIUM, 2, 200.00);
        assertEquals(424.00, normFare, 0.01);
    }

    // --- 2. CREATE BOOKING ---
    /**
     * Verifies the "Happy Path" for creating a booking.
     * Scenario: User books 2 seats on a train with 10 seats available.
     * Expected:
     * 1. Method returns true.
     * 2. Train seats decrease by 2 (10 -> 8).
     * 3. Booking is saved to the repository.
     */
    @Test
    void testCreateBooking_Success() {
        // Setup: Train with 10 seats
        Train dbTrain = new Train("T001", "Penang", LocalDate.now(), LocalTime.now(),
                10, 10, 50.0, 80.0, TrainStatus.ACTIVE);
        mockTrainRepo.save(dbTrain);

        // UI passes a simplified Train object (simulating selection)
        Booking newBooking = new Booking("B001", "User", SeatTier.STANDARD, 2, 100.0, dbTrain, "S001");

        boolean result = bookingService.createBooking(newBooking, dbTrain);

        assertTrue(result, "Booking should be successful");
        assertEquals(8, dbTrain.getStandardSeatQty(), "Seats should be deducted in the repository object");
        assertEquals(1, mockBookingRepo.getAll().size(), "Booking should be saved");
    }


    /**
     * Verifies validation logic for insufficient inventory.
     * Scenario: User requests 2 seats but only 1 is available.
     * Expected: Returns false, and NO seats are deducted.
     */
    @Test
    void testCreateBooking_NotEnoughSeats() {
        // Setup: Train with only 1 seat
        Train dbTrain = new Train("T001", "Penang", LocalDate.now(), LocalTime.now(),
                1, 10, 50.0, 80.0, TrainStatus.ACTIVE);
        mockTrainRepo.save(dbTrain);

        Booking newBooking = new Booking("B001", "User", SeatTier.STANDARD, 2, 100.0, dbTrain, "S001");

        boolean result = bookingService.createBooking(newBooking, dbTrain);

        assertFalse(result, "Should fail due to insufficient seats");
        assertEquals(1, dbTrain.getStandardSeatQty(), "Seats should NOT be deducted");
    }

    // --- 3. CANCEL BOOKING ---
    /**
     * Verifies the cancellation and refund logic.
     * Scenario: Cancelling a booking of 2 seats.
     * Expected:
     * 1. Booking is removed from repository.
     * 2. Train seats are restored (8 -> 10).
     */
    @Test
    void testCancelBooking_Success() {
        // Setup: Train with 8 seats (2 booked)
        Train dbTrain = new Train("T001", "Penang", LocalDate.now(), LocalTime.now(),
                8, 10, 50.0, 80.0, TrainStatus.ACTIVE);
        mockTrainRepo.save(dbTrain);

        // Existing booking
        Booking booking = new Booking("B001", "User", SeatTier.STANDARD, 2, 100.0, dbTrain, "S001");
        mockBookingRepo.add(booking);

        boolean result = bookingService.cancelBooking("B001");

        assertTrue(result, "Cancel should succeed");
        assertTrue(mockBookingRepo.getAll().isEmpty(), "Booking should be removed");
        assertEquals(10, dbTrain.getStandardSeatQty(), "Seats should be restored to 10");
    }

    // --- 4. GENERATE ID ---
    /**
     * Verifies auto-increment logic for IDs.
     * Expected:
     * - Empty Repo -> B001
     * - Repo has B005 -> Returns B006
     */
    @Test
    void testGenerateNewBookingId() {
        assertEquals("B001", bookingService.generateNewBookingId());

        mockBookingRepo.add(new Booking("B005", "User", SeatTier.STANDARD, 1, 50.0, null, "S001"));
        assertEquals("B006", bookingService.generateNewBookingId());
    }

    // --- Internal mock classes ---
    static class MockBookingRepository implements IBookingRepository {
        private final ArrayList<Booking> db = new ArrayList<>();
        public ArrayList<Booking> getAll() { return new ArrayList<>(db); }
        public void saveAll(Collection<Booking> list) { db.clear(); db.addAll(list); }
        public void add(Booking b) { db.add(b); }
        public void delete(String id) { db.removeIf(b -> b.getBookingID().equals(id)); }
    }

    static class MockTrainRepository implements TrainInterface {
        private final List<Train> db = new ArrayList<>();
        public void save(Train t) { db.add(t); }
        public List<Train> loadAll() { return new ArrayList<>(db); }
        public void saveAll(List<Train> trains) { /* Simulates saving to file */ }
    }

    static class MockStaffRepository implements IStaffRepository {
        private final LinkedHashMap<String, Staff> db = new LinkedHashMap<>();
        public void save(Staff s) { db.put(s.getId(), s); }
        public LinkedHashMap<String, Staff> getAll() { return new LinkedHashMap<>(db); }
        public void saveAll(Collection<Staff> list) { /* Simulates saving */ }
    }
}