package me.tahacheji.mafananetwork.data;

import de.tr7zw.nbtapi.NBTItem;
import me.tahacheji.mafananetwork.MafanaBank;
import me.tahacheji.mafananetwork.util.NBTUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GamePlayerCreditCard {

    public final OfflinePlayer player;
    public final String ccn;
    public final String cvs;

    public final String cs;
    public final String ccdoc;


    public GamePlayerCreditCard(OfflinePlayer player, String ccn, String cvs, String cs, String ccdoc) {
        this.player = player;
        this.ccn = ccn;
        this.cvs = cvs;
        this.cs = cs;
        this.ccdoc = ccdoc;
    }

    public static ItemStack getCreditCard(Player player) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "CreditCard");
        GamePlayerBank gamePlayerBank = MafanaBank.getInstance().getGamePlayerBank();
        lore.add(ChatColor.GOLD + "CardNumber: " + gamePlayerBank.getCreditCardNumber(player));
        lore.add(ChatColor.GOLD + "DateOfCreation: " + gamePlayerBank.getCreditCardDOC(player));
        lore.add("");
        lore.add(ChatColor.GOLD + "CVS: "+  gamePlayerBank.getCreditCardCVS(player));
        lore.add(ChatColor.GOLD + "CreditScore: " + gamePlayerBank.getCreditScore(player));
        lore.add("");
        lore.add(ChatColor.GOLD + "Active: " + ChatColor.GREEN + "TRUE");
        lore.add(ChatColor.DARK_PURPLE + "CardHolder: " + ChatColor.GOLD + player.getDisplayName());
        meta.setLore(lore);
        item.setItemMeta(meta);
        item = NBTUtil.setString(item, "CardNumbers", gamePlayerBank.getCreditCardNumber(player));
        item = NBTUtil.setString(item, "DateOfCreation", gamePlayerBank.getCreditCardDOC(player));
        item = NBTUtil.setString(item, "CVS", gamePlayerBank.getCreditCardCVS(player));
        item = NBTUtil.setString(item, "ItemKey", "CreditCard");
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setUUID("CardHolder", player.getUniqueId());
        item = nbtItem.getItem();
        return item;
    }

    public static void updateCard(GamePlayerCreditCard gamePlayerCreditCard) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            for(ItemStack itemStack : player.getInventory()) {
                if(itemStack == null) {
                    continue;
                }
                if(itemStack.getItemMeta() == null) {
                    continue;
                }
                if(new NBTItem(itemStack).getString("ItemKey").equalsIgnoreCase("CreditCard")) {
                    if(new NBTItem(itemStack).getString("CardNumbers").equalsIgnoreCase(gamePlayerCreditCard.getCcn())) {
                        if(new NBTItem(itemStack).getString("CVS").equalsIgnoreCase(gamePlayerCreditCard.getCvs())) {
                            ItemMeta meta = itemStack.getItemMeta();
                            ArrayList<String> lore = new ArrayList<>();
                            for (String string : meta.getLore()) {
                                lore.add(string);
                            }
                            lore.set(4, ChatColor.GOLD + "CreditScore: " + MafanaBank.getInstance().getGamePlayerBank().getCreditScore(gamePlayerCreditCard.getPlayer()));
                            if(MafanaBank.getInstance().getGamePlayerBank().getCreditCardNumber(gamePlayerCreditCard.getPlayer()) == new NBTItem(itemStack).getString("CardNumbers")) {
                                lore.add(ChatColor.GOLD + "Active: " + ChatColor.RED + "FALSE");
                            }
                            meta.setLore(lore);
                            itemStack.setItemMeta(meta);
                        }
                    } else {
                        ItemMeta meta = itemStack.getItemMeta();
                        ArrayList<String> lore = new ArrayList<>();
                        for (String string : meta.getLore()) {
                            lore.add(string);
                        }
                        lore.set(6, ChatColor.GOLD + "Active: " + ChatColor.RED + "FALSE");
                        meta.setLore(lore);
                        itemStack.setItemMeta(meta);
                    }
                }
            }
        }
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public String getCcn() {
        return ccn;
    }

    public String getCvs() {
        return cvs;
    }

    public String getCs() {
        return cs;
    }

    public String getCcdoc() {
        return ccdoc;
    }
}
