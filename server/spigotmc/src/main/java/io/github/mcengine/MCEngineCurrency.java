package io.github.mcengine;

import io.github.mcengine.api.MCEngineCurrencyApi;
import io.github.mcengine.api.MCEngineApiUtil;
import io.github.mcengine.common.currency.command.MCEngineCurrencyCommonCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class MCEngineCurrency extends JavaPlugin {

    private MCEngineCurrencyApi currencyApi;

    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();
            MCEngineApiUtil.saveResourceIfNotExists(this, "data.db");

            // Retrieve the database type from the config
            String sqlType = getConfig().getString("database.type", "sqlite").toLowerCase();
            initializeCurrencyApi(sqlType);

            // Initialize the database
            if (currencyApi != null) {
                currencyApi.initDB();
                getLogger().info("Database connection initialized successfully.");
            }

            // Register the /currency command
            if (getCommand("currency") != null) {
                getCommand("currency").setExecutor(new MCEngineCurrencyCommonCommand(currencyApi));
                getLogger().info("MCEngineCurrency plugin enabled successfully.");
            } else {
                throw new IllegalStateException("Command 'currency' is not registered in plugin.yml.");
            }
        } catch (Exception e) {
            getLogger().severe("Failed to enable the plugin: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void initializeCurrencyApi(String sqlType) throws Exception {
        String[] sqlInfo;
        switch (sqlType) {
            case "mysql":
                String dbHost = getConfig().getString("database.host", "localhost");
                String dbPort = getConfig().getString("database.port", "3306");
                String dbUser = getConfig().getString("database.user", "root");
                String dbPassword = getConfig().getString("database.password", "");
                String dbName = getConfig().getString("database.name", "minecraft");
                sqlInfo = new String[]{dbHost, dbPort, dbName, dbUser, dbPassword};
                currencyApi = new MCEngineCurrencyApi(sqlType, sqlInfo);
                break;

            case "sqlite":
                String dbFile = getConfig().getString("database.path", "plugins/MCEngineCurrency/data.db");
                sqlInfo = new String[]{dbFile};
                currencyApi = new MCEngineCurrencyApi(sqlType, sqlInfo);
                break;

            default:
                throw new IllegalArgumentException("Invalid database type specified in config.yml. Supported types: mysql, sqlite.");
        }
    }

    @Override
    public void onDisable() {
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
