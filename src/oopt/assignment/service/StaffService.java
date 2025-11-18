package oopt.assignment.service;

import oopt.assignment.model.Staff;
import oopt.assignment.model.StaffRepository;
import oopt.assignment.util.PasswordUtil;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.time.LocalDateTime;
import java.time.Duration;

public class StaffService {

    private final StaffRepository repository;
    // Using LinkedHashMap for O(1) access speed + Order preservation
    private final LinkedHashMap<String, Staff> staffCache;

    public StaffService(StaffRepository repository) {
        this.repository = repository;
        // The repository now returns the LinkedHashMap directly.
        this.staffCache = this.repository.readStaffFile();
    }

    // --- Static Helper Methods ---
    private static StaffRepository getStaticRepo() {
        return new StaffRepository();
    }

    // Keeps compatibility with external modules (like Booking) that expect ArrayList
    public static ArrayList<Staff> getAllStaffStatic() {
        return new ArrayList<>(getStaticRepo().readStaffFile().values());
    }

    public static void incrementBookingHandleStatic(String staffId) {
        StaffRepository repo = getStaticRepo();

        // Read as Map
        LinkedHashMap<String, Staff> staffMap = repo.readStaffFile();

        // Direct lookup O(1) - Instant access
        if (staffMap.containsKey(staffId)) {
            Staff s = staffMap.get(staffId);
            s.setNoOfBookingHandle(s.getNoOfBookingHandle() + 1);

            // Save the values back to file
            repo.updateStaffFile(staffMap.values());
        }
    }

    // --- Instance Methods ---

    // Returns ArrayList for the UI to iterate over
    public ArrayList<Staff> getAllStaff() {
        return new ArrayList<>(this.staffCache.values());
    }

    public void saveAllStaff() {
        // Pass the values collection directly to the repository
        repository.updateStaffFile(this.staffCache.values());
    }

    public Staff getStaffById(String id) {
        // Instant lookup from Map
        return this.staffCache.get(id);
    }

    public Staff loginStaff(String id, String password) {
        Staff staff = getStaffById(id);

        if (staff == null) {
            return null;
        }

        // Check Lockout
        if (staff.getLockTime() != null) {
            if (staff.getLockTime().isAfter(LocalDateTime.now())) {
                long minutesLeft = Duration.between(LocalDateTime.now(), staff.getLockTime()).toMinutes() + 1;
                throw new RuntimeException("Account is locked due to multiple failed attempts. Please try again in " + minutesLeft + " minutes.");
            } else {
                staff.setLockTime(null);
                staff.setFailedAttempts(0);
            }
        }

        // Check Password
        if (PasswordUtil.checkPassword(password, staff.getPassword())) {
            staff.setFailedAttempts(0);
            return staff;
        } else {
            int attempts = staff.getFailedAttempts() + 1;
            staff.setFailedAttempts(attempts);

            if (attempts >= 4) {
                staff.setLockTime(LocalDateTime.now().plusMinutes(5));
                throw new RuntimeException("Maximum login attempts (4) reached. Account locked for 5 minutes.");
            }
            return null;
        }
    }

    public boolean isNameValid(String name) {
        if (name.isEmpty()) return false;
        name = name.toUpperCase().trim();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isAlphabetic(c) && !Character.isWhitespace(c)) return false;
        }
        return true;
    }

    public boolean isCNValid(String cn) {
        if (cn.isEmpty() || (cn.length() != 10 && cn.length() != 11) || cn.charAt(0) != '0') return false;
        for (int i = 0; i < cn.length(); i++) {
            char c = cn.charAt(i);
            if (!Character.isDigit(c)) return false;
        }
        // Iterate values to check duplicate contact
        for (Staff staff : this.staffCache.values()) {
            if (staff.getContactNo().equals(cn)) return false;
        }
        return true;
    }

    public boolean isICValid(String ic) {
        if (ic.length() != 12) return false;
        for (int i = 0; i < ic.length(); i++) {
            char c = ic.charAt(i);
            if (!Character.isDigit(c)) return false;
        }
        // Iterate values to check duplicate IC
        for (Staff staff : this.staffCache.values()) {
            if (staff.getIc().equals(ic)) return false;
        }
        return true;
    }

    public boolean isIDValid(String id) {
        id = id.toUpperCase();
        if (id.length() != 4 || id.trim().isEmpty() || id.charAt(0) != 'S') return false;
        for (int i = 1; i < id.length(); i++) {
            char c = id.charAt(i);
            if (!Character.isDigit(c)) return false;
        }

        // Instant duplicate check via Map Key
        if (this.staffCache.containsKey(id)) {
            return false;
        }
        return true;
    }

    public boolean isPWValid(String password) {
        if (password.length() < 8) return false;
        boolean hasAlphabetOrSymbol = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isLetter(c) || !Character.isLetterOrDigit(c)) {
                hasAlphabetOrSymbol = true;
                break;
            }
        }
        return hasAlphabetOrSymbol;
    }

    public void createStaff(String name, String cn, String ic, String id, String pw) {
        String hashedPassword = PasswordUtil.hashPassword(pw);
        if (hashedPassword == null) {
            System.err.println("Error creating staff: Hashing failed.");
            return;
        }
        Staff staff = new Staff(name, cn, ic, id, hashedPassword, 0);

        // Put into Map (Instant)
        this.staffCache.put(id, staff);
        saveAllStaff();
    }

    public void updateStaffPassword(String id, String newPassword) {
        Staff s = this.staffCache.get(id);
        if (s != null) {
            String hashed = PasswordUtil.hashPassword(newPassword);
            if (hashed == null) return;
            s.setPassword(hashed);
            saveAllStaff();
        }
    }

    public void updateStaffName(String id, String newName) {
        Staff s = this.staffCache.get(id);
        if (s != null) {
            s.setName(newName);
            saveAllStaff();
        }
    }

    public void updateStaffContact(String id, String newCn) {
        Staff s = this.staffCache.get(id);
        if (s != null) {
            s.setContactNo(newCn);
            saveAllStaff();
        }
    }

    public void updateStaffIC(String id, String newIc) {
        Staff s = this.staffCache.get(id);
        if (s != null) {
            s.setIc(newIc);
            saveAllStaff();
        }
    }

    public boolean deleteStaff(String idToDelete, String currentStaffId) {
        if (idToDelete.equals(currentStaffId)) return false;

        // Instant Check and Remove
        if (this.staffCache.containsKey(idToDelete)) {
            this.staffCache.remove(idToDelete);
            saveAllStaff();
            return true;
        }
        return false;
    }

    public boolean changePassword(String id, String ic, String newPassword) {
        Staff s = this.staffCache.get(id);

        if (s != null && s.getIc().equals(ic)) {
            String hashed = PasswordUtil.hashPassword(newPassword);
            if (hashed == null) return false;
            s.setPassword(hashed);
            saveAllStaff();
            return true;
        }
        return false;
    }
}