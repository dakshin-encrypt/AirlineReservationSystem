
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/airline_reservation";

    public static Connection getConnection() throws SQLException {
        String user = System.getenv("AIRLINE_DB_USER");
        String password = System.getenv("AIRLINE_DB_PASSWORD");

        if (user == null || user.isBlank()) {
            throw new SQLException(
                    "AIRLINE_DB_USER environment variable is not set.");
        }

        if (password == null || password.isEmpty()) {
            throw new SQLException(
                    "AIRLINE_DB_PASSWORD environment variable is not set.");
        }

        return DriverManager.getConnection(URL, user, password);
    }
}
