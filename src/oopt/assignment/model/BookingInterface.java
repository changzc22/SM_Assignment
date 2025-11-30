package oopt.assignment.model;

import java.util.ArrayList;

/**
 * Interface defining data access operations for Booking.
 */
public interface BookingInterface {
    ArrayList<Booking> getAll();
    void saveAll(ArrayList<Booking> bookingList);
    void add(Booking booking);
    void delete(String bookingId);
}