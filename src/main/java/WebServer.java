
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import dao.BookingDAO;
import dao.FlightDAO;
import dao.PassengerDAO;
import dao.SeatDAO;
import model.Booking;
import model.Flight;
import model.Passenger;
import model.Seat;

public class WebServer {

    private static final int PORT = 8080;

    private static final FlightDAO flightDAO = new FlightDAO();
    private static final PassengerDAO passengerDAO = new PassengerDAO();
    private static final SeatDAO seatDAO = new SeatDAO();
    private static final BookingDAO bookingDAO = new BookingDAO();

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", PORT), 0);

        server.createContext("/api/flights/search",
                WebServer::handleFlightSearch);

        server.createContext("/api/flights",
                WebServer::handleFlights);

        server.createContext("/api/passengers",
                WebServer::handlePassengerRegistration);

        server.createContext("/api/seats",
                WebServer::handleSeats);

        server.createContext("/api/bookings/create",
                WebServer::handleCreateBooking);

        server.createContext("/api/bookings",
                WebServer::handleBookings);

        server.createContext("/",
                WebServer::handleStaticFiles);

        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();

        System.out.println("Airline Reservation web server started.");
        System.out.println("Open http://localhost:" + PORT);
        System.out.println("Press Ctrl+C to stop the server.");
    }

    // Retrieve all flights.
    private static void handleFlights(HttpExchange exchange)
            throws IOException {

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "GET");
            sendJson(exchange, 405,
                    "{\"error\":\"Only GET is supported.\"}");
            return;
        }

        try {
            sendJson(exchange, 200,
                    flightsToJson(flightDAO.getAllFlights()));
        } catch (RuntimeException e) {
            System.err.println("Could not load flights: " + e.getMessage());
            sendJson(exchange, 500,
                    "{\"error\":\"Could not load flights.\"}");
        }
    }

    // Search flights by source and destination.
    private static void handleFlightSearch(HttpExchange exchange)
            throws IOException {

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "GET");
            sendJson(exchange, 405,
                    "{\"error\":\"Only GET is supported.\"}");
            return;
        }

        Map<String, String> params =
                parseQuery(exchange.getRequestURI().getRawQuery());

        String source = params.getOrDefault("source", "").trim();
        String destination =
                params.getOrDefault("destination", "").trim();

        if (source.isEmpty() || destination.isEmpty()) {
            sendJson(exchange, 400,
                    "{\"error\":\"Source and destination are required.\"}");
            return;
        }

        try {
            List<Flight> flights =
                    flightDAO.searchFlights(source, destination);

            sendJson(exchange, 200, flightsToJson(flights));
        } catch (RuntimeException e) {
            System.err.println("Flight search failed: " + e.getMessage());
            sendJson(exchange, 500,
                    "{\"error\":\"Flight search failed.\"}");
        }
    }

    // Register a passenger in the database.
    private static void handlePassengerRegistration(
            HttpExchange exchange) throws IOException {

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "POST");
            sendJson(exchange, 405,
                    "{\"error\":\"Only POST is supported.\"}");
            return;
        }

        String contentType = exchange.getRequestHeaders()
                .getFirst("Content-Type");

        if (contentType == null
                || !contentType.toLowerCase()
                        .startsWith("application/x-www-form-urlencoded")) {
            sendJson(exchange, 415,
                    "{\"error\":\"Unsupported content type.\"}");
            return;
        }

        byte[] body = exchange.getRequestBody().readNBytes(16385);

        if (body.length > 16384) {
            sendJson(exchange, 413,
                    "{\"error\":\"Request is too large.\"}");
            return;
        }

        Map<String, String> params = parseQuery(
                new String(body, StandardCharsets.UTF_8));

        String name = params.getOrDefault("name", "").trim();
        String email = params.getOrDefault("email", "").trim();
        String phone = params.getOrDefault("phone", "").trim();
        String passportNumber =
                params.getOrDefault("passportNumber", "").trim();

        if (name.isEmpty() || email.isEmpty()
                || phone.isEmpty() || passportNumber.isEmpty()) {
            sendJson(exchange, 400,
                    "{\"error\":\"All passenger fields are required.\"}");
            return;
        }

        if (name.length() > 100 || email.length() > 150
                || phone.length() > 20
                || passportNumber.length() > 30) {
            sendJson(exchange, 400,
                    "{\"error\":\"One or more fields are too long.\"}");
            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            sendJson(exchange, 400,
                    "{\"error\":\"Enter a valid email address.\"}");
            return;
        }

        try {
            Passenger passenger = new Passenger(
                    0, name, email, phone, passportNumber);

            boolean saved = passengerDAO.addPassenger(passenger);

            if (saved) {
                sendJson(exchange, 201,
                        "{\"message\":\"Passenger registered successfully.\"}");
            } else {
                sendJson(exchange, 409,
                        "{\"error\":\"Registration failed. The email or "
                        + "passport may already exist.\"}");
            }

        } catch (RuntimeException e) {
            System.err.println(
                    "Passenger registration failed: " + e.getMessage());

            sendJson(exchange, 500,
                    "{\"error\":\"An internal server error occurred.\"}");
        }
    }

    // Retrieve all seats for a flight, including booked seats.
    private static void handleSeats(HttpExchange exchange)
            throws IOException {

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "GET");
            sendJson(exchange, 405,
                    "{\"error\":\"Only GET is supported.\"}");
            return;
        }

        Map<String, String> params =
                parseQuery(exchange.getRequestURI().getRawQuery());

        int flightId;

        try {
            flightId = Integer.parseInt(
                    params.getOrDefault("flightId", ""));
        } catch (NumberFormatException e) {
            sendJson(exchange, 400,
                    "{\"error\":\"A valid flightId is required.\"}");
            return;
        }

        if (flightId <= 0) {
            sendJson(exchange, 400,
                    "{\"error\":\"Flight ID must be positive.\"}");
            return;
        }

        try {
            List<Seat> seats = seatDAO.getSeatsByFlightId(flightId);
            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < seats.size(); i++) {
                Seat seat = seats.get(i);

                if (i > 0) {
                    json.append(",");
                }

                json.append("{")
                    .append("\"seatId\":").append(seat.getSeatId()).append(",")
                    .append("\"flightId\":").append(seat.getFlightId()).append(",")
                    .append("\"seatNumber\":").append(quote(seat.getSeatNumber())).append(",")
                    .append("\"seatClass\":").append(quote(seat.getSeatClass())).append(",")
                    .append("\"booked\":").append(seat.isBooked())
                    .append("}");
            }

            json.append("]");
            sendJson(exchange, 200, json.toString());

        } catch (SQLException e) {
            System.err.println("Could not load seats: " + e.getMessage());
            sendJson(exchange, 500,
                    "{\"error\":\"Could not load seats.\"}");
        }
    }

    // Create a ticket booking.
    private static void handleCreateBooking(HttpExchange exchange)
            throws IOException {

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "POST");
            sendJson(exchange, 405,
                    "{\"error\":\"Only POST is supported.\"}");
            return;
        }

        String contentType = exchange.getRequestHeaders()
                .getFirst("Content-Type");

        if (contentType == null
                || !contentType.toLowerCase()
                        .startsWith("application/x-www-form-urlencoded")) {
            sendJson(exchange, 415,
                    "{\"error\":\"Unsupported content type.\"}");
            return;
        }

        byte[] body = exchange.getRequestBody().readNBytes(16385);

        if (body.length > 16384) {
            sendJson(exchange, 413,
                    "{\"error\":\"Request is too large.\"}");
            return;
        }

        Map<String, String> params = parseQuery(
                new String(body, StandardCharsets.UTF_8));

        int passengerId;
        int flightId;
        int seatId;

        try {
            passengerId = Integer.parseInt(
                    params.getOrDefault("passengerId", ""));
            flightId = Integer.parseInt(
                    params.getOrDefault("flightId", ""));
            seatId = Integer.parseInt(
                    params.getOrDefault("seatId", ""));
        } catch (NumberFormatException e) {
            sendJson(exchange, 400,
                    "{\"error\":\"Valid passengerId, flightId, "
                    + "and seatId are required.\"}");
            return;
        }

        if (passengerId <= 0 || flightId <= 0 || seatId <= 0) {
            sendJson(exchange, 400,
                    "{\"error\":\"IDs must be positive.\"}");
            return;
        }

        try {
            Booking booking = new Booking(passengerId, flightId, seatId);
            boolean saved = bookingDAO.bookTicket(booking);

            if (!saved) {
                sendJson(exchange, 409,
                        "{\"error\":\"Booking failed. Verify the passenger, "
                        + "flight, and seat are valid and the seat is available.\"}");
                return;
            }

            String json = "{"
                    + "\"message\":\"Ticket booked successfully.\","
                    + "\"bookingId\":" + booking.getBookingId() + ","
                    + "\"passengerId\":" + booking.getPassengerId() + ","
                    + "\"flightId\":" + booking.getFlightId() + ","
                    + "\"seatId\":" + booking.getSeatId() + ","
                    + "\"bookingStatus\":" + quote(booking.getBookingStatus())
                    + "}";

            sendJson(exchange, 201, json);

        } catch (RuntimeException e) {
            System.err.println("Booking request failed: " + e.getMessage());
            sendJson(exchange, 500,
                    "{\"error\":\"An internal server error occurred.\"}");
        }
    }

    // List bookings and cancel a booking by ID.
    // GET    /api/bookings
    // DELETE /api/bookings/{bookingId}
    private static void handleBookings(HttpExchange exchange)
            throws IOException {

        String method = exchange.getRequestMethod();

        if ("DELETE".equalsIgnoreCase(method)) {
            String path = exchange.getRequestURI().getPath();
            String prefix = "/api/bookings/";

            if (!path.startsWith(prefix)) {
                sendJson(exchange, 400,
                        "{\"error\":\"Booking ID is required.\"}");
                return;
            }

            String idText = path.substring(prefix.length()).trim();

            if (idText.isEmpty() || idText.contains("/")) {
                sendJson(exchange, 400,
                        "{\"error\":\"A valid booking ID is required.\"}");
                return;
            }

            int bookingId;

            try {
                bookingId = Integer.parseInt(idText);
            } catch (NumberFormatException e) {
                sendJson(exchange, 400,
                        "{\"error\":\"A valid booking ID is required.\"}");
                return;
            }

            if (bookingId <= 0) {
                sendJson(exchange, 400,
                        "{\"error\":\"Booking ID must be positive.\"}");
                return;
            }

            try {
                boolean cancelled = bookingDAO.cancelBooking(bookingId);

                if (cancelled) {
                    sendJson(exchange, 200,
                            "{\"message\":\"Booking cancelled successfully.\"}");
                } else {
                    sendJson(exchange, 409,
                            "{\"error\":\"Cancellation failed. Verify that "
                            + "the booking exists and is eligible for cancellation.\"}");
                }
            } catch (RuntimeException e) {
                System.err.println(
                        "Booking cancellation failed: " + e.getMessage());
                sendJson(exchange, 500,
                        "{\"error\":\"An internal server error occurred.\"}");
            }

            return;
        }

        if (!"GET".equalsIgnoreCase(method)) {
            exchange.getResponseHeaders().set("Allow", "GET, DELETE");
            sendJson(exchange, 405,
                    "{\"error\":\"Only GET and DELETE are supported.\"}");
            return;
        }

        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < bookings.size(); i++) {
                Booking booking = bookings.get(i);

                if (i > 0) {
                    json.append(",");
                }

                json.append("{")
                    .append("\"bookingId\":").append(booking.getBookingId()).append(",")
                    .append("\"passengerId\":").append(booking.getPassengerId()).append(",")
                    .append("\"flightId\":").append(booking.getFlightId()).append(",")
                    .append("\"seatId\":").append(booking.getSeatId()).append(",")
                    .append("\"bookingDate\":")
                    .append(quote(String.valueOf(booking.getBookingDate()))).append(",")
                    .append("\"bookingStatus\":")
                    .append(quote(booking.getBookingStatus()))
                    .append("}");
            }

            json.append("]");
            sendJson(exchange, 200, json.toString());

        } catch (RuntimeException e) {
            System.err.println("Could not load bookings: " + e.getMessage());
            sendJson(exchange, 500,
                    "{\"error\":\"Could not load bookings.\"}");
        }
    }

    // Parse URL query parameters or form data.
    private static Map<String, String> parseQuery(String query) {

        Map<String, String> params = new HashMap<>();

        if (query == null || query.isBlank()) {
            return params;
        }

        for (String part : query.split("&")) {
            String[] pair = part.split("=", 2);

            String key = URLDecoder.decode(
                    pair[0], StandardCharsets.UTF_8);

            String value = pair.length > 1
                    ? URLDecoder.decode(
                            pair[1], StandardCharsets.UTF_8)
                    : "";

            params.put(key, value);
        }

        return params;
    }

    // Convert flight objects to JSON.
    private static String flightsToJson(List<Flight> flights) {

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        for (Flight flight : flights) {
            if (!first) {
                json.append(",");
            }

            first = false;

            json.append("{")
                .append("\"flightId\":")
                .append(flight.getFlightId()).append(",")

                .append("\"flightNumber\":")
                .append(quote(flight.getFlightNumber())).append(",")

                .append("\"airlineName\":")
                .append(quote(flight.getAirlineName())).append(",")

                .append("\"source\":")
                .append(quote(flight.getSource())).append(",")

                .append("\"destination\":")
                .append(quote(flight.getDestination())).append(",")

                .append("\"departureTime\":")
                .append(quote(String.valueOf(
                        flight.getDepartureTime()))).append(",")

                .append("\"arrivalTime\":")
                .append(quote(String.valueOf(
                        flight.getArrivalTime()))).append(",")

                .append("\"totalSeats\":")
                .append(flight.getTotalSeats()).append(",")

                .append("\"availableSeats\":")
                .append(flight.getAvailableSeats())

                .append("}");
        }

        return json.append("]").toString();
    }

    // Escape text safely when creating JSON strings.
    private static String quote(String value) {

        if (value == null) {
            return "null";
        }

        StringBuilder result = new StringBuilder("\"");

        for (char c : value.toCharArray()) {
            switch (c) {
                case '"':
                    result.append("\\\"");
                    break;

                case '\\':
                    result.append("\\\\");
                    break;

                case '\n':
                    result.append("\\n");
                    break;

                case '\r':
                    result.append("\\r");
                    break;

                case '\t':
                    result.append("\\t");
                    break;

                default:
                    if (c < 0x20) {
                        result.append(
                                String.format("\\u%04x", (int) c));
                    } else {
                        result.append(c);
                    }
            }
        }

        return result.append("\"").toString();
    }

    // Send JSON responses to the browser.
    private static void sendJson(
            HttpExchange exchange, int status, String json)
            throws IOException {

        byte[] body = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");

        exchange.getResponseHeaders().set(
                "Cache-Control", "no-store");

        exchange.getResponseHeaders().set(
                "X-Content-Type-Options", "nosniff");

        exchange.sendResponseHeaders(status, body.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(body);
        }
    }

    // Serve website files from src/main/resources/public.
    private static void handleStaticFiles(HttpExchange exchange)
            throws IOException {

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "GET");
            sendJson(exchange, 405,
                    "{\"error\":\"Only GET is supported.\"}");
            return;
        }

        String path = exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            path = "/index.html";
        }

        // Prevent path traversal outside the public folder.
        if (path.contains("..") || path.contains("\\")
                || path.contains("%")) {
            sendJson(exchange, 400,
                    "{\"error\":\"Invalid path.\"}");
            return;
        }

        String resource = "/public" + path;

        try (InputStream input =
                WebServer.class.getResourceAsStream(resource)) {

            if (input == null) {
                sendJson(exchange, 404,
                        "{\"error\":\"Page not found. "
                        + "Check src/main/resources/public.\"}");
                return;
            }

            byte[] body = input.readAllBytes();

            exchange.getResponseHeaders().set(
                    "Content-Type", contentType(path));

            exchange.getResponseHeaders().set(
                    "X-Content-Type-Options", "nosniff");

            exchange.sendResponseHeaders(200, body.length);

            try (OutputStream output = exchange.getResponseBody()) {
                output.write(body);
            }
        }
    }

    // Determine the content type of a static file.
    private static String contentType(String path) {

        String lower = path.toLowerCase();

        if (lower.endsWith(".html")) {
            return "text/html; charset=UTF-8";
        }

        if (lower.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }

        if (lower.endsWith(".js")) {
            return "text/javascript; charset=UTF-8";
        }

        if (lower.endsWith(".json")) {
            return "application/json; charset=UTF-8";
        }

        if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        }

        if (lower.endsWith(".png")) {
            return "image/png";
        }

        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }

        return "application/octet-stream";
    }
}
