package me.tahacheji.mafananetwork.event;

import me.tahacheji.mafananetwork.MafanaBank;
import me.tahacheji.mafananetwork.data.GamePlayerCreditCard;
import me.tahacheji.mafananetwork.data.TransactionType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinServerEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        MafanaBank.getInstance().getGamePlayerCoins().addPlayer(event.getPlayer());
        MafanaBank.getInstance().getGamePlayerBank().addPlayer(event.getPlayer());
    }
}
