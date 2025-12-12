package test.util;

import ets.util.PasswordUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Password Utility Tests")
public class PasswordUtilTest {
    /**
     * Verifies that the hashing algorithm is deterministic.
     * The same input password must always produce the exact same hash output.
     */
    @Test
    void testHashConsistency() {
        String password = "Password123";
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);

        assertNotNull(hash1);
        assertEquals(hash1, hash2, "Hashing the same password twice must result in the same hash");
    }

    /**
     * Verifies that the checkPassword method correctly identifies a match
     * when the provided plain text password corresponds to the stored hash.
     */
    @Test
    void testCheckPasswordSuccess() {
        String plain = "SecurePass1";
        String hash = PasswordUtil.hashPassword(plain);
        assertTrue(PasswordUtil.checkPassword(plain, hash), "Password verification should succeed for correct input");
    }

    /**
     * Verifies that the checkPassword method correctly rejects a mismatch,
     * ensuring that an incorrect password does not validate against the hash.
     */
    @Test
    void testCheckPasswordFailure() {
        String plain = "SecurePass1";
        String hash = PasswordUtil.hashPassword(plain);
        assertFalse(PasswordUtil.checkPassword("WrongPass", hash), "Password verification should fail for wrong input");
    }
}
