package oopt.assignment.model;

import oopt.assignment.model.Train;

import java.util.List;

/**
 * Abstraction for Train persistence.
 * swap file-based storage for DB in the future
 */
public interface TrainInterface {

    /**
     * Loads all trains from the underlying data source.
     * @return list of trains
     */
    List<Train> loadAll();

    /**
     * Saves all trains to thedata source.
     * @param trains trains to persist
     */
    void saveAll(List<Train> trains);
}
