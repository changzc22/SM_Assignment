package test.model;

import ets.model.Passenger;
import ets.model.PassengerTier;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PassengerTest - Test the Passenger model class
 */
class PassengerTest {

    /**
     * Verifies that the parameterised constructor correctly assigns all fields.
     * This test ensures that getter methods return the expected values and that
     * Passenger inherits Person's fields correctly. It also indirectly verifies
     * both Person.toString() and Passenger.toString() by checking the output contains
     * key values such as name and tier.
     */
    @Test
    void constructorAndGetters_workCorrectly() {
        LocalDate joined = LocalDate.of(2025, 1, 1);
        Passenger p = new Passenger(
                "Alice",
                "0123456789",
                "010203040506",
                "P001",
                'F',
                joined,
                PassengerTier.SILVER
        );

        assertEquals("Alice", p.getName());
        assertEquals("0123456789", p.getContactNo());
        assertEquals("010203040506", p.getIc());
        assertEquals("P001", p.getId());
        assertEquals('F', p.getGender());
        assertEquals(joined, p.getDateJoined());
        assertEquals(PassengerTier.SILVER, p.getPassengerTier());

        String text = p.toString();
        assertTrue(text.contains("Alice"));
        assertTrue(text.contains("SILVER"));
    }

    /**
     * Confirms that setter methods correctly update individual fields in the
     * Passenger object. This ensures mutability works as expected and that the
     * inherited fields from Person (name, contact, IC, ID) can be modified safely.
     */
    @Test
    void setters_updateFields() {
        Passenger p = new Passenger();
        p.setName("Bob");
        p.setContactNo("0987654321");
        p.setIc("111122223333");
        p.setId("P002");
        p.setGender('M');
        p.setPassengerTier(PassengerTier.GOLD);

        assertEquals("Bob", p.getName());
        assertEquals("0987654321", p.getContactNo());
        assertEquals("111122223333", p.getIc());
        assertEquals("P002", p.getId());
        assertEquals('M', p.getGender());
        assertEquals(PassengerTier.GOLD, p.getPassengerTier());
    }
}
