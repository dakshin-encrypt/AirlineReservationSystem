package dao;

import model.Passenger;

public class TestPassengerDAO {

    public static void main(String[] args) {

        Passenger passenger = new Passenger(
                0,
                "Rahul Kumar",
                "rahul@example.com",
                "9876543210",
                "P1234567"
        );

        PassengerDAO passengerDAO = new PassengerDAO();

        boolean result = passengerDAO.addPassenger(passenger);

        if (result) {
            System.out.println("Passenger added successfully!");
        } else {
            System.out.println("Failed to add passenger.");
        }
    }
}