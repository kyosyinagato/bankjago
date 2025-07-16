package com.arayx.bankjago;

import com.arayx.bankjago.command.Bayar;
import com.arayx.bankjago.listener.TransferChatListener;
import net.milkbowl.vault.economy.Economy;
import com.arayx.bankjago.api.BankJagoAPI;
import com.arayx.bankjago.command.BankAdminCommand;
import com.arayx.bankjago.command.BankCommand;
import com.arayx.bankjago.gui.*;
import com.arayx.bankjago.util.PinjamanManager;
import com.arayx.bankjago.listener.GantiNorekListener;
import com.arayx.bankjago.listener.AdminCashInputListener;
import com.arayx.bankjago.listener.ChatListener;
import com.arayx.bankjago.util.BankEconomy;
import com.arayx.bankjago.util.DatabaseManager;
import com.arayx.bankjago.util.ItemBuilder;
import com.arayx.bankjago.listener.PinjamanListener;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BankJago extends JavaPlugin {

    private static BankJago instance;

    private Economy economy;
    private DatabaseManager databaseManager;
    private MessageManager messageManager;
    private MainBankGUI mainBankGUI;
    private RegisterBankGUI registerBankGUI;
    private BankStorageGUI storageGUI;
    private AdminBankGUI adminBankGUI;
    private PlayerListGUI playerListGUI;
    private HistoryGUI historyGUI;
    private AdminCashInputListener inputListener;
    private ChatListener chatListener;
    private TransferChatListener transferChatListener;
    private final Map<UUID, PlayerBankData> playerDataMap = new HashMap<>();
    private GantiNoRekening gantiNoRekening;
    private PinjamanManager pinjamanManager;
    private PinjamanListener pinjamanListener;
    private boolean citizenIdEnabled;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResource("messages.yml", false);
        citizenIdEnabled = getConfig().getBoolean("citizenid-enabled", true);
        MessageManager.init(this);

        FileConfiguration cfg = getConfig();
        String type = cfg.getString("database.type", "sqlite").toLowerCase();
        String jdbcUrl;
        if (type.equals("mysql")) {
            String host = cfg.getString("database.mysql.host");
            int port = cfg.getInt("database.mysql.port");
            String db = cfg.getString("database.mysql.database");
            String user = cfg.getString("database.mysql.user");
            String pass = cfg.getString("database.mysql.pass", "");
            jdbcUrl = String.format(
                    "jdbc:mysql://%s:%d/%s?user=%s&password=%s",
                    host, port, db, user, pass
            );
        } else {
            File f = new File(getDataFolder(), "bankjago.sqlite");
            jdbcUrl = "jdbc:sqlite:" + f.getAbsolutePath();
        }

        databaseManager = new DatabaseManager(jdbcUrl);
        try {
            databaseManager.connect();
            databaseManager.createTables();
            databaseManager.loadAllPlayerData();
            playerDataMap.putAll(databaseManager.getPlayerDataMap());
        } catch (SQLException e) {
            getLogger().severe("Could not initialize database: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        pinjamanManager = new PinjamanManager(databaseManager);
        pinjamanManager.startReminderTask();
        pinjamanManager.startPenaltyTask();

        economy = new BankEconomy(this);
        getServer().getServicesManager().register(Economy.class, economy, this, ServicePriority.High);


        BankJagoAPIImpl apiImpl = new BankJagoAPIImpl(this);
        mainBankGUI = new MainBankGUI(this, messageManager);
        // registerBankGUI = new RegisterBankGUI(this, messageManager);
        storageGUI = new BankStorageGUI(this, messageManager);
        adminBankGUI = new AdminBankGUI(this, messageManager);
        playerListGUI = new PlayerListGUI(this, messageManager);
        historyGUI = new HistoryGUI(this, messageManager);
        inputListener = new AdminCashInputListener(this, messageManager);
        chatListener = new ChatListener(this, messageManager);
        transferChatListener = new TransferChatListener(this, messageManager);
        gantiNoRekening = new GantiNoRekening(this, messageManager);
        pinjamanListener = new PinjamanListener(this, messageManager, pinjamanManager);

        getServer().getPluginManager().registerEvents(pinjamanListener, this);

        getCommand("bank").setExecutor(new BankCommand(this, messageManager));
        getCommand("bankadmin").setExecutor(new BankAdminCommand(this, messageManager));
        getCommand("bayar").setExecutor(new Bayar(this));

        Bukkit.getServicesManager().register(
                BankJagoAPI.class,
                (BankJagoAPI) apiImpl,
                this,
                ServicePriority.Normal
        );
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            try {
                databaseManager.disconnect();
            } catch (SQLException e) {
                getLogger().warning("Error closing database: " + e.getMessage());
            }
        }
    }

    public static BankJago getInstance() {
        return instance;
    }

    public void openBankRegistration(Player player) {
        new UniversalConfirmationGUI(
                player,
                () -> chatListener.startRegisterBank(player),
                () -> messageManager.sendMsg(player, "registration_cancelled"),
                messageManager.get("confirm.title"),
                messageManager.get("confirm.subtitle"),
                this
        );
    }

    public DatabaseManager getDatabaseManager() {return databaseManager;}
    public MessageManager getMessageManager() {return messageManager;}
    public MainBankGUI getMainBankGUI() {return mainBankGUI;}
    public RegisterBankGUI getRegisterBankGUI() {return registerBankGUI;}
    public BankStorageGUI getStorageGUI() {return storageGUI;}
    public PlayerListGUI getPlayerListGUI() {return playerListGUI;}
    public HistoryGUI getHistoryGUI() {return historyGUI;}
    public AdminCashInputListener getAdminCashInputListener() {return inputListener;}
    public ChatListener getChatListener() {return chatListener;}
    public TransferChatListener getTransferChatListener() { return transferChatListener; }
    public AdminBankGUI getAdminBankGUI() {return adminBankGUI;}
    public Map<UUID, PlayerBankData> getPlayerDataMap() {return playerDataMap;}
    public GantiNoRekening getGantiNoRekening() { return gantiNoRekening; }
    public PinjamanManager getPinjamanManager() { return pinjamanManager; }
    public PinjamanListener getPinjamanListener() { return pinjamanListener; }
    public boolean isCitizenIdEnabled() {return citizenIdEnabled;}


    public void savePlayerBankData(UUID uuid) {
        PlayerBankData data = playerDataMap.get(uuid);
        if (data != null) {
            databaseManager.getPlayerDataMap().put(uuid, data); // sync to DB cache
            databaseManager.savePlayerData(uuid, data);
        }
    }

    public Economy getEconomy() {return economy;}
    public ItemBuilder getItemBuilder(Material type) {return new ItemBuilder(type);}

    public String formatRupiah(long amount) {
        return String.format("Rp%,d", amount).replace(',', '.');
    }

    public String generateRandomNoRek() {
        int random = ThreadLocalRandom.current().nextInt(100_000_000, 999_999_999);
        return String.valueOf(random);
    }

    public PlayerBankData findByAccountNumber(String accountNumber) {
        for (PlayerBankData data : playerDataMap.values()) {
            if (data.getAccountNumber() != null && data.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                return data;
            }
        }
        return null;
    }
    public PlayerBankData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public boolean transfer(UUID from, UUID to, double amount) {
        PlayerBankData sender = playerDataMap.get(from);
        PlayerBankData receiver = playerDataMap.get(to);

        if (sender == null || receiver == null) {
            return false;
        }

        if (sender.getBalance() < amount) {
            return false;
        }

        sender.withdraw((long) amount);
        receiver.deposit((long) amount);

        savePlayerBankData(from);
        savePlayerBankData(to);
        return true;
    }

}
