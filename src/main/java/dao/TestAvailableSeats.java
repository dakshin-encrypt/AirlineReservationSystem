package dao;

import java.util.List;

import model.Seat;

public class TestAvailableSeats {

    public static void main(String[] args) {

        SeatDAO seatDAO = new SeatDAO();

        try {
            List<Seat> availableSeats =
                    seatDAO.getAvailableSeats(1);

            System.out.println("\n======= AVAILABLE SEATS =======");

            if (availableSeats.isEmpty()) {
                System.out.println("No available seats found.");
            } else {
                for (Seat seat : availableSeats) {
                    System.out.println(seat);
                }
            }

            System.out.println(
                    "Total available seats: " + availableSeats.size());

            System.out.println("===============================");

        } catch (Exception e) {
            System.err.println(
                    "Error retrieving available seats: " + e.getMessage());
        }
    }
}