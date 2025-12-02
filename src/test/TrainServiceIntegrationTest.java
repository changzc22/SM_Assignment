package test;

import oopt.assignment.model.*;
import oopt.assignment.service.TrainService;
import oopt.assignment.service.TrainValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for  TrainService.
 */
class TrainServiceIntegrationTest {

    private static class InMemoryTrainRepository implements TrainInterface {
        private final java.util.List<Train> backing = new java.util.ArrayList<>();

        @Override
        public java.util.List<Train> loadAll() {
            return new java.util.ArrayList<>(backing);
        }

        @Override
        public void saveAll(java.util.List<Train> trains) {
            backing.clear();
            backing.addAll(trains);
        }
    }

    private InMemoryTrainRepository repo;
    private TrainService service;

    @BeforeEach
    void setUp() {
        repo = new InMemoryTrainRepository();
        service = new TrainService(repo, new TrainValidator());
    }

    /**
     * Verifies end-to-end creation of a train (validation + persistence).
     */
    @Test
    void testCreateTrainEndToEnd() {
        TrainCreationRequest req = new TrainCreationRequest();
        req.trainId = "T555";
        req.destination = "Penang";
        req.departureDate = LocalDate.now().plusDays(5);
        req.departureTime = LocalTime.of(9, 0);
        req.standardSeatQty = 100;
        req.premiumSeatQty = 20;
        req.standardSeatPrice = 50.0;
        req.premiumSeatPrice = 120.0;

        assertTrue(service.isValidTrainIdFormat(req.trainId));
        assertTrue(service.isValidDestinationFormat(req.destination));

        Train created = service.createTrain(req);

        List<Train> all = repo.backing;
        assertEquals(1, all.size());
        assertEquals(created, all.get(0));
        assertEquals(TrainStatus.ACTIVE, created.getStatus());
    }

    /**
     * Verifies that discontinueTrain changes status and persists via repository.
     */
    @Test
    void testDiscontinueTrainEndToEnd() {
        Train t = new Train(
                "T600", "Johor",
                LocalDate.now().plusDays(10),
                LocalTime.of(13, 0),
                100, 20, 55.0, 130.0,
                TrainStatus.ACTIVE
        );
        repo.backing.add(t);

        service = new TrainService(repo, new TrainValidator());

        Train loaded = service.findById("T600").orElseThrow();
        assertEquals(TrainStatus.ACTIVE, loaded.getStatus());

        service.discontinueTrain(loaded);

        assertEquals(TrainStatus.DISCONTINUED, loaded.getStatus());
        assertEquals(TrainStatus.DISCONTINUED,
                repo.backing.stream()
                        .filter(x -> "T600".equals(x.getTrainID()))
                        .findFirst().orElseThrow()
                        .getStatus());
    }
}
