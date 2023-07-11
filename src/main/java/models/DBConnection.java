package models;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * This class is responsible for connecting to the DB and building it if it doesn't exist.
 */
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
        Connection conn = DriverManager.getConnection(_CONNECTION_URL);
        StringBuilder queryReader;
        String schemaString;
        String initString;

        // I want this build to be atomic, so I'm going to do manual commits for this section
        conn.setAutoCommit(false);

        try (InputStream schemaStream = DBConnection.class.getClassLoader().getResourceAsStream("sql/schema.sql");
             InputStream initStream = DBConnection.class.getClassLoader().getResourceAsStream("sql/init.sql")) {

            // Build schema string
            queryReader = new StringBuilder();
            while (schemaStream.available() > 0) {
                queryReader.append((char) schemaStream.read());
            }
            schemaString = queryReader.toString();

            // Build init string
            queryReader = new StringBuilder();
            while (initStream.available() > 0) {
                queryReader.append((char) initStream.read());
            }
            initString = queryReader.toString();

            // Build DB
            conn.beginRequest();
            for (String query : schemaString.split(";")) {
                if (query.isBlank()) continue; // Skip empty queries (last one will be empty)
                conn.createStatement().execute(query+";");
            }
            for (String query : initString.split(";")) {
                if (query.isBlank()) continue; // Skip empty queries (last one will be empty)
                conn.createStatement().execute(query+";");
            }
            conn.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.endRequest();
            conn.setAutoCommit(true);
        }

        return conn;
    }
}
