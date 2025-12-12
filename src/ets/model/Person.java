package ets.model;

/**
 * Abstract base class representing a generic Person.
 * Implements common attributes for inheritance by specific roles (e.g., Staff).
 */
public abstract class Person {

    private String name;
    private String contactNo;
    private String ic;
    private String id;

    /**
     * Default Constructor
     */
    public Person() {
    }

    /**
     * Parameterised constructor
     * @param name Person's full name
     * @param contactNo Person's contact number
     * @param ic Person's identification card number
     * @param id Person's unique identifier
     */
    public Person(String name, String contactNo, String ic, String id) {
        this.name = name;
        this.contactNo = contactNo;
        this.ic = ic;
        this.id = id;
    }

    // Getters and Setters
    /**
     * Gets the person's name
     * @return name in string format
     */
    public String getName() { return name; }

    /**
     * Sets the person's name
     * @param name new name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the person's contact number
     * @return contact number in string format
     */
    public String getContactNo() { return contactNo; }

    /**
     * Sets the person's contact number
     * @param contactNo new contact number to set
     */
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    /**
     * Gets the person's IC number
     * @return IC number in string format
     */
    public String getIc() { return ic; }

    /**
     * Sets the person's IC number
     * @param ic new IC number to set
     */
    public void setIc(String ic) { this.ic = ic; }

    /**
     * Gets the person's unique ID
     * @return unique ID in string format
     */
    public String getId() { return id; }

    /**
     * Sets the person's unique ID
     * @param id new unique ID to set
     */
    public void setId(String id) { this.id = id; }

    /**
     * Returns a string representation of the Person object
     * @return formatted string containing name, contact, ic and id
     */
    @Override
    public String toString() {
        return String.format("Name            : %s\nContact Number  : %s\nIC Number       : %s\nID              : %s",
                name, contactNo, ic, id);
    }
}