package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Passenger;
import util.DBConnection;

public class PassengerDAO {

    // Add a new passenger to the database
    public boolean addPassenger(Passenger passenger) {

        String sql = "INSERT INTO passengers " +
                     "(name, email, phone, passport_number) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, passenger.getName());
            statement.setString(2, passenger.getEmail());
            statement.setString(3, passenger.getPhone());
            statement.setString(4, passenger.getPassportNumber());

            int rowsInserted = statement.executeUpdate();

            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Error adding passenger: " + e.getMessage());
            return false;
        }
    }

    // Retrieve all passengers from the database
    public ArrayList<Passenger> getAllPassengers() {

        ArrayList<Passenger> passengers = new ArrayList<>();

        String sql = "SELECT * FROM passengers";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Passenger passenger = new Passenger(
                        resultSet.getInt("passenger_id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getString("passport_number")
                );

                passengers.add(passenger);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving passengers: " + e.getMessage());
        }

        return passengers;
    }
}