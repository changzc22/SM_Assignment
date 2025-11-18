package oopt.assignment.model;

import java.time.LocalDateTime;

// This class now only contains data (fields, constructors, getters, setters)
// It inherits from the Person model.
public class Staff extends Person {

    private String password;
    private int noOfBookingHandle = 0;

    // --- SECURITY FIELDS (Transient / In-Memory) ---
    // These track login attempts while the app is running.
    private int failedAttempts = 0;
    private LocalDateTime lockTime = null;

    public Staff() {
        super();
    }

    public Staff(String name, String contactNo, String ic, String id) {
        super(name, contactNo, ic, id);
    }

    public Staff(String name, String contactNo, String ic, String id, String password, int noOfBookingHandle) {
        super(name, contactNo, ic, id);
        this.password = password;
        this.noOfBookingHandle = noOfBookingHandle;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNoOfBookingHandle() {
        return noOfBookingHandle;
    }

    public void setNoOfBookingHandle(int noOfBookingHandle) {
        this.noOfBookingHandle = noOfBookingHandle;
    }

    // --- Getters and Setters for Security Fields ---

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getLockTime() {
        return lockTime;
    }

    public void setLockTime(LocalDateTime lockTime) {
        this.lockTime = lockTime;
    }

    @Override
    public String toString() {
        return super.toString() + "\nNumber Of Booking Handle: " + noOfBookingHandle;
    }
}