package test.model;

import oopt.assignment.Train;
import oopt.assignment.model.Booking;
import oopt.assignment.model.BookingRepository;
import oopt.assignment.model.SeatTier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        // 1. Create Data (Using Legacy Train)
        Train train = new Train();
        train.setTrainID("T001");

        Booking b = new Booking("B100", "Integration Test", SeatTier.STANDARD, 1, 50.0, train, "S001");

        // 2. Action: Save to File
        repository.add(b);

        // 3. Action: Read from File (New instance to ensure actual file read)
        BookingRepository repoReader = new BookingRepository();
        ArrayList<Booking> results = repoReader.getAll(); // Now returns ArrayList

        // 4. Assert (Loop through List to find B100)
        Booking retrieved = null;
        for (Booking booking : results) {
            if (booking.getBookingID().equals("B100")) {
                retrieved = booking;
                break;
            }
        }

        assertNotNull(retrieved, "Should retrieve the booking object B100 from the file");
        assertEquals("Integration Test", retrieved.getName());
        assertEquals("S001", retrieved.getStaffId());

        // Cleanup
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
        for (Booking booking : results) {
            if (booking.getBookingID().equals("B200")) {
                found = true;
                break;
            }
        }

        assertFalse(found, "Booking B200 should be deleted from the list");
    }
}