package dao;

import java.util.ArrayList;

import model.Passenger;

public class TestPassengerList {

    public static void main(String[] args) {

        PassengerDAO passengerDAO = new PassengerDAO();

        ArrayList<Passenger> passengers = passengerDAO.getAllPassengers();

        System.out.println("===== Passenger List =====");

        for (Passenger passenger : passengers) {
            System.out.println(passenger);
        }

        System.out.println("==========================");
        System.out.println("Total passengers: " + passengers.size());
    }
}