
package dao;

import java.util.ArrayList;

import model.Flight;

public class TestFlightSearch {

    public static void main(String[] args) {

        FlightDAO flightDAO = new FlightDAO();

        String source = "Chennai";
        String destination = "Delhi";

        ArrayList<Flight> flights =
                flightDAO.searchFlights(source, destination);

        System.out.println("\n======= FLIGHT SEARCH RESULTS =======");
        System.out.println("From: " + source);
        System.out.println("To: " + destination);

        if (flights.isEmpty()) {
            System.out.println("No flights found for this route.");
        } else {
            for (Flight flight : flights) {
                System.out.println(flight);
            }

            System.out.println("Flights found: " + flights.size());
        }

        System.out.println("=====================================");
    }
}
