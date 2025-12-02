package oopt.assignment.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents validated data needed to create a Train Used by UI to collect input
 */
public class TrainCreationRequest {
    public String trainId;
    public String destination;
    public LocalDate departureDate;
    public LocalTime departureTime;
    public int standardSeatQty;
    public int premiumSeatQty;
    public double standardSeatPrice;
    public double premiumSeatPrice;
}
