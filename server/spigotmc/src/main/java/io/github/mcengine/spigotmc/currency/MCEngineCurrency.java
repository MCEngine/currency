package io.github.mcengine.spigotmc.currency;

import io.github.mcengine.api.currency.MCEngineCurrencyApi;
import io.github.mcengine.api.MCEngineApiUtil;
import io.github.mcengine.common.currency.command.MCEngineCurrencyCommonCommand;
import io.github.mcengine.common.currency.listener.MCEngineCurrencyCommonListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MCEngineCurrency extends JavaPlugin {

    private MCEngineCurrencyApi currencyApi;
    private final MCEngineApiUtil mcengineApiUtil = new MCEngineApiUtil();

    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();
            mcengineApiUtil.saveResourceIfNotExists(this, "data.db");

            // Retrieve the database type from the config
            String sqlType = getConfig().getString("database.type", "sqlite").toLowerCase();
            initializeCurrencyApi(sqlType);

            // Initialize the database
            if (currencyApi != null) {
                currencyApi.initDB();
                getLogger().info("Database connection initialized successfully.");
            }

            // Register Listener
            getServer().getPluginManager().registerEvents(new MCEngineCurrencyCommonListener(currencyApi), this);
            
            // Register Command
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

    private void initializeCurrencyApi(String sqlType) {
        try {
            switch (sqlType) {
                case "mysql":
                    String dbHost = getConfig().getString("database.host", "localhost");
                    String dbPort = getConfig().getString("database.port", "3306");
                    String dbUser = getConfig().getString("database.user", "root");
                    String dbPassword = getConfig().getString("database.password", "");
                    String dbName = getConfig().getString("database.name", "minecraft");

                    String[] sqlInfo = {dbHost, dbPort, dbName, dbUser, dbPassword};
                    currencyApi = new MCEngineCurrencyApi(sqlType, sqlInfo);
                    break;
    
                case "sqlite":
                    // Ensure the plugin folder exists
                    File pluginFolder = getDataFolder();
                    if (!pluginFolder.exists()) {
                        pluginFolder.mkdirs();
                    }
    
                    // Set the SQLite database file path inside the plugin folder
                    File dbFile = new File(pluginFolder, getConfig().getString("database.path", "data.db"));
                    currencyApi = new MCEngineCurrencyApi(sqlType, new String[]{dbFile.getAbsolutePath()});
                    break;
    
                default:
                    throw new IllegalArgumentException("Invalid database type specified in config.yml. Supported types: mysql, sqlite.");
            }
            getLogger().info("Currency API initialized with " + sqlType);
        } catch (Exception e) {
            getLogger().severe("Error initializing Currency API: " + e.getMessage());
            e.printStackTrace();
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
