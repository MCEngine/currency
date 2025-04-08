package io.github.mcengine.currency.app.page;

public class Listener {
    public static String getHtml() {
        return """
            <h3 style='color: #343a40;'>How Listeners Work in MCEngine Currency</h3>
            <p>Listeners in MCEngine Currency handle various events in Minecraft using Bukkit's event system. Here are two key listeners:</p>

            <h4>1. Player Join Listener</h4>
            <p>
                This listener ensures every player has an initialized currency account.
                When a player joins the server, the plugin checks if they exist in the database,
                and if not, creates a default entry for them.
            </p>
            <pre style='text-align: left; background-color: #f1f1f1; padding: 10px; border-radius: 8px;'>
@EventHandler
public void onPlayerJoin(PlayerJoinEvent event) {
    UUID playerUUID = event.getPlayer().getUniqueId();
    if (!currencyApi.checkIfPlayerExists(playerUUID)) {
        currencyApi.initPlayerData(playerUUID);
    }
}
            </pre>

            <h4>2. Cash Item Deposit Listener</h4>
            <p>
                This listener allows players to deposit money by right-clicking a special
                item created with HeadDB. The item contains metadata that defines the coin type and amount.
            </p>
            <pre style='text-align: left; background-color: #f1f1f1; padding: 10px; border-radius: 8px;'>
@EventHandler
public void onRightClick(PlayerInteractEvent event) {
    if (event.getHand() != EquipmentSlot.HAND) return;
    ItemStack item = player.getInventory().getItemInMainHand();
    if (!isValidCashItem(item)) return;

    double amount = getAmount(item);
    String coinType = getCoinType(item);

    currencyApi.addCoin(player.getUniqueId(), coinType, amount);
    player.sendMessage(ChatColor.GREEN + "Deposited " + amount + " " + coinType);
    item.setAmount(item.getAmount() - 1);
}
            </pre>

            <p>These listeners make the currency system dynamic and interactive within the game.</p>
        """;
    }
}
