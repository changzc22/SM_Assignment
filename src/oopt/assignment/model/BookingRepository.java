package oopt.assignment.model;

import oopt.assignment.Train;
import oopt.assignment.util.AppConstants;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles file I/O operations for Booking data.
 */
public class BookingRepository implements BookingInterface {

    private static final Logger logger = Logger.getLogger(BookingRepository.class.getName());

    @Override
    public ArrayList<Booking> getAll() {
        ArrayList<Booking> bookingList = new ArrayList<>();
        File file = new File(AppConstants.BOOKING_FILE_PATH);

        if (!file.exists()) {
            createNewFile(file);
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

    @Override
    public void saveAll(ArrayList<Booking> bookings) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(AppConstants.BOOKING_FILE_PATH, false))) {
            for (Booking b : bookings) {
                writer.write(formatBookingForFile(b));
                writer.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing to booking file", e);
        }
    }

    @Override
    public void add(Booking booking) {
        ArrayList<Booking> list = getAll();
        list.add(booking);
        saveAll(list);
    }

    @Override
    public void delete(String bookingId) {
        ArrayList<Booking> list = getAll();
        boolean removed = list.removeIf(b -> b.getBookingID().equals(bookingId));
        if (removed) {
            saveAll(list);
        } else {
            // Changed from WARNING to INFO or removed entirely to avoid scaring the user
            // logger.log(Level.INFO, "Delete requested for non-existent booking: " + bookingId);
        }
    }

    // --- Private Helpers ---

    private void createNewFile(File file) {
        try {
            if (file.createNewFile()) {
                logger.info("Created new booking file: " + AppConstants.BOOKING_FILE_PATH);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not create booking file", e);
        }
    }

    private Booking parseLineToBooking(String line) {
        try {
            // Format: ID|Name|TierCode|Seats|Fare|TrainID
            String[] fields = line.split("\\|");

            String id = fields[0];
            String name = fields[1];

            // Convert char code to Enum
            char tierCode = fields[2].charAt(0);
            SeatTier tier = SeatTier.fromCode(tierCode);

            int seats = Integer.parseInt(fields[3]);
            double fare = Double.parseDouble(fields[4]);
            String trainId = fields[5];

            String staffId = (fields.length > 6) ? fields[6] : "Unknown";
            Train train = new Train();
            train.setTrainID(trainId);

            return new Booking(id, name, tier, seats, fare, train, staffId);

        } catch (Exception e) {
            logger.log(Level.WARNING, "Corrupted data line skipped: " + line);
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