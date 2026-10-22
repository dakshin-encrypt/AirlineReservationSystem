
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Booking;
import util.DBConnection;

public class BookingDAO {

    // Book a ticket
    public boolean bookTicket(Booking booking) {

        String checkPassengerSql =
                "SELECT passenger_id FROM passengers WHERE passenger_id = ?";

        String checkSeatSql =
                "SELECT is_booked FROM seats " +
                "WHERE seat_id = ? AND flight_id = ? FOR UPDATE";

        String updateFlightSql =
                "UPDATE flights SET available_seats = available_seats - 1 " +
                "WHERE flight_id = ? AND available_seats > 0";

        String updateSeatSql =
                "UPDATE seats SET is_booked = TRUE " +
                "WHERE seat_id = ? AND flight_id = ? AND is_booked = FALSE";

        String insertBookingSql =
                "INSERT INTO bookings " +
                "(passenger_id, flight_id, seat_id, booking_status) " +
                "VALUES (?, ?, ?, 'CONFIRMED')";

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                // Check passenger exists
                try (PreparedStatement ps =
                             connection.prepareStatement(checkPassengerSql)) {

                    ps.setInt(1, booking.getPassengerId());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            connection.rollback();
                            System.out.println("Passenger not found.");
                            return false;
                        }
                    }
                }

                // Lock and check the selected seat
                try (PreparedStatement ps =
                             connection.prepareStatement(checkSeatSql)) {

                    ps.setInt(1, booking.getSeatId());
                    ps.setInt(2, booking.getFlightId());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            connection.rollback();
                            System.out.println(
                                    "Seat not found for this flight.");
                            return false;
                        }

                        if (rs.getBoolean("is_booked")) {
                            connection.rollback();
                            System.out.println("Seat is already booked.");
                            return false;
                        }
                    }
                }

                // Reduce flight availability
                try (PreparedStatement ps =
                             connection.prepareStatement(updateFlightSql)) {

                    ps.setInt(1, booking.getFlightId());

                    if (ps.executeUpdate() != 1) {
                        connection.rollback();
                        System.out.println(
                                "No available seats on this flight.");
                        return false;
                    }
                }

                // Mark seat as booked
                try (PreparedStatement ps =
                             connection.prepareStatement(updateSeatSql)) {

                    ps.setInt(1, booking.getSeatId());
                    ps.setInt(2, booking.getFlightId());

                    if (ps.executeUpdate() != 1) {
                        connection.rollback();
                        System.out.println(
                                "Unable to reserve the selected seat.");
                        return false;
                    }
                }

                // Insert booking record
                try (PreparedStatement ps =
                             connection.prepareStatement(
                                     insertBookingSql,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, booking.getPassengerId());
                    ps.setInt(2, booking.getFlightId());
                    ps.setInt(3, booking.getSeatId());

                    ps.executeUpdate();

                    // Retrieve generated booking ID
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            booking.setBookingId(keys.getInt(1));
                        } else {
                            throw new SQLException(
                                    "Could not retrieve generated booking ID.");
                        }
                    }
                }

                // Retrieve the database-generated booking date
                String dateSql =
                        "SELECT booking_date FROM bookings WHERE booking_id = ?";

                try (PreparedStatement ps =
                             connection.prepareStatement(dateSql)) {

                    ps.setInt(1, booking.getBookingId());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            java.sql.Timestamp timestamp =
                                    rs.getTimestamp("booking_date");

                            if (timestamp != null) {
                                booking.setBookingDate(
                                        timestamp.toLocalDateTime());
                            }
                        }
                    }
                }

                // Update booking status in the Java object
                booking.setBookingStatus("CONFIRMED");

                // Commit transaction
                connection.commit();

                System.out.println("Ticket booked successfully!");
                System.out.println(booking);

                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw e;

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println("Booking failed: " + e.getMessage());
            return false;
        }
    }

    // Retrieve all bookings
    public ArrayList<Booking> getAllBookings() {

        ArrayList<Booking> bookings = new ArrayList<>();

        String sql =
                "SELECT booking_id, passenger_id, flight_id, seat_id, " +
                "booking_date, booking_status FROM bookings " +
                "ORDER BY booking_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Booking booking = new Booking(
                        rs.getInt("passenger_id"),
                        rs.getInt("flight_id"),
                        rs.getInt("seat_id")
                );

                booking.setBookingId(rs.getInt("booking_id"));

                java.sql.Timestamp timestamp =
                        rs.getTimestamp("booking_date");

                if (timestamp != null) {
                    booking.setBookingDate(
                            timestamp.toLocalDateTime());
                }

                booking.setBookingStatus(
                        rs.getString("booking_status"));

                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.err.println(
                    "Could not retrieve bookings: " + e.getMessage());
        }

        return bookings;
    }

    // Cancel a confirmed booking
    public boolean cancelBooking(int bookingId) {

        String findBookingSql =
                "SELECT flight_id, seat_id FROM bookings " +
                "WHERE booking_id = ? AND booking_status = 'CONFIRMED' " +
                "FOR UPDATE";

        String cancelBookingSql =
                "UPDATE bookings SET booking_status = 'CANCELLED' " +
                "WHERE booking_id = ? AND booking_status = 'CONFIRMED'";

        String releaseSeatSql =
                "UPDATE seats SET is_booked = FALSE " +
                "WHERE seat_id = ? AND flight_id = ? AND is_booked = TRUE";

        String restoreFlightSql =
                "UPDATE flights SET available_seats = available_seats + 1 " +
                "WHERE flight_id = ? AND available_seats < total_seats";

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                int flightId;
                int seatId;

                // Lock and find the confirmed booking
                try (PreparedStatement ps =
                             connection.prepareStatement(findBookingSql)) {

                    ps.setInt(1, bookingId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            connection.rollback();
                            System.out.println(
                                    "Confirmed booking not found.");
                            return false;
                        }

                        flightId = rs.getInt("flight_id");
                        seatId = rs.getInt("seat_id");
                    }
                }

                // Change booking status
                try (PreparedStatement ps =
                             connection.prepareStatement(cancelBookingSql)) {

                    ps.setInt(1, bookingId);

                    if (ps.executeUpdate() != 1) {
                        connection.rollback();
                        System.out.println(
                                "Unable to cancel the booking.");
                        return false;
                    }
                }

                // Release the seat
                try (PreparedStatement ps =
                             connection.prepareStatement(releaseSeatSql)) {

                    ps.setInt(1, seatId);
                    ps.setInt(2, flightId);

                    if (ps.executeUpdate() != 1) {
                        connection.rollback();
                        System.out.println(
                                "Unable to release the seat.");
                        return false;
                    }
                }

                // Restore flight availability
                try (PreparedStatement ps =
                             connection.prepareStatement(restoreFlightSql)) {

                    ps.setInt(1, flightId);

                    if (ps.executeUpdate() != 1) {
                        connection.rollback();
                        System.out.println(
                                "Unable to restore flight availability.");
                        return false;
                    }
                }

                connection.commit();

                System.out.println(
                        "Booking " + bookingId +
                        " cancelled successfully!");

                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw e;

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.err.println(
                    "Cancellation failed: " + e.getMessage());
            return false;
        }
    }
}
