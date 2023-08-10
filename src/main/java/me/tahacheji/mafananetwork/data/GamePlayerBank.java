package me.tahacheji.mafananetwork.data;

import de.tr7zw.nbtapi.NBTItem;
import me.TahaCheji.MainStash;
import me.TahaCheji.mysqlData.MySQL;
import me.TahaCheji.mysqlData.MysqlValue;
import me.TahaCheji.mysqlData.SQLGetter;
import me.tahacheji.mafananetwork.MafanaBank;
import me.tahacheji.mafananetwork.util.EncryptionUtil;
import me.tahacheji.mafananetwork.util.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GamePlayerBank extends MySQL {

    public GamePlayerBank() {
        super("localhost", "3306", "mafanation", "root", "");
    }

    public void addPlayer(OfflinePlayer player) {
        if (!sqlGetter.exists(player.getUniqueId())) {
            sqlGetter.setString(new MysqlValue("NAME", player.getUniqueId(), player.getName()));
            sqlGetter.setInt(new MysqlValue("BALANCE", player.getUniqueId(), 0));

            String formattedCardNumber = generateFormattedCreditCardNumber();
            int[] cvs = {new RandomUtil().getRandom(1, 9), new RandomUtil().getRandom(1, 9), new RandomUtil().getRandom(1, 9)};
            sqlGetter.setString(new MysqlValue("CREDIT_CARD_NUMBER", player.getUniqueId(), formattedCardNumber));
            sqlGetter.setString(new MysqlValue("CREDIT_CARD_CVS", player.getUniqueId(), "" + cvs[0] + cvs[1] + cvs[2]));
            sqlGetter.setString(new MysqlValue("CREDIT_SCORE", player.getUniqueId(), "650"));
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            String formattedDate = currentDate.format(formatter);
            sqlGetter.setString(new MysqlValue("CREDIT_CARD_DOC", player.getUniqueId(), formattedDate));

            sqlGetter.setInt(new MysqlValue("LOAN_AMOUNT", player.getUniqueId(), 0));
            sqlGetter.setInt(new MysqlValue("LOAN_DAYS", player.getUniqueId(), 0));
            sqlGetter.setString(new MysqlValue("COLLATERAL", player.getUniqueId(), ""));
            sqlGetter.setString(new MysqlValue("TRANSACTIONS", player.getUniqueId(), ""));

            sqlGetter.setUUID(new MysqlValue("UUID", player.getUniqueId(), player.getUniqueId()));
        }
    }

    private String generateFormattedCreditCardNumber() {
        int[] cardNumber = generateRandomNumbers(16, 0, 9);
        return formatCreditCardNumber(cardNumber);
    }

    private int[] generateRandomNumbers(int length, int min, int max) {
        int[] numbers = new int[length];
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            numbers[i] = random.nextInt(max - min + 1) + min;
        }
        return numbers;
    }

    private String formatCreditCardNumber(int[] cardNumber) {
        StringBuilder formattedNumber = new StringBuilder();
        for (int i = 0; i < cardNumber.length; i++) {
            formattedNumber.append(cardNumber[i]);
            if ((i + 1) % 4 == 0 && i != cardNumber.length - 1) {
                formattedNumber.append(" ");
            }
        }
        return formattedNumber.toString();
    }

    public void withdrawFromAccount(OfflinePlayer offlinePlayer, int i) {
        if (offlinePlayer.isOnline()) {
            if (offlinePlayer.getPlayer().getInventory().getItemInHand().getItemMeta() != null) {
                if (new NBTItem(offlinePlayer.getPlayer().getItemInHand()).hasTag("CardNumbers")) {
                    if (!new NBTItem(offlinePlayer.getPlayer().getItemInHand()).getString("CardNumbers").equalsIgnoreCase(getCreditCardNumber(offlinePlayer))) {
                        if (!new NBTItem(offlinePlayer.getPlayer().getItemInHand()).getString("CVS").equalsIgnoreCase(getCreditCardCVS(offlinePlayer))) {
                            offlinePlayer.getPlayer().sendMessage(ChatColor.RED + "MafanaBank: CARD_NOT_ACTIVE");
                            return;
                        }
                    }
                }
            }
        }
        if (i <= getBalanceAmount(offlinePlayer)) {
            setBalanceAmount(offlinePlayer, getBalanceAmount(offlinePlayer) - i);
            MafanaBank.getInstance().getGamePlayerCoins().addCoins(offlinePlayer, i);
            offlinePlayer.getPlayer().sendMessage(ChatColor.GOLD + "MafanaBank:" + ChatColor.WHITE + " Withdraw: -" + i);
            addTransaction(offlinePlayer, i, TransactionType.WITHDRAW);
        } else {
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                offlinePlayer.getPlayer().sendMessage(ChatColor.RED + "MafanaBank: ERROR_INSUFFICIENT_FUNDS");
            }
        }
    }

    public void depositIntoAccount(OfflinePlayer offlinePlayer, int i) {
        if (offlinePlayer.isOnline()) {
            if (offlinePlayer.getPlayer().getInventory().getItemInHand().getItemMeta() != null) {
                if (new NBTItem(offlinePlayer.getPlayer().getItemInHand()).hasTag("CardNumbers")) {
                    if (!new NBTItem(offlinePlayer.getPlayer().getItemInHand()).getString("CardNumbers").equalsIgnoreCase(getCreditCardNumber(offlinePlayer))) {
                        if (!new NBTItem(offlinePlayer.getPlayer().getItemInHand()).getString("CVS").equalsIgnoreCase(getCreditCardCVS(offlinePlayer))) {
                            offlinePlayer.getPlayer().sendMessage(ChatColor.RED + "MafanaBank: CARD_NOT_ACTIVE");
                            return;
                        }
                    }
                }
            }
        }
        if (MafanaBank.getInstance().getGamePlayerCoins().getCoins(offlinePlayer) >= i) {
            addToBalanceAmount(offlinePlayer, i);
            MafanaBank.getInstance().getGamePlayerCoins().removeCoins(offlinePlayer, i);
            if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                offlinePlayer.getPlayer().sendMessage(ChatColor.GOLD + "MafanaBank:" + ChatColor.WHITE + " Deposit: +" + i);
                addTransaction(offlinePlayer, i, TransactionType.DEPOSIT);
            }
        } else {
            offlinePlayer.getPlayer().sendMessage(ChatColor.RED + "MafanaBank: ERROR_INSUFFICIENT_FUNDS");
        }
    }

    public void withdrawFromAccount(OfflinePlayer cardHolder, OfflinePlayer bankOwner, int i) {
        if (cardHolder.isOnline()) {
            if (!new NBTItem(cardHolder.getPlayer().getItemInHand()).getString("CardNumbers").equalsIgnoreCase(getCreditCardNumber(bankOwner))) {
                if (!new NBTItem(cardHolder.getPlayer().getItemInHand()).getString("CVS").equalsIgnoreCase(getCreditCardCVS(bankOwner))) {
                    cardHolder.getPlayer().sendMessage(ChatColor.RED + "MafanaBank: CARD_NOT_ACTIVE");
                    return;
                }
            }
        }
        if (i <= getBalanceAmount(bankOwner)) {
            setBalanceAmount(bankOwner, getBalanceAmount(bankOwner) - i);
            MafanaBank.getInstance().getGamePlayerCoins().addCoins(cardHolder, i);
            bankOwner.getPlayer().sendMessage(ChatColor.GOLD + "MafanaBank: Withdraw: -" + i);
            addTransaction(cardHolder, bankOwner, i, TransactionType.WITHDRAW);
        } else {
            if (bankOwner.isOnline() && bankOwner.getPlayer() != null) {
                bankOwner.getPlayer().sendMessage(ChatColor.RED + "MafanaBank: ERROR_INSUFFICIENT_FUNDS");
            }
        }
    }

    public void depositIntoAccount(OfflinePlayer cardHolder, OfflinePlayer bankOwner, int i) {
        if (cardHolder.isOnline()) {
            if (!new NBTItem(cardHolder.getPlayer().getItemInHand()).getString("CardNumbers").equalsIgnoreCase(getCreditCardNumber(bankOwner))) {
                if (!new NBTItem(cardHolder.getPlayer().getItemInHand()).getString("CVS").equalsIgnoreCase(getCreditCardCVS(bankOwner))) {
                    cardHolder.getPlayer().sendMessage(ChatColor.RED + "MafanaBank: CARD_NOT_ACTIVE");
                    return;
                }
            }
        }
        if (MafanaBank.getInstance().getGamePlayerCoins().getCoins(cardHolder) >= i) {
            addToBalanceAmount(bankOwner, i);
            MafanaBank.getInstance().getGamePlayerCoins().removeCoins(cardHolder, i);
            if (bankOwner.isOnline() && bankOwner.getPlayer() != null) {
                bankOwner.getPlayer().sendMessage(ChatColor.GOLD + "MafanaBank: Deposit: +" + i);
                addTransaction(cardHolder, bankOwner, i, TransactionType.DEPOSIT);
            }
        } else {
            bankOwner.getPlayer().sendMessage(ChatColor.RED + "MafanaBank: ERROR_INSUFFICIENT_FUNDS");
        }
    }

    public Integer getBalanceAmount(OfflinePlayer player) {
        return sqlGetter.getInt(player.getUniqueId(), new MysqlValue("BALANCE"));
    }

    public void setBalanceAmount(OfflinePlayer player, int i) {
        sqlGetter.setInt(new MysqlValue("BALANCE", player.getUniqueId(), i));
    }

    public void addToBalanceAmount(OfflinePlayer player, int i) {
        sqlGetter.setInt(new MysqlValue("BALANCE", player.getUniqueId(), i + getBalanceAmount(player)));
    }

    public void removeFromBalanceAmount(OfflinePlayer player, int i) {
        sqlGetter.setInt(new MysqlValue("BALANCE", player.getUniqueId(), getBalanceAmount(player) - i));
    }

    public Integer getLoanAmount(OfflinePlayer offlinePlayer) {
        return sqlGetter.getInt(offlinePlayer.getUniqueId(), new MysqlValue("LOAN_AMOUNT"));
    }

    public void setLoanAmount(OfflinePlayer player, int i) {
        sqlGetter.setInt(new MysqlValue("LOAN_AMOUNT", player.getUniqueId(), i));
    }

    public void addLoanAmount(OfflinePlayer player, int i) {
        sqlGetter.setInt(new MysqlValue("LOAN_AMOUNT", player.getUniqueId(), i + getLoanAmount(player)));
    }

    public void removeLoanAmount(OfflinePlayer player, int i) {
        sqlGetter.setInt(new MysqlValue("LOAN_AMOUNT", player.getUniqueId(), getLoanAmount(player) - i));
        MafanaBank.getInstance().getGamePlayerBank().addTransaction(player, i, TransactionType.PAYLOAN);
        MafanaBank.getInstance().getGamePlayerCoins().removeCoins(player, i);
        if (getLoanAmount(player) <= 0) {
            setLoanAmount(player, 0);
            setLoanDays(player, 0);
            pickUpCollateral(player.getPlayer());
            addCreditScore(player, 150);
            player.getPlayer().sendMessage(ChatColor.GOLD + "MafanaBank: " + ChatColor.WHITE + "Your credit score has increased by 150 points");
            player.getPlayer().sendMessage(ChatColor.GOLD + "MafanaBank: " + ChatColor.WHITE + "Your items have been restored");
            addTransaction(player, 0, TransactionType.PAYLOAN);
        } else {
            player.getPlayer().sendMessage(ChatColor.GOLD + "MafanaBank: " + ChatColor.WHITE + "You have payed back $" + i + " coins you still have $" + getLoanAmount(player) + " left");
        }
    }

    public List<Player> getAllPlayersWithLoans() {
        List<Player> playersWithLoans = new ArrayList<>();
        try {
            List<UUID> uuids = sqlGetter.getAllUUID(new MysqlValue("UUID"));
            for (UUID uuid : uuids) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && getLoanAmount(player) != 0) {
                    playersWithLoans.add(player);
                }
            }
        } catch (SQLException ignored) {
        }
        return playersWithLoans;
    }


    public Integer getLoanDays(OfflinePlayer offlinePlayer) {
        return sqlGetter.getInt(offlinePlayer.getUniqueId(), new MysqlValue("LOAN_DAYS"));
    }

    public void setLoanDays(OfflinePlayer player, int i) {
        sqlGetter.setInt(new MysqlValue("LOAN_DAYS", player.getUniqueId(), i));
    }

    public void addLoanDays(OfflinePlayer player, int i) {
        sqlGetter.setInt(new MysqlValue("LOAN_DAYS", player.getUniqueId(), i + getLoanAmount(player)));
    }

    public List<ItemStack> getCollateral(OfflinePlayer player) {
        try {
            return Arrays.asList(new EncryptionUtil().decodeItems(sqlGetter.getString(player.getUniqueId(), new MysqlValue("COLLATERAL"))));
        } catch (Exception e) {
            return null;
        }
    }

    public void setCollateral(OfflinePlayer player, List<ItemStack> i) {
        ItemStack[] itemStacks = i.toArray(new ItemStack[0]);
        sqlGetter.setString(new MysqlValue("COLLATERAL", player.getUniqueId(), new EncryptionUtil().encodeItems(itemStacks)));
    }

    public String getRawCollateral(OfflinePlayer player) {
        return sqlGetter.getString(player.getUniqueId(), new MysqlValue("COLLATERAL"));
    }

    public void addCollateral(OfflinePlayer player, ItemStack itemStack) {
        List<ItemStack> i = getCollateral(player);
        if (i == null) {
            List<ItemStack> itemStacks = new ArrayList<>();
            itemStacks.add(itemStack);
            String s = new EncryptionUtil().encodeItems(itemStacks.toArray(new ItemStack[0]));
            sqlGetter.setString(new MysqlValue("COLLATERAL", player.getUniqueId(), s));
            return;
        }
        List<ItemStack> updatedItems = new ArrayList<>(i); // Create a new ArrayList from i
        updatedItems.add(itemStack);
        String s = new EncryptionUtil().encodeItems(updatedItems.toArray(new ItemStack[0]));
        sqlGetter.setString(new MysqlValue("COLLATERAL", player.getUniqueId(), s));
    }


    public void removeCollateral(OfflinePlayer player, ItemStack itemsToRemove) {
        List<ItemStack> items = new ArrayList<>(getCollateral(player));
        Iterator<ItemStack> iterator = items.iterator();
        while (iterator.hasNext()) {
            ItemStack itemStack = iterator.next();
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            if (itemsToRemove.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName())) {
                iterator.remove();
                break;
            }
        }
        setCollateral(player, items);
    }

    public void pickUpCollateral(Player player) {
        List<ItemStack> items = new ArrayList<>(getCollateral(player));
        if (items.isEmpty()) {
            player.sendMessage("No items in your collateral");
            return;
        }
        if (items != null) {
            List<ItemStack> itemsToRemove = new ArrayList<>();

            for (ItemStack itemStack : items) {
                player.getInventory().addItem(itemStack);
                player.sendMessage(ChatColor.GREEN + "+" + itemStack.getItemMeta().getDisplayName() + " x" + itemStack.getAmount());
                itemsToRemove.add(itemStack);
            }

            Iterator<ItemStack> iterator = items.iterator();
            while (iterator.hasNext()) {
                ItemStack itemStack = iterator.next();
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }
                for (ItemStack i : itemsToRemove) {
                    if (i == null || i.getType() == Material.AIR) {
                        continue;
                    }
                    if (i.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName())) {
                        iterator.remove();
                        break;
                    }
                }
            }

            setCollateral(player, items);
        }
    }

    private void removeCollateral(OfflinePlayer player, List<ItemStack> itemsToRemove) {
        List<ItemStack> items = new ArrayList<>(getCollateral(player));
        Iterator<ItemStack> iterator = items.iterator();
        while (iterator.hasNext()) {
            ItemStack itemStack = iterator.next();
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            for (ItemStack i : itemsToRemove) {
                if (i == null || i.getType() == Material.AIR) {
                    continue;
                }
                if (i.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName())) {
                    iterator.remove();
                    break;
                }
            }
        }

        setCollateral(player, items);
    }

    public void getNewCreditCard(OfflinePlayer player) {
        int[] cvs = {new RandomUtil().getRandom(1, 9), new RandomUtil().getRandom(1, 9), new RandomUtil().getRandom(1, 9)};
        sqlGetter.setString(new MysqlValue("CREDIT_CARD_NUMBER", player.getUniqueId(), generateFormattedCreditCardNumber()));
        sqlGetter.setString(new MysqlValue("CREDIT_CARD_CVS", player.getUniqueId(), "" + cvs[0] + cvs[1] + cvs[2]));
        sqlGetter.setString(new MysqlValue("CREDIT_SCORE", player.getUniqueId(), getCreditScore(player)));
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedDate = currentDate.format(formatter);
        sqlGetter.setString(new MysqlValue("CREDIT_CARD_DOC", player.getUniqueId(), formattedDate));
        addTransaction(player, 0, TransactionType.RESET);
        GamePlayerCreditCard.updateCard(new GamePlayerCreditCard(player, getCreditCardNumber(player), getCreditCardCVS(player), getCreditScore(player), getCreditCardDOC(player)));
    }

    public String getCreditCardNumber(OfflinePlayer offlinePlayer) {
        return sqlGetter.getString(offlinePlayer.getUniqueId(), new MysqlValue("CREDIT_CARD_NUMBER"));
    }

    public String getCreditCardCVS(OfflinePlayer offlinePlayer) {
        return sqlGetter.getString(offlinePlayer.getUniqueId(), new MysqlValue("CREDIT_CARD_CVS"));
    }

    public String getCreditScore(OfflinePlayer offlinePlayer) {
        return sqlGetter.getString(offlinePlayer.getUniqueId(), new MysqlValue("CREDIT_SCORE"));
    }

    public void addCreditScore(OfflinePlayer offlinePlayer, int i) {
        if (!(Integer.parseInt(getCreditScore(offlinePlayer)) >= 3500)) {
            int x = Integer.parseInt(getCreditScore(offlinePlayer)) + i;
            sqlGetter.setString(new MysqlValue("CREDIT_SCORE", offlinePlayer.getUniqueId(), "" + x));
            GamePlayerCreditCard.updateCard(new GamePlayerCreditCard(offlinePlayer, getCreditCardNumber(offlinePlayer), getCreditCardCVS(offlinePlayer), getCreditScore(offlinePlayer), getCreditCardDOC(offlinePlayer)));
        }
    }

    public void removeCreditScore(OfflinePlayer offlinePlayer, int i) {
        if (!(Integer.parseInt(getCreditScore(offlinePlayer)) <= 0)) {
            int x = Integer.parseInt(getCreditScore(offlinePlayer)) - i;
            sqlGetter.setString(new MysqlValue("CREDIT_SCORE", offlinePlayer.getUniqueId(), "" + x));
        }
    }

    public void setCreditScore(OfflinePlayer offlinePlayer, int i) {
        sqlGetter.setString(new MysqlValue("CREDIT_SCORE", offlinePlayer.getUniqueId(), "" + i));
    }

    public String getCreditCardDOC(OfflinePlayer offlinePlayer) {
        return sqlGetter.getString(offlinePlayer.getUniqueId(), new MysqlValue("CREDIT_CARD_DOC"));
    }

    public GamePlayerCreditCard getGamePlayerCreditCard(OfflinePlayer offlinePlayer) {
        return new GamePlayerCreditCard(offlinePlayer, getCreditCardNumber(offlinePlayer), getCreditCardCVS(offlinePlayer), getCreditScore(offlinePlayer), getCreditCardDOC(offlinePlayer));
    }

    public String getRawTransactions(OfflinePlayer player) {
        return sqlGetter.getString(player.getUniqueId(), new MysqlValue("TRANSACTIONS"));
    }

    public List<String> getTransactions(OfflinePlayer offlinePlayer) {
        String encryptedTransactions = getRawTransactions(offlinePlayer);

        if (encryptedTransactions == null || encryptedTransactions.isEmpty()) {
            return new ArrayList<>();
        }
        return new EncryptionUtil().decryptToList(encryptedTransactions);
    }

    public void setTransaction(OfflinePlayer offlinePlayer, List<String> s) {
        sqlGetter.setString(new MysqlValue("TRANSACTIONS", offlinePlayer.getUniqueId(), new EncryptionUtil().encryptList(s)));
    }

    public void addTransaction(OfflinePlayer offlinePlayer, int i, TransactionType type) {
        String s = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        if (Objects.equals(type.getLore(), "Deposit")) {
            s = "[" + dtf.format(now) + "]" + " Transaction: " + offlinePlayer.getName() + " " + type.getLore() + " +" + i;
        } else if (Objects.equals(type.getLore(), "Withdraw")) {
            s = "[" + dtf.format(now) + "]" + " Transaction: " + offlinePlayer.getName() + " " + type.getLore() + " -" + i;
        } else if (Objects.equals(type.getLore(), "PayedLoan")) {
            if (i == 0) {
                s = "[" + dtf.format(now) + "]" + " Transaction: " + offlinePlayer.getName() + " " + type.getLore() + " FINISHED_LOAN";
            } else {
                s = "[" + dtf.format(now) + "]" + " Transaction: " + offlinePlayer.getName() + " " + type.getLore() + " -" + i;
            }
        } else if (Objects.equals(type.getLore(), "Loan")) {
            s = "[" + dtf.format(now) + "]" + " Transaction: " + offlinePlayer.getName() + " " + type.getLore() + " +" + i;
        } else if (Objects.equals(type.getLore(), "Reset")) {
            s = "[" + dtf.format(now) + "]" + " Transaction: " + offlinePlayer.getName() + " " + type.getLore() + " RESET_CARD";
        }
        List<String> x = new ArrayList<>();
        if (getTransactions(offlinePlayer) != null) {
            x.addAll(getTransactions(offlinePlayer));
        }
        x.add(s);
        setTransaction(offlinePlayer, x);
    }

    public void addTransaction(OfflinePlayer cardHolder, OfflinePlayer bankOwner, int i, TransactionType type) {
        String s;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        if (Objects.equals(type.getLore(), "Deposit")) {
            s = "[" + dtf.format(now) + "]" + " Transaction: " + cardHolder.getName() + " " + type.getLore() + " +" + i;
        } else if (Objects.equals(type.getLore(), "Withdraw")) {
            s = "[" + dtf.format(now) + "]" + " Transaction: " + cardHolder.getName() + " " + type.getLore() + " -" + i;
        } else {
            s = "[" + dtf.format(now) + "]" + " Transaction: " + cardHolder.getName() + " " + type.getLore() + i;
        }
        List<String> x = new ArrayList<>();
        if (getTransactions(bankOwner) != null) {
            x.addAll(getTransactions(bankOwner));
        }
        x.add(s);
        setTransaction(bankOwner, x);
    }

    SQLGetter sqlGetter = new SQLGetter(this);

    @Override
    public void setSqlGetter(SQLGetter sqlGetter) {
        this.sqlGetter = sqlGetter;
    }

    @Override
    public void connect() {
        super.connect();
        if (this.isConnected()) sqlGetter.createTable("player_banking",
                new MysqlValue("NAME", ""),
                new MysqlValue("BALANCE", 0),
                new MysqlValue("CREDIT_CARD_NUMBER", ""),
                new MysqlValue("CREDIT_CARD_CVS", ""),
                new MysqlValue("CREDIT_SCORE", ""),
                new MysqlValue("CREDIT_CARD_DOC", ""),
                new MysqlValue("LOAN_AMOUNT", 0),
                new MysqlValue("LOAN_DAYS", 0),
                new MysqlValue("COLLATERAL", ""),
                new MysqlValue("TRANSACTIONS", ""));
    }
}
