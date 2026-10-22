
package dao;

import service.BookingService;

public class TestBookingCancellation {

    public static void main(String[] args) {

        BookingService bookingService = new BookingService();

        // Cancel the existing test booking.
        int bookingId = 1;

        boolean success =
                bookingService.cancelBooking(bookingId);

        if (success) {
            System.out.println(
                    "Cancellation test passed for booking ID: "
                            + bookingId);
        } else {
            System.out.println(
                    "Cancellation test failed. Check the message above.");
        }
    }
}
