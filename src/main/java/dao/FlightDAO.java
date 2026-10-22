
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import model.Flight;
import util.DBConnection;

public class FlightDAO {

    // Add a new flight to the database
    public boolean addFlight(Flight flight) {

        String sql = "INSERT INTO flights " +
                "(flight_number, airline_name, source, destination, " +
                "departure_time, arrival_time, total_seats, available_seats) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, flight.getFlightNumber());
            statement.setString(2, flight.getAirlineName());
            statement.setString(3, flight.getSource());
            statement.setString(4, flight.getDestination());

            statement.setTimestamp(
                    5, Timestamp.valueOf(flight.getDepartureTime()));

            statement.setTimestamp(
                    6, Timestamp.valueOf(flight.getArrivalTime()));

            statement.setInt(7, flight.getTotalSeats());
            statement.setInt(8, flight.getAvailableSeats());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding flight: " + e.getMessage());
            return false;
        }
    }

    // Retrieve all flights from the database
    public ArrayList<Flight> getAllFlights() {

        ArrayList<Flight> flights = new ArrayList<>();

        String sql = "SELECT * FROM flights ORDER BY departure_time";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Flight flight = mapFlight(resultSet);
                flights.add(flight);
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error retrieving flights: " + e.getMessage());
        }

        return flights;
    }

    // Search flights by source and destination
    public ArrayList<Flight> searchFlights(
            String source, String destination) {

        ArrayList<Flight> flights = new ArrayList<>();

        String sql = "SELECT * FROM flights " +
                "WHERE LOWER(source) = LOWER(?) " +
                "AND LOWER(destination) = LOWER(?) " +
                "ORDER BY departure_time";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, source.trim());
            statement.setString(2, destination.trim());

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    Flight flight = mapFlight(resultSet);
                    flights.add(flight);
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error searching flights: " + e.getMessage());
        }

        return flights;
    }

    // Convert a database row into a Flight object
    private Flight mapFlight(ResultSet resultSet) throws SQLException {

        return new Flight(
                resultSet.getInt("flight_id"),
                resultSet.getString("flight_number"),
                resultSet.getString("airline_name"),
                resultSet.getString("source"),
                resultSet.getString("destination"),
                resultSet.getTimestamp("departure_time")
                        .toLocalDateTime(),
                resultSet.getTimestamp("arrival_time")
                        .toLocalDateTime(),
                resultSet.getInt("total_seats"),
                resultSet.getInt("available_seats")
        );
    }
}