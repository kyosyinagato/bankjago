package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BayarTagihanKonfirmasiGUI {

    private final BankJago plugin;
    private final MessageManager msg;

    public BayarTagihanKonfirmasiGUI(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
    }

    public void open(Player player, String rekap) {
        Inventory inv = Bukkit.createInventory(null, 27, msg.get("pinjaman_bayar_konfirmasi_title"));

        inv.setItem(11, plugin.getItemBuilder(Material.GREEN_WOOL)
                .name(msg.get("confirm_yes_name"))
                .build());

        inv.setItem(13, plugin.getItemBuilder(Material.PAPER)
                .name(msg.get("rekap_name"))
                .lore(rekap.split("\n"))
                .build());

        inv.setItem(15, plugin.getItemBuilder(Material.RED_WOOL)
                .name(msg.get("confirm_no_name"))
                .build());

        player.openInventory(inv);
    }
}
