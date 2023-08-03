package me.tahacheji.mafananetwork.command;

import me.tahacheji.mafananetwork.MafanaBank;
import me.tahacheji.mafananetwork.data.GamePlayerCreditCard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Bank implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("Bank")) {
            Player player = (Player) sender;
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "MafanaBank ERROR: /Bank [[Balance {player_name}],Transactions,LoanDept,[Card {new}]]");
                return true;
            }
            if (args[0].equalsIgnoreCase("Balance")) {
                if (args.length == 2) {
                    Player x = Bukkit.getPlayer(args[1]);
                    if(x == null) {
                        player.sendMessage(ChatColor.RED + "MafanaBank: PLAYER_NOT_EXIST");
                        return true;
                    }
                    player.sendMessage(ChatColor.GOLD + "MafanaBank " + x.getDisplayName() + " Balance: " + ChatColor.WHITE + + me.tahacheji.mafananetwork.MafanaBank.getInstance().getGamePlayerBank().getBalanceAmount(x));
                    return true;
                }
                player.sendMessage(ChatColor.GOLD + "MafanaBank Balance: " + ChatColor.WHITE + + me.tahacheji.mafananetwork.MafanaBank.getInstance().getGamePlayerBank().getBalanceAmount(player));
                return true;
            }
            if (args[0].equalsIgnoreCase("Transactions")) {
                for (String string : me.tahacheji.mafananetwork.MafanaBank.getInstance().getGamePlayerBank().getTransactions(player)) {
                    player.sendMessage(ChatColor.YELLOW + "MafanaBank: " + string);
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("LoanDept")) {
                if (me.tahacheji.mafananetwork.MafanaBank.getInstance().getGamePlayerBank().getLoanAmount(player) != 0) {
                    player.sendMessage(ChatColor.GOLD + "MafanaBank Loan-Amount: " + ChatColor.WHITE + + me.tahacheji.mafananetwork.MafanaBank.getInstance().getGamePlayerBank().getLoanAmount(player)
                            + " [Loan-Days: " + me.tahacheji.mafananetwork.MafanaBank.getInstance().getGamePlayerBank().getLoanDays(player) + "]");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("Card")) {
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("New")) {
                        player.sendMessage(ChatColor.GOLD + "MafanaBank: " + ChatColor.WHITE + "Your card has been reset.");
                        MafanaBank.getInstance().getGamePlayerBank().getNewCreditCard(player);
                        player.getInventory().addItem(GamePlayerCreditCard.getCreditCard(player));
                        return true;
                    }
                }
                player.sendMessage(ChatColor.GOLD + "MafanaBank: You have been provided with a card.");
                player.getInventory().addItem(GamePlayerCreditCard.getCreditCard(player));
                return true;
            }
        }
        return false;
    }
}
