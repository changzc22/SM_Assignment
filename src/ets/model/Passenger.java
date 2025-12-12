package ets.model;

import java.time.LocalDate;

/**
 * Passenger - To keep passenger data in one clear object instead of passing many separate variables everywhere.
 */
public class Passenger extends Person {

    private char gender;
    private LocalDate dateJoined;
    private PassengerTier passengerTier;

    /**
     * Empty constructor
     */
    public Passenger() {
    }

    /**
     * Parameter constructor
     * @param name Passenger's name
     * @param contactNo Passenger's contact number/phone number
     * @param ic Passenger's identity card number
     * @param id Passenger's id
     * @param gender Passenger's gender
     * @param dateJoined Passenger's joined (registered) date
     * @param passengerTier Passenger's tier in the system
     */
    public Passenger(String name,
                     String contactNo,
                     String ic,
                     String id,
                     char gender,
                     LocalDate dateJoined,
                     PassengerTier passengerTier) {

        super(name, contactNo, ic, id);
        this.gender = gender;
        this.dateJoined = dateJoined;
        this.passengerTier = passengerTier;
    }

    /**
     * Getter 1
     * @return gender (Passenger's gender)
     */
    public char getGender() {
        return gender;
    }

    /**
     * Setter 1
     * @param gender Passenger's gender
     */
    public void setGender(char gender) {
        this.gender = gender;
    }

    /**
     * Getter 2
     * @return dateJoined (Passenger's joined (registered) date)
     */
    public LocalDate getDateJoined() {
        return dateJoined;
    }

    /**
     * Getter 3
     * @return passengerTier (Passenger's tier in the system)
     */
    public PassengerTier getPassengerTier() { return passengerTier; }


    /**
     * Setter 2
     * @param passengerTier Passenger's tier in the system
     */
    public void setPassengerTier(PassengerTier passengerTier) { this.passengerTier = passengerTier; }

    /**
     * Display passenger's information (gender, dateJoined, passengerTier)
     * @return all passenger information
     */
    @Override
    public String toString() {
        return super.toString()
                + "\nGender          : " + gender
                + "\nDate Joined     : " + dateJoined
                + "\nTier            : " + passengerTier;
    }
}
