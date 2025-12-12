package oopt.assignment.model;

import oopt.assignment.util.AppConstants;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Handles File I/O for Booking data using a text file.
 * Implements IBookingRepository for loose coupling.
 */
public class BookingRepository implements IBookingRepository {

    private static final Logger logger = Logger.getLogger(BookingRepository.class.getName());


    /**
     * Reads all bookings from the file.
     * @return ArrayList of Booking objects
     */
    @Override
    public ArrayList<Booking> getAll() {
        ArrayList<Booking> bookingList = new ArrayList<>();
        File file = new File(AppConstants.BOOKING_FILE_PATH);

        if (!file.exists()) {
            return bookingList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Booking booking = parseLineToBooking(line);
                if (booking != null) {
                    bookingList.add(booking);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading booking file", e);
        }
        return bookingList;
    }


    /**
     * Overwrites the booking file with the provided collection.
     * @param bookings The list of bookings to persist
     */
    @Override
    public void saveAll(Collection<Booking> bookings) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(AppConstants.BOOKING_FILE_PATH, false))) {
            for (Booking b : bookings) {
                writer.write(formatBookingForFile(b));
                writer.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing to booking file", e);
        }
    }


    /**
     * Helper to append a single booking to the file.
     * @param booking The booking object to add
     */
    @Override
    public void add(Booking booking) {
        ArrayList<Booking> list = getAll();
        list.add(booking);
        saveAll(list);
    }


    /**
     * Helper to remove a booking by ID and update the file.
     * @param bookingId The ID of the booking to remove
     */
    @Override
    public void delete(String bookingId) {
        ArrayList<Booking> list = getAll();
        if (list.removeIf(b -> b.getBookingID().equals(bookingId))) {
            saveAll(list);
        }
    }

    private Booking parseLineToBooking(String line) {
        try {
            String[] fields = line.split("\\|");
            String id = fields[0];
            String name = fields[1];
            SeatTier tier = SeatTier.fromCode(fields[2].charAt(0));
            int seats = Integer.parseInt(fields[3]);
            double fare = Double.parseDouble(fields[4]);
            String trainId = fields[5];
            String staffId = (fields.length > 6) ? fields[6] : "UNKNOWN";

            Train train = new Train();
            train.setTrainID(trainId);

            return new Booking(id, name, tier, seats, fare, train, staffId);
        } catch (Exception e) {
            return null;
        }
    }

    private String formatBookingForFile(Booking b) {
        return String.join("|",
                b.getBookingID(),
                b.getName(),
                String.valueOf(b.getSeatTier().getCode()),
                String.valueOf(b.getNumOfSeatBook()),
                String.format("%.2f", b.getTotalFare()),
                b.getTrain().getTrainID(),
                b.getStaffId()
        );
    }
}