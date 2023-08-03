package me.tahacheji.mafananetwork.command;

import me.tahacheji.mafananetwork.MafanaBank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Loan implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("Loan")) {
            Player player = (Player) sender;
            if(args.length == 0) {
                player.sendMessage(ChatColor.RED + "MafanaBank ERROR: /Loan [amount]");
                return true;
            }
            if(args[1].equalsIgnoreCase("Pay")) {
                int coins = Integer.parseInt(args[2]);
                if(coins > MafanaBank.getInstance().getGamePlayerCoins().getCoins(player)) {
                    player.sendMessage(ChatColor.RED + "MafanaBank ERROR: You do not have that amount of coins in your bag");
                    return true;
                }
                MafanaBank.getInstance().getGamePlayerBank().removeLoanAmount(player, coins);
                MafanaBank.getInstance().getGamePlayerCoins().removeCoins(player, coins);
                return true;
            } else {
                int coins = Integer.parseInt(args[1]);
                if (coins > (25000 * Integer.parseInt(MafanaBank.getInstance().getGamePlayerBank().getCreditScore(player)) / 1250)) {
                    player.sendMessage(ChatColor.GOLD + "MafanaBank: " + ChatColor.WHITE + "You can only get a loan lower then $" + 25000 * Integer.parseInt(MafanaBank.getInstance().getGamePlayerBank().getCreditScore(player)) / 1250);
                    return true;
                }
            }

        }
        return false;
    }
}
