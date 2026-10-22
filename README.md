# Airline Reservation System

A console-based Airline Reservation System built using Java, Object-Oriented Programming (OOP), Java Collections, JDBC, and MySQL. The application manages passengers, flights, seat availability, ticket bookings, and cancellations through a menu-driven interface.

## Features

- **Passenger Management:** Add and view passenger records.
- **Flight Management:** View flights and search for flights.
- **Seat Management:** Check seat availability for a selected flight.
- **Ticket Booking:** Book an available seat for a passenger.
- **Booking Management:** View booking records and cancel confirmed bookings.
- **Database Integration:** Store and retrieve information using JDBC and MySQL.
- **Booking Consistency:** Update booking status, seat availability, and flight availability during booking and cancellation.

## Technologies Used

- Java 26
- Object-Oriented Programming (OOP)
- Java Collections Framework
- JDBC (Java Database Connectivity)
- MySQL 8.4
- MySQL Connector/J 9.5.0
- Apache Maven
- Git and GitHub
- Visual Studio Code

## Prerequisites

Install the following before running the application:

- JDK 26
- Apache Maven
- MySQL Server
- MySQL Workbench (optional, for database management)

Verify Java and Maven installations:

```powershell
java --version
javac --version
mvn --version
```

## Database Setup

1. Start your MySQL server.
2. Open MySQL Workbench or another MySQL client.
3. Open `sql/database.sql`.
4. Review the script and execute it to create the database and tables.
5. Confirm that the `airline_reservation` database and its required tables exist.

**Important:** The schema script creates the database structure. It does not necessarily populate sample passengers, flights, or seats. Use the application's supported workflows or a separately reviewed seed script to add test data.

## Database Credentials

The application reads database credentials from environment variables instead of storing passwords in the source code.

In PowerShell, set the credentials for the current terminal session:

```powershell
$env:AIRLINE_DB_USER = "root"
$securePassword = Read-Host "Enter your MySQL password" -AsSecureString
$env:AIRLINE_DB_PASSWORD = [System.Net.NetworkCredential]::new("", $securePassword).Password
```

Replace `root` with your MySQL username if you use a different account. Enter your password only at the secure prompt; do not commit passwords to GitHub.

These environment variables apply to the current PowerShell session. Set them again in a new terminal when necessary.

## Build and Run

Open PowerShell in the project root directory and compile the project:

```powershell
mvn clean compile
```

Run the console application:

```powershell
mvn exec:java "-Dexec.mainClass=Main"
```

Follow the on-screen menu to use the available features.

## Application Menu

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

## Project Structure

```text
AirlineReservationSystem/
├── src/
│   └── main/
│       └── java/
│           ├── Main.java
│           ├── model/
│           ├── dao/
│           ├── service/
│           └── util/
├── sql/
│   └── database.sql
├── pom.xml
├── README.md
└── .gitignore
```

The `model`, `dao`, `service`, and `util` packages separate data models, database access, business operations, and utility functionality.

## Testing

Test the application with valid and invalid inputs. Verify passenger registration, flight searching, seat availability, successful booking, viewing bookings, cancellation, and database updates.

Use test records only, and confirm that cancelled bookings release seats and restore flight availability correctly.

## Current Project Status

The core console workflows have been implemented and compiled successfully. Final validation, documentation review, and cleanup checks are part of the project completion process.

## Future Enhancements

- Flight creation and management through an administrative interface
- Fare calculation and payment simulation
- Improved input validation and user-friendly error messages
- Automated unit and integration tests
- A graphical user interface or web interface

## Author

Developed as an academic project to demonstrate Java programming, object-oriented design, collections, database connectivity, and Git-based version control.
