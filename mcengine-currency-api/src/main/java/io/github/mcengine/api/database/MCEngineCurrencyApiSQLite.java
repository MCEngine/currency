package io.github.mcengine.api.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class MCEngineCurrencyApiSQLite {
    private Connection connection;

    // Establish connection to SQLite
    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/MCEngineCurrency/currency.db");
            System.out.println("Connected to SQLite database.");
        } catch (SQLException e) {
            System.err.println("Failed to connect to SQLite database." + e.getMessage());
        }
    }

    // Get the current connection
    public Connection getConnection() {
        return connection;
    }

    // Disconnect from SQLite
    public void disConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from SQLite database.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to disconnect from SQLite database: " + e.getMessage());
        }
    }

    // Create the currency table if it doesn't exist
    public void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS currency ("
            + "player_uuid CHAR(36) PRIMARY KEY, "
            + "coin DECIMAL(10,2), "
            + "copper DECIMAL(10,2), "
            + "silver DECIMAL(10,2), "
            + "gold DECIMAL(10,2));";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table 'currency' created successfully in SQLite database.");
        } catch (SQLException e) {
            System.err.println("Error creating table 'currency': " + e.getMessage());
        }
    }

    // Insert currency information
    public void insertCurrency(String playerUuid, double coin, double copper, double silver, double gold) {
        String insertSQL = "INSERT INTO currency (player_uuid, coin, copper, silver, gold) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, playerUuid);
            pstmt.setDouble(2, coin);
            pstmt.setDouble(3, copper);
            pstmt.setDouble(4, silver);
            pstmt.setDouble(5, gold);
            pstmt.executeUpdate();
            System.out.println("Currency information added for player_uuid: " + playerUuid);
        } catch (SQLException e) {
            System.err.println("Error inserting currency for player uuid: " + playerUuid + " - " + e.getMessage());
        }
    }

    public void updateCurrencyValue(String playerUuid, String operator, String coinType, double amt) {
        // Validate coinType against allowed columns
        if (!coinType.matches("coin|copper|silver|gold")) {
            throw new IllegalArgumentException("Invalid coin type: " + coinType);
        }

        String query = "UPDATE currency SET " + coinType + " = " + coinType + " " + operator
        + " ? WHERE player_uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDouble(1, amt);
            pstmt.setString(2, playerUuid);
            pstmt.executeUpdate();
            System.out.println("Updated " + coinType + " for player uuid: " + playerUuid);
        } catch (SQLException e) {
            System.err.println("Error updating " + coinType + " for player uuid: " + playerUuid + " - " + e.getMessage());
        }
    }
}
