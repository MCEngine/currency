package io.github.mcengine.api.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MCEngineCurrencyApiMySQL {
    private String dbHost, dbPort, dbName, dbUser, dbPassword;
    private Connection connection;

    public MCEngineCurrencyApiMySQL(String dbHost, String dbPort, String dbName, String dbUser, String dbPassword) {
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(
                "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName,
                dbUser,
                dbPassword
            );
            System.out.println("Connected to MySQL database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void disConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from MySQL database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS currency ("
            + "uuid CHAR(36) PRIMARY KEY, "
            + "coin DECIMAL(10,2), "
            + "copper DECIMAL(10,2), "
            + "silver DECIMAL(10,2), "
            + "gold DECIMAL(10,2));";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table 'currency' created successfully in MySQL database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to insert information into the table
    public void insertCurrency(String uuid, double coin, double copper, double silver, double gold) {
        String insertSQL = "INSERT INTO currency (uuid, coin, copper, silver, gold) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, uuid);
            pstmt.setDouble(2, coin);
            pstmt.setDouble(3, copper);
            pstmt.setDouble(4, silver);
            pstmt.setDouble(5, gold);
            pstmt.executeUpdate();
            System.out.println("Currency information added for uuid: " + uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCurrencyValue(String uuid, String operator, String coinType, Double amt) {
        // Ensure the coinType is properly sanitized for SQL injection safety
        String query = "UPDATE currency SET "
            + coinType + " = " + coinType + " " + operator
            + " ? WHERE uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Use setDouble for numeric values like amt
            pstmt.setDouble(1, amt);
            pstmt.setString(2, uuid);
            
            // Execute the update query
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
