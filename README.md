# SkyBook — Airline Reservation System

A Java-based Airline Reservation System built using Object-Oriented Programming (OOP), the Java Collections Framework, JDBC, and MySQL. The project provides a console-based application and a browser-based web interface for managing passengers, searching flights, checking seat availability, booking tickets, viewing bookings, and cancelling reservations.

## Features

### Flight Management
- View available flight records.
- Search flights by source and destination.
- Display flight details, including airline, departure, arrival, and seat availability.

### Passenger Management
- Register passengers with their name, email, phone number, and passport number.
- Validate required passenger information.
- Store passenger records in MySQL.

### Seat Management
- View seats for a selected flight.
- Display seat numbers, classes, and booking status.
- Prevent booking attempts when a seat is unavailable through the booking workflow.

### Booking Management
- Book a seat for a registered passenger.
- View booking history and booking status.
- Cancel eligible bookings.
- Update seat and flight availability during booking and cancellation.

### Web Interface
- Browse the application using a web browser.
- Search for flights.
- Register passengers.
- Select flights and seats for booking.
- View booking history and cancel bookings.

### Database Integration
- Use JDBC to connect Java components to MySQL.
- Store passengers, flights, seats, and bookings in a relational database.
- Read database credentials from environment variables instead of hardcoding passwords in the source code.

## Technologies Used

- Java 26
- Object-Oriented Programming (OOP)
- Java Collections Framework
- JDBC
- MySQL 8.4
- MySQL Connector/J 9.5.0
- Apache Maven 3.10.0
- Git and GitHub
- Visual Studio Code
- Java's built-in HTTP server for the web interface
- HTML, CSS, and JavaScript

## Prerequisites

Install the following software before running the project:

- JDK 26
- Apache Maven
- MySQL Server
- MySQL Workbench (optional)

Verify the Java and Maven installations:

```powershell
java --version
javac --version
mvn --version
```

## Database Setup

1. Start the MySQL server.
2. Open MySQL Workbench or another MySQL client.
3. Open `sql/database.sql`.
4. Review the SQL script and execute it.
5. Confirm that the `airline_reservation` database and its required tables exist.

The schema script establishes the database structure. It may not populate sample passengers, flights, or seats. Add suitable test data before testing workflows that require existing records.

**Important:** Review the SQL script before executing it, especially if your database already contains project data.

## Database Configuration

SkyBook reads the database username and password from environment variables.

Open PowerShell in the project root and configure the variables for the current terminal session:

```powershell
$env:AIRLINE_DB_USER = "root"
$securePassword = Read-Host "Enter your MySQL password" -AsSecureString
$env:AIRLINE_DB_PASSWORD = [System.Net.NetworkCredential]::new("", $securePassword).Password
```

Replace `root` with your MySQL username if necessary. Enter your password only at the prompt; do not write it into Java source files or commit it to GitHub.

These environment variables apply to the current PowerShell session. If you open a new terminal, configure them again.

## Build the Project

From the project root directory, run:

```powershell
mvn clean compile
```

The command compiles the Java source files and copies the application resources into the build output directory.

## Run the Console Application

Start the menu-driven application using:

```powershell
mvn exec:java "-Dexec.mainClass=Main"
```

The console menu provides the following operations:

| Option | Operation |
|---|---|
| 1 | Add Passenger |
| 2 | View Passengers |
| 3 | View Flights |
| 4 | Search Flights |
| 5 | View Available Seats |
| 6 | Book Ticket |
| 7 | View Bookings |
| 8 | Cancel Booking |
| 0 | Exit |

Follow the prompts displayed in the terminal.

## Run the SkyBook Web Application

First, configure the database environment variables in the current PowerShell session and ensure that MySQL is running.

Start the web server with:

```powershell
mvn exec:java "-Dexec.mainClass=WebServer" "-Dexec.classpathScope=runtime"
```

When the server starts successfully, open the following address in your browser:

**http://localhost:8080**

The web server binds to `127.0.0.1`, making the application available locally on your machine.

Keep the terminal open while using the website. Press `Ctrl+C` in the server terminal to stop it.

## API Endpoints

The web interface communicates with the Java server through HTTP endpoints.

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/flights` | Retrieve flights |
| GET | `/api/flights/search?source=Chennai&destination=Delhi` | Search flights |
| POST | `/api/passengers` | Register a passenger |
| GET | `/api/seats?flightId=1` | Retrieve seats for a flight |
| POST | `/api/bookings/create` | Create a booking |
| GET | `/api/bookings` | Retrieve booking history |
| DELETE | `/api/bookings/{bookingId}` | Cancel a booking |

The POST endpoints accept URL-encoded form data. The booking endpoint requires valid passenger, flight, and seat IDs.

## Project Structure

```text
AirlineReservationSystem/
├── src/
│   └── main/
│       ├── java/
│       │   ├── Main.java
│       │   ├── WebServer.java
│       │   ├── model/
│       │   ├── dao/
│       │   ├── service/
│       │   └── util/
│       └── resources/
│           └── public/
│               └── index.html
├── sql/
│   └── database.sql
├── pom.xml
├── .gitignore
└── README.md
```

The main packages are organized by responsibility:

- `model` — represents application entities.
- `dao` — handles database access.
- `service` — contains business operations.
- `util` — provides utility functionality, including database connectivity.
- `WebServer.java` — handles HTTP requests and serves the web interface.
- `src/main/resources/public/index.html` — contains the browser-based user interface.
- `sql/database.sql` — contains the database schema.

## Testing and Validation

The project has successfully passed a Maven clean compilation. The web interface has also been exercised with flight retrieval, booking history, booking, and cancellation workflows.

For further validation, test the following scenarios:

1. Register a passenger using valid information.
2. Search for flights using different source and destination values.
3. View seats and verify their booking status.
4. Book an available seat.
5. Confirm that the booking appears in booking history.
6. Cancel an eligible booking.
7. Verify that the seat becomes available again.
8. Try invalid IDs, missing fields, and unavailable seats.
9. Confirm that database changes are reflected in the application.

Use test data and verify database records when checking booking consistency. A successful compilation does not, by itself, establish that every workflow has passed testing.

## Security and Configuration Notes

- Do not commit database passwords or other secrets.
- Configure credentials using environment variables.
- Keep the local web server bound to the loopback address unless a properly secured deployment is planned.
- Review SQL scripts before executing them against an existing database.
- Use appropriate validation and database constraints to maintain data integrity.

## Future Enhancements

- Administrative flight creation and management.
- Fare calculation and payment simulation.
- Automated unit and integration tests.
- Improved logging and error handling.
- Enhanced accessibility and responsive interface design.
- Deployment with appropriate authentication and security controls.

## Author

Developed as an academic project to demonstrate Java programming, object-oriented design, collections, JDBC, MySQL integration, HTTP request handling, and Git-based version control.
