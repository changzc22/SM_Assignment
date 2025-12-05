package test.model;

import oopt.assignment.Train;
import oopt.assignment.model.Booking;
import oopt.assignment.model.BookingRepository;
import oopt.assignment.model.SeatTier;
import oopt.assignment.util.AppConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BookingRepositoryTest {

    private BookingRepository repository;

    @BeforeEach
    void setUp() {
        repository = new BookingRepository();
    }

    @Test
    void testAddAndRetrieveBooking() {
        // 1. Create Data
        Train train = new Train();
        train.setTrainID("T001");
        // Added "S001" as staffId
        Booking b = new Booking("B100", "Integration Test", SeatTier.STANDARD, 1, 50.0, train, "S001");

        // 2. Action: Save to File
        repository.add(b);

        // 3. Action: Read from File (New instance to ensure we read from disk)
        BookingRepository repoReader = new BookingRepository();
        ArrayList<Booking> results = repoReader.getAll();

        // 4. Assert / Search for our inserted booking
        Booking retrieved = null;
        for(Booking book : results) {
            if(book.getBookingID().equals("B100")) {
                retrieved = book;
                break;
            }
        }

        assertNotNull(retrieved, "Should find the saved booking");
        assertEquals("B100", retrieved.getBookingID());
        assertEquals("Integration Test", retrieved.getName());
        assertEquals(SeatTier.STANDARD, retrieved.getSeatTier());
        assertEquals("S001", retrieved.getStaffId()); // Check Staff ID persistence

        // Cleanup (Optional)
        repository.delete("B100");
    }

    @Test
    void testDeleteBooking() {
        // Setup
        Train train = new Train();
        train.setTrainID("T001");
        repository.add(new Booking("B200", "To Delete", SeatTier.PREMIUM, 1, 100.0, train, "S001"));

        // Action
        repository.delete("B200");

        // Assert
        ArrayList<Booking> results = repository.getAll();
        boolean found = false;
        for(Booking b : results) {
            if(b.getBookingID().equals("B200")) {
                found = true;
                break;
            }
        }
        assertFalse(found, "Booking B200 should be deleted");
    }
}