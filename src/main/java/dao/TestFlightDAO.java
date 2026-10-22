
package dao;

import java.time.LocalDateTime;
import java.util.ArrayList;

import model.Flight;

public class TestFlightDAO {

    public static void main(String[] args) {

        FlightDAO flightDAO = new FlightDAO();

        // Create a sample flight
        Flight flight = new Flight(
                0,
                "AI101",
                "Air India",
                "Chennai",
                "Delhi",
                LocalDateTime.of(2026, 11, 15, 10, 30),
                LocalDateTime.of(2026, 11, 15, 13, 15),
                180,
                180
        );

        // Insert the flight into the database
        boolean result = flightDAO.addFlight(flight);

        if (result) {
            System.out.println("Flight added successfully!");
        } else {
            System.out.println("Flight could not be added.");
        }

        // Retrieve and display all flights
        ArrayList<Flight> flights = flightDAO.getAllFlights();

        System.out.println("\n========== FLIGHT LIST ==========");

        for (Flight currentFlight : flights) {
            System.out.println(currentFlight);
        }

        System.out.println("Total flights: " + flights.size());
        System.out.println("=================================");
    }
}