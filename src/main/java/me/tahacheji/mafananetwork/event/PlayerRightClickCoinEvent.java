package me.tahacheji.mafananetwork.event;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import me.tahacheji.mafananetwork.MafanaBank;
import me.tahacheji.mafananetwork.util.NBTUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerRightClickCoinEvent implements Listener {


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Check if the player right-clicked the item and it has the "MBC" NBT tag
        if (event.getAction().isRightClick() && itemInHand.getType() == Material.GOLD_NUGGET && NBTUtil.getString(itemInHand, "MBC") != null) {
            int coinsToClaim = Integer.valueOf(NBTUtil.getString(itemInHand, "MBC"));
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            MafanaBank.getInstance().getGamePlayerCoins().addCoins(player, coinsToClaim);
            player.sendMessage(ChatColor.GOLD + "MafanaBank: You claimed " + coinsToClaim + " coins!");
        }
    }
}
