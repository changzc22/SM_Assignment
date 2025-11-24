package oopt.assignment.model;

import java.util.Collection;
import java.util.LinkedHashMap;

public interface IStaffRepository {
    LinkedHashMap<String, Staff> getAll();
    void saveAll(Collection<Staff> staffList);
}
