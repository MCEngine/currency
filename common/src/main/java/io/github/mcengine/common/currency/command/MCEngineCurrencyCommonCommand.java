package io.github.mcengine.common.currency.command;

import io.github.mcengine.api.MCEngineCurrencyApi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MCEngineCurrencyCommonCommand implements CommandExecutor {

    private final MCEngineCurrencyApi currencyApi;

    public MCEngineCurrencyCommonCommand(MCEngineCurrencyApi currencyApi) {
        this.currencyApi = currencyApi;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            String[] messages = {
                "Invalid command usage.",
                "Usage:",
                "/currency add <player> <amount>",
                "/currency check <coinType>",
                "/currency pay <player> <amount> <currencyType>"
            };
            
            for (String message : messages) {
                sender.sendMessage(ChatColor.RED + message);
            }
            return true;
        }

        Player senderPlayer = (Player) sender;

        String action = args[0].toLowerCase();

        switch (action) {
            case "add": {
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
            
                // Validate the coin type
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
            
                // Add coins to the player's account
                currencyApi.addCoin(targetUUID, coinType, amount);
            
                sender.sendMessage(ChatColor.GREEN + "Added " + amount + " " + coinType + " to " + targetPlayer.getName() + ".");
                targetPlayer.sendMessage(ChatColor.GREEN + "You have been given " + amount + " " + coinType + " by " + sender.getName() + ".");
                return true;
            }
            case "check": {
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
            case "pay": {
                if (args.length != 4) {
                    senderPlayer.sendMessage(ChatColor.RED + "Usage: /currency pay <player> <amount> <currencyType>");
                    return true;
                }

                if (!senderPlayer.hasPermission("mcengine.currency.pay")) {
                    senderPlayer.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                    return true;
                }

                String targetPlayerName = args[1];
                String amountStr = args[2];
                String currencyType = args[3].toLowerCase();

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

                double senderBalance = currencyApi.getCoin(senderUUID, currencyType);
                if (senderBalance < amount) {
                    senderPlayer.sendMessage(ChatColor.RED + "You do not have enough " + currencyType + ".");
                    return true;
                }

                currencyApi.minusCoin(senderUUID, currencyType, amount);
                currencyApi.addCoin(targetUUID, currencyType, amount);

                currencyApi.createTransaction(senderUUID, targetUUID, currencyType, "pay", amount, "");

                senderPlayer.sendMessage(ChatColor.GREEN + "You have sent " + amount + " " + currencyType + " to " + targetPlayer.getName() + ".");
                targetPlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + " " + currencyType + " from " + senderPlayer.getName() + ".");
                return true;
            }
            default:
                senderPlayer.sendMessage(ChatColor.RED + "Invalid action. Usage: /currency <check||pay> <currencyType||player> <amount> <currencyType>");
                return true;
        }
    }
}
