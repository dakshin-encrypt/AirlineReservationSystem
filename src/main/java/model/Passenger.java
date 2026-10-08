package model;

public class Passenger {

    private int passengerId;
    private String name;
    private String email;
    private String phone;
    private String passportNumber;

    public Passenger() {
    }

    public Passenger(int passengerId, String name, String email,
                     String phone, String passportNumber) {
        this.passengerId = passengerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passportNumber = passportNumber;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "passengerId=" + passengerId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                '}';
    }
}