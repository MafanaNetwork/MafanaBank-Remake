package me.tahacheji.mafananetwork.gui;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import me.TahaCheji.MafanaMarket;
import me.TahaCheji.data.market.ItemType;
import me.tahacheji.mafananetwork.MafanaBank;
import me.tahacheji.mafananetwork.command.Loan;
import me.tahacheji.mafananetwork.util.NBTUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BankGUI implements Listener {


    @EventHandler
    public void onClick(InventoryInteractEvent event) {
        event.setCancelled(true);
    }

    public void openBankMenu(Player player) {
        Gui gui = Gui.gui()
                .title(Component.text(ChatColor.GOLD + "MafanaBank Menu"))
                .rows(4) // Increase the number of rows to make the GUI bigger
                .create();

        // Add buttons to the main bank GUI

        // Add check balance button
        int balance = MafanaBank.getInstance().getGamePlayerBank().getBalanceAmount(player);
        gui.setItem(11, ItemBuilder.from(Material.GOLD_INGOT).name(Component.text(ChatColor.GOLD + "Check Balance"))
                .lore(Component.text(ChatColor.GREEN + "Your bank balance: " + balance + " coins")).asGuiItem(event -> {
                    player.sendMessage(ChatColor.GOLD + "MafanaBank: " + ChatColor.GREEN + "Your bank balance: " + balance + " coins");
                    player.closeInventory();
                }));

        // Add deposit button
        gui.setItem(13, ItemBuilder.from(Material.GREEN_DYE).name(Component.text(ChatColor.GOLD + "Deposit")).asGuiItem(event -> {
            player.closeInventory();
            openDepositMenu(player);
        }));

        // Add withdraw button
        gui.setItem(15, ItemBuilder.from(Material.RED_DYE).name(Component.text(ChatColor.GOLD + "Withdraw")).asGuiItem(event -> {
            player.closeInventory();
            openWithdrawMenu(player);
        }));
        if (MafanaBank.getInstance().getGamePlayerBank().getLoanAmount(player) <= 0) {
            gui.setItem(31, ItemBuilder.from(Material.IRON_INGOT).name(Component.text(ChatColor.DARK_GRAY + "Loan")).asGuiItem(event -> {
                player.closeInventory();
                openSearchSignLoan(player);
            }));
        } else {
            gui.setItem(31, ItemBuilder.from(Material.COAL).name(Component.text(ChatColor.DARK_GRAY + "Out Standing Loan")).lore
                    (Component.text(ChatColor.RED + "You already have a outstanding loan"), Component.text(ChatColor.GOLD + "Click Me")).asGuiItem(event -> {
                player.closeInventory();
                openLoanMenu(player);
            }));
        }

        ItemStack greystainedglass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta newmeta = greystainedglass.getItemMeta();
        newmeta.setDisplayName(ChatColor.GRAY + " ");
        newmeta.setLore(new ArrayList<>());
        greystainedglass.setItemMeta(newmeta);
        greystainedglass = NBTUtil.setString(greystainedglass, "NONCLICKABLE", "");

        gui.getFiller().fill(ItemBuilder.from(greystainedglass).asGuiItem());

        // Open the main bank GUI for the player
        gui.open(player);
        // Implement the rest of your methods for deposit, withdraw, and bank transactions

    }

    public void openLoanMenu(Player player) {
        Gui gui = Gui.gui()
                .title((Component.text(ChatColor.GOLD + "MafanaBank Loan Menu")))
                .rows(3)
                .create();

        // Add buttons to the deposit GUI

        // Add deposit all button
        gui.setItem(4, ItemBuilder.from(Material.GREEN_DYE).name(Component.text(ChatColor.GOLD + "Pay Loan All")).asGuiItem(event -> {
            MafanaBank.getInstance().getGamePlayerBank().removeLoanAmount(player, MafanaBank.getInstance().getGamePlayerCoins().getCoins(player));
            player.closeInventory();
        }));

        // Add deposit 100 button
        gui.setItem(10, ItemBuilder.from(Material.LIME_DYE).name(Component.text(ChatColor.GOLD + "Pay Loan 100")).asGuiItem(event -> {
            MafanaBank.getInstance().getGamePlayerBank().removeLoanAmount(player, 100);
            player.closeInventory();
        }));

        // Add deposit 500 button
        gui.setItem(12, ItemBuilder.from(Material.LIME_DYE).name(Component.text(ChatColor.GOLD + "Pay Loan 500")).asGuiItem(event -> {
            MafanaBank.getInstance().getGamePlayerBank().removeLoanAmount(player, 500);
            player.closeInventory();
        }));

        // Add deposit 1000 button
        gui.setItem(14, ItemBuilder.from(Material.LIME_DYE).name(Component.text(ChatColor.GOLD + "Pay Loan 1000")).asGuiItem(event -> {
            MafanaBank.getInstance().getGamePlayerBank().removeLoanAmount(player, 1000);
            player.closeInventory();
        }));

        // Add deposit custom amount button
        gui.setItem(16, ItemBuilder.from(Material.NAME_TAG).name(Component.text(ChatColor.GOLD + "Pay Loan Custom Amount")).asGuiItem(event -> {
            player.closeInventory();
            openSearchPayLoan(player);
        }));

        ItemStack greystainedglass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta newmeta = greystainedglass.getItemMeta();
        newmeta.setDisplayName(ChatColor.GRAY + " ");
        newmeta.setLore(new ArrayList<>());
        greystainedglass.setItemMeta(newmeta);
        greystainedglass = NBTUtil.setString(greystainedglass, "NONCLICKABLE", "");

        gui.getFiller().fill(ItemBuilder.from(greystainedglass).asGuiItem());

        // Open the deposit GUI for the player
        gui.open(player);
    }

    public void openDepositMenu(Player player) {
        Gui gui = Gui.gui()
                .title((Component.text(ChatColor.GOLD + "MafanaBank Deposit Menu")))
                .rows(3)
                .create();

        // Add buttons to the deposit GUI

        // Add deposit all button
        gui.setItem(4, ItemBuilder.from(Material.GREEN_DYE).name(Component.text(ChatColor.GOLD + "Deposit All")).asGuiItem(event -> {
            MafanaBank.getInstance().getGamePlayerBank().depositIntoAccount(player, MafanaBank.getInstance().getGamePlayerCoins().getCoins(player));
            player.closeInventory();
        }));

        // Add deposit 100 button
        gui.setItem(10, ItemBuilder.from(Material.LIME_DYE).name(Component.text(ChatColor.GOLD + "Deposit 100")).asGuiItem(event -> {
            MafanaBank.getInstance().getGamePlayerBank().depositIntoAccount(player, 100);
            player.closeInventory();
        }));

        // Add deposit 500 button
        gui.setItem(12, ItemBuilder.from(Material.LIME_DYE).name(Component.text(ChatColor.GOLD + "Deposit 500")).asGuiItem(event -> {
            MafanaBank.getInstance().getGamePlayerBank().depositIntoAccount(player, 500);
            player.closeInventory();
            player.closeInventory();
        }));

        // Add deposit 1000 button
        gui.setItem(14, ItemBuilder.from(Material.LIME_DYE).name(Component.text(ChatColor.GOLD + "Deposit 1000")).asGuiItem(event -> {
            MafanaBank.getInstance().getGamePlayerBank().depositIntoAccount(player, 1000);
            player.closeInventory();
            player.closeInventory();
        }));

        // Add deposit custom amount button
        gui.setItem(16, ItemBuilder.from(Material.NAME_TAG).name(Component.text(ChatColor.GOLD + "Deposit Custom Amount")).asGuiItem(event -> {
            player.closeInventory();
            openSearchSignDeposit(player);
        }));

        ItemStack greystainedglass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta newmeta = greystainedglass.getItemMeta();
        newmeta.setDisplayName(ChatColor.GRAY + " ");
        newmeta.setLore(new ArrayList<>());
        greystainedglass.setItemMeta(newmeta);
        greystainedglass = NBTUtil.setString(greystainedglass, "NONCLICKABLE", "");

        gui.getFiller().fill(ItemBuilder.from(greystainedglass).asGuiItem());

        // Open the deposit GUI for the player
        gui.open(player);
    }

    public void openWithdrawMenu(Player player) {
        Gui gui = Gui.gui()
                .title((Component.text(ChatColor.GOLD + "MafanaBank Withdraw Menu")))
                .rows(3)
                .create();

        // Add buttons to the deposit GUI

        // Add deposit all button
        gui.setItem(4, ItemBuilder.from(Material.RED_DYE).name(Component.text(ChatColor.GOLD + "Withdraw All")).asGuiItem(event -> {
            MafanaBank.getInstance().getGamePlayerBank().withdrawFromAccount(player, MafanaBank.getInstance().getGamePlayerCoins().getCoins(player));
            player.closeInventory();
        }));

        // Add deposit 100 button
        gui.setItem(10, ItemBuilder.from(Material.PINK_DYE).name(Component.text(ChatColor.GOLD + "Withdraw 100")).asGuiItem(event -> {
            MafanaBank.getInstance().getGamePlayerBank().withdrawFromAccount(player, 100);
            player.closeInventory();
        }));

        // Add deposit 500 button
        gui.setItem(12, ItemBuilder.from(Material.PINK_DYE).name(Component.text(ChatColor.GOLD + "Withdraw 500")).asGuiItem(event -> {
            MafanaBank.getInstance().getGamePlayerBank().withdrawFromAccount(player, 500);
            player.closeInventory();
            player.closeInventory();
        }));

        // Add deposit 1000 button
        gui.setItem(14, ItemBuilder.from(Material.PINK_DYE).name(Component.text(ChatColor.GOLD + "Withdraw 1000")).asGuiItem(event -> {
            MafanaBank.getInstance().getGamePlayerBank().withdrawFromAccount(player, 1000);
            player.closeInventory();
            player.closeInventory();
        }));

        // Add deposit custom amount button
        gui.setItem(16, ItemBuilder.from(Material.NAME_TAG).name(Component.text(ChatColor.GOLD + "Withdraw Custom Amount")).asGuiItem(event -> {
            player.closeInventory();
            openSearchSignWithdraw(player);
        }));

        ItemStack greystainedglass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta newmeta = greystainedglass.getItemMeta();
        newmeta.setDisplayName(ChatColor.GRAY + " ");
        newmeta.setLore(new ArrayList<>());
        greystainedglass.setItemMeta(newmeta);
        greystainedglass = NBTUtil.setString(greystainedglass, "NONCLICKABLE", "");

        gui.getFiller().fill(ItemBuilder.from(greystainedglass).asGuiItem());
        // Open the deposit GUI for the player
        gui.open(player);
    }

    public void openSearchSignDeposit(Player player) {
        SignGUI.builder()
                .setLines(null, "---------------", "Enter Amount", "MafanaBank") // set lines
                .setType(Material.DARK_OAK_SIGN) // set the sign type
                .setHandler((p, result) -> { // set the handler/listener (called when the player finishes editing)
                    String x = result.getLineWithoutColor(0);
                    if (x.isEmpty()) {
                        return List.of(SignGUIAction.run(() -> openBankMenu(player)));
                    }
                    return List.of(SignGUIAction.run(() -> MafanaBank.getInstance().getGamePlayerBank().depositIntoAccount(player, Integer.parseInt(x))));
                }).callHandlerSynchronously(MafanaMarket.getInstance()).build().open(player);
    }

    public void openSearchSignLoan(Player player) {
        SignGUI.builder()
                .setLines(null, "---------------", "Enter Amount", "MafanaBank") // set lines
                .setType(Material.DARK_OAK_SIGN) // set the sign type
                .setHandler((p, result) -> { // set the handler/listener (called when the player finishes editing)
                    String x = result.getLineWithoutColor(0);
                    if (x == null) {
                        return List.of(SignGUIAction.run(() -> openBankMenu(player)));
                    }
                    if (Integer.parseInt(x) > (25000 * Integer.parseInt(MafanaBank.getInstance().getGamePlayerBank().getCreditScore(player)) / 1250)) {
                        player.sendMessage(ChatColor.GOLD + "MafanaBank: " + ChatColor.WHITE + "You can only get a loan lower then $" + 25000 * Integer.parseInt(MafanaBank.getInstance().getGamePlayerBank().getCreditScore(player)) / 1250);
                        return List.of(SignGUIAction.run(() -> openBankMenu(player)));
                    }
                    return List.of(SignGUIAction.openInventory(MafanaBank.getInstance(), new LoanGUI().getLoanGUI(Integer.parseInt(x), player)));
                }).callHandlerSynchronously(MafanaMarket.getInstance()).build().open(player);
    }

    public void openSearchPayLoan(Player player) {
        SignGUI.builder()
                .setLines(null, "---------------", "Enter Amount", "MafanaBank") // set lines
                .setType(Material.DARK_OAK_SIGN) // set the sign type
                .setHandler((p, result) -> { // set the handler/listener (called when the player finishes editing)
                    String x = result.getLineWithoutColor(0);
                    if (x.isEmpty()) {
                        return List.of(SignGUIAction.run(() -> openBankMenu(player)));
                    }
                    if(Integer.parseInt(x) > MafanaBank.getInstance().getGamePlayerCoins().getCoins(player)) {
                        return List.of(SignGUIAction.run(() -> player.sendMessage(ChatColor.RED + "MafanaBank ERROR: You do not have that amount of coins in your bag")));
                    }
                    return List.of(SignGUIAction.run(() -> MafanaBank.getInstance().getGamePlayerBank().removeLoanAmount(player, Integer.parseInt(x))));
                }).callHandlerSynchronously(MafanaMarket.getInstance()).build().open(player);
    }

    public void openSearchSignWithdraw(Player player) {
        SignGUI.builder()
                .setLines(null, "---------------", "Enter Amount", "MafanaBank") // set lines
                .setType(Material.DARK_OAK_SIGN) // set the sign type
                .setHandler((p, result) -> { // set the handler/listener (called when the player finishes editing)
                    String x = result.getLineWithoutColor(0);
                    if (x.isEmpty()) {
                        return List.of(SignGUIAction.run(() -> openBankMenu(player)));
                    }
                    return List.of(SignGUIAction.run(() -> MafanaBank.getInstance().getGamePlayerBank().withdrawFromAccount(player, Integer.parseInt(x))));
                }).callHandlerSynchronously(MafanaMarket.getInstance()).build().open(player);
    }
}
