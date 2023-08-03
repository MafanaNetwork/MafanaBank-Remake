package me.tahacheji.mafananetwork.event;

import me.TahaCheji.event.MSNextDayEvent;
import me.TahaCheji.event.MSNextSeasonEvent;
import me.tahacheji.mafananetwork.MafanaBank;
import me.tahacheji.mafananetwork.data.GamePlayerBank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;

public class PlayerNextLoanDayEvent implements Listener {

    @EventHandler
    public void nextDay(MSNextDayEvent event) {
        GamePlayerBank playerBank = MafanaBank.getInstance().getGamePlayerBank();
        for (Player player : playerBank.getAllPlayersWithLoans()) {
            if(playerBank.getLoanDays(player) <= 0) {
                //sell the collate items in MafanaMarket
                playerBank.setLoanAmount(player, 0);
                playerBank.setLoanDays(player, 0);
                playerBank.removeCreditScore(player, 500);
                player.sendMessage(ChatColor.GOLD + "MafanaBank: " + ChatColor.WHITE + "Your credit score has dropped by 250 points");
                player.sendMessage(ChatColor.GOLD + "MafanaBank: " + ChatColor.WHITE + "The items you put in as collateral have been listed in the market");
                continue;
            }
            double loanAmount = playerBank.getLoanAmount(player);
            double increaseAmount = loanAmount * 0.001; // 0.1% (1/10 of 1%) increase
            double newLoanAmount = loanAmount + increaseAmount;
            playerBank.setLoanAmount(player, (int) newLoanAmount);
            playerBank.setLoanDays(player, playerBank.getLoanDays(player) - 1);
            player.sendMessage(ChatColor.GOLD + "MafanaBank: " + ChatColor.WHITE + "Your loan has increased by " + increaseAmount + " coins. Your new loan amount is: " + newLoanAmount + " coins.");
        }
    }

    @EventHandler
    public void nextSeason(MSNextSeasonEvent event) {
        GamePlayerBank playerBank = MafanaBank.getInstance().getGamePlayerBank();
        for (Player player : playerBank.getAllPlayersWithLoans()) {
            playerBank.removeCreditScore(player, 50);
            player.sendMessage(ChatColor.GOLD + "MafanaBank: " + ChatColor.WHITE + "You still have a outstanding loan");
            player.sendMessage(ChatColor.GOLD + "MafanaBank: " + ChatColor.WHITE + "Your credit score has dropped by 50 points");
        }
    }

}
