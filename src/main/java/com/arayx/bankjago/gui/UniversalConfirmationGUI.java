package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class UniversalConfirmationGUI implements Listener {

    private final Player player;
    private final Runnable onConfirm;
    private final Runnable onCancel;
    private final String title;
    private final Inventory inv;

    public UniversalConfirmationGUI(Player player, Runnable onConfirm, Runnable onCancel,
                                    String title, String subtitle, BankJago plugin) {
        this.player = player;
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
        this.title = title;

        inv = Bukkit.createInventory(null, 27, title);

        inv.setItem(11, plugin.getItemBuilder(Material.LIME_WOOL).name("§aYA").build());
        inv.setItem(15, plugin.getItemBuilder(Material.RED_WOOL).name("§cTIDAK").build());

        player.openInventory(inv);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(this.title)) return;
        if (!e.getWhoClicked().equals(player)) return;
        e.setCancelled(true);

        if (e.getSlot() == 11) {
            player.closeInventory();
            onConfirm.run();
        } else if (e.getSlot() == 15) {
            player.closeInventory();
            onCancel.run();
        }
    }
}
