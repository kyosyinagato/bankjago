package com.arayx.bankjago.util;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import com.arayx.bankjago.PlayerBankData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TransferManager {

    public static long getTransferFee(BankJago plugin, long amount) {
        if (amount < 100_000) {
            return plugin.getConfig().getLong("transfer-fees.under-100000");
        } else if (amount < 1_000_000) {
            return plugin.getConfig().getLong("transfer-fees.under-1000000");
        } else if (amount < 10_000_000) {
            return plugin.getConfig().getLong("transfer-fees.under-10000000");
        } else {
            return plugin.getConfig().getLong("transfer-fees.under-100000000");
        }
    }

    public static boolean executeTransfer(BankJago plugin, Player sender, String norekTujuan, long nominal, long fee) {
        UUID senderUUID = sender.getUniqueId();

        Map<UUID, PlayerBankData> allData = plugin.getDatabaseManager().getPlayerDataMap();

        PlayerBankData senderData = allData.get(senderUUID);
        if (senderData == null) {
            MessageManager.sendMsg(sender, "transfer.no-account");
            return false;
        }

        PlayerBankData targetData = allData.values().stream()
                .filter(data -> data.getAccountNumber() != null && data.getAccountNumber().equalsIgnoreCase(norekTujuan))
                .findFirst()
                .orElse(null);

        if (targetData == null) {
            MessageManager.sendMsg(sender, "transfer.invalid-target");
            return false;
        }

        if (senderData.getBalance() < (nominal + fee)) {
            MessageManager.sendMsg(sender, "transfer.insufficient-funds");
            return false;
        }

        senderData.setBalance(senderData.getBalance() - (nominal + fee));
        targetData.setBalance(targetData.getBalance() + nominal);

        plugin.getDatabaseManager().savePlayerBankData(senderUUID);
        plugin.getDatabaseManager().savePlayerBankData(targetData.getUuid());

        return true;
    }

    /**
     * Utility untuk membuat ItemStack recap tujuan transfer (PAPER) untuk AnvilGUI.
     */
    public static ItemStack createRecapItem(String alias, String norek) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();

        meta.setDisplayName("§fInformasi Tujuan Rekening");
        List<String> lore = new ArrayList<>();
        lore.add("§7===============================");
        lore.add("§7No Rekening Tujuan: §b" + norek);
        lore.add("§7Atas Nama: §e" + alias);
        lore.add("§7===============================");
        lore.add("§7Masukkan nominal transfer");
        meta.setLore(lore);

        paper.setItemMeta(meta);
        return paper;
    }
}
