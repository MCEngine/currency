package io.github.mcengine.common.currency.listener;

import io.github.mcengine.api.currency.MCEngineCurrencyApi;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

/**
 * Listener class for handling currency-related events in MCEngine.
 * This listener checks if a player exists in the currency database upon joining
 * and initializes their data if necessary.
 */
public class MCEngineCurrencyCommonListener implements Listener {
    private final MCEngineCurrencyApi currencyApi;
    private static final NamespacedKey CASH_KEY = new NamespacedKey("mcengine", "cash");
    private static final NamespacedKey COIN_TYPE_KEY = new NamespacedKey("mcengine", "coinType");
    private static final NamespacedKey AMOUNT_KEY = new NamespacedKey("mcengine", "amount");

    /**
     * Constructs a new listener for handling player currency data.
     *
     * @param currencyApi The currency API instance used for database interactions.
     */
    public MCEngineCurrencyCommonListener(MCEngineCurrencyApi currencyApi) {
        this.currencyApi = currencyApi;
    }

    /**
     * Event handler for when a player joins the server.
     * Checks if the player exists in the currency database and initializes their data if not.
     *
     * @param event The PlayerJoinEvent triggered when a player joins.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        // Check if the player exists in the database
        if (!currencyApi.checkIfPlayerExists(playerUUID)) {
            // Initialize player data with default values
            currencyApi.initPlayerData(playerUUID);
        }
    }

    /**
     * Handles the right-click event for a player.
     * <p>
     * When a player right-clicks with an item in their main hand, this method checks if the item
     * is a valid cash item (containing specific persistent data keys), and if so, deposits the
     * currency into the player's account and removes one instance of the item.
     * </p>
     *
     * @param event the {@link PlayerInteractEvent} triggered when a player interacts (e.g., right-clicks)
     */
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        // Ignore off-hand interactions
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        // Check if the item is a valid cash item using its persistent data keys
        if (!meta.getPersistentDataContainer().has(CASH_KEY, PersistentDataType.BYTE)) return;

        String coinType = meta.getPersistentDataContainer().get(COIN_TYPE_KEY, PersistentDataType.STRING);
        Double amount = meta.getPersistentDataContainer().get(AMOUNT_KEY, PersistentDataType.DOUBLE);

        if (coinType == null || amount == null) return;

        // Deposit the money into the player's account
        currencyApi.addCoin(player.getUniqueId(), coinType, amount);
        player.sendMessage(ChatColor.GREEN + "Deposited " + amount + " " + coinType + " from cash item.");

        // Remove one instance of the item from the player's hand
        item.setAmount(item.getAmount() - 1);
    }
}
