
package model;

import java.time.LocalDateTime;

public class Booking {

    private int bookingId;
    private int passengerId;
    private int flightId;
    private int seatId;
    private LocalDateTime bookingDate;
    private String bookingStatus;

    public Booking() {
    }

    public Booking(int passengerId, int flightId, int seatId) {
        this.passengerId = passengerId;
        this.flightId = flightId;
        this.seatId = seatId;
        this.bookingStatus = "CONFIRMED";
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    @Override
    public String toString() {
        return "Booking ID: " + bookingId
                + ", Passenger ID: " + passengerId
                + ", Flight ID: " + flightId
                + ", Seat ID: " + seatId
                + ", Booking Date: " + bookingDate
                + ", Status: " + bookingStatus;
    }
}
