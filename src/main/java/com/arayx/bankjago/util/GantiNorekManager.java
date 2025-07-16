package com.arayx.bankjago.util;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import com.arayx.bankjago.PlayerBankData;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GantiNorekManager {

    public static final Map<UUID, String> modeCache = new HashMap<>();
    public static final Map<UUID, String> customInput = new HashMap<>();

    /**
     * Konfirmasi: jalankan logika perubahan no rekening
     *
     * @param plugin BankJago
     * @param msg    MessageManager
     * @param player Player
     * @param ya     true jika pemain klik YA
     */
    public static void konfirmasi(BankJago plugin, MessageManager msg, Player player, boolean ya) {
        UUID uuid = player.getUniqueId();

        if (!ya) {
            modeCache.remove(uuid);
            customInput.remove(uuid);
            return;
        }

        String mode = modeCache.getOrDefault(uuid, "");
        PlayerBankData data = plugin.getPlayerDataMap().get(uuid);
        if (data == null) {
            msg.sendMsg(player, "transfer.no-account");
            return;
        }

        int biaya = 250_000;

        if (mode.endsWith("_cash")) {
            double cash = plugin.getEconomy().getBalance(player);
            if (cash < biaya) {
                msg.sendMsg(player, "change_account.not_enough_cash");
                return;
            }
            plugin.getEconomy().withdrawPlayer(player, biaya);

        } else if (mode.endsWith("_bank")) {
            if (data.getBalance() < biaya) {
                msg.sendMsg(player, "change_account.not_enough_bank");
                return;
            }
            data.setBalance(data.getBalance() - biaya);

        } else {
            return;
        }

        String lama = data.getAccountNumber();
        String baru = mode.contains("custom")
                ? customInput.getOrDefault(uuid, generateRandom())
                : generateRandom();

        data.setAccountNumber(baru);
        plugin.savePlayerBankData(uuid);

        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        msg.sendMsg(player, "change_account.change-success");
        player.sendMessage(msg.get("change_account.result_header"));
        player.sendMessage(msg.get("change_account.result_old").replace("{old}", lama));
        player.sendMessage(msg.get("change_account.result_new").replace("{new}", baru));
        player.sendMessage(msg.get("change_account.result_balance")
                .replace("{balance}", plugin.formatRupiah(data.getBalance())));
        player.sendMessage(msg.get("change_account.result_footer"));

        modeCache.remove(uuid);
        customInput.remove(uuid);
    }

    /**
     * Generate random no rekening 8 digit
     *
     * @return String
     */
    private static String generateRandom() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(10_000_000, 100_000_000));
    }
}
