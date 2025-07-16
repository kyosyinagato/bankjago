package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class AjukanPinjamanGUI {

    private final BankJago plugin;
    private final MessageManager msg;

    public AjukanPinjamanGUI(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, msg.get("pinjaman_ajukan_title"));

        inv.setItem(11, plugin.getItemBuilder(Material.PAPER)
                .name(msg.get("pinjaman_ajukan_cash_name"))
                .lore(msg.getList("pinjaman_ajukan_cash_lore"))
                .build());

        inv.setItem(15, plugin.getItemBuilder(Material.PAPER)
                .name(msg.get("pinjaman_ajukan_saldo_name"))
                .lore(msg.getList("pinjaman_ajukan_saldo_lore"))
                .build());

        inv.setItem(26, plugin.getItemBuilder(Material.ARROW)
                .name(msg.get("back_name"))
                .build());

        player.openInventory(inv);
    }
}
