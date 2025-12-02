package test;

import oopt.assignment.model.Train;
import oopt.assignment.model.TrainRepository;
import oopt.assignment.model.TrainStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainRepositoryTest{
/**
 * Verifies that loadAll handles IO errors
 */
@Test
void testLoadAllHandlesIOExceptionGracefully(@TempDir Path tempDir) {
    File dirAsFile = tempDir.resolve("train_dir").toFile();
    assertTrue(dirAsFile.mkdir());

    TrainRepository repo = new TrainRepository(dirAsFile.getAbsolutePath());
    List<Train> trains = repo.loadAll();

    assertNotNull(trains);
    assertTrue(trains.isEmpty());
}

/**
 * Verifies that saveAll catches IOExceptions
 */
@Test
void testSaveAllHandlesIOExceptionGracefully(@TempDir Path tempDir) {
    // Again, use a directory instead of a regular file
    File dirAsFile = tempDir.resolve("train_dir").toFile();
    assertTrue(dirAsFile.mkdir());

    TrainRepository repo = new TrainRepository(dirAsFile.getAbsolutePath());

    List<Train> trains = List.of(
            new Train(
                    "T999",
                    "TestDest",
                    LocalDate.of(2035, 5, 5),
                    LocalTime.of(15, 30),
                    50,
                    10,
                    40.0,
                    100.0,
                    TrainStatus.ACTIVE
            )
    );

    assertDoesNotThrow(() -> repo.saveAll(trains));

    assertTrue(dirAsFile.isDirectory());
}
}
