
import java.util.List;
import java.util.Scanner;

import dao.BookingDAO;
import dao.FlightDAO;
import dao.PassengerDAO;
import dao.SeatDAO;
import model.Booking;
import model.Flight;
import model.Passenger;
import model.Seat;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final PassengerDAO passengerDAO = new PassengerDAO();
    private static final FlightDAO flightDAO = new FlightDAO();
    private static final SeatDAO seatDAO = new SeatDAO();
    private static final BookingDAO bookingDAO = new BookingDAO();

    public static void main(String[] args) {

        boolean running = true;

        System.out.println("==========================================");
        System.out.println("       AIRLINE RESERVATION SYSTEM");
        System.out.println("==========================================");

        while (running) {
            displayMenu();

            System.out.print("Enter your choice: ");
            int choice = readInt();

            switch (choice) {
                case 1:
                    addPassenger();
                    break;
                case 2:
                    viewPassengers();
                    break;
                case 3:
                    viewFlights();
                    break;
                case 4:
                    searchFlights();
                    break;
                case 5:
                    viewAvailableSeats();
                    break;
                case 6:
                    bookTicket();
                    break;
                case 7:
                    viewBookings();
                    break;
                case 8:
                    cancelBooking();
                    break;
                case 0:
                    running = false;
                    System.out.println(
                            "\nThank you for using the Airline Reservation System!");
                    break;
                default:
                    System.out.println(
                            "Invalid choice. Please select a valid menu option.");
            }

            System.out.println();
        }

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("------------------------------------------");
        System.out.println("                 MAIN MENU");
        System.out.println("------------------------------------------");
        System.out.println("1. Add Passenger");
        System.out.println("2. View Passengers");
        System.out.println("3. View Flights");
        System.out.println("4. Search Flights");
        System.out.println("5. View Available Seats");
        System.out.println("6. Book Ticket");
        System.out.println("7. View Bookings");
        System.out.println("8. Cancel Booking");
        System.out.println("0. Exit");
        System.out.println("------------------------------------------");
    }

    private static int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Enter a whole number: ");
            scanner.next();
        }

        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println(
                    "Input cannot be empty. Please try again.");
        }
    }

    private static void addPassenger() {
        try {
            String name =
                    readNonEmptyString("Enter passenger name: ");
            String email =
                    readNonEmptyString("Enter passenger email: ");
            String phone =
                    readNonEmptyString("Enter passenger phone: ");
            String passportNumber =
                    readNonEmptyString("Enter passport number: ");

            Passenger passenger = new Passenger(
                    0, name, email, phone, passportNumber);

            passengerDAO.addPassenger(passenger);

            System.out.println("Passenger added successfully.");

        } catch (Exception e) {
            System.out.println(
                    "Error adding passenger: " + e.getMessage());
        }
    }

    private static void viewPassengers() {
        try {
            List<Passenger> passengers =
                    passengerDAO.getAllPassengers();

            System.out.println("\n========== PASSENGER LIST ==========");

            if (passengers.isEmpty()) {
                System.out.println("No passengers found.");
                return;
            }

            for (Passenger passenger : passengers) {
                System.out.println(passenger);
            }

            System.out.println(
                    "Total passengers: " + passengers.size());

        } catch (Exception e) {
            System.out.println(
                    "Error retrieving passengers: " + e.getMessage());
        }
    }

    private static void viewFlights() {
        try {
            List<Flight> flights = flightDAO.getAllFlights();

            System.out.println("\n============== FLIGHT LIST ==============");

            if (flights.isEmpty()) {
                System.out.println("No flights found.");
                return;
            }

            for (Flight flight : flights) {
                System.out.println(flight);
            }

            System.out.println("Total flights: " + flights.size());

        } catch (Exception e) {
            System.out.println(
                    "Error retrieving flights: " + e.getMessage());
        }
    }

    private static void searchFlights() {
        try {
            String source =
                    readNonEmptyString("Enter source city: ");
            String destination =
                    readNonEmptyString("Enter destination city: ");

            List<Flight> flights =
                    flightDAO.searchFlights(source, destination);

            System.out.println("\n========== SEARCH RESULTS ==========");

            if (flights.isEmpty()) {
                System.out.println("No matching flights found.");
                return;
            }

            for (Flight flight : flights) {
                System.out.println(flight);
            }

            System.out.println(
                    "Matching flights: " + flights.size());

        } catch (Exception e) {
            System.out.println(
                    "Error searching flights: " + e.getMessage());
        }
    }

    private static void viewAvailableSeats() {
        try {
            System.out.print("Enter flight ID: ");
            int flightId = readInt();

            if (flightId <= 0) {
                System.out.println("Invalid flight ID.");
                return;
            }

            List<Seat> seats =
                    seatDAO.getAvailableSeats(flightId);

            System.out.println("\n========== AVAILABLE SEATS ==========");

            if (seats.isEmpty()) {
                System.out.println(
                        "No available seats found for this flight.");
                return;
            }

            for (Seat seat : seats) {
                System.out.println(seat);
            }

            System.out.println(
                    "Total available seats: " + seats.size());

        } catch (Exception e) {
            System.out.println(
                    "Error retrieving seats: " + e.getMessage());
        }
    }

    private static void bookTicket() {
        try {
            System.out.print("Enter passenger ID: ");
            int passengerId = readInt();

            System.out.print("Enter flight ID: ");
            int flightId = readInt();

            System.out.print("Enter seat ID: ");
            int seatId = readInt();

            if (passengerId <= 0 || flightId <= 0 || seatId <= 0) {
                System.out.println(
                        "IDs must be positive numbers.");
                return;
            }

            Booking booking =
                    new Booking(passengerId, flightId, seatId);

            bookingDAO.bookTicket(booking);

            System.out.println("Booking request processed.");

        } catch (Exception e) {
            System.out.println(
                    "Error booking ticket: " + e.getMessage());
        }
    }

    private static void viewBookings() {
        try {
            List<Booking> bookings =
                    bookingDAO.getAllBookings();

            System.out.println(
                    "\n============== BOOKING HISTORY ==============");

            if (bookings.isEmpty()) {
                System.out.println("No bookings found.");
                return;
            }

            for (Booking booking : bookings) {
                System.out.println(booking);
            }

            System.out.println(
                    "Total bookings: " + bookings.size());

        } catch (Exception e) {
            System.out.println(
                    "Error retrieving bookings: " + e.getMessage());
        }
    }

    private static void cancelBooking() {
        try {
            System.out.print("Enter booking ID to cancel: ");
            int bookingId = readInt();

            if (bookingId <= 0) {
                System.out.println("Invalid booking ID.");
                return;
            }

            bookingDAO.cancelBooking(bookingId);

            System.out.println("Cancellation request processed.");

        } catch (Exception e) {
            System.out.println(
                    "Error cancelling booking: " + e.getMessage());
        }
    }
}
