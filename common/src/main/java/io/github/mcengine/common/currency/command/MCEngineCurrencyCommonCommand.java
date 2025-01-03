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

        Player senderPlayer = (Player) sender;

        if (args.length != 4) {
            senderPlayer.sendMessage(ChatColor.RED + "Usage: /currency <pay> <player> <amount> <currencyType>");
            return true;
        }

        String action = args[0].toLowerCase();
        String targetPlayerName = args[1];
        String amountStr = args[2];
        String currencyType = args[3].toLowerCase();

        switch (action) {
            case "pay": {
                if (!senderPlayer.hasPermission("mcengine.currency.pay")) {
                    senderPlayer.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
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
                senderPlayer.sendMessage(ChatColor.RED + "Invalid action. Usage: /currency <pay> <player> <amount> <currencyType>");
                return true;
        }
    }
}
