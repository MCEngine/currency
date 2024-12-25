package io.github.mcengine.api.database;

public interface MCEngineCurrencyApiDBInterface {
    public void connect();
    public void createTable();
    public void disConnection();
    public double getCoin(String playerUuid, String coinType);
    public Connection getConnection();
    public void insertCurrency(String playerUuid, double coin, double copper, double silver, double gold);
    public void insertTransaction(String playerUuidSender, String playerUuidReceiver, String currencyType, String transactionType, double amount, String notes);
    public boolean playerExists(String uuid);
    public void updateCurrencyValue(String playerUuid, String operator, String coinType, double amt);
}