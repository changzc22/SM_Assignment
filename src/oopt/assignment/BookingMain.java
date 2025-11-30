package oopt.assignment;

import oopt.assignment.model.Booking;
import oopt.assignment.model.BookingRepository;
import java.util.ArrayList;

/**
 * COMPATIBILITY CLASS
 * This class exists ONLY to support legacy modules (like Passenger)
 * that still depend on static BookingMain methods.
 * * It acts as a Bridge to the new BookingRepository.
 */
public class BookingMain {

    private static final BookingRepository repository = new BookingRepository();

    /**
     * Used by Passenger.java to read bookings.
     * Redirects to the new Repository.
     */
    public static ArrayList<Booking> readBookingFile() {
        return repository.getAll();
    }

    /**
     * Used by Passenger.java to update bookings (e.g. when renaming a passenger).
     * Redirects to the new Repository.
     */
    public static void writeBookingFile(ArrayList<Booking> bookingList) {
        repository.saveAll(bookingList);
    }

    /**
     * DEPRECATED: Redirects to the new UI.
     * Kept just in case any other legacy code calls it.
     */
    public static void bookingMain(String staffID) {
        System.out.println("[Notice] Redirecting to new Booking Module...");
        new oopt.assignment.ui.BookingUI().start();
    }
}