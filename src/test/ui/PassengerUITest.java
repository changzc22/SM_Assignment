package test.ui;

import oopt.assignment.service.PassengerService;
import oopt.assignment.ui.PassengerUI;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PassengerUITest {

    @Test
    void readInt_skipsInvalidAndReturnsValid() {
        String fakeInput = "abc\n10\n2\n4\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(fakeInput.getBytes()));

        PassengerService dummyService = new PassengerService();

        PassengerUI ui = new PassengerUI(dummyService, scanner);

        int result = ui.readInt("Enter choice > ", 1, 6);

        assertEquals(2, result);
    }
}
