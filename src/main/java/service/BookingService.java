
package service;

import dao.BookingDAO;

public class BookingService {

    private final BookingDAO bookingDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
    }

    public boolean cancelBooking(int bookingId) {
        if (bookingId <= 0) {
            System.out.println("Invalid booking ID.");
            return false;
        }

        return bookingDAO.cancelBooking(bookingId);
    }
}
