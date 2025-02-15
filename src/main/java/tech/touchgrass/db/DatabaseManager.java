package tech.touchgrass.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/database.db";
    private static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database");
        }
    }

    public static void executeUpdate(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public static ResultSet executeQuery(String sql) throws SQLException {
        Statement stmt = connection.createStatement();
        if (sql.contains("SELECT")) {
            return stmt.executeQuery(sql);
        }
        stmt.executeUpdate(sql);
        return null;
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}