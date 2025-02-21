package io.github.mcengine.common.currency.listener;

import io.github.mcengine.api.currency.MCEngineCurrencyApi;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class MCEngineCurrencyCommonListener implements Listener {
    private final MCEngineCurrencyApi currencyApi;

    public MCEngineCurrencyCommonListener(MCEngineCurrencyApi currencyApi) {
        this.currencyApi = currencyApi;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        // Check if the player exists in the database
        if (!currencyApi.checkIfPlayerExists(playerUUID)) {
            // Initialize player data with default values
            currencyApi.initPlayerData(playerUUID);
        }
    }
}
