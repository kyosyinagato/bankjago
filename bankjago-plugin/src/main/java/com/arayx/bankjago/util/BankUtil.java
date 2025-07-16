package com.arayx.bankjago.util;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.PlayerBankData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class BankUtil {

    /**
     * Register a new bank account for a player.
     * Initializes with default balance and random account number.
     */
    public static void registerBank(Player player) {
        BankJago plugin = BankJago.getInstance();
        UUID uuid = player.getUniqueId();

        PlayerBankData data = new PlayerBankData(
                uuid,
                plugin.generateRandomNoRek(),
                plugin.getConfig().getLong("bank.initial-balance", 25000),
                false
        );

        plugin.getPlayerDataMap().put(uuid, data);
        plugin.savePlayerBankData(uuid);
    }

    /**
     * Utility to quickly create a named ItemStack.
     */
    public static ItemStack item(Material material, String name) {
        return new ItemBuilder(material).name(name).build();
    }

    public static ItemStack item(Material material, String name, String lore) {
        return new ItemBuilder(material).name(name).lore(lore).build();
    }

    /**
     * Format number to Rupiah string.
     */
    public static String formatRupiah(long amount) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        return "Rp" + nf.format(amount);
    }

    /**
     * Get timestamp now.
     */
    public static String getTimestampNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(new Date());
    }
}
