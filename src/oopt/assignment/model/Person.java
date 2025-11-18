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

    public String getName() {
        return name;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getIc() {
        return ic;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIc(String ic) {
        this.ic = ic;
    }

    @Override
    public String toString() {
        return "Name            : " + name
                + "\nContact Number  : " + contactNo
                + "\nIC Number       : " + ic
                + "\nID              : " + id;
    }
}