package me.tahacheji.mafananetwork.data;

import me.TahaCheji.mysqlData.MySQL;
import me.TahaCheji.mysqlData.MysqlValue;
import me.TahaCheji.mysqlData.SQLGetter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GamePlayerCoins extends MySQL {

    public GamePlayerCoins() {
        super("localhost", "3306", "mafanation", "root", "");
    }

    SQLGetter sqlGetter = new SQLGetter(this);

    @Override
    public void setSqlGetter(SQLGetter sqlGetter) {
        this.sqlGetter = sqlGetter;
    }

    public void addPlayer(Player player) {
        if(!sqlGetter.exists(player.getUniqueId())) {
            sqlGetter.setInt(new MysqlValue("COINS", player.getUniqueId(), 0));
            sqlGetter.setString(new MysqlValue("NAME", player.getUniqueId(), player.getName()));
            sqlGetter.setUUID(new MysqlValue("PLAYER", player.getUniqueId(), player.getUniqueId()));
        }
    }

    public void setCoins(Player player, int i) {
        sqlGetter.setInt(new MysqlValue("COINS", player.getUniqueId(), i));
        sqlGetter.setString(new MysqlValue("NAME", player.getUniqueId(), player.getName()));
        sqlGetter.setUUID(new MysqlValue("PLAYER", player.getUniqueId(), player.getUniqueId()));
    }

    public void addCoins(Player player, int i) {
        sqlGetter.setInt(new MysqlValue("COINS", player.getUniqueId(), i + getCoins(player)));
        sqlGetter.setString(new MysqlValue("NAME", player.getUniqueId(), player.getName()));
        sqlGetter.setUUID(new MysqlValue("PLAYER",player.getUniqueId(), player.getUniqueId()));
    }

    public void removeCoins(Player player, int i) {
        sqlGetter.setInt(new MysqlValue("COINS", player.getUniqueId(), getCoins(player) - i));
        sqlGetter.setString(new MysqlValue("NAME", player.getUniqueId(), player.getName()));
        sqlGetter.setUUID(new MysqlValue("PLAYER",player.getUniqueId(), player.getUniqueId()));
    }

    public int getCoins(OfflinePlayer player) {
        return getSqlGetter().getInt(player.getUniqueId(), new MysqlValue("COINS"));
    }

    public void setCoins(OfflinePlayer player, int i) {
        sqlGetter.setInt(new MysqlValue("COINS", player.getUniqueId(), i));
        sqlGetter.setString(new MysqlValue("NAME", player.getUniqueId(), player.getName()));
        sqlGetter.setUUID(new MysqlValue("PLAYER", player.getUniqueId(), player.getUniqueId()));
    }

    public void addCoins(OfflinePlayer player, int i) {
        sqlGetter.setInt(new MysqlValue("COINS", player.getUniqueId(), i + getCoins(player)));
        sqlGetter.setString(new MysqlValue("NAME", player.getUniqueId(), player.getName()));
        sqlGetter.setUUID(new MysqlValue("PLAYER",player.getUniqueId(), player.getUniqueId()));
    }

    public void removeCoins(OfflinePlayer player, int i) {
        sqlGetter.setInt(new MysqlValue("COINS", player.getUniqueId(), getCoins(player) - i));
        sqlGetter.setString(new MysqlValue("NAME", player.getUniqueId(), player.getName()));
        sqlGetter.setUUID(new MysqlValue("PLAYER",player.getUniqueId(), player.getUniqueId()));
    }

    public int getCoins(Player player) {
        return getSqlGetter().getInt(player.getUniqueId(), new MysqlValue("COINS"));
    }



    @Override
    public void connect() {
        super.connect();
        if(this.isConnected()) sqlGetter.createTable("players_coins", new MysqlValue("COINS", 1), new MysqlValue("NAME", ""),
                new MysqlValue("PLAYER", UUID.randomUUID()));
    }

    @Override
    public SQLGetter getSqlGetter() {
        return sqlGetter;
    }
}
