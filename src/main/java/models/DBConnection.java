package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // If this weren't being designed to only use an in-memory DB, this would be in a .env
    private static final String _CONNECTION_URL = "jdbc:sqlite::memory:";
    private static Connection _conn;

    public static Connection getConnection() throws SQLException {
        if (_conn == null || _conn.isClosed()) {
            // Since this DB is in-memory, build it if we're not connected.
            // TODO if this were a real app, make sure DB is initialized on launch, rather than at connection time
            _conn = buildDB();
        }

        return _conn;
    }

    private static Connection buildDB() throws SQLException {
        Connection created = DriverManager.getConnection(_CONNECTION_URL);
        // TODO use schema and init to actually create the DB

        return created;
    }
}
