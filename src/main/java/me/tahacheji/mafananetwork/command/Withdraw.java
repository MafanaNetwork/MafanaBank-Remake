package me.tahacheji.mafananetwork.command;

import de.tr7zw.nbtapi.NBTItem;
import me.tahacheji.mafananetwork.MafanaBank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Withdraw implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("Withdraw")) {
            Player player = (Player) sender;
            if (args.length == 0 || !isInteger(args[0]) || player.getItemInHand() == null) {
                player.sendMessage(ChatColor.RED + "MafanaBank ERROR: [Holding Card] /Withdraw [amount]");
                return true;
            }
            if (player.getItemInHand().getItemMeta() == null) {
                player.sendMessage(ChatColor.RED + "MafanaBank ERROR: [Holding Card] /Withdraw [amount]");
                return true;
            }
            if (!new NBTItem(player.getItemInHand()).getString("ItemKey").equalsIgnoreCase("CreditCard")) {
                player.sendMessage(ChatColor.RED + "MafanaBank ERROR: [Holding Card] /Withdraw [amount]");
                return true;
            }
            int x = Integer.parseInt(args[0]);
            if (new NBTItem(player.getItemInHand()).getUUID("CardHolder").toString().equalsIgnoreCase(player.getUniqueId().toString())) {
                MafanaBank.getInstance().getGamePlayerBank().withdrawFromAccount(Bukkit.getPlayer(new NBTItem(player.getItemInHand()).getUUID("CardHolder")), x);
                return true;
            } else {
                MafanaBank.getInstance().getGamePlayerBank().withdrawFromAccount(player, Bukkit.getPlayer(new NBTItem(player.getItemInHand()).getUUID("CardHolder")), x);
            }
        }
        return false;
    }

    public boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
