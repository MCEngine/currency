package io.github.mcengine.api.currency.database;

import java.sql.Connection;

/**
 * Interface for handling database operations related to the MCEngine Currency system.
 */
public interface MCEngineCurrencyApiDBInterface {

    /**
     * Establishes a connection to the database.
     */
    void connect();

    /**
     * Creates the necessary tables for storing currency data.
     */
    void createTable();

    /**
     * Closes the database connection.
     */
    void disConnection();

    /**
     * Retrieves the amount of a specific coin type for a given player.
     *
     * @param playerUuid The UUID of the player.
     * @param coinType   The type of coin (e.g., "Copper", "Silver", "Gold").
     * @return The balance of the specified coin type.
     */
    double getCoin(String playerUuid, String coinType);

    /**
     * Retrieves the active database connection.
     *
     * @return The active {@link Connection} object.
     */
    Connection getConnection();

    /**
     * Inserts a new currency record for a player.
     *
     * @param playerUuid The UUID of the player.
     * @param coin       The total coin balance.
     * @param copper     The amount of copper coins.
     * @param silver     The amount of silver coins.
     * @param gold       The amount of gold coins.
     */
    void insertCurrency(String playerUuid, double coin, double copper, double silver, double gold);

    /**
     * Records a currency transaction between two players.
     *
     * @param playerUuidSender   The UUID of the sender.
     * @param playerUuidReceiver The UUID of the receiver.
     * @param currencyType       The type of currency being transacted.
     * @param transactionType    The type of transaction (e.g., "SEND", "RECEIVE").
     * @param amount             The amount of currency involved in the transaction.
     * @param notes              Additional notes about the transaction.
     */
    void insertTransaction(String playerUuidSender, String playerUuidReceiver, String currencyType, String transactionType, double amount, String notes);

    /**
     * Checks if a player exists in the database.
     *
     * @param uuid The UUID of the player.
     * @return {@code true} if the player exists, {@code false} otherwise.
     */
    boolean playerExists(String uuid);

    /**
     * Updates a player's currency value using an arithmetic operation.
     *
     * @param playerUuid The UUID of the player.
     * @param operator   The arithmetic operator (e.g., "+", "-").
     * @param coinType   The type of coin being updated.
     * @param amt        The amount to be updated.
     */
    void updateCurrencyValue(String playerUuid, String operator, String coinType, double amt);
}
