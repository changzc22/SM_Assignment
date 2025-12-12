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

/**
 * Integration tests for BookingRepository.
 */
public class BookingRepositoryTest {

    private BookingRepository repository;

    // Define the real file and a backup file
    private final File dataFile = new File(AppConstants.BOOKING_FILE_PATH);
    private final File backupFile = new File(AppConstants.BOOKING_FILE_PATH + ".bak");

    /**
     * Prepares a clean environment.
     * Backs up any existing 'BookingFile.txt' to avoid data loss.
     */
    @BeforeEach
    void setUp() {
        // 1. BACKUP: If a real file exists, rename it to .bak so we don't lose it
        if (dataFile.exists()) {
            dataFile.renameTo(backupFile);
        }

        // 2. Initialize Repo (It will create a new, empty BookingFile.txt for testing)
        repository = new BookingRepository();
    }

    /**
     * Restores the environment.
     * Deletes the dummy test file and restores the original data.
     */
    @AfterEach
    void tearDown() {
        // 1. CLEANUP: Delete the dummy test file created during the test
        if (dataFile.exists()) {
            dataFile.delete();
        }

        // 2. RESTORE: Rename the .bak file back to the original name
        if (backupFile.exists()) {
            backupFile.renameTo(dataFile);
        }
    }

    /**
     * Verifies that data can be persisted to disk and retrieved correctly.
     * Checks if formatting (pipe-separated) is handled properly.
     */
    @Test
    void testAddAndRetrieveBooking() {
        // 1. Create Dummy Train (New Model)
        Train train = new Train();
        train.setTrainID("T001");

        // 2. Create Booking
        Booking b = new Booking("B100", "Integration Test", SeatTier.STANDARD, 1, 50.0, train, "S001");

        // 3. Action: Save to File
        repository.add(b);

        // 4. Action: Read from File
        BookingRepository repoReader = new BookingRepository();
        ArrayList<Booking> results = repoReader.getAll();

        // 5. Assert
        Booking retrieved = results.stream()
                .filter(bk -> bk.getBookingID().equals("B100"))
                .findFirst()
                .orElse(null);

        assertNotNull(retrieved, "Should retrieve the booking from the file");
        assertEquals("Integration Test", retrieved.getName());
        assertEquals("S001", retrieved.getStaffId());
        assertEquals("T001", retrieved.getTrain().getTrainID());
    }

    @Test
    void testDeleteBooking() {
        // Setup
        Train train = new Train();
        train.setTrainID("T001");
        Booking b = new Booking("B200", "To Delete", SeatTier.PREMIUM, 1, 100.0, train, "S001");
        repository.add(b);

        // Verify it exists first
        assertFalse(repository.getAll().isEmpty());

        // Action: Delete
        repository.delete("B200");

        // Assert
        ArrayList<Booking> results = repository.getAll();
        boolean found = results.stream().anyMatch(booking -> booking.getBookingID().equals("B200"));

        assertFalse(found, "Booking B200 should be deleted from the list");
    }
}