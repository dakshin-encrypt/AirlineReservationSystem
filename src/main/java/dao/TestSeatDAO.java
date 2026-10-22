
package dao;

import java.sql.SQLException;
import java.util.List;

import model.Seat;

public class TestSeatDAO {

    public static void main(String[] args) {
        try {
            SeatDAO seatDAO = new SeatDAO();

            Seat[] sampleSeats = {
                new Seat(0, 1, "1A", "BUSINESS", false),
                new Seat(0, 1, "1B", "BUSINESS", false),
                new Seat(0, 1, "2A", "ECONOMY", false),
                new Seat(0, 1, "2B", "ECONOMY", false)
            };

            for (Seat seat : sampleSeats) {
                boolean added = seatDAO.addSeat(seat);

                if (added) {
                    System.out.println("Added seat: " + seat.getSeatNumber());
                } else {
                    System.out.println("Could not add seat: " + seat.getSeatNumber());
                }
            }

            List<Seat> seats = seatDAO.getAvailableSeats(1);

            System.out.println("\n======= SEAT LIST =======");

            for (Seat seat : seats) {
                System.out.println(seat);
            }

            System.out.println("Total seats recorded: " + seats.size());
            System.out.println("=========================");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error testing seats: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
