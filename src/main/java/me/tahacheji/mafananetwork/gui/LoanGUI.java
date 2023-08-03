package me.tahacheji.mafananetwork.gui;

import me.tahacheji.mafananetwork.util.NBTUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.swing.plaf.basic.BasicButtonUI;
import java.util.ArrayList;
import java.util.List;

public class LoanGUI {

    public Inventory getLoanGUI(int loanAmount, Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.GOLD + "MafanaBank Loan: " + player.getDisplayName());
        ItemStack greystainedglass = new ItemStack(Material.BROWN_STAINED_GLASS_PANE);
        ItemMeta newmeta = greystainedglass.getItemMeta();
        newmeta.setDisplayName(ChatColor.GRAY + " ");
        newmeta.setLore(new ArrayList<>());
        greystainedglass.setItemMeta(newmeta);
        greystainedglass = NBTUtil.setString(greystainedglass, "NONCLICKABLE", "");
        inventory.setItem(27, greystainedglass);
        inventory.setItem(28, greystainedglass);
        inventory.setItem(29, greystainedglass);
        inventory.setItem(30, greystainedglass);
        inventory.setItem(31, greystainedglass);
        inventory.setItem(32, greystainedglass);
        inventory.setItem(33, greystainedglass);
        inventory.setItem(34, greystainedglass);
        inventory.setItem(35, greystainedglass);
        inventory.setItem(36, greystainedglass);
        inventory.setItem(37, greystainedglass);
        inventory.setItem(38, greystainedglass);
        inventory.setItem(39, greystainedglass);
        inventory.setItem(41, greystainedglass);
        inventory.setItem(42, greystainedglass);
        inventory.setItem(43, greystainedglass);
        inventory.setItem(44, greystainedglass);
        inventory.setItem(45, greystainedglass);
        inventory.setItem(46, greystainedglass);
        inventory.setItem(47, greystainedglass);
        inventory.setItem(48, greystainedglass);
        inventory.setItem(50, greystainedglass);
        inventory.setItem(51, greystainedglass);
        inventory.setItem(52, greystainedglass);
        inventory.setItem(53, greystainedglass);

        inventory.setItem(40, getLoanInfo(loanAmount, player, null));

        inventory.setItem(49, null);
        return inventory;
    }

    public static ItemStack getLoanInfo(int loanAmount, Player player, List<ItemStack> itemStacks) {
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta meta = info.getItemMeta();
        List<String> infoLore = new ArrayList<>();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Loan Info");
        infoLore.add(ChatColor.GOLD + "Loan Amount: $" + loanAmount);
        if(itemStacks != null) {
            infoLore.add(ChatColor.GOLD + "Item Value Amount: ");
            for(ItemStack itemStack : itemStacks) {
                infoLore.add(ChatColor.DARK_GRAY + "-" + itemStack.getItemMeta().getDisplayName() + " $VALUE x " + itemStack.getAmount());
            }
            infoLore.add("");
            infoLore.add("The item value must > or = to the loan amount-");
            infoLore.add("so you can get your loan");
            meta.setLore(infoLore);
            info.setItemMeta(meta);
            info = NBTUtil.setInt(info, "LoanAmount", loanAmount);
            info = NBTUtil.setString(info, "NONCLICKABLE", "");
            info = NBTUtil.setString(info, "UUID", player.getUniqueId().toString());
            return info;
        }
        infoLore.add(ChatColor.GOLD + "Item Value Amount: $" + "0");
        infoLore.add("");
        infoLore.add("The item value must > or = to the loan amount-");
        infoLore.add("so you can get your loan");
        meta.setLore(infoLore);
        info.setItemMeta(meta);
        info = NBTUtil.setInt(info, "LoanAmount", loanAmount);
        info = NBTUtil.setString(info, "NONCLICKABLE", "");
        info = NBTUtil.setString(info, "UUID", player.getUniqueId().toString());
        return info;
    }

    public static ItemStack getAcceptIngot(int loanAmount, Player player, ItemStack... list) {
        ItemStack ingot = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = ingot.getItemMeta();
        List<String> infoLore = new ArrayList<>();
        meta.setDisplayName(ChatColor.GOLD + "Confirm Loan");
        infoLore.add(ChatColor.DARK_RED + "Info: " + "You will receive your items after you pay back the loan in 240 in GAME DAYS.");
        meta.setLore(infoLore);
        ingot.setItemMeta(meta);
        ingot = NBTUtil.setInt(ingot, "LoanAmount", loanAmount);
        ingot = NBTUtil.setString(ingot, "NONCLICKABLE", "");
        ingot = NBTUtil.setString(ingot, "UUID", player.getUniqueId().toString());
        return ingot;
    }




}
