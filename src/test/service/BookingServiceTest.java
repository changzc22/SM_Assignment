package test.service;

import oopt.assignment.Train;
import oopt.assignment.model.*;
import oopt.assignment.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for BookingService.
 * Focus: Business Logic (Fare Calculation, Validation)
 */
public class BookingServiceTest {

    private BookingService bookingService;
    private MockBookingRepository mockRepository;

    @BeforeEach
    void setUp() {
        mockRepository = new MockBookingRepository();
        bookingService = new BookingService(mockRepository);
    }

    // --- TEST 1: Fare Calculation Logic ---
    @Test
    void testCalculateFare_Standard_Gold() {
        // Price: 100 * 1 qty * 0.75 (Gold) * 1.06 (Tax) = 79.50
        double fare = bookingService.calculateFare(PassengerTier.GOLD, SeatTier.STANDARD, 1, 100.00);
        assertEquals(79.50, fare, 0.01, "Fare calculation for Gold/Standard is incorrect");
    }

    @Test
    void testCalculateFare_Premium_Normal() {
        // Price: 200 * 2 qty * 1.00 (Normal) * 1.06 (Tax) = 424.00
        double fare = bookingService.calculateFare(PassengerTier.NORMAL, SeatTier.PREMIUM, 2, 200.00);
        assertEquals(424.00, fare, 0.01, "Fare calculation for Normal/Premium is incorrect");
    }

    // --- TEST 2: Validation Logic ---
    @Test
    void testCreateBooking_Success() {
        Train train = new Train("T001", "Penang", LocalDate.now(), LocalTime.now(), 10, 10, 50.0, 80.0, true);
        // Note: Added "S001" as staffId
        Booking booking = new Booking("B001", "Test User", SeatTier.STANDARD, 2, 100.0, train, "S001");

        boolean result = bookingService.createBooking(booking, train);

        assertTrue(result, "Booking should be successful");
        assertEquals(8, train.getStandardSeatQty(), "Train seats should decrease by 2");
    }

    @Test
    void testCreateBooking_NotEnoughSeats() {
        Train train = new Train("T001", "Penang", LocalDate.now(), LocalTime.now(), 1, 10, 50.0, 80.0, true);
        // Try to book 2 seats when only 1 is available
        Booking booking = new Booking("B001", "Test User", SeatTier.STANDARD, 2, 100.0, train, "S001");

        boolean result = bookingService.createBooking(booking, train);

        assertFalse(result, "Booking should fail due to insufficient seats");
        assertEquals(1, train.getStandardSeatQty(), "Train seats should not change");
    }

    @Test
    void testCreateBooking_InvalidQuantity() {
        Train train = new Train("T001", "Penang", LocalDate.now(), LocalTime.now(), 10, 10, 50.0, 80.0, true);
        Booking booking = new Booking("B001", "Test User", SeatTier.STANDARD, -5, 100.0, train, "S001");

        boolean result = bookingService.createBooking(booking, train);

        assertFalse(result, "Booking should fail for negative quantity");
    }

    // --- TEST 3: ID Generation ---
    @Test
    void testGenerateBookingId_EmptyRepo() {
        // Repository is empty initially
        String id = bookingService.generateNewBookingId();
        assertEquals("B001", id);
    }

    @Test
    void testGenerateBookingId_Increment() {
        // Add a booking with ID B005
        mockRepository.add(new Booking("B005", "User", SeatTier.STANDARD, 1, 10.0, null, "S001"));

        String id = bookingService.generateNewBookingId();
        assertEquals("B006", id, "Should increment the highest ID found");
    }

    // --- INTERNAL MOCK CLASS (To isolate Unit Tests) ---
    static class MockBookingRepository implements BookingInterface {
        private ArrayList<Booking> db = new ArrayList<>();

        @Override
        public ArrayList<Booking> getAll() { return db; }
        @Override
        public void saveAll(ArrayList<Booking> list) { db = list; }
        @Override
        public void add(Booking b) { db.add(b); }
        @Override
        public void delete(String id) { db.removeIf(b -> b.getBookingID().equals(id)); }
    }
}