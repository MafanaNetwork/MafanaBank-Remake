package me.tahacheji.mafananetwork.event;

import de.tr7zw.nbtapi.NBTItem;
import me.TahaCheji.MafanaMarket;
import me.tahacheji.mafananetwork.MafanaBank;
import me.tahacheji.mafananetwork.command.Loan;
import me.tahacheji.mafananetwork.data.TransactionType;
import me.tahacheji.mafananetwork.gui.LoanGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerClickInventoryLoanEvent implements Listener {


    private final Map<Player, BukkitRunnable> tasks = new HashMap<>();

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if (!event.getView().getTitle().contains(ChatColor.GOLD + "MafanaBank Loan: ")) {
            return;
        }
        // Start the repeating task when the player opens the loan inventory
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                checkInventory(player);
            }
        };
        task.runTaskTimer(MafanaBank.getInstance(), 0L, 5L); // Replace 'yourPluginInstance' with your plugin instance
        tasks.put(player, task);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!event.getView().getTitle().contains(ChatColor.GOLD + "MafanaBank Loan: ")) {
            return;
        }
        ItemStack itemStack = event.getCurrentItem();
        if(itemStack == null) {
            return;
        }
        if(itemStack.getItemMeta() == null) {
            return;
        }
        if(new NBTItem(itemStack).hasTag("NONCLICKABLE")) {
            event.setCancelled(true);
            if(itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Confirm Loan")) {
                int i = (new NBTItem(itemStack).getInteger("LoanAmount") * 10) / 100;
                int x = i + new NBTItem(itemStack).getInteger("LoanAmount");
                MafanaBank.getInstance().getGamePlayerBank().setLoanAmount(player, x);
                MafanaBank.getInstance().getGamePlayerBank().setLoanDays(player, 240);
                MafanaBank.getInstance().getGamePlayerBank().setCollateral(player, getItems(player));
                MafanaBank.getInstance().getGamePlayerCoins().addCoins(player, new NBTItem(itemStack).getInteger("LoanAmount"));
                player.sendMessage(ChatColor.GOLD + "MafanaBank: " + ChatColor.WHITE + "Loan confirmed you have 240 ingame days to pay it back.");
                MafanaBank.getInstance().getGamePlayerBank().addTransaction(player, new NBTItem(itemStack).getInteger("LoanAmount"), TransactionType.LOAN);
                for (int slot = 0; slot <= 26; slot++) {
                    if (event.getInventory().getItem(slot) != null) {
                        event.getInventory().setItem(slot, null);
                    }
                }
                player.closeInventory();
            }
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getView().getTitle().contains(ChatColor.GOLD + "MafanaBank Loan: ")) {
            for (int slot = 0; slot <= 26; slot++) {
                if (event.getInventory().getItem(slot) != null) {
                    player.getInventory().addItem(event.getInventory().getItem(slot));
                }
            }
            BukkitRunnable task = tasks.remove(player);
            if (task != null) {
                task.cancel();
            }
        }
    }

    private void checkInventory(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        List<ItemStack> i = new ArrayList<>();
        int x = 0;
        for (int slot = 0; slot <= 26; slot++) {
            if (inventory.getItem(slot) != null) {
                i.add(inventory.getItem(slot));
                x = x + MafanaMarket.getInstance().getListingData().getAveragePrice(inventory.getItem(slot));
                //run threw all the items and add up the value here then update the Loan info itemstack and if it is good then set slot 40 to getAcceptIngot
            }
        }
        if(x >= new NBTItem(inventory.getItem(40)).getInteger("LoanAmount")) {
            inventory.setItem(49, LoanGUI.getAcceptIngot(new NBTItem(inventory.getItem(40)).getInteger("LoanAmount"), player));
        } else {
            inventory.setItem(49, null);
        }
        inventory.setItem(40, LoanGUI.getLoanInfo(new NBTItem(inventory.getItem(40)).getInteger("LoanAmount"), player, i));
    }
    private List<ItemStack> getItems(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        List<ItemStack> i = new ArrayList<>();
        for (int slot = 0; slot <= 26; slot++) {
            if (inventory.getItem(slot) != null) {
                i.add(inventory.getItem(slot));
            }
        }
        return i;
    }
}
