package oopt.assignment.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Model class representing a Train entity system.
 */
public class Train {

    private String trainID;
    private String destination;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private int standardSeatQty;
    private int premiumSeatQty;
    private double standardSeatPrice;
    private double premiumSeatPrice;
    private TrainStatus status; // ACTIVE / DISCONTINUED

    /**
     * constructor
     */
    public Train() {
    }

    /**
     * constructors with full fields
     * @param trainID            unique train identifier in format TXXX
     * @param destination        destination station name
     * @param departureDate      scheduled departure date
     * @param departureTime      scheduled departure time
     * @param standardSeatQty    number of standard seats available
     * @param premiumSeatQty     number of premium seats available
     * @param standardSeatPrice  ticket price for a standard seat
     * @param premiumSeatPrice   ticket price for a premium seat
     * @param status             operational status of the train
     */
    public Train(String trainID,
                 String destination,
                 LocalDate departureDate,
                 LocalTime departureTime,
                 int standardSeatQty,
                 int premiumSeatQty,
                 double standardSeatPrice,
                 double premiumSeatPrice,
                 TrainStatus status) {
        this.trainID = trainID;
        this.destination = destination;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.standardSeatQty = standardSeatQty;
        this.premiumSeatQty = premiumSeatQty;
        this.standardSeatPrice = standardSeatPrice;
        this.premiumSeatPrice = premiumSeatPrice;
        this.status = status;
    }

    /**
     * Get train ID.
     * @return unique train identifier as string
     */
    public String getTrainID() {
        return trainID;
    }

    /**
     * Get train destination.
     * @return destination station name as string
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Get train departure date.
     * @return scheduled departure date as LocalDate
     */
    public LocalDate getDepartureDate() {
        return departureDate;
    }

    /**
     * Get train departure time.
     * @return scheduled departure time as  LocalTime
     */
    public LocalTime getDepartureTime() {
        return departureTime;
    }

    /**
     * Get number of standard seats.
     * @return total standard seat quantity in integer
     */
    public int getStandardSeatQty() {
        return standardSeatQty;
    }

    /**
     * Get number of premium seats.
     * @return total premium seat quantity in integer
     */
    public int getPremiumSeatQty() {
        return premiumSeatQty;
    }

    /**
     * Get standard seat price.
     * @return price of a standard seat in double
     */
    public double getStandardSeatPrice() {
        return standardSeatPrice;
    }

    /**
     * Get premium seat price.
     * @return price of a premium seat in double
     */
    public double getPremiumSeatPrice() {
        return premiumSeatPrice;
    }

    /**
     * Get train status.
     * @return current train status as TrainStatus
     */
    public TrainStatus getStatus() {
        return status;
    }


    /**
     * Set train ID.
     * @param trainID unique train identifier in format TXXX
     */
    public void setTrainID(String trainID) {
        this.trainID = trainID;
    }

    /**
     * Set train destination.
     * @param destination destination station name
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * Set train departure date.
     * @param departureDate scheduled departure date
     */
    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    /**
     * Set train departure time.
     * @param departureTime scheduled departure time
     */
    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    /**
     * Set number of standard seats.
     * @param standardSeatQty total standard seat quantity in integer
     */
    public void setStandardSeatQty(int standardSeatQty) {
        this.standardSeatQty = standardSeatQty;
    }

    /**
     * Set number of premium seats.
     * @param premiumSeatQty total premium seat quantity in integer
     */
    public void setPremiumSeatQty(int premiumSeatQty) {
        this.premiumSeatQty = premiumSeatQty;
    }

    /**
     * Set standard seat price.
     * @param standardSeatPrice price of a standard seat in double
     */
    public void setStandardSeatPrice(double standardSeatPrice) {
        this.standardSeatPrice = standardSeatPrice;
    }

    /**
     * Set premium seat price.
     * @param premiumSeatPrice price of a premium seat in double
     */
    public void setPremiumSeatPrice(double premiumSeatPrice) {
        this.premiumSeatPrice = premiumSeatPrice;
    }

    /**
     * Set train status.
     * @param status new train status as {@link TrainStatus}
     */
    public void setStatus(TrainStatus status) {
        this.status = status;
    }

    /**
     * To String method.
     * @return multi-line string containing formatted train details that contains all fields
     */
    @Override
    public String toString() {
        return "Train ID                : " + trainID
                + "\nDestination             : " + destination
                + "\nDeparture Date          : " + departureDate
                + "\nDeparture Time          : " + departureTime
                + "\nStandard Seat Quantity  : " + standardSeatQty
                + "\nPremium Seat Quantity   : " + premiumSeatQty
                + "\nStandard Seat Price     : RM " + String.format("%.2f", standardSeatPrice)
                + "\nPremium Seat Price      : RM " + String.format("%.2f", premiumSeatPrice)
                + "\nTrain Status            : " + (status == TrainStatus.ACTIVE ? "Active" : "Discontinued");
    }
}
