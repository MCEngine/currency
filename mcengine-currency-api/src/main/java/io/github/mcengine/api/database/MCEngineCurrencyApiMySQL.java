package io.github.mcengine.api.database;

import java.sql.Connection;
import java.sql.DriverManager;
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
}
