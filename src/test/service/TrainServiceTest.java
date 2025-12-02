package test.service;

import oopt.assignment.model.*;
import oopt.assignment.service.TrainService;
import oopt.assignment.service.TrainValidator;
import oopt.assignment.util.AppConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TrainService using an memory fake repository.
 */
class TrainServiceTest {

    /**
     * Simple repository for unit testing.
     */
    private static class InMemoryTrainRepository implements TrainInterface {
        private final List<Train> store = new ArrayList<>();

        @Override
        public List<Train> loadAll() {
            return new ArrayList<>(store);
        }

        @Override
        public void saveAll(List<Train> trains) {
            store.clear();
            store.addAll(trains);
        }

        public List<Train> internalStore() {
            return store;
        }
    }

    private InMemoryTrainRepository repo;
    private TrainService service;

    @BeforeEach
    void setUp() {
        repo = new InMemoryTrainRepository();
        TrainValidator validator = new TrainValidator();

        repo.internalStore().add(new Train(
                "T100", "Penang",
                LocalDate.of(2030, 1, 1),
                LocalTime.of(10, 0),
                100, 20, 50.0, 120.0,
                TrainStatus.ACTIVE
        ));
        repo.internalStore().add(new Train(
                "T200", "Johor",
                LocalDate.of(2030, 2, 1),
                LocalTime.of(12, 0),
                80, 10, 45.0, 110.0,
                TrainStatus.DISCONTINUED
        ));

        service = new TrainService(repo, validator);
    }

    /**
     * Verifies that getAllTrains returns a copy and not the internal list.
     */
    @Test
    void testGetAllTrainsReturnsCopy() {
        List<Train> list1 = service.getAllTrains();
        List<Train> list2 = service.getAllTrains();

        assertEquals(2, list1.size());
        assertNotSame(list1, list2);

        list1.clear();
        assertEquals(2, service.getAllTrains().size());
    }

    /**
     * Verifies that active trains are filtered correctly.
     */
    @Test
    void testGetActiveTrains() {
        List<Train> active = service.getActiveTrains();
        assertEquals(1, active.size());
        assertEquals("T100", active.get(0).getTrainID());
        assertEquals(TrainStatus.ACTIVE, active.get(0).getStatus());
    }

    /**
     * Verifies finding trains by ID
     */
    @Test
    void testFindById() {
        Optional<Train> found = service.findById("T100");
        assertTrue(found.isPresent());
        assertEquals("Penang", found.get().getDestination());

        assertTrue(service.findById("T999").isEmpty());
    }

    /**
     * Verifies that addTrain appends to list and calls save().
     */
    @Test
    void testAddTrain() {
        Train newTrain = new Train(
                "T300", "Melaka",
                LocalDate.of(2030, 3, 3),
                LocalTime.of(15, 0),
                90, 15, 55.0, 130.0,
                TrainStatus.ACTIVE
        );

        service.addTrain(newTrain);

        assertEquals(3, service.getAllTrains().size());
        assertEquals(3, repo.internalStore().size());
        assertEquals("T300", repo.internalStore().get(2).getTrainID());
    }

    /**
     * Verifies that createTrain builds a Train from TrainCreationRequest
     */
    @Test
    void testCreateTrain() {
        TrainCreationRequest req = new TrainCreationRequest();
        req.trainId = "T400";
        req.destination = "Ipoh";
        req.departureDate = LocalDate.of(2030, 4, 4);
        req.departureTime = LocalTime.of(18, 0);
        req.standardSeatQty = 100;
        req.premiumSeatQty = 20;
        req.standardSeatPrice = 60.0;
        req.premiumSeatPrice = 150.0;

        Train created = service.createTrain(req);

        assertEquals("T400", created.getTrainID());
        assertEquals("Ipoh", created.getDestination());
        assertEquals(TrainStatus.ACTIVE, created.getStatus());
        assertEquals(3, repo.internalStore().size());
    }

    /**
     * Verifies that discontinueTrain updates the status and triggers save().
     */
    @Test
    void testDiscontinueTrain() {
        Train t = service.findById("T100").orElseThrow();
        assertEquals(TrainStatus.ACTIVE, t.getStatus());

        service.discontinueTrain(t);

        assertEquals(TrainStatus.DISCONTINUED, t.getStatus());
        assertEquals(TrainStatus.DISCONTINUED,
                repo.internalStore().stream()
                        .filter(x -> "T100".equals(x.getTrainID()))
                        .findFirst().orElseThrow()
                        .getStatus());
    }

    /**
     * Verifies duplicate train ID and destination checks.
     */
    @Test
    void testDuplicateChecks() {
        assertTrue(service.isDuplicateTrainId("T100"));
        assertFalse(service.isDuplicateTrainId("T999"));

        assertTrue(service.isDuplicateDestination("Penang"));   // active
        assertFalse(service.isDuplicateDestination("Johor"));    // discontinued
        assertFalse(service.isDuplicateDestination("Unknown"));
    }

    /**
     * Verifies that parseDate and parseTime use the shared formatters correctly.
     */
    @Test
    void testParseDateAndTime() {
        assertEquals(LocalDate.of(2030, 1, 2),
                service.parseDate("2030-01-02"));
        assertEquals(LocalTime.of(9, 15),
                service.parseTime("09:15"));
    }

    /**
     * Verifies that validation delegate methods  to TrainValidator.
     */
    @Test
    void testValidationDelegates() {
        assertTrue(service.isValidTrainIdFormat("T123"));
        assertTrue(service.isValidDestinationFormat("Penang"));
        assertFalse(service.isValidFutureDate("2000-01-01"));
        assertTrue(service.isValidTime("10:00"));


        assertDoesNotThrow(() -> service.validateStandardSeatQuantity(
                AppConstants.MIN_STANDARD_SEATS + 5,
                AppConstants.MIN_PREMIUM_SEATS));
        assertDoesNotThrow(() -> service.validatePremiumSeatQuantity(
                AppConstants.MIN_PREMIUM_SEATS + 1,
                AppConstants.MIN_PREMIUM_SEATS + 5));
        assertDoesNotThrow(() -> service.validateStandardSeatPrice(
                AppConstants.MIN_STANDARD_PRICE + 1.0,
                AppConstants.MIN_PREMIUM_PRICE + 10.0));
        assertDoesNotThrow(() -> service.validatePremiumSeatPrice(
                AppConstants.MIN_PREMIUM_PRICE + 5.0,
                AppConstants.MIN_PREMIUM_PRICE));
    }
}
