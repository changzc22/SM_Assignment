package ets.model;

import ets.util.AppConstants;
import ets.util.ErrorMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File-based implementation of the repository.
 */
public class TrainRepository implements TrainInterface {

    private static final Logger LOGGER = Logger.getLogger(TrainRepository.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    private final String filePath;

    public TrainRepository() {
        this(AppConstants.TRAIN_FILE_PATH);
    }

    /**
     * constructor that allows tests to inject a custom file path.
     * @param filePath path to the file used for persistence
     */
    public TrainRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Load all Train records from the configured file path.
     * @return list of Train objects loaded from the file
     */
    @Override
    public List<Train> loadAll() {
        List<Train> result = new ArrayList<>();
        File trainFile = new File(filePath);

        if (!trainFile.exists()) {
            LOGGER.info("Train file not found. A new one will be created upon save.");
            return result;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(trainFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] fields = line.split("\\|");
                    String trainID = fields[0];
                    String destination = fields[1];
                    LocalDate departureDate = LocalDate.parse(fields[2], DATE_FORMATTER);
                    LocalTime departureTime = LocalTime.parse(fields[3], TIME_FORMATTER);
                    int standardSeatQty = Integer.parseInt(fields[4]);
                    int premiumSeatQty = Integer.parseInt(fields[5]);
                    double standardSeatPrice = Double.parseDouble(fields[6]);
                    double premiumSeatPrice = Double.parseDouble(fields[7]);
                    boolean statusFlag = Boolean.parseBoolean(fields[8]);

                    Train train = new Train(
                            trainID,
                            destination,
                            departureDate,
                            departureTime,
                            standardSeatQty,
                            premiumSeatQty,
                            standardSeatPrice,
                            premiumSeatPrice,
                            TrainStatus.fromBoolean(statusFlag)
                    );
                    result.add(train);
                } catch (Exception parseEx) {
                    LOGGER.log(Level.WARNING, ErrorMessage.CORRUPTED_DATA + line, parseEx);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, ErrorMessage.FILE_READ_ERROR, e);
        }
        return result;
    }

    /**
     * Persist all Train records to the configured file path.
     * @param trains list of trains to be persisted to file
     */
    @Override
    public void saveAll(List<Train> trains) {
        File trainFile = new File(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(trainFile))) {
            for (Train train : trains) {
                String line = train.getTrainID() + "|"
                        + train.getDestination() + "|"
                        + train.getDepartureDate().format(DATE_FORMATTER) + "|"
                        + train.getDepartureTime().format(TIME_FORMATTER) + "|"
                        + train.getStandardSeatQty() + "|"
                        + train.getPremiumSeatQty() + "|"
                        + train.getStandardSeatPrice() + "|"
                        + train.getPremiumSeatPrice() + "|"
                        + train.getStatus().toBoolean();

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, ErrorMessage.FILE_WRITE_ERROR, e);
        }
    }
}
