package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.GameMode;

public class BankStorageGUI implements Listener {
    private final BankJago plugin;
    private final MessageManager msg;
    private final String title;

    public BankStorageGUI(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
        this.title = msg.getString("gui.storage.title");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, title);
        inv.setItem(8, plugin.getItemBuilder(Material.BARRIER)
                .name(msg.getString("gui.admin.close.name")).build());
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (!e.getView().getTitle().equals(title)) return;

        e.setCancelled(true);

        if (e.getRawSlot() == 8) {
            player.closeInventory();
        }
    }
}
