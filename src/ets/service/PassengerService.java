package ets.service;

import ets.model.IPassengerRepository;
import ets.model.Passenger;
import ets.model.PassengerRepository;
import ets.model.PassengerTier;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * PassengerService - Core business logic layer for the passenger module
 */
public class PassengerService {

    private final IPassengerRepository repository;
    private final PassengerValidator validator;

    public PassengerService(IPassengerRepository repository,
                            PassengerValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    /**
     * Convenience constructor if you just want to use the file-based repository.
     */
    public PassengerService() {
        this(new PassengerRepository(), new PassengerValidator());
    }

    /**
     * Use for list all passenger data
     * @return all passengers data
     */
    public Collection<Passenger> getAllPassengers() {
        return repository.getAll().values();
    }

    /**
     * Call the validator from the UI
     * @param name Passenger's name
     */
    public void validateName(String name) {
        validator.validateName(name);
    }

    /**
     * Call the validator from the UI
     * @param contactNo Passenger's contact number/phone number
     */
    public void validateContact(String contactNo) {
        validator.validateContact(contactNo);
    }

    /**
     * Call the validator from the UI
     * @param ic Passenger's identity card number
     */
    public void validateIc(String ic) {
        validator.validateIc(ic);
    }

    /**
     * Call the validator from the UI
     * @param gender Passenger's gender
     */
    public void validateGender(char gender) {
        validator.validateGender(gender);
    }


    /**
     * Search passenger using id
     * @param id Passenger's id
     * @return passenger data
     */
    public Optional<Passenger> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        Passenger p = repository.getAll().get(id);
        return Optional.ofNullable(p);
    }

    /**
     * Register new passenger into the system
     * @param name Passenger's name
     * @param contactNo Passenger's contact number/phone number
     * @param ic Passenger's identity card number
     * @param gender Passenger's gender
     * @return new passenger data
     */
    public Passenger registerNewPassenger(String name,
                                          String contactNo,
                                          String ic,
                                          char gender) {

        validator.validateName(name);
        validator.validateContact(contactNo);
        validator.validateIc(ic);
        ensureIcUnique(ic, null);
        validator.validateGender(gender);

        PassengerRepository concreteRepo = ensureConcreteRepo();

        String id = concreteRepo.generateNewId();
        LocalDate joinedDate = LocalDate.now();
        PassengerTier defaultTier = PassengerTier.NORMAL;

        Passenger newPassenger = new Passenger(
                name, contactNo, ic, id, Character.toUpperCase(gender),
                joinedDate, defaultTier
        );

        LinkedHashMap<String, Passenger> all = repository.getAll();
        all.put(id, newPassenger);
        repository.saveAll(all.values());

        return newPassenger;
    }

    /**
     * Update passenger information in the system
     * @param updatedPassenger updated passenger data
     */
    public void updatePassenger(Passenger updatedPassenger) {
        LinkedHashMap<String, Passenger> all = repository.getAll();

        if (!all.containsKey(updatedPassenger.getId())) {
            throw new IllegalArgumentException("Passenger not found: " + updatedPassenger.getId());
        }

        validator.validateName(updatedPassenger.getName());
        validator.validateContact(updatedPassenger.getContactNo());
        validator.validateIc(updatedPassenger.getIc());
        ensureIcUnique(updatedPassenger.getIc(), updatedPassenger.getId());
        validator.validateGender(updatedPassenger.getGender());

        all.put(updatedPassenger.getId(), updatedPassenger);
        repository.saveAll(all.values());
    }

    /**
     * Change (upgrade/downgrade) the passenger's tier manually by staff
     * @param passengerId Passenger's id
     * @param newTierCode Passenger's tier in the system
     */
    public void changeTier(String passengerId, char newTierCode) {
        LinkedHashMap<String, Passenger> all = repository.getAll();

        Passenger p = all.get(passengerId);
        if (p == null) {
            throw new IllegalArgumentException("Passenger not found: " + passengerId);
        }

        validator.validateTier(newTierCode);
        PassengerTier newTier = PassengerTier.fromCode(newTierCode);
        p.setPassengerTier(newTier);
        repository.saveAll(all.values());
    }


    /**
     * To check the PassengerRepository is concrete
     * @return PassengerRepository
     */
    private PassengerRepository ensureConcreteRepo() {
        if (repository instanceof PassengerRepository repo) {
            return repo;
        }
        throw new IllegalStateException(
                "New ID generation requires PassengerRepository concrete type.");
    }

    /**
     * To ensure the IC is unique for all passengers
     * @param ic Passenger's ic
     * @param currentPassengerId Passenger's id
     */
    private void ensureIcUnique(String ic, String currentPassengerId) {
        LinkedHashMap<String, Passenger> all = repository.getAll();

        for (Passenger existing : all.values()) {
            if (existing.getIc().equals(ic)) {
                if (!existing.getId().equals(currentPassengerId)) {
                    throw new IllegalArgumentException("IC is already registered in the system.");
                }
            }
        }
    }

}
