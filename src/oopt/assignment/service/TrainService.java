package oopt.assignment.service;

import oopt.assignment.model.Train;
import oopt.assignment.model.TrainCreationRequest;
import oopt.assignment.model.TrainInterface;
import oopt.assignment.model.TrainRepository;
import oopt.assignment.model.TrainStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for the Train module.
 */
public class TrainService {

    private final TrainInterface repository;
    private final TrainValidator validator;
    private final List<Train> trains;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Default constructor.
     */
    public TrainService() {
        this(new TrainRepository(), new TrainValidator());
    }

    /**
     * Constructor for dependency injection and testing.
     * @param repository repository implementation used to load and save trains
     * @param validator  validator used to enforce train-related business rules
     */
    public TrainService(TrainInterface repository, TrainValidator validator) {
        this.repository = repository;
        this.validator = validator;
        this.trains = new ArrayList<>(repository.loadAll());
    }

    /**
     * Returns a  copy of all trains.
     * @return new List containing all trains
     */
    public List<Train> getAllTrains() {
        return new ArrayList<>(trains);
    }

    /**
     * Returns a list of trains whose status is active
     * @return list of active trains
     */
    public List<Train> getActiveTrains() {
        return trains.stream()
                .filter(t -> t.getStatus() == TrainStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    /**
     * Searches for a train by its unique Train ID.
     * @param trainId train ID to search
     * @return Optional containing the matching train if found or empty if not found
     */
    public Optional<Train> findById(String trainId) {
        return trains.stream()
                .filter(t -> t.getTrainID().equalsIgnoreCase(trainId))
                .findFirst();
    }

    /**
     * Adds a new train to the list and persists the change.
     * @param train fully constructed to be added
     * @return the same Train instance that was added
     */
    public Train addTrain(Train train) {
        trains.add(train);
        save();
        return train;
    }

    /**
     * Creates a Train from a validated TrainCreationRequest and persists it.
     * @param req validated TrainCreationRequest containing all necessary data
     * @return the newly created and persisted Train instance
     */
    public Train createTrain(TrainCreationRequest req) {
        Train train = new Train(
                req.trainId,
                req.destination,
                req.departureDate,
                req.departureTime,
                req.standardSeatQty,
                req.premiumSeatQty,
                req.standardSeatPrice,
                req.premiumSeatPrice,
                TrainStatus.ACTIVE
        );
        return addTrain(train);
    }

    /**
     * Marks the given Train} as discontinued and persists the change.
     * @param train existing train whose status should be set to discontinue
     */
    public void discontinueTrain(Train train) {
        train.setStatus(TrainStatus.DISCONTINUED);
        save();
    }

    /**
     * Persists the current list of trains to the underlying repository.
     */
    public void save() {
        repository.saveAll(trains);
    }


    /**
     * Delegates Train ID format validation
     * @param trainId Train ID string to validate
     * @return {true if the format is valid and false otherwise
     */
    public boolean isValidTrainIdFormat(String trainId) {
        return validator.isValidTrainIdFormat(trainId);
    }

    /**
     * Delegates destination format validation
     * @param destination destination string to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidDestinationFormat(String destination) {
        return validator.isValidDestinationFormat(destination);
    }

    /**
     * Delegates future date validation
     * @param valueDate date string to validate (expected yyyy-MM-dd)
     * @return true if the date is valid and after today, false}otherwise
     */
    public boolean isValidFutureDate(String valueDate) {
        return validator.isValidFutureDate(valueDate);
    }

    /**
     * Delegates time format validation
     * @param valueTime time string to validate (expected HH:mm)
     * @return true if the time is valid, false otherwise
     */
    public boolean isValidTime(String valueTime) {
        return validator.isValidTime(valueTime);
    }

    /**
     * Delegates validation of a new standard seat quantity
     * @param newQty        proposed new standard seat quantity
     * @param premiumSeats  current premium seat quantity for the train
     * @throws IllegalArgumentException if the quantity violates any business rule
     */
    public void validateStandardSeatQuantity(int newQty, int premiumSeats) {
        validator.validateStandardSeatQuantity(newQty, premiumSeats);
    }

    /**
     * Delegates validation of a new premium seat quantity
     * @param newQty         proposed new premium seat quantity
     * @param standardSeats  current standard seat quantity for the train
     * @throws IllegalArgumentException if the quantity violates any business rule
     */
    public void validatePremiumSeatQuantity(int newQty, int standardSeats) {
        validator.validatePremiumSeatQuantity(newQty, standardSeats);
    }

    /**
     * Delegates validation of a new standard seat price
     * @param newPrice     proposed new standard seat price
     * @param premiumPrice current premium seat price for the train
     * @throws IllegalArgumentException if the price violates any business rule
     */
    public void validateStandardSeatPrice(double newPrice, double premiumPrice) {
        validator.validateStandardSeatPrice(newPrice, premiumPrice);
    }

    /**
     * Delegates validation of a new premium seat price
     * @param newPrice      proposed new premium seat price
     * @param standardPrice current standard seat price for the train
     * @throws IllegalArgumentException if the price violates any business rule
     */
    public void validatePremiumSeatPrice(double newPrice, double standardPrice) {
        validator.validatePremiumSeatPrice(newPrice, standardPrice);
    }

    /**
     * Checks whether a given Train ID already exists in the list.
     * @param trainId train ID to check
     * @return true if a train with the same ID already exists, false otherwise
     */
    public boolean isDuplicateTrainId(String trainId) {
        return trains.stream()
                .anyMatch(t -> t.getTrainID().equalsIgnoreCase(trainId));
    }

    /**
     * Checks whether there is already an antive train with the same destination.
     * @param destination destination to check
     * @return rue if an active train exists with the same destination, false otherwise
     */
    public boolean isDuplicateDestination(String destination) {
        return trains.stream()
                .anyMatch(t -> t.getDestination().equalsIgnoreCase(destination)
                        && t.getStatus() == TrainStatus.ACTIVE);
    }

    /**
     * Parses a date string using the shared formatter
     * @param value date string in yyyy-MM-dd format
     * @return parsed LocalDate
     */
    public LocalDate parseDate(String value) {
        return LocalDate.parse(value, DATE_FORMATTER);
    }

    /**
     * Parses a time string using the shared formatter (HH:mm).
     * @param value time string in HH:mm format
     * @return parsed LocalTime
     */
    public LocalTime parseTime(String value) {
        return LocalTime.parse(value, TIME_FORMATTER);
    }
}
