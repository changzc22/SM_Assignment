package oopt.assignment.model;

import java.time.LocalDateTime;

public class Staff extends Person {

    private String password;
    private int noOfBookingHandle = 0;

    // Security Session Fields (Transient)
    private int failedAttempts = 0;
    private LocalDateTime lockTime = null;

    /**
     * Default Constructor
     */
    public Staff() {
        super();
    }

    /**
     * Parameterised constructor
     * @param name staff name
     * @param contactNo staff contact number
     * @param ic staff identification no
     * @param id staff unique id
     * @param password staff login password
     * @param noOfBookingHandle number of booking handled by staff
     */
    public Staff(String name, String contactNo, String ic, String id, String password, int noOfBookingHandle) {
        super(name, contactNo, ic, id);
        this.password = password;
        this.noOfBookingHandle = noOfBookingHandle;
    }

    // Getters and Setters

    /**
     * Get staff password
     * @return staff password
     */
    public String getPassword() { return password; }

    /**
     * Set staff password
     * @param password staff password from user
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Get number of booking handled by the staff
     * @return number of booking handled by staff in integer
     */
    public int getNoOfBookingHandle() { return noOfBookingHandle; }

    /**
     * Get number of booking handled by the staff
     * @param noOfBookingHandle number of booking handled by staff in integer
     */
    public void setNoOfBookingHandle(int noOfBookingHandle) { this.noOfBookingHandle = noOfBookingHandle; }

    /**
     * Get number of login failed attempts by the staff
     * @return number of login failed attempts by staff in integer
     */
    public int getFailedAttempts() { return failedAttempts; }

    /**
     * Set number of login failed attempts by the staff
     * @param failedAttempts number of login failed attempts by staff in integer
     */
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }

    /**
     * Get staff login failed lock time
     * @return the staff login failed lock time in local date time
     */
    public LocalDateTime getLockTime() { return lockTime; }

    /**
     * Set staff login failed lock time
     * @param lockTime the staff login failed lock time in local date time
     */
    public void setLockTime(LocalDateTime lockTime) { this.lockTime = lockTime; }

    /**
     * To String method
     * @return the string of staff object with number of booking handled
     */
    @Override
    public String toString() {
        return super.toString() + "\nBookings Handled  : " + noOfBookingHandle;
    }
}