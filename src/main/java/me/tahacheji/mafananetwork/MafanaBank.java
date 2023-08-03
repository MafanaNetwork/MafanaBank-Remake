package me.tahacheji.mafananetwork;

import me.tahacheji.mafananetwork.command.*;
import me.tahacheji.mafananetwork.data.GamePlayerBank;
import me.tahacheji.mafananetwork.data.GamePlayerCoins;
import me.tahacheji.mafananetwork.event.PlayerClickInventoryLoanEvent;
import me.tahacheji.mafananetwork.event.PlayerJoinServerEvent;
import me.tahacheji.mafananetwork.event.PlayerNextLoanDayEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MafanaBank extends JavaPlugin {

    private static MafanaBank instance;
    private GamePlayerBank gamePlayerBank = new GamePlayerBank();
    private GamePlayerCoins gamePlayerCoins = new GamePlayerCoins();
    @Override
    public void onEnable() {
        instance = this;
        gamePlayerBank.connect();
        gamePlayerCoins.connect();
        getServer().getPluginManager().registerEvents(new PlayerJoinServerEvent(),  this);
        getServer().getPluginManager().registerEvents(new PlayerClickInventoryLoanEvent(),  this);
        getServer().getPluginManager().registerEvents(new PlayerNextLoanDayEvent(),  this);
        getCommand("Deposit").setExecutor(new Deposit());
        getCommand("Bank").setExecutor(new Bank());
        getCommand("Loan").setExecutor(new Loan());
        getCommand("MB").setExecutor(new MafanaBankAdmin());
        getCommand("Withdraw").setExecutor(new Withdraw());
    }

    @Override
    public void onDisable() {
        gamePlayerBank.disconnect();
        gamePlayerCoins.disconnect();
    }

    public GamePlayerBank getGamePlayerBank() {
        return gamePlayerBank;
    }

    public static MafanaBank getInstance() {
        return instance;
    }

    public GamePlayerCoins getGamePlayerCoins() {
        return gamePlayerCoins;
    }
}
