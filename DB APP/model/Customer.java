package model;

public class Customer {
    private int customerID;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private double balance;

    public Customer() {}

    public Customer(int customerID, String firstName, String lastName, String email,
                    String phoneNumber, double balance) {
        this.customerID = customerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
    }

    public int getCustomerID() {
        return customerID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("model.Customer[ID=%d, Name=%s %s, Email=%s, Phone=%s, Balance=%.2f]",
                customerID,
                firstName != null ? firstName : "N/A",
                lastName != null ? lastName : "",
                email != null ? email : "N/A",
                phoneNumber != null ? phoneNumber : "N/A",
                balance);
    }
}