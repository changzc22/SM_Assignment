package oopt.assignment.model;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Interface defining the contract for Booking persistence.
 */
public interface IBookingRepository {

    /**
     * Retrieves all booking records from the underlying storage.
     *
     * @return An ArrayList containing all Booking objects.
     */
    ArrayList<Booking> getAll();

    /**
     * Persists a collection of bookings to the storage media.
     * This overwrites the existing data with the new list.
     *
     * @param bookingList The collection of bookings to save.
     */
    void saveAll(Collection<Booking> bookingList);

    /**
     * Adds a single booking to the storage.
     *
     * @param booking The new Booking object to persist.
     */
    void add(Booking booking);

    /**
     * Removes a booking from storage based on its unique ID.
     *
     * @param bookingId The ID of the booking to delete.
     */
    void delete(String bookingId);
}