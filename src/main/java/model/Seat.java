
package model;

public class Seat {

    private int seatId;
    private int flightId;
    private String seatNumber;
    private String seatClass;
    private boolean booked;

    public Seat() {
    }

    public Seat(int seatId, int flightId, String seatNumber,
                String seatClass, boolean booked) {
        this.seatId = seatId;
        this.flightId = flightId;
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.booked = booked;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatId=" + seatId +
                ", flightId=" + flightId +
                ", seatNumber='" + seatNumber + '\'' +
                ", seatClass='" + seatClass + '\'' +
                ", booked=" + booked +
                '}';
    }
}
