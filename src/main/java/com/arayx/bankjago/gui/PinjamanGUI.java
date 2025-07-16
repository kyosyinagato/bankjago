package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PinjamanGUI {

    private final BankJago plugin;
    private final MessageManager msg;

    public PinjamanGUI(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, msg.get("pinjamanmenu.gui_title"));

        inv.setItem(11, plugin.getItemBuilder(Material.GOLD_INGOT)
                .name(msg.get("pinjamanmenu.ajukan"))
                .lore(msg.getList("pinjamanmenu.ajukan_lore"))
                .build());

        inv.setItem(13, plugin.getItemBuilder(Material.PAPER)
                .name(msg.get("pinjamanmenu.tagihan_saat_ini"))
                .lore(msg.getList("pinjamanmenu.tagihan_saat_ini_lore"))
                .build());

        inv.setItem(15, plugin.getItemBuilder(Material.EMERALD_BLOCK)
                .name(msg.get("pinjamanmenu.bayar_semua"))
                .lore(msg.getList("pinjamanmenu.bayar_semua_lore"))
                .build());

        inv.setItem(26, plugin.getItemBuilder(Material.ARROW)
                .name(msg.get("back"))
                .build());

        player.openInventory(inv);
    }
}
