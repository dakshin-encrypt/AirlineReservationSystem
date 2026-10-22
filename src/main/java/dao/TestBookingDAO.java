
package dao;

import model.Booking;

public class TestBookingDAO {

    public static void main(String[] args) {

        BookingDAO bookingDAO = new BookingDAO();

        // Existing sample data:
        // Passenger ID: 1
        // Flight ID: 1
        // Seat ID: 1

        Booking booking = new Booking(1, 1, 1);

        boolean success = bookingDAO.bookTicket(booking);

        if (success) {
            System.out.println(
                    "Test passed. Booking ID: "
                            + booking.getBookingId());
        } else {
            System.out.println(
                    "Booking was not completed. Check the message above.");
        }
    }
}
