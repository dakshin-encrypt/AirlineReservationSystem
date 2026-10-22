
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Seat;
import util.DBConnection;

public class SeatDAO {

    // View all seats for a flight, sorted by seat number
    public List<Seat> getSeatsByFlightId(int flightId)
            throws SQLException {

        List<Seat> seats = new ArrayList<>();

        String sql = "SELECT * FROM seats WHERE flight_id = ? " +
                "ORDER BY CAST(seat_number AS UNSIGNED), " +
                "SUBSTRING(seat_number, " +
                "LENGTH(CAST(seat_number AS UNSIGNED)) + 1)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, flightId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapSeat(rs));
                }
            }
        }

        return seats;
    }

    // Compatibility method for existing project code
    public List<Seat> getSeatsByFlight(int flightId)
            throws SQLException {
        return getSeatsByFlightId(flightId);
    }

    // View available seats for a flight
    public List<Seat> getAvailableSeats(int flightId)
            throws SQLException {

        List<Seat> seats = new ArrayList<>();

        String sql = "SELECT * FROM seats " +
                "WHERE flight_id = ? AND is_booked = FALSE " +
                "ORDER BY CAST(seat_number AS UNSIGNED), " +
                "SUBSTRING(seat_number, " +
                "LENGTH(CAST(seat_number AS UNSIGNED)) + 1)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, flightId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapSeat(rs));
                }
            }
        }

        return seats;
    }

    // Find a seat using its ID
    public Seat getSeatById(int seatId) throws SQLException {

        String sql = "SELECT * FROM seats WHERE seat_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, seatId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSeat(rs);
                }
            }
        }

        return null;
    }

    // Add a seat to the database
    public boolean addSeat(Seat seat) throws SQLException {

        String sql = "INSERT INTO seats " +
                "(flight_id, seat_number, seat_class, is_booked) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, seat.getFlightId());
            ps.setString(2, seat.getSeatNumber());
            ps.setString(3, seat.getSeatClass());
            ps.setBoolean(4, seat.isBooked());

            return ps.executeUpdate() > 0;
        }
    }

    // Map a database row to a Seat object
    private Seat mapSeat(ResultSet rs) throws SQLException {

        Seat seat = new Seat();

        seat.setSeatId(rs.getInt("seat_id"));
        seat.setFlightId(rs.getInt("flight_id"));
        seat.setSeatNumber(rs.getString("seat_number"));
        seat.setSeatClass(rs.getString("seat_class"));
        seat.setBooked(rs.getBoolean("is_booked"));

        return seat;
    }
}
