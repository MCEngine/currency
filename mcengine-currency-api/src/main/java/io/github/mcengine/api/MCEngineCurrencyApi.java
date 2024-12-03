package io.github.mcengine.api;

import java.util.UUID;

public class MCEngineCurrencyApi {
    private final Object databaseInstance;

    public MCEngineCurrencyApi(String sqlType, String[] sqlInfo) {
        Object tempInstance = null;
        try {
            if (sqlType.equalsIgnoreCase("mysql")) {
                tempInstance = initializeDatabase(
                        "io.github.mcengine.api.database.MCEngineCurrencyApiMySQL",
                        sqlInfo[0], sqlInfo[1], sqlInfo[2], sqlInfo[3], sqlInfo[4]
                );
            } else if (sqlType.equalsIgnoreCase("sqlite")) {
                tempInstance = initializeDatabase(
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

    private Object initializeDatabase(String className, Object... constructorArgs) throws Exception {
        Class<?> clazz = Class.forName(className);
        Class<?>[] parameterTypes = new Class[constructorArgs.length];
        for (int i = 0; i < constructorArgs.length; i++) {
            parameterTypes[i] = mapWrapperToPrimitive(constructorArgs[i].getClass());
        }
        return clazz.getConstructor(parameterTypes).newInstance(constructorArgs);
    }    

    public void initDB() {
        invokeMethod("connect");
        invokeMethod("createTable");
    }

    public void initPlayerData(UUID uuid) {
        invokeMethod("insertCurrency", uuid.toString(), 0.0, 0.0, 0.0, 0.0);
    }

    public void addCoin(UUID uuid, String coinType, double amt) {
        updateCurrency(uuid, "+", coinType, amt);
    }

    public void minusCoin(UUID uuid, String coinType, double amt) {
        updateCurrency(uuid, "-", coinType, amt);
    }

    private void updateCurrency(UUID uuid, String operator, String coinType, double amt) {
        invokeMethod("updateCurrencyValue", uuid.toString(), operator, coinType, amt);
    }

    public void disConnect() {
        invokeMethod("disConnection");
    }

    private void invokeMethod(String methodName, Object... args) {
        try {
            Class<?>[] argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = mapWrapperToPrimitive(args[i].getClass());
            }
            databaseInstance.getClass().getMethod(methodName, argTypes).invoke(databaseInstance, args);
        } catch (Exception e) {
            throw new RuntimeException("Error invoking method '" + methodName + "': " + e.getMessage(), e);
        }
    }

    private Class<?> mapWrapperToPrimitive(Class<?> clazz) {
        if (clazz == Double.class) return double.class;
        if (clazz == Integer.class) return int.class;
        if (clazz == Long.class) return long.class;
        if (clazz == Boolean.class) return boolean.class;
        if (clazz == Float.class) return float.class;
        if (clazz == Character.class) return char.class;
        if (clazz == Byte.class) return byte.class;
        if (clazz == Short.class) return short.class;
        return clazz; // Return original if no mapping needed
    }    
}
