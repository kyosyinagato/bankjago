package com.arayx.bankjago.listener;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import com.arayx.bankjago.PlayerBankData;
import com.arayx.bankjago.gui.CustomAnvilGUI;
import com.arayx.bankjago.util.BankUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TransferChatListener implements Listener {

    private final BankJago plugin;
    private final MessageManager msg;

    private final Set<UUID> waitingPlayers = new HashSet<>();

    public TransferChatListener(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Mulai menunggu input nomor rekening tujuan dari player
     */
    public void startTransferInput(Player player) {
        waitingPlayers.add(player.getUniqueId());
        player.sendMessage(msg.get("prompt.enter-target-rekening"));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!waitingPlayers.contains(uuid)) return;

        e.setCancelled(true);
        waitingPlayers.remove(uuid);

        String norekTujuan = e.getMessage().trim();

        if (!accountNumberExists(norekTujuan)) {
            player.sendMessage(msg.get("error.invalid-rekening"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1.0f, 1.0f);
            return;
        }

        String alias = getAliasByAccountNumber(norekTujuan);

        Bukkit.getScheduler().runTask(plugin, () -> {
            new CustomAnvilGUI(plugin, player, norekTujuan, alias);
        });
    }

    private boolean accountNumberExists(String norek) {
        return plugin.getPlayerDataMap().values().stream()
                .anyMatch(d -> d.getAccountNumber().equalsIgnoreCase(norek));
    }

    private String getAliasByAccountNumber(String norek) {
        return plugin.getPlayerDataMap().values().stream()
                .filter(d -> d.getAccountNumber().equalsIgnoreCase(norek))
                .map(PlayerBankData::getAlias)
                .findFirst()
                .orElse("<unknown>");
    }

    /**
     * Proses transfer uang cash ke saldo bank penerima
     */
    public void transfer(Player pengirim, String norekTujuan, String aliasTujuan, int jumlah) {
        double saldoCash = plugin.getEconomy().getBalance(pengirim);
        if (saldoCash < jumlah) {
            pengirim.sendMessage(msg.get("error.insufficient-cash"));
            return;
        }

        plugin.getEconomy().withdrawPlayer(pengirim, jumlah);

        PlayerBankData dataPenerima = plugin.findByAccountNumber(norekTujuan);
        if (dataPenerima != null) {
            dataPenerima.setBalance(dataPenerima.getBalance() + jumlah);
            plugin.savePlayerBankData(dataPenerima.getUuid());
        }

        Player penerima = Bukkit.getPlayer(aliasTujuan);
        if (penerima != null && penerima.isOnline()) {
            String notif = msg.get("transfer.receive-notif")
                    .replace("{pengirim}", pengirim.getName())
                    .replace("{jumlah}", BankUtil.formatRupiah(jumlah));
            penerima.sendMessage(notif);
            penerima.playSound(penerima.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        }

        String successMsg = msg.get("transfer.tf-success")
                .replace("{jumlah}", BankUtil.formatRupiah(jumlah))
                .replace("{norek}", norekTujuan)
                .replace("{alias}", aliasTujuan)
                .replace("{waktu}", BankUtil.getTimestampNow());
        pengirim.sendMessage(successMsg);
        pengirim.playSound(pengirim.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);

    }
}
