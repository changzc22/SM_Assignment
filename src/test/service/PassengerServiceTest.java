package test.service;

import oopt.assignment.model.Passenger;
import oopt.assignment.model.PassengerRepository;
import oopt.assignment.model.PassengerTier;
import oopt.assignment.service.PassengerService;
import oopt.assignment.service.PassengerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PassengerServiceTest {

    private PassengerRepository repo;
    private PassengerValidator validator;
    private PassengerService service;

    @BeforeEach
    void setUp() {
        // Clean the file before each test to avoid interference
        File file = new File("PassengerFile.txt");
        if (file.exists()) {
            assertTrue(file.delete(), "Failed to delete test PassengerFile.txt");
        }

        repo = new PassengerRepository();
        validator = new PassengerValidator();
        service = new PassengerService(repo, validator);
    }

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

    @Test
    void findById_null_returnsEmptyOptional() {
        assertTrue(service.findById(null).isEmpty());
    }

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

    @Test
    void changeTier_notFound_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> service.changeTier("P999", 'G'));
        assertTrue(ex.getMessage().contains("Passenger not found"));
    }
}
