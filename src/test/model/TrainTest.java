package test.model;

import oopt.assignment.model.Train;
import oopt.assignment.model.TrainStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Train
 */
class TrainTest {

    /**
     * Verifies that the no-arg constructor creates a Train
     */
    @Test
    void testNoArgConstructorAndSetters() {
        Train train = new Train();

        LocalDate date = LocalDate.of(2030, 5, 10);
        LocalTime time = LocalTime.of(14, 30);

        train.setTrainID("T123");
        train.setDestination("KL Sentral");
        train.setDepartureDate(date);
        train.setDepartureTime(time);
        train.setStandardSeatQty(100);
        train.setPremiumSeatQty(20);
        train.setStandardSeatPrice(50.0);
        train.setPremiumSeatPrice(120.0);
        train.setStatus(TrainStatus.ACTIVE);

        assertEquals("T123", train.getTrainID());
        assertEquals("KL Sentral", train.getDestination());
        assertEquals(date, train.getDepartureDate());
        assertEquals(time, train.getDepartureTime());
        assertEquals(100, train.getStandardSeatQty());
        assertEquals(20, train.getPremiumSeatQty());
        assertEquals(50.0, train.getStandardSeatPrice());
        assertEquals(120.0, train.getPremiumSeatPrice());
        assertEquals(TrainStatus.ACTIVE, train.getStatus());
    }

    /**
     * Verifies the full-arg constructor correctly assigns all fields.
     */
    @Test
    void testFullArgConstructor() {
        LocalDate date = LocalDate.of(2031, 1, 1);
        LocalTime time = LocalTime.of(9, 0);

        Train train = new Train(
                "T777",
                "Johor",
                date,
                time,
                80,
                10,
                45.5,
                100.0,
                TrainStatus.DISCONTINUED
        );

        assertEquals("T777", train.getTrainID());
        assertEquals("Johor", train.getDestination());
        assertEquals(date, train.getDepartureDate());
        assertEquals(time, train.getDepartureTime());
        assertEquals(80, train.getStandardSeatQty());
        assertEquals(10, train.getPremiumSeatQty());
        assertEquals(45.5, train.getStandardSeatPrice());
        assertEquals(100.0, train.getPremiumSeatPrice());
        assertEquals(TrainStatus.DISCONTINUED, train.getStatus());
    }

    /**
     * Verifies that toString includes all fields and formats prices and status correctly.
     */
    @Test
    void testToStringFormatting() {
        Train train = new Train(
                "T001",
                "Penang",
                LocalDate.of(2030, 12, 25),
                LocalTime.of(16, 45),
                120,
                30,
                60.0,
                150.0,
                TrainStatus.ACTIVE
        );

        String s = train.toString();

        assertTrue(s.contains("Train ID                : T001"));
        assertTrue(s.contains("Destination             : Penang"));
        assertTrue(s.contains("Departure Date          : 2030-12-25"));
        assertTrue(s.contains("Departure Time          : 16:45"));
        assertTrue(s.contains("Standard Seat Quantity  : 120"));
        assertTrue(s.contains("Premium Seat Quantity   : 30"));
        assertTrue(s.contains("Standard Seat Price     : RM 60.00"));
        assertTrue(s.contains("Premium Seat Price      : RM 150.00"));
        assertTrue(s.contains("Train Status            : Active"));
    }
}
