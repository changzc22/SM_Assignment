package oopt.assignment.model;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * IPassengerRepository - Describes how passengers are stored (file, database, in-memory for testing) without changing the service.
 */
public interface IPassengerRepository {
    /**
     * Returns all passengers keyed by their ID.
     */
    LinkedHashMap<String, Passenger> getAll();

    /**
     * Persists all passengers to the underlying storage.
     */
    void saveAll(Collection<Passenger> passengers);
}
