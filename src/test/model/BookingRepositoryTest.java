package test.model;

import oopt.assignment.model.Booking;
import oopt.assignment.model.BookingRepository;
import oopt.assignment.model.SeatTier;
import oopt.assignment.model.Train;
import oopt.assignment.util.AppConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BookingRepositoryTest {

    private BookingRepository repository;
    private final File file = new File(AppConstants.BOOKING_FILE_PATH);

    @BeforeEach
    void setUp() {
        // Delete any existing file to ensure a clean test environment
        if (file.exists()) {
            file.delete();
        }
        repository = new BookingRepository();
    }

    @AfterEach
    void tearDown() {
        // Clean up the garbage file created by the test
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testAddAndRetrieveBooking() {
        // 1. Create Dummy Train
        Train train = new Train();
        train.setTrainID("T001");

        // 2. Create and Save Booking
        Booking b = new Booking("B100", "Integration Test", SeatTier.STANDARD, 1, 50.0, train, "S001");
        repository.add(b);

        // 3. Read back from Disk
        BookingRepository repoReader = new BookingRepository();
        ArrayList<Booking> results = repoReader.getAll();

        // 4. Verify
        Booking retrieved = results.stream()
                .filter(bk -> bk.getBookingID().equals("B100"))
                .findFirst()
                .orElse(null);

        assertNotNull(retrieved, "Should retrieve the booking from the file");
        assertEquals("Integration Test", retrieved.getName());
        assertEquals("S001", retrieved.getStaffId());
        assertEquals("T001", retrieved.getTrain().getTrainID());
    }
}