package com.arayx.bankjago.listener;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import com.arayx.bankjago.PlayerBankData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;

public class AdminCashInputListener implements Listener {

    private final BankJago plugin;
    private final MessageManager msg;

    public AdminCashInputListener(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void addCash(UUID targetUUID, long amount, Player admin) {
        PlayerBankData data = plugin.getPlayerDataMap().get(targetUUID);
        if (data == null) {
            data = new PlayerBankData(targetUUID, null, 0, false);
            plugin.getPlayerDataMap().put(targetUUID, data);
        }

        data.setCash(data.getCash() + amount);

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
        String targetName = target.getName() != null ? target.getName() : targetUUID.toString();

        if (target.isOnline()) {
            Player online = target.getPlayer();
            plugin.getEconomy().depositPlayer(online, amount);
            online.sendMessage(msg.get("admin.cash.added").replace("{amount}", String.valueOf(amount)));
        }

        admin.sendMessage(msg.get("admin.cash.added-admin")
                .replace("{player}", targetName)
                .replace("{amount}", String.valueOf(amount)));
    }

    public void reduceCash(UUID targetUUID, long amount, Player admin) {
        PlayerBankData data = plugin.getPlayerDataMap().get(targetUUID);
        if (data == null) {
            data = new PlayerBankData(targetUUID, null, 0, false);
            plugin.getPlayerDataMap().put(targetUUID, data);
        }

        data.setCash(Math.max(0, data.getCash() - amount));

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
        String targetName = target.getName() != null ? target.getName() : targetUUID.toString();

        if (target.isOnline()) {
            Player online = target.getPlayer();
            plugin.getEconomy().withdrawPlayer(online, amount);
            online.sendMessage(msg.get("admin.cash.reduced").replace("{amount}", String.valueOf(amount)));
        }

        admin.sendMessage(msg.get("admin.cash.reduced-admin")
                .replace("{player}", targetName)
                .replace("{amount}", String.valueOf(amount)));
    }

    public void startDeleteAllData(Player admin) {
        admin.sendMessage(msg.getString("admin.delete.prompt"));
    }

    public void deleteAllDataConfirmed(Player admin) {
        plugin.getDatabaseManager().deleteAllData();
        admin.sendMessage(msg.getString("admin.delete.del-success"));
    }
}
