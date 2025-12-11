package oopt.assignment.service;

import oopt.assignment.model.IStaffRepository;
import oopt.assignment.model.Staff;
import oopt.assignment.model.StaffRepository;
import oopt.assignment.util.AppConstants;
import oopt.assignment.util.ErrorMessage;
import oopt.assignment.util.PasswordUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Handles all business logic for Staff operations.
 * Implements clean code standards and exception handling.
 */
public class StaffService {

    private final IStaffRepository repository;
    private final StaffValidator validator;
    private final LinkedHashMap<String, Staff> staffCache;

    /**
     * Parameterised constructor for Dependency Injection
     * @param repository The data source provider interface
     */
    public StaffService(IStaffRepository repository) {
        this.repository = repository;
        this.validator = new StaffValidator();
        this.staffCache = this.repository.getAll();
    }

    // =========================================================================
    // SECTION: Instance Methods
    // =========================================================================

    /**
     * Retrieves all staff members currently in the system
     * @return ArrayList containing all Staff objects
     */
    public ArrayList<Staff> getAllStaff() {
        return new ArrayList<>(this.staffCache.values());
    }

    /**
     * Retrieves a specific staff member by their unique ID
     * @param id The unique staff ID to search for
     * @return The Staff object if found, otherwise null
     */
    public Staff getStaffById(String id) {
        return this.staffCache.get(id);
    }

    /**
     * Authenticates a staff member based on ID and password.
     * Checks for lockouts before processing password.
     * @param id The staff ID input
     * @param password The plain-text password input
     * @return The authenticated Staff object if successful, or null if failed
     */
    public Staff loginStaff(String id, String password) {
        Staff staff = getStaffById(id);
        if (staff == null) return null;

        checkLockoutStatus(staff);

        if (PasswordUtil.checkPassword(password, staff.getPassword())) {
            staff.setFailedAttempts(0);
            return staff;
        } else {
            handleFailedLogin(staff);
            return null;
        }
    }

    /**
     * Creates a new staff member and persists them to the repository
     * @param name Staff full name
     * @param cn Staff contact number
     * @param ic Staff identification card number
     * @param id Staff unique ID
     * @param pw Staff plain-text password (will be hashed)
     */
    public void createStaff(String name, String cn, String ic, String id, String pw) {
        String hashedPassword = PasswordUtil.hashPassword(pw);
        Staff staff = new Staff(name, cn, ic, id, hashedPassword, 0);

        this.staffCache.put(id, staff);
        repository.saveAll(staffCache.values());
    }

    /**
     * Updates a specific field of an existing staff member.
     * Uses Constants to avoid magic strings.
     * @param id The ID of the staff to update
     * @param fieldType The type of field to update (From AppConstants)
     * @param newValue The new value to assign to the field
     */
    public void updateStaffField(String id, String fieldType, String newValue) {
        Staff s = staffCache.get(id);
        if (s == null) return;

        switch (fieldType) {
            case AppConstants.FIELD_NAME -> s.setName(newValue);
            case AppConstants.FIELD_CONTACT -> s.setContactNo(newValue);
            case AppConstants.FIELD_IC -> s.setIc(newValue);
            case AppConstants.FIELD_PASSWORD -> s.setPassword(PasswordUtil.hashPassword(newValue));
        }
        repository.saveAll(staffCache.values());
    }

    /**
     * Deletes a staff member from the system, preventing self-deletion
     * @param idToDelete The ID of the staff to remove
     * @param currentStaffId The ID of the currently logged-in staff
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteStaff(String idToDelete, String currentStaffId) {
        if (idToDelete.equals(currentStaffId) || !staffCache.containsKey(idToDelete)) {
            return false;
        }
        staffCache.remove(idToDelete);
        repository.saveAll(staffCache.values());
        return true;
    }

    // --- Private Helpers ---

    /**
     * Checks if the staff account is currently locked due to excessive failed attempts
     * @param staff The staff object to check
     * @throws RuntimeException if the account is currently locked
     */
    private void checkLockoutStatus(Staff staff) {
        if (staff.getLockTime() != null) {
            if (staff.getLockTime().isAfter(LocalDateTime.now())) {
                long minutesLeft = Duration.between(LocalDateTime.now(), staff.getLockTime()).toMinutes() + 1;
                throw new RuntimeException(String.format(ErrorMessage.LOGIN_LOCKED, minutesLeft));
            }
            // Reset lock if time has passed
            staff.setLockTime(null);
            staff.setFailedAttempts(0);
        }
    }

    /**
     * Processes a failed login attempt and locks the account if max attempts are reached
     * @param staff The staff object associated with the failed attempt
     * @throws RuntimeException if the max attempts are reached and account becomes locked
     */
    private void handleFailedLogin(Staff staff) {
        int attempts = staff.getFailedAttempts() + 1;
        staff.setFailedAttempts(attempts);
        if (attempts >= AppConstants.MAX_LOGIN_ATTEMPTS) {
            staff.setLockTime(LocalDateTime.now().plusMinutes(AppConstants.LOCKOUT_MINUTES));
            throw new RuntimeException(String.format(ErrorMessage.LOGIN_MAX_ATTEMPTS, AppConstants.LOCKOUT_MINUTES));
        }
    }

    // --- Validation Delegates ---
    public boolean isNameValid(String name) { return validator.isNameValid(name); }
    public boolean isCNValid(String cn) { return validator.isCNValid(cn, staffCache); }
    public boolean isICValid(String ic) { return validator.isICValid(ic, staffCache); }
    public boolean isIDValid(String id) { return validator.isIDValid(id, staffCache); }
    public boolean isPWValid(String pw) { return validator.isPasswordComplexityValid(pw); }


    // =========================================================================
    // SECTION: Static Compatibility Layer (For Legacy Booking.java)
    // =========================================================================

    private static StaffService staticInstance;

    private static StaffService getStaticInstance() {
        if (staticInstance == null) {
            staticInstance = new StaffService(new StaffRepository());
        }
        return staticInstance;
    }

    /**
     * REQUIRED BY: Booking.java
     * Provides a list of all staff statically for legacy modules
     * @return ArrayList containing all Staff objects
     */
    public static ArrayList<Staff> getAllStaffStatic() {
        return getStaticInstance().getAllStaff();
    }

    /**
     * REQUIRED BY: Booking.java
     * Increments the booking handle count for a staff member statically
     * @param staffId The ID of the staff member who handled the booking
     */
    public void incrementBookingHandle(String staffId) {
        Staff s = staffCache.get(staffId);
        if (s != null) {
            s.setNoOfBookingHandle(s.getNoOfBookingHandle() + 1);
            repository.saveAll(staffCache.values());
        }
    }
}