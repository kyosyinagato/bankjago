package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class NominalPinjamanGUI {

    private final BankJago plugin;
    private final MessageManager msg;

    public NominalPinjamanGUI(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, msg.get("pinjaman_nominal_title"));

        inv.setItem(10, plugin.getItemBuilder(Material.PAPER)
                .name(msg.get("pinjaman_nominal_250k"))
                .build());

        inv.setItem(12, plugin.getItemBuilder(Material.PAPER)
                .name(msg.get("pinjaman_nominal_500k"))
                .build());

        inv.setItem(14, plugin.getItemBuilder(Material.PAPER)
                .name(msg.get("pinjaman_nominal_1m"))
                .build());

        inv.setItem(16, plugin.getItemBuilder(Material.PAPER)
                .name(msg.get("pinjaman_nominal_2_5m"))
                .build());

        inv.setItem(26, plugin.getItemBuilder(Material.ARROW)
                .name(msg.get("back_name"))
                .build());

        player.openInventory(inv);
    }
}
