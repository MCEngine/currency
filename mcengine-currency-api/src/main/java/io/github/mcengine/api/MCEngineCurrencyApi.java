package io.github.mcengine.api;

import java.util.UUID;

public class MCEngineCurrencyApi {
    private final Object databaseInstance;

    public MCEngineCurrencyApi(String sqlType, String[] sqlInfo) {
        Object tempInstance = null;
        try {
            if (sqlType.equalsIgnoreCase("mysql")) {
                Class<?> clazz = Class.forName("io.github.mcengine.api.database.MCEngineCurrencyApiMySQL");
                tempInstance = clazz.getDeclaredConstructor(String.class, String.class, String.class, String.class, String.class)
                        .newInstance(sqlInfo[0], sqlInfo[1], sqlInfo[2], sqlInfo[3], sqlInfo[4]);
            } else if (sqlType.equalsIgnoreCase("sqlite")) {
                Class<?> clazz = Class.forName("io.github.mcengine.api.database.MCEngineCurrencyApiSQLite");
                tempInstance = clazz.getDeclaredConstructor(String.class).newInstance(sqlInfo[0]);
            } else {
                throw new IllegalArgumentException("Unsupported SQL type: " + sqlType);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading database implementation: " + e.getMessage(), e);
        }
        this.databaseInstance = tempInstance;
    }

    public void initDB() {
        try {
            databaseInstance.getClass().getMethod("connect").invoke(databaseInstance);
            databaseInstance.getClass().getMethod("createTable").invoke(databaseInstance);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing database: " + e.getMessage(), e);
        }
    }

    public void initPlayerData(UUID uuid) {
        try {
            databaseInstance.getClass()
                .getMethod("insertCurrency", String.class, double.class, double.class, double.class, double.class)
                .invoke(databaseInstance, uuid.toString(), 0.0, 0.0, 0.0, 0.0);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing player data: " + e.getMessage(), e);
        }
    }

    public void addCoin(UUID uuid, String coinType, double amt) {
        updateCurrency(uuid, "+", coinType, amt);
    }

    public void minusCoin(UUID uuid, String coinType, double amt) {
        updateCurrency(uuid, "-", coinType, amt);
    }

    private void updateCurrency(UUID uuid, String operator, String coinType, double amt) {
        try {
            databaseInstance.getClass()
                .getMethod("updateCurrencyValue", String.class, String.class, String.class, double.class)
                .invoke(databaseInstance, uuid.toString(), operator, coinType, amt);
        } catch (Exception e) {
            throw new RuntimeException("Error updating currency: " + e.getMessage(), e);
        }
    }

    public void disConnect() {
        try {
            databaseInstance.getClass().getMethod("disConnection").invoke(databaseInstance);
        } catch (Exception e) {
            throw new RuntimeException("Error disconnecting database: " + e.getMessage(), e);
        }
    }
}
