package io.github.mcengine.api.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class MCEngineCurrencyApiMySQL {
    private final String dbHost, dbPort, dbName, dbUser, dbPassword;
    private Connection connection;

    /**
     * Constructs an instance of MCEngineCurrencyApiMySQL.
     *
     * @param dbHost     the database host address
     * @param dbPort     the database port number
     * @param dbName     the name of the database
     * @param dbUser     the username for the database connection
     * @param dbPassword the password for the database connection
     */
    public MCEngineCurrencyApiMySQL(String dbHost, String dbPort, String dbName, String dbUser, String dbPassword) {
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    /**
     * Establishes a connection to the MySQL database.
     */
    public void connect() {
        String dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?useSSL=false&serverTimezone=UTC";
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Connected to MySQL database");
        } catch (SQLException e) {
            System.err.println("Failed to connect to MySQL database: " + e.getMessage());
        }
    }

    /**
     * Returns the active database connection.
     *
     * @return the current database connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Closes the current database connection.
     */
    public void disConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from MySQL database.");
            }
        } catch (SQLException e) {
            System.err.println("Error while disconnecting from MySQL database: " + e.getMessage());
        }
    }

    /**
     * Creates the required tables in the database if they do not already exist.
     *
     * @param connection The active database connection used to execute SQL statements.
     *
     * The 'currency' table stores player-specific balances for different currency types:
     * - player_uuid: Unique identifier for the player (primary key).
     * - coin, copper, silver, gold: Decimal values representing the player's balance for each currency type.
     *
     * The 'currency_transaction' table records individual transactions involving currency:
     * - transaction_id: Unique identifier for each transaction (primary key, auto-incremented).
     * - player_uuid: Identifier linking the transaction to a player (foreign key referencing 'currency.player_uuid').
     * - currency_type: Specifies the type of currency involved ('coin', 'copper', 'silver', or 'gold').
     * - transaction_type: Indicates the type of transaction ('credit' or 'debit').
     * - amount: The amount of currency involved in the transaction.
     * - timestamp: Automatically records the time of the transaction.
     * - notes: Optional field for additional transaction details.
     */
    public void createTable() {
        // SQL for creating the 'currency' table
        String createCurrencyTableSQL = "CREATE TABLE IF NOT EXISTS currency ("
            + "player_uuid CHAR(36) PRIMARY KEY, "
            + "coin DECIMAL(10,2), "
            + "copper DECIMAL(10,2), "
            + "silver DECIMAL(10,2), "
            + "gold DECIMAL(10,2));";

        // SQL for creating the 'currency_transaction' table
        String createTransactionTableSQL = "CREATE TABLE IF NOT EXISTS currency_transaction ("
            + "transaction_id INT AUTO_INCREMENT PRIMARY KEY, "
            + "player_uuid CHAR(36) NOT NULL, "
            + "currency_type ENUM('coin', 'copper', 'silver', 'gold') NOT NULL, "
            + "transaction_type ENUM('pay', 'purchase') NOT NULL, "
            + "amount DECIMAL(10,2) NOT NULL, "
            + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
            + "notes VARCHAR(255), "
            + "FOREIGN KEY (player_uuid) REFERENCES currency(player_uuid));";

        try (Statement stmt = connection.createStatement()) {
            // Execute the SQL to create the 'currency' table
            stmt.executeUpdate(createCurrencyTableSQL);
            System.out.println("Table 'currency' created successfully in MySQL database.");

            // Execute the SQL to create the 'currency_transaction' table
            stmt.executeUpdate(createTransactionTableSQL);
            System.out.println("Table 'currency_transaction' created successfully in MySQL database.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }  

    /**
     * Inserts or updates a player's currency values in the database.
     *
     * @param playerUuid the unique identifier for the player
     * @param coin       the amount of coin currency
     * @param copper     the amount of copper currency
     * @param silver     the amount of silver currency
     * @param gold       the amount of gold currency
     */
    public void insertCurrency(String playerUuid, double coin, double copper, double silver, double gold) {
        String query = "INSERT INTO currency (player_uuid, coin, copper, silver, gold) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE player_uuid = player_uuid;";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, playerUuid);
            pstmt.setDouble(2, coin);
            pstmt.setDouble(3, copper);
            pstmt.setDouble(4, silver);
            pstmt.setDouble(5, gold);
            pstmt.executeUpdate();
            System.out.println("Currency information added for player uuid: " + playerUuid);
        } catch (SQLException e) {
            System.err.println("Error inserting currency for player uuid: " + playerUuid + " - " + e.getMessage());
        }
    }

    /**
     * Updates a specific currency value for a player.
     *
     * @param playerUuid the unique identifier for the player
     * @param operator   the operation to perform ('+' or '-')
     * @param coinType   the type of currency to update (e.g., 'coin', 'copper', 'silver', 'gold')
     * @param amt        the amount to adjust the currency value by
     */
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
