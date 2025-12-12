package test.service;

import ets.model.Passenger;
import ets.model.PassengerRepository;
import ets.model.PassengerTier;
import ets.service.PassengerService;
import ets.service.PassengerValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PassengerServiceTest - To test the PassengerService class
 */
class PassengerServiceTest {

    private PassengerRepository repo;
    private PassengerValidator validator;
    private PassengerService service;

    // Real file + backup (similar to your Booking test)
    private final File dataFile   = new File("PassengerFile.txt");
    private final File backupFile = new File("PassengerFile.txt.bak");

    /**
     * Move the original data to a new data file
     */
    @BeforeEach
    void setUp() {
        // 1. BACKUP: if real file exists, move it to .bak
        if (dataFile.exists()) {
            boolean ok = dataFile.renameTo(backupFile);
            assertTrue(ok, "Failed to backup PassengerFile.txt");
        }

        // 2. Make sure we start from a clean test file (optional)
        if (dataFile.exists()) {
            assertTrue(dataFile.delete(), "Failed to delete test PassengerFile.txt");
        }

        // 3. Use normal repository & service (they will use PassengerFile.txt)
        repo = new PassengerRepository();
        validator = new PassengerValidator();
        service = new PassengerService(repo, validator);
    }

    /**
     * Move the backup file back to the original
     */
    @AfterEach
    void tearDown() {
        // 1. Delete the test file produced during this test run
        if (dataFile.exists()) {
            assertTrue(dataFile.delete(), "Failed to delete test PassengerFile.txt");
        }

        // 2. Restore original file from backup (if it existed before)
        if (backupFile.exists()) {
            boolean ok = backupFile.renameTo(dataFile);
            assertTrue(ok, "Failed to restore original PassengerFile.txt from backup");
        }
    }

    /**
     * Verifies that a new passenger is successfully created, assigned a unique ID,
     * and saved into the repository. Also checks that all default values such as
     * tier (NORMAL) and dateJoined (today) are correctly applied.
     */
    @Test
    void registerNewPassenger_createsPassengerAndPersists() {
        Passenger created = service.registerNewPassenger(
                "Alice",
                "0123456789",
                "010203040506",
                'F'
        );

        assertNotNull(created.getId());
        assertEquals("Alice", created.getName());
        assertEquals(PassengerTier.NORMAL, created.getPassengerTier());
        assertEquals(LocalDate.now(), created.getDateJoined());

        // Verify repository actually has it
        Collection<Passenger> all = service.getAllPassengers();
        assertEquals(1, all.size());
        Optional<Passenger> fetched = service.findById(created.getId());
        assertTrue(fetched.isPresent());
    }

    /**
     * Ensures that searching for a null ID does not throw an exception and
     * correctly returns an empty Optional, following safe querying behaviour.
     */
    @Test
    void findById_null_returnsEmptyOptional() {
        assertTrue(service.findById(null).isEmpty());
    }

    /**
     * Tests that modifying an existing passenger and calling updatePassenger()
     * correctly validates input, overwrites the stored record, and persists the changes.
     */
    @Test
    void updatePassenger_existingPassenger_updatesAndSaves() {
        Passenger created = service.registerNewPassenger(
                "Alice",
                "0123456789",
                "010203040506",
                'F'
        );

        created.setName("Alice Updated");
        created.setContactNo("0987654321");
        service.updatePassenger(created);

        Optional<Passenger> updated = service.findById(created.getId());
        assertTrue(updated.isPresent());
        assertEquals("Alice Updated", updated.get().getName());
        assertEquals("0987654321", updated.get().getContactNo());
    }

    /**
     * Ensures that updating a passenger who does not exist in the repository
     * correctly triggers a validation failure with a meaningful exception message.
     */
    @Test
    void updatePassenger_notFound_throws() {
        Passenger p = new Passenger(
                "Ghost",
                "0123456789",
                "010203040506",
                "P999",
                'M',
                LocalDate.now(),
                PassengerTier.NORMAL
        );

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> service.updatePassenger(p));
        assertTrue(ex.getMessage().contains("Passenger not found"));
    }

    /**
     * Confirms that providing a valid tier code updates the passenger's tier,
     * saves the change into the repository, and correctly loads the updated value.
     */
    @Test
    void changeTier_validTier_updatesTier() {
        Passenger created = service.registerNewPassenger(
                "Alice",
                "0123456789",
                "010203040506",
                'F'
        );

        service.changeTier(created.getId(), 'G');

        Optional<Passenger> updated = service.findById(created.getId());
        assertTrue(updated.isPresent());
        assertEquals(PassengerTier.GOLD, updated.get().getPassengerTier());
    }

    /**
     * Verifies that passing an invalid tier character triggers a validation exception.
     * Ensures that only G/S/N are accepted and improper values are rejected safely.
     */
    @Test
    void changeTier_invalidTier_throws() {
        Passenger created = service.registerNewPassenger(
                "Alice",
                "0123456789",
                "010203040506",
                'F'
        );

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> service.changeTier(created.getId(), 'X'));
        assertEquals("Tier must be G (Gold), S (Silver) or N (Normal).", ex.getMessage());
    }

    /**
     * Ensures that changing the tier of a non-existent passenger
     * results in a clear and meaningful exception.
     */
    @Test
    void changeTier_notFound_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> service.changeTier("P999", 'G'));
        assertTrue(ex.getMessage().contains("Passenger not found"));
    }

    /**
     * Verifies that the service correctly prevents registering two passengers
     * with the same IC number, enforcing uniqueness rules in the business logic.
     */
    @Test
    void registerNewPassenger_duplicateIc_throws() {
        service.registerNewPassenger("Alice", "0123456789", "010203040506", 'F');

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.registerNewPassenger("Bob", "0987654321", "010203040506", 'M')
        );

        assertEquals("IC is already registered in the system.", ex.getMessage());
    }

}
