package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class HistoryGUI implements Listener {
    private final BankJago plugin;
    private final MessageManager msg;
    private final String title;

    public HistoryGUI(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
        this.title = msg.getString("gui.history.title");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player admin) {
        Inventory inv = Bukkit.createInventory(null, 9, title);
        inv.setItem(0, plugin.getItemBuilder(Material.GOLD_INGOT)
                .name(msg.getString("gui.history.cash-log")).build());
        inv.setItem(1, plugin.getItemBuilder(Material.DIAMOND)
                .name(msg.getString("gui.history.bank-balance-log")).build());
        inv.setItem(2, plugin.getItemBuilder(Material.NAME_TAG)
                .name(msg.getString("gui.history.bank-name-log")).build());
        inv.setItem(3, plugin.getItemBuilder(Material.REDSTONE_TORCH)
                .name(msg.getString("gui.history.unsuspend-log")).build());
        inv.setItem(8, plugin.getItemBuilder(Material.BARRIER)
                .name(msg.getString("gui.admin.close.name")).build());
        admin.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (!e.getView().getTitle().equals(title)) return;

        e.setCancelled(true);

        Player admin = player;

        switch (e.getRawSlot()) {
            case 0 -> plugin.getDatabaseManager().sendLastLogs(admin, "CASH", 20);
            case 1 -> plugin.getDatabaseManager().sendLastLogs(admin, "BANK_BALANCE", 20);
            case 2 -> plugin.getDatabaseManager().sendLastLogs(admin, "BANK_NAME", 20);
            case 3 -> plugin.getDatabaseManager().sendLastLogs(admin, "UNSUSPEND", 20);
            case 8 -> admin.closeInventory();
        }
    }
}
