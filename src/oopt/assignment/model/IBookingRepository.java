package oopt.assignment.model;

import java.util.ArrayList;
import java.util.Collection;

public interface IBookingRepository {
    ArrayList<Booking> getAll();
    void saveAll(Collection<Booking> bookingList);
    void add(Booking booking);
    void delete(String bookingId);
}