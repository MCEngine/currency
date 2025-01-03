package io.github.mcengine;

import io.github.mcengine.api.MCEngineCurrencyApi;
import org.bukkit.plugin.java.JavaPlugin;

public class MCEngineCurrency extends JavaPlugin {

    private MCEngineCurrencyApi currencyApi;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Retrieve the database type from the config
        String sqlType = getConfig().getString("database.type", "sqlite").toLowerCase();

        String[] sqlInfo;

        try {
            switch (sqlType) {
                case "mysql":
                    String dbHost = getConfig().getString("database.host", "localhost");
                    String dbPort = getConfig().getString("database.port", "3306");
                    String dbUser = getConfig().getString("database.user", "root");
                    String dbPassword = getConfig().getString("database.password", "");
                    String dbName = getConfig().getString("database.name", "minecraft"); // Default name for MySQL
                    sqlInfo = new String[]{dbHost, dbPort, dbName, dbUser, dbPassword};
                    currencyApi = new MCEngineCurrencyApi(sqlType, sqlInfo);
                    break;

                case "sqlite":
                    String dbFile = getConfig().getString("database.path", "plugins/MCEngineCurrency/data.db");
                    sqlInfo = new String[]{dbFile};
                    currencyApi = new MCEngineCurrencyApi(sqlType, sqlInfo);
                    break;

                default:
                    getLogger().severe("Invalid database type specified in config.yml. Supported types: mysql, sqlite");
                    getServer().getPluginManager().disablePlugin(this);
                    return;
            }

            // Initialize the database
            currencyApi.initDB();
            getLogger().info("Database connection initialized successfully.");
        } catch (Exception e) {
            getLogger().severe("Failed to initialize the database connection: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Disconnect from the database
        if (currencyApi != null) {
            try {
                currencyApi.disConnect();
                getLogger().info("Database connection closed successfully.");
            } catch (Exception e) {
                getLogger().severe("Failed to close the database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
