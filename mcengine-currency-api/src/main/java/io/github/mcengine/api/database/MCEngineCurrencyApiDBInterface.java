package io.github.mcengine.api.database;

import java.sql.Connection;

public interface MCEngineCurrencyApiDBInterface {
    void connect();
    void createTable();
    void disConnection();
    double getCoin(String playerUuid, String coinType);
    Connection getConnection();
    void insertCurrency(String playerUuid, double coin, double copper, double silver, double gold);
    void insertTransaction(String playerUuidSender, String playerUuidReceiver, String currencyType, String transactionType, double amount, String notes);
    boolean playerExists(String uuid);
    void updateCurrencyValue(String playerUuid, String operator, String coinType, double amt);
}