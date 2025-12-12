package ets.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PassengerRepository - Filed-based implementation of IPassengerRepository
 */
public class PassengerRepository implements IPassengerRepository {

    private static final String FILE_NAME = "PassengerFile.txt";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LinkedHashMap<String, Passenger> getAll() {
        LinkedHashMap<String, Passenger> map = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                if (fields.length < 7) {
                    continue; // skip corrupted/invalid line
                }

                String name = fields[0];
                String contactNo = fields[1];
                String ic = fields[2];
                String id = fields[3];
                char gender = fields[4].charAt(0);
                LocalDate joinedDate = LocalDate.parse(fields[5], DATE_FORMATTER);
                char tierCode = fields[6].charAt(0);
                PassengerTier tier = PassengerTier.fromCode(tierCode);

                Passenger passenger = new Passenger(
                        name, contactNo, ic, id, gender, joinedDate, tier
                );
                map.put(id, passenger);
            }
        } catch (java.io.FileNotFoundException e) {
            // First run: file does not exist yet -> treat as "no passengers".
            // Do nothing, just return empty map.
            // (No stack trace printed, so your tests look clean.)
        } catch (IOException e) {
            // Real I/O error while reading an existing file
            e.printStackTrace();
        }

        return map;
    }

    @Override
    public void saveAll(Collection<Passenger> passengers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Passenger p : passengers) {
                writer.write(
                        p.getName() + "|" +
                                p.getContactNo() + "|" +
                                p.getIc() + "|" +
                                p.getId() + "|" +
                                p.getGender() + "|" +
                                p.getDateJoined().format(DATE_FORMATTER) + "|" +
                                p.getPassengerTier().getCode()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper to generate a new unique passenger ID (e.g. P001, P002...).
     * You can keep this here or move it into a util class if preferred.
     */
    public String generateNewId() {
        LinkedHashMap<String, Passenger> passengers = getAll();
        int maxNumber = 0;

        for (Map.Entry<String, Passenger> entry : passengers.entrySet()) {
            String id = entry.getKey(); // assume format P001, P002...
            try {
                int num = Integer.parseInt(id.substring(1));
                if (num > maxNumber) {
                    maxNumber = num;
                }
            } catch (Exception ignored) {
            }
        }

        int next = maxNumber + 1;
        return String.format("P%03d", next);
    }
}
