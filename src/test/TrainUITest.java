package test;

import oopt.assignment.model.Train;
import oopt.assignment.model.TrainCreationRequest;
import oopt.assignment.model.TrainInterface;
import oopt.assignment.model.TrainStatus;
import oopt.assignment.service.TrainService;
import oopt.assignment.service.TrainValidator;
import oopt.assignment.ui.TrainUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TrainUI
 */
class TrainUITest {

    /**
     * Simple repository so TrainService does not touch the file system.
     */
    private static class InMemoryTrainRepository implements TrainInterface {
        private final List<Train> store = new ArrayList<>();

        @Override
        public List<Train> loadAll() {
            return new ArrayList<>(store);
        }

        @Override
        public void saveAll(List<Train> trains) {
            store.clear();
            store.addAll(trains);
        }
    }

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void setUpStreams() {
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    /**
     * Utility method to invoke a private method on TrainUI.
     */
    private Object invokePrivate(
            TrainUI ui,
            String methodName,
            Class<?>[] paramTypes,
            Object... args
    ) throws Exception {
        Method m = TrainUI.class.getDeclaredMethod(methodName, paramTypes);
        m.setAccessible(true);
        return m.invoke(ui, args);
    }

    /**
     * Creates a TrainService with an in-memory repository for use by TrainUI.
     */
    private TrainService createInMemoryService() {
        InMemoryTrainRepository repo = new InMemoryTrainRepository();
        repo.store.add(new Train(
                "T001", "Penang",
                LocalDate.of(2030, 1, 1),
                LocalTime.of(10, 0),
                100, 20,
                50.0, 120.0,
                TrainStatus.ACTIVE
        ));
        return new TrainService(repo, new TrainValidator());
    }

    /**
     * Verifies that readInt re-prompts on invalid input and eventually returns a valid integer.
     */
    @Test
    void testReadIntRetriesOnInvalidInput() throws Exception {
        String input = "abc\n42\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        TrainUI ui = new TrainUI(createInMemoryService(), scanner);

        int result = (int) invokePrivate(
                ui,
                "readInt",
                new Class<?>[]{String.class},
                "Enter number: "
        );

        assertEquals(42, result);

        String output = outContent.toString();
        assertTrue(output.contains("Enter number:"));
    }

    /**
     * Verifies that readDouble re-prompts on invalid input and returns a valid double.
     */
    @Test
    void testReadDoubleRetriesOnInvalidInput() throws Exception {
        String input = "xyz\n3.14\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        TrainUI ui = new TrainUI(createInMemoryService(), scanner);

        double result = (double) invokePrivate(
                ui,
                "readDouble",
                new Class<?>[]{String.class},
                "Enter amount: "
        );

        assertEquals(3.14, result, 0.0001);

        String output = outContent.toString();
        assertTrue(output.contains("Enter amount:"));
    }

    /**
     * Verifies that readIntWithSentinel returns null when sentinel value is entered.
     */
    @Test
    void testReadIntWithSentinelReturnsNullOnSentinel() throws Exception {
        String input = "-1\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        TrainUI ui = new TrainUI(createInMemoryService(), scanner);

        Integer result = (Integer) invokePrivate(
                ui,
                "readIntWithSentinel",
                new Class<?>[]{String.class, int.class},
                "Enter quantity: ",
                -1
        );

        assertNull(result);
    }

    /**
     * Verifies that readIntWithSentinel returns the parsed integer when not sentinel.
     */
    @Test
    void testReadIntWithSentinelReturnsValueWhenNotSentinel() throws Exception {
        String input = "10\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        TrainUI ui = new TrainUI(createInMemoryService(), scanner);

        Integer result = (Integer) invokePrivate(
                ui,
                "readIntWithSentinel",
                new Class<?>[]{String.class, int.class},
                "Enter quantity: ",
                -1
        );

        assertEquals(10, result);
    }

    /**
     * Verifies that readDoubleWithSentinel returns null when sentinel is entered.
     */
    @Test
    void testReadDoubleWithSentinelReturnsNullOnSentinel() throws Exception {
        String input = "-1.0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        TrainUI ui = new TrainUI(createInMemoryService(), scanner);

        Double result = (Double) invokePrivate(
                ui,
                "readDoubleWithSentinel",
                new Class<?>[]{String.class, double.class},
                "Enter price: ",
                -1.0
        );

        assertNull(result);
    }

    /**
     * Verifies that readDoubleWithSentinel returns the parsed double for non-sentinel value.
     */
    @Test
    void testReadDoubleWithSentinelReturnsValueWhenNotSentinel() throws Exception {
        String input = "99.50\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        TrainUI ui = new TrainUI(createInMemoryService(), scanner);

        Double result = (Double) invokePrivate(
                ui,
                "readDoubleWithSentinel",
                new Class<?>[]{String.class, double.class},
                "Enter price: ",
                -1.0
        );

        assertEquals(99.50, result, 0.0001);
    }

    /**
     * Verifies that askYesNo keeps re-prompting until a valid Y/N is entered
     */
    @Test
    void testAskYesNoAcceptsYAndNAndRetriesOnInvalid() throws Exception {
        String input = "maybe\nY\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        TrainUI ui = new TrainUI(createInMemoryService(), scanner);

        boolean result = (boolean) invokePrivate(
                ui,
                "askYesNo",
                new Class<?>[]{String.class},
                "Confirm?"
        );

        assertTrue(result);

        String output = outContent.toString();
        assertTrue(output.contains("Confirm? (Y/N):"));
    }

    /**
     * Verifies that confirmChange prints the "From" and "To" values
     */
    @Test
    void testConfirmChangePrintsContextAndUsesAskYesNo() throws Exception {
        String input = "Y\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        TrainUI ui = new TrainUI(createInMemoryService(), scanner);

        boolean confirmed = (boolean) invokePrivate(
                ui,
                "confirmChange",
                new Class<?>[]{String.class, String.class, String.class},
                "Standard Seat Quantity",
                "100",
                "120"
        );

        assertTrue(confirmed);

        String output = outContent.toString();
        assertTrue(output.contains("Standard Seat Quantity From : 100"));
        assertTrue(output.contains("To   : 120"));
    }

    /**
     * Verifies that displayAddContext prints only the fields that are non-default
     */
    @Test
    void testDisplayAddContextPrintsOnlyFilledFields() throws Exception {
        Scanner scanner = new Scanner(new ByteArrayInputStream(new byte[0]));
        TrainUI ui = new TrainUI(createInMemoryService(), scanner);

        TrainCreationRequest req = new TrainCreationRequest();
        req.trainId = "T999";
        req.destination = "Penang";
        req.departureDate = LocalDate.of(2031, 1, 1);
        req.departureTime = LocalTime.of(9, 0);
        req.standardSeatQty = 100;
        req.standardSeatPrice = 60.0;

        invokePrivate(
                ui,
                "displayAddContext",
                new Class<?>[]{TrainCreationRequest.class},
                req
        );

        String output = outContent.toString();
        assertTrue(output.contains("Train ID        : T999"));
        assertTrue(output.contains("Destination     : Penang"));
        assertTrue(output.contains("Departure Date  : 2031-01-01"));
        assertTrue(output.contains("Departure Time  : 09:00"));
        assertTrue(output.contains("Standard Seat Quantity  : 100"));
        assertTrue(output.contains("Standard Seat Price     : RM 60.00"));
        assertFalse(output.contains("Premium Seat Quantity"));
        assertFalse(output.contains("Premium Seat Price"));
    }

    /**
     * Verifies that showMenu displays the menu once and returns when the user chooses "Return".
     */
    @Test
    void testShowMenuReturnImmediately() {
        String input = "5\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        TrainUI ui = new TrainUI(createInMemoryService(), scanner);

        ui.showMenu();

        String output = outContent.toString();

        assertTrue(output.contains("Train Information Module"));
        assertTrue(output.contains("Add New Train"));
        assertTrue(output.contains("Return to Main Menu"));
    }
}
