package io.github.mcengine.api;

import java.util.UUID;

import io.github.mcengine.api.MCEngineApiUtil;

public class MCEngineCurrencyApi {
    private final Object databaseInstance;

    public MCEngineCurrencyApi(String sqlType, String[] sqlInfo) {
        Object tempInstance = null;
        try {
            if (sqlType.equalsIgnoreCase("mysql")) {
                tempInstance = MCEngineApiUtil.initialize(
                        "io.github.mcengine.api.database.MCEngineCurrencyApiMySQL",
                        sqlInfo[0], sqlInfo[1], sqlInfo[2], sqlInfo[3], sqlInfo[4]
                );
            } else if (sqlType.equalsIgnoreCase("sqlite")) {
                tempInstance = MCEngineApiUtil.initialize(
                        "io.github.mcengine.api.database.MCEngineCurrencyApiSQLite",
                        sqlInfo[0]
                );
            } else {
                throw new IllegalArgumentException("Unsupported SQL type: " + sqlType);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading database implementation: " + e.getMessage(), e);
        }
        this.databaseInstance = tempInstance;
    }

    /**
     * Initializes the database by connecting and creating the necessary table.
     */
    public void initDB() {
        MCEngineApiUtil.invokeMethod(databaseInstance, "connect");
        MCEngineApiUtil.invokeMethod(databaseInstance, "createTable");
    }

    /**
     * Initializes player data in the database with default currency values.
     *
     * @param uuid the unique identifier of the player
     */
    public void initPlayerData(UUID uuid) {
        MCEngineApiUtil.invokeMethod(databaseInstance, "insertCurrency", uuid.toString(), 0.0, 0.0, 0.0, 0.0);
    }

    /**
     * Adds a specified amount of a given type of coin to a player's account.
     *
     * @param uuid the unique identifier of the player
     * @param coinType the type of coin to add (e.g., "gold", "silver")
     * @param amt the amount of coin to add
     */
    public void addCoin(UUID uuid, String coinType, double amt) {
        updateCurrency(uuid, "+", coinType, amt);
    }

    /**
     * Checks if a player exists in the database.
     *
     * @param uuid the unique identifier of the player
     * @return true if the player exists, false otherwise
     */
    public boolean checkIfPlayerExists(UUID uuid) {
        Object result = MCEngineApiUtil.invokeMethod(databaseInstance, "playerExists", uuid.toString());
        if (result instanceof Boolean) {
            return (Boolean) result;
        } else {
            throw new RuntimeException("Error checking if player exists in the database.");
        }
    }

    /**
     * Records a transaction between two players in the database.
     *
     * @param playerUuidSender the unique identifier of the sender
     * @param playerUuidReceiver the unique identifier of the receiver
     * @param currencyType the type of currency involved in the transaction (e.g., "coin", "copper")
     * @param transactionType the type of transaction (e.g., "pay", "purchase")
     * @param amount the amount of currency involved
     * @param notes optional notes for the transaction
     */
    public void createTransaction(UUID playerUuidSender, UUID playerUuidReceiver, String currencyType, String transactionType, double amount, String notes) {
        MCEngineApiUtil.invokeMethod(databaseInstance, "insertTransaction", playerUuidSender.toString(), playerUuidReceiver.toString(), currencyType, transactionType, amount, notes);
    }

    /**
     * Disconnects from the database.
     */
    public void disConnect() {
        MCEngineApiUtil.invokeMethod(databaseInstance, "disConnection");
    }

    /**
     * Retrieves the balance of a specified coin type for a player.
     *
     * @param uuid     the unique identifier of the player
     * @param coinType the type of coin to retrieve (e.g., "coin", "copper", "silver", "gold")
     * @return the balance of the specified coin type for the player
     * @throws IllegalArgumentException if the coinType is invalid
     */
    public double getCoin(UUID uuid, String coinType) {
        if (!coinType.matches("coin|copper|silver|gold")) {
            throw new IllegalArgumentException("Invalid coin type: " + coinType);
        }

        Object result = MCEngineApiUtil.invokeMethod(databaseInstance, "getCoin", uuid.toString(), coinType);
        if (result instanceof Double) {
            return (Double) result;
        } else {
            throw new RuntimeException("Error retrieving coin balance from the database.");
        }
    }

    /**
     * Deducts a specified amount of a given type of coin from a player's account.
     *
     * @param uuid the unique identifier of the player
     * @param coinType the type of coin to deduct (e.g., "gold", "silver")
     * @param amt the amount of coin to deduct
     */
    public void minusCoin(UUID uuid, String coinType, double amt) {
        updateCurrency(uuid, "-", coinType, amt);
    }

    /**
     * Updates the currency value for a player with a specific operation.
     *
     * @param uuid the unique identifier of the player
     * @param operator the operation to perform ("+" to add, "-" to subtract)
     * @param coinType the type of coin to update
     * @param amt the amount of coin to update
     */
    private void updateCurrency(UUID uuid, String operator, String coinType, double amt) {
        MCEngineApiUtil.invokeMethod(databaseInstance, "updateCurrencyValue", uuid.toString(), operator, coinType, amt);
    }
}
