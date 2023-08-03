package me.tahacheji.mafananetwork.command;

import me.tahacheji.mafananetwork.MafanaBank;
import me.tahacheji.mafananetwork.gui.LoanGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MafanaBankAdmin implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("MB")) {
            Player player = (Player) sender;
            if(args[0].equalsIgnoreCase("coins")) {
                MafanaBank.getInstance().getGamePlayerCoins().addCoins((Player) sender, Integer.parseInt(args[1]));
            }
            if(args[0].equalsIgnoreCase("open")) {
                player.openInventory(new LoanGUI().getLoanGUI(100, player));
            }
            if(args[0].equalsIgnoreCase("set")) {
                MafanaBank.getInstance().getGamePlayerBank().setLoanAmount(player, 1000);
            }
        }
        return false;
    }
}
