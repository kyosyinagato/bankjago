package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TempoPinjamanGUI {

    private final BankJago plugin;
    private final MessageManager msg;

    public TempoPinjamanGUI(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, msg.get("pinjaman_tempo_title"));

        inv.setItem(10, plugin.getItemBuilder(Material.CLOCK)
                .name(msg.get("pinjaman_tempo_7"))
                .build());

        inv.setItem(13, plugin.getItemBuilder(Material.CLOCK)
                .name(msg.get("pinjaman_tempo_14"))
                .build());

        inv.setItem(16, plugin.getItemBuilder(Material.CLOCK)
                .name(msg.get("pinjaman_tempo_30"))
                .build());

        inv.setItem(26, plugin.getItemBuilder(Material.ARROW)
                .name(msg.get("back_name"))
                .build());

        player.openInventory(inv);
    }
}
