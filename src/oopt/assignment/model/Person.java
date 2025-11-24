package oopt.assignment.model;

public abstract class Person {

    private String name;
    private String contactNo;
    private String ic;
    private String id;

    public Person() {
    }

    public Person(String name, String contactNo, String ic, String id) {
        this.name = name;
        this.contactNo = contactNo;
        this.ic = ic;
        this.id = id;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getIc() { return ic; }
    public void setIc(String ic) { this.ic = ic; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @Override
    public String toString() {
        return String.format("Name            : %s\nContact Number  : %s\nIC Number       : %s\nID              : %s",
                name, contactNo, ic, id);
    }
}