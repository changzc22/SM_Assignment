package test.model;

import oopt.assignment.model.Passenger;
import oopt.assignment.model.PassengerTier;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PassengerTest {

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

        // also covers Person.toString() and Passenger.toString()
        String text = p.toString();
        assertTrue(text.contains("Alice"));
        assertTrue(text.contains("SILVER"));
    }

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
