package oopt.assignment.model;

import oopt.assignment.Train;

public class Booking {

    // --- DATA FIELDS ---
    private String bookingID;
    private String name;
    private SeatTier seatTier;
    private int numOfSeatBook;
    private double totalFare;
    private Train train;       // Association

    // --- CONSTRUCTORS ---
    public Booking() {
    }

    public Booking(String bookingID, String name, SeatTier seatTier, int numOfSeatBook, double totalFare, Train train) {
        this.bookingID = bookingID;
        this.name = name;
        this.seatTier = seatTier;
        this.numOfSeatBook = numOfSeatBook;
        this.totalFare = totalFare;
        this.train = train;
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

    
    @Override
    public String toString() {
        return String.format("Booking [ID=%s, Name=%s, Train=%s, Seats=%d, Fare=RM%.2f]",
                bookingID, name, (train != null ? train.getTrainID() : "null"), numOfSeatBook, totalFare);
    }
}