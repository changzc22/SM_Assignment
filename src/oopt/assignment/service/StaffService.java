package oopt.assignment.service;

import oopt.assignment.model.IStaffRepository;
import oopt.assignment.model.Staff;
import oopt.assignment.model.StaffRepository;
import oopt.assignment.util.PasswordUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Handles all business logic for Staff operations.
 * Updated to include a Compatibility Layer for the Booking module.
 */
public class StaffService {

    // Dependencies (DIP: Depending on Interface)
    private final IStaffRepository repository;
    private final StaffValidator validator;

    // In-memory cache for O(1) access speed
    private final LinkedHashMap<String, Staff> staffCache;

    // Constants for Business Rules
    private static final int MAX_LOGIN_ATTEMPTS = 4;
    private static final int LOCKOUT_MINUTES = 5;

    /**
     * Constructor for Dependency Injection.
     * @param repository The data source provider.
     */
    public StaffService(IStaffRepository repository) {
        this.repository = repository;
        this.validator = new StaffValidator();
        this.staffCache = this.repository.getAll();
    }

    // =========================================================================
    // SECTION: Instance Methods (New Clean Code Style)
    // =========================================================================

    public ArrayList<Staff> getAllStaff() {
        return new ArrayList<>(this.staffCache.values());
    }

    public Staff getStaffById(String id) {
        return this.staffCache.get(id);
    }

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

    public void createStaff(String name, String cn, String ic, String id, String pw) {
        String hashedPassword = PasswordUtil.hashPassword(pw);
        Staff staff = new Staff(name, cn, ic, id, hashedPassword, 0);

        this.staffCache.put(id, staff);
        repository.saveAll(staffCache.values());
    }

    public void updateStaffField(String id, String fieldType, String newValue) {
        Staff s = staffCache.get(id);
        if (s == null) return;

        switch (fieldType) {
            case "NAME" -> s.setName(newValue);
            case "CONTACT" -> s.setContactNo(newValue);
            case "IC" -> s.setIc(newValue);
            case "PASSWORD" -> s.setPassword(PasswordUtil.hashPassword(newValue));
        }
        repository.saveAll(staffCache.values());
    }

    public boolean deleteStaff(String idToDelete, String currentStaffId) {
        if (idToDelete.equals(currentStaffId) || !staffCache.containsKey(idToDelete)) {
            return false;
        }
        staffCache.remove(idToDelete);
        repository.saveAll(staffCache.values());
        return true;
    }

    // --- Private Helpers ---

    private void checkLockoutStatus(Staff staff) {
        if (staff.getLockTime() != null) {
            if (staff.getLockTime().isAfter(LocalDateTime.now())) {
                long minutesLeft = Duration.between(LocalDateTime.now(), staff.getLockTime()).toMinutes() + 1;
                throw new RuntimeException("Account locked. Try again in " + minutesLeft + " minutes.");
            }
            staff.setLockTime(null);
            staff.setFailedAttempts(0);
        }
    }

    private void handleFailedLogin(Staff staff) {
        int attempts = staff.getFailedAttempts() + 1;
        staff.setFailedAttempts(attempts);
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            staff.setLockTime(LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES));
            throw new RuntimeException("Max attempts reached. Account locked for " + LOCKOUT_MINUTES + " minutes.");
        }
    }

    // --- Validation Delegates ---
    public boolean isNameValid(String name) { return validator.isNameValid(name); }
    public boolean isCNValid(String cn) { return validator.isCNValid(cn, staffCache); }
    public boolean isICValid(String ic) { return validator.isICValid(ic, staffCache); }
    public boolean isIDValid(String id) { return validator.isIDValid(id, staffCache); }
    public boolean isPWValid(String pw) { return validator.isPasswordComplexityValid(pw); }


    // =========================================================================
    // SECTION: Static Compatibility Layer (For Booking.java)
    // =========================================================================
    /* * These methods exist solely to support legacy modules (like Booking.java)
     * that rely on static method calls. They act as a bridge to the new
     * instance-based logic.
     */

    private static StaffService staticInstance;

    /**
     * Creates a Singleton instance of the service for static context usage.
     */
    private static StaffService getStaticInstance() {
        if (staticInstance == null) {
            staticInstance = new StaffService(new StaffRepository());
        }
        return staticInstance;
    }

    /**
     * REQUIRED BY: Booking.java (addBooking method)
     * Provides a list of all staff statically.
     */
    public static ArrayList<Staff> getAllStaffStatic() {
        return getStaticInstance().getAllStaff();
    }

    /**
     * REQUIRED BY: Booking.java (addBooking method)
     * Increments the booking handle count for a staff member.
     */
    public static void incrementBookingHandleStatic(String staffId) {
        StaffService service = getStaticInstance();
        Staff s = service.getStaffById(staffId);
        if (s != null) {
            s.setNoOfBookingHandle(s.getNoOfBookingHandle() + 1);
            service.repository.saveAll(service.staffCache.values());
        }
    }
}