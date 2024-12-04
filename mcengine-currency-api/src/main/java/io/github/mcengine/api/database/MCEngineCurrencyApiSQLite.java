package io.github.mcengine.api.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class MCEngineCurrencyApiSQLite {
    private final String dbPath;
    private Connection connection;

    /**
     * Constructor to initialize the SQLite API with a database path.
     * @param dbPath the path to the SQLite database file.
     */
    public MCEngineCurrencyApiSQLite(String dbPath) {
        this.dbPath = dbPath;
    }

    /**
     * Establishes a connection to the SQLite database.
     */
    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            System.out.println("Connected to SQLite database.");
        } catch (SQLException e) {
            System.err.println("Failed to connect to SQLite database: " + e.getMessage());
        }
    }

    /**
     * Returns the current connection to the SQLite database.
     * @return the current {@link Connection}.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Disconnects from the SQLite database.
     */
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

    /**
     * Creates the 'currency' and 'currency_transaction' tables in the database if they do not exist.
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
            + "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "player_uuid CHAR(36) NOT NULL, "
            + "currency_type TEXT CHECK(currency_type IN ('coin', 'copper', 'silver', 'gold')) NOT NULL, "
            + "transaction_type TEXT CHECK(transaction_type IN ('pay', 'purchase')) NOT NULL, "
            + "amount DECIMAL(10,2) NOT NULL, "
            + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "notes TEXT, "
            + "FOREIGN KEY (player_uuid) REFERENCES currency(player_uuid));";

        try (Statement stmt = connection.createStatement()) {
            // Execute the SQL to create the 'currency' table
            stmt.executeUpdate(createCurrencyTableSQL);
            System.out.println("Table 'currency' created successfully in SQLite database.");

            // Execute the SQL to create the 'currency_transaction' table
            stmt.executeUpdate(createTransactionTableSQL);
            System.out.println("Table 'currency_transaction' created successfully in SQLite database.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    /**
     * Inserts currency information for a player into the database.
     * @param playerUuid the unique identifier for the player.
     * @param coin the amount of coin currency.
     * @param copper the amount of copper currency.
     * @param silver the amount of silver currency.
     * @param gold the amount of gold currency.
     */
    public void insertCurrency(String playerUuid, double coin, double copper, double silver, double gold) {
        String query = "INSERT INTO currency (player_uuid, coin, copper, silver, gold) VALUES (?, ?, ?, ?, ?) ON CONFLICT(player_uuid) DO NOTHING;";
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
     * Updates a specific type of currency for a player.
     * @param playerUuid the unique identifier for the player.
     * @param operator the SQL operator to apply (+, -, etc.).
     * @param coinType the type of currency to update (coin, copper, silver, or gold).
     * @param amt the amount by which to update the currency.
     * @throws IllegalArgumentException if the coinType is invalid.
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
