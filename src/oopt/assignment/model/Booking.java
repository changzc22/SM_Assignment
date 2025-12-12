package oopt.assignment.model;


/**
 * Domain entity representing a confirmed Booking.
 */
public class Booking {

    private String bookingID;
    private String name;
    private SeatTier seatTier;
    private int numOfSeatBook;
    private double totalFare;
    private Train train;
    private String staffId;

    public Booking() {
    }

    /**
     * Parameterized constructor to create a fully populated Booking object.
     *
     * @param bookingID     Unique identifier for the booking (e.g., B001)
     * @param name          Name of the passenger
     * @param seatTier      The tier of seats selected (Standard/Premium)
     * @param numOfSeatBook Number of seats reserved
     * @param totalFare     Total calculated price for the booking
     * @param train         The Train object associated with this booking
     * @param staffId       ID of the staff member who processed this booking
     */
    public Booking(String bookingID, String name, SeatTier seatTier, int numOfSeatBook, double totalFare, Train train, String staffId) {
        this.bookingID = bookingID;
        this.name = name;
        this.seatTier = seatTier;
        this.numOfSeatBook = numOfSeatBook;
        this.totalFare = totalFare;
        this.train = train;
        this.staffId = staffId;
    }

    // --- GETTERS AND SETTERS ---
    public String getBookingID() { return bookingID; }
    public void setBookingID(String bookingID) { this.bookingID = bookingID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public SeatTier getSeatTier() { return seatTier; }
    public void setSeatTier(SeatTier seatTier) { this.seatTier = seatTier; }

    public int getNumOfSeatBook() { return numOfSeatBook; }
    public void setNumOfSeatBook(int numOfSeatBook) { this.numOfSeatBook = numOfSeatBook; }

    public double getTotalFare() { return totalFare; }
    public void setTotalFare(double totalFare) { this.totalFare = totalFare; }

    public Train getTrain() { return train; }
    public void setTrain(Train train) { this.train = train; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }


    /**
     * Checks equality based on the unique Booking ID.
     * Essential for identifying duplicates in collections.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        // Two bookings are equal if their IDs are the same
        return bookingID != null && bookingID.equalsIgnoreCase(booking.bookingID);
    }

    /**
     * Generates a hash code consistent with the equals() method.
     */
    @Override
    public int hashCode() {
        return bookingID != null ? bookingID.toUpperCase().hashCode() : 0;
    }


    /**
     * Returns a string representation of the booking, primarily for debugging.
     */
    @Override
    public String toString() {
        return String.format("Booking [ID=%s, Name=%s, Train=%s, Seats=%d, Fare=RM%.2f, Staff=%s]",
                bookingID, name, (train != null ? train.getTrainID() : "null"), numOfSeatBook, totalFare, staffId);
    }
}