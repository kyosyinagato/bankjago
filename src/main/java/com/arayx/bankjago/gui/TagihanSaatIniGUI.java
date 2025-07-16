package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TagihanSaatIniGUI {

    private final BankJago plugin;
    private final MessageManager msg;

    public TagihanSaatIniGUI(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, msg.get("pinjaman.tagihan_gui_title"));

        inv.setItem(13, plugin.getItemBuilder(Material.PAPER)
                .name(msg.get("pinjaman.detail_tagihan"))
                .lore(msg.getList("pinjaman.detail_tagihan_lore"))
                .build());

        inv.setItem(26, plugin.getItemBuilder(Material.ARROW)
                .name(msg.get("back"))
                .build());

        player.openInventory(inv);
    }
}
