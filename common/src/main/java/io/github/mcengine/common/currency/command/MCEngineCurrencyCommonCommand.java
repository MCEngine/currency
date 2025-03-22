package io.github.mcengine.common.currency.command;

import io.github.mcengine.api.currency.MCEngineCurrencyApi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Handles currency-related commands for players in the game.
 */
public class MCEngineCurrencyCommonCommand implements CommandExecutor {

    private final MCEngineCurrencyApi currencyApi;

    /**
     * Constructs a new currency command handler.
     *
     * @param currencyApi The currency API instance for handling transactions.
     */
    public MCEngineCurrencyCommonCommand(MCEngineCurrencyApi currencyApi) {
        this.currencyApi = currencyApi;
    }

    /**
     * Processes commands related to the currency system.
     *
     * @param sender  The command sender.
     * @param command The command that was executed.
     * @param label   The command alias used.
     * @param args    The command arguments.
     * @return true if the command was executed successfully, false otherwise.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            sendUsageMessage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> handleAddCommand(sender, args);
            case "check" -> handleCheckCommand(player, args);
            case "pay" -> handlePayCommand(player, args);
            default -> sender.sendMessage(ChatColor.RED + "Invalid action. Usage: /currency <check||pay> <currencyType||player> <amount> <currencyType>");
        }
        return true;
    }

    private void sendUsageMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Invalid command usage.\nUsage:");
        sender.sendMessage(ChatColor.RED + "/currency add <player> <coinType> <amount>");
        sender.sendMessage(ChatColor.RED + "/currency check <coinType>");
        sender.sendMessage(ChatColor.RED + "/currency pay <player> <amount> <currencyType> <note>");
    }

    /**
     * Handles the "add" command to give currency to a player.
     *
     * @param sender The command sender.
     * @param args   The command arguments.
     * @return true if the command was executed successfully.
     */
    private boolean handleAddCommand(CommandSender sender, String[] args) {
        if (args.length != 4) {
            sender.sendMessage(ChatColor.RED + "Usage: /currency add <player> <coinType> <amount>");
            return true;
        }

        if (!sender.hasPermission("mcengine.currency.add")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        String targetPlayerName = args[1];
        String coinType = args[2].toLowerCase();
        String amountStr = args[3];

        if (!coinType.matches("coin|copper|silver|gold")) {
            sender.sendMessage(ChatColor.RED + "Invalid coin type: " + coinType + ". Valid types are: coin, copper, silver, gold.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "The amount must be a valid number.");
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "The amount must be greater than zero.");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayerExact(targetPlayerName);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        UUID targetUUID = targetPlayer.getUniqueId();
        currencyApi.addCoin(targetUUID, coinType, amount);

        sender.sendMessage(ChatColor.GREEN + "Added " + amount + " " + coinType + " to " + targetPlayer.getName() + ".");
        targetPlayer.sendMessage(ChatColor.GREEN + "You have been given " + amount + " " + coinType + " by " + sender.getName() + ".");
        return true;
    }

    /**
     * Handles the "check" command to check a player's currency balance.
     *
     * @param senderPlayer The player executing the command.
     * @param args         The command arguments.
     * @return true if the command was executed successfully.
     */
    private boolean handleCheckCommand(Player senderPlayer, String[] args) {
        if (args.length != 2) {
            senderPlayer.sendMessage(ChatColor.RED + "Usage: /currency check <coinType>");
            return true;
        }

        String coinType = args[1].toLowerCase();

        try {
            double balance = currencyApi.getCoin(senderPlayer.getUniqueId(), coinType);
            senderPlayer.sendMessage(ChatColor.GREEN + "You have " + balance + " " + coinType + ".");
        } catch (IllegalArgumentException e) {
            senderPlayer.sendMessage(ChatColor.RED + "Invalid coin type: " + coinType + ".");
        }
        return true;
    }

    /**
     * Handles the "pay" command to send currency to another player.
     *
     * @param senderPlayer The player executing the command.
     * @param args         The command arguments.
     * @return true if the command was executed successfully.
     */
    private boolean handlePayCommand(Player senderPlayer, String[] args) {
        if (args.length != 5) {
            senderPlayer.sendMessage(ChatColor.RED + "Usage: /currency pay <player> <amount> <currencyType> <note>");
            return true;
        }

        if (!senderPlayer.hasPermission("mcengine.currency.pay")) {
            senderPlayer.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        String targetPlayerName = args[1];
        String amountStr = args[2];
        String currencyType = args[3].toLowerCase();
        String note = args[4];

        if (!currencyType.matches("coin|copper|silver|gold")) {
            senderPlayer.sendMessage(ChatColor.RED + "Invalid currency type. Allowed types: coin, copper, silver, gold.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            senderPlayer.sendMessage(ChatColor.RED + "The amount must be a valid number.");
            return true;
        }

        if (amount <= 0) {
            senderPlayer.sendMessage(ChatColor.RED + "The amount must be greater than zero.");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayerExact(targetPlayerName);
        if (targetPlayer == null) {
            senderPlayer.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        UUID senderUUID = senderPlayer.getUniqueId();
        UUID targetUUID = targetPlayer.getUniqueId();

        if (senderUUID.equals(targetUUID)) {
            senderPlayer.sendMessage(ChatColor.RED + "You cannot pay yourself.");
            return true;
        }

        double senderBalance = currencyApi.getCoin(senderUUID, currencyType);
        if (senderBalance < amount) {
            senderPlayer.sendMessage(ChatColor.RED + "You do not have enough " + currencyType + ".");
            return true;
        }

        try {
            currencyApi.minusCoin(senderUUID, currencyType, amount);
            currencyApi.addCoin(targetUUID, currencyType, amount);
            currencyApi.createTransaction(senderUUID, targetUUID, currencyType, "pay", amount, note);

            senderPlayer.sendMessage(ChatColor.GREEN + "You have sent " + amount + " " + currencyType + " to " + targetPlayer.getName() + ". Note: " + note);
            targetPlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + " " + currencyType + " from " + senderPlayer.getName() + ". Note: " + note);
        } catch (Exception e) {
            senderPlayer.sendMessage(ChatColor.RED + "An error occurred during the transaction. Please try again later.");
            Bukkit.getLogger().warning("Transaction failed: " + e.getMessage());
        }
        return true;
    }
}
