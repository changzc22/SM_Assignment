package oopt.assignment.model;

import java.time.LocalDateTime;

public class Staff extends Person {

    private String password;
    private int noOfBookingHandle = 0;

    // Security Session Fields (Transient)
    private int failedAttempts = 0;
    private LocalDateTime lockTime = null;

    public Staff() {
        super();
    }

    public Staff(String name, String contactNo, String ic, String id, String password, int noOfBookingHandle) {
        super(name, contactNo, ic, id);
        this.password = password;
        this.noOfBookingHandle = noOfBookingHandle;
    }

    // Getters and Setters
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getNoOfBookingHandle() { return noOfBookingHandle; }
    public void setNoOfBookingHandle(int noOfBookingHandle) { this.noOfBookingHandle = noOfBookingHandle; }

    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }

    public LocalDateTime getLockTime() { return lockTime; }
    public void setLockTime(LocalDateTime lockTime) { this.lockTime = lockTime; }

    @Override
    public String toString() {
        return super.toString() + "\nBookings Handled  : " + noOfBookingHandle;
    }
}