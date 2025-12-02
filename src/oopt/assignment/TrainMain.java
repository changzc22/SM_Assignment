package oopt.assignment;

import oopt.assignment.ui.MainUI;

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
import java.util.Scanner;

public class TrainMain {

    public static void trainMain() {
        int trainSelection = 0;
        Scanner sc = new Scanner(System.in);
        boolean validTrainInput;

        ArrayList<Train> trainList = TrainMain.readTrainFile();
        do {
            MainUI.clearScreen();
            do {
                validTrainInput = false;
                Train.displayTrainMenu();
                try {
                    System.out.print("Enter your choice: ");
                    trainSelection = sc.nextInt();
                    if (trainSelection >= 1 && trainSelection <= 6) {
                        validTrainInput = true;
                    } else {
                        MainUI.clearScreen();
                        System.err.println("Invalid Input. You should only enter between 1 and 6 options!");
                    }
                } catch (Exception e) {
                   MainUI.clearScreen();
                    System.err.println("You should not enter other characters!");
                    sc.nextLine();
                }
            } while (!validTrainInput);

            switch (trainSelection) {
                case 1:
                    Train.addNewTrain();
                    break;
                case 2:
                    Train.searchTrain();
                    break;
                case 3:
                    Train.modifyTrain();
                    break;
                case 4:
                    Train.displayTrain();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid input! Please enter again!");
            }
        } while (trainSelection != 5);
    }

    public static ArrayList<Train> readTrainFile() {
        File trainFile = new File("TrainFile.txt");

        ArrayList<Train> trainList = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(trainFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|");
                String trainID = fields[0];
                String destination = fields[1];
                LocalDate departureDate = LocalDate.parse(fields[2], dateFormatter);
                LocalTime departureTime = LocalTime.parse(fields[3], timeFormatter);
                int standardSeatQty = Integer.parseInt(fields[4]);
                int premiumSeatQty = Integer.parseInt(fields[5]);
                double standardSeatPrice = Double.parseDouble(fields[6]);
                double premiumSeatPrice = Double.parseDouble(fields[7]);
                boolean trainStatus = Boolean.parseBoolean(fields[8]);

                Train train1 = new Train(trainID, destination, departureDate, departureTime, standardSeatQty, premiumSeatQty, standardSeatPrice, premiumSeatPrice, trainStatus);
                trainList.add(train1);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trainList;
    }

    public static void writeTrainFile(ArrayList<Train> trainList) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("TrainFile.txt"));
            for (Train train : trainList) {
                String line
                        = train.getTrainID() + "|"
                        + train.getDestination() + "|"
                        + train.getDepartureDate() + "|"
                        + train.getDepartureTime() + "|"
                        + train.getStandardSeatQty() + "|"
                        + train.getPremiumSeatQty() + "|"
                        + train.getStandardSeatPrice() + "|"
                        + train.getPremiumSeatPrice() + "|"
                        + train.isTrainStatus();

                writer.write(line);
                writer.newLine();
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

}
