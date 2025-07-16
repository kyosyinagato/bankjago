package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import com.arayx.bankjago.util.BankUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class TransferGUI implements Listener {

    private final BankJago plugin;
    private final MessageManager msg;
    private final String title;

    public TransferGUI(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
        this.title = msg.get("gui.transfer.title");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, title);
        inv.setItem(11, BankUtil.item(Material.PAPER, msg.get("gui.transfer.button.enter-account")));
        inv.setItem(15, BankUtil.item(Material.BARRIER, msg.get("gui.transfer.button.cancel")));
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(title)) return;
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) {
            return;
        }
        String name = e.getCurrentItem().getItemMeta().getDisplayName();

        if (name.equals(msg.get("gui.transfer.button.enter-account"))) {
            p.closeInventory();
            plugin.getTransferChatListener().startTransferInput(p);
        } else if (name.equals(msg.get("gui.transfer.button.cancel"))) {
            p.closeInventory();
            p.sendMessage(msg.get("gui.transfer.cancelled"));
        }
    }
}
