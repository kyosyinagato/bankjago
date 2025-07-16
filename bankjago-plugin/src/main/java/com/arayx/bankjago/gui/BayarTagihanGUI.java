package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import com.arayx.bankjago.model.PlayerPinjamanData;
import com.arayx.bankjago.util.PinjamanManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BayarTagihanGUI {

    private final BankJago plugin;
    private final MessageManager msg;
    private final PinjamanManager pinjamanManager;

    public BayarTagihanGUI(BankJago plugin, MessageManager msg, PinjamanManager pinjamanManager) {
        this.plugin = plugin;
        this.msg = msg;
        this.pinjamanManager = pinjamanManager;
    }

    public void open(Player player) {
        PlayerPinjamanData pinjaman = pinjamanManager.getPinjaman(player.getUniqueId());
        if (pinjaman == null) {
            msg.sendMsg(player, "loan.no-current");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 54, msg.get("loan.gui.tagihan_title"));

        long cicilan = pinjaman.getCicilanPerHari();
        int tempo = pinjaman.getTempo();
        double penalty = pinjaman.getPenalty();

        for (int i = 0; i < tempo && i < 45; i++) {
            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta meta = paper.getItemMeta();
            meta.setDisplayName(msg.get("loan.gui.tagihan_paper_name").replace("{hari}", String.valueOf(i + 1)));

            List<String> lore = new ArrayList<>();
            long totalCicilan = cicilan + Math.round(cicilan * penalty);
            lore.add(msg.get("loan.gui.tagihan_lore1").replace("{cicilan}", plugin.formatRupiah(totalCicilan)));
            lore.add(msg.get("loan.gui.tagihan_lore2").replace("{tempo}", String.valueOf(tempo)));
            meta.setLore(lore);

            paper.setItemMeta(meta);
            inv.setItem(i, paper);
        }

        // Tombol bayar semua
        inv.setItem(49, plugin.getItemBuilder(Material.EMERALD)
                .name(msg.get("loan.gui.button.pay_all"))
                .build());

        // Tombol kembali
        inv.setItem(53, plugin.getItemBuilder(Material.ARROW)
                .name(msg.get("back_name"))
                .build());

        player.openInventory(inv);
    }
}
