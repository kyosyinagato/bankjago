package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.GameMode;

public class AdminBankGUI implements Listener {
    private final BankJago plugin;
    private final MessageManager msg;
    private final String title;

    public AdminBankGUI(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg    = msg;
        this.title  = msg.getString("gui.admin.title");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player admin) {
        Inventory inv = Bukkit.createInventory(null, 9, title);

        inv.setItem(0, plugin.getItemBuilder(Material.GOLD_INGOT)
                .name(msg.getString("gui.admin.edit-cash.name")).build());
        inv.setItem(1, plugin.getItemBuilder(Material.DIAMOND)
                .name(msg.getString("gui.admin.edit-bank-balance.name")).build());
        inv.setItem(2, plugin.getItemBuilder(Material.NAME_TAG)
                .name(msg.getString("gui.admin.edit-bank-name.name")).build());
        inv.setItem(3, plugin.getItemBuilder(Material.REDSTONE_TORCH)
                .name(msg.getString("gui.admin.unsuspend-bank.name")).build());
        inv.setItem(4, plugin.getItemBuilder(Material.OAK_SIGN)
                .name(msg.getString("gui.admin.history-log.name")).build());
        inv.setItem(5, plugin.getItemBuilder(Material.PAPER)
                .name(msg.getString("gui.admin.reload.name")).build());
        inv.setItem(6, plugin.getItemBuilder(Material.REDSTONE)
                .name(msg.getString("gui.admin.delete-data.name")).build());
        inv.setItem(7, plugin.getItemBuilder(Material.REDSTONE_BLOCK)
                .name(msg.getString("gui.admin.suspend-bank.name")).build());

        admin.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        String invTitle = ChatColor.stripColor(e.getView().getTitle());
        String expectedTitle = ChatColor.stripColor(title);

        if (!invTitle.equalsIgnoreCase(expectedTitle)) return;

        e.setCancelled(true);

        Player admin = player;

        switch (e.getRawSlot()) {
            case 0 -> plugin.getPlayerListGUI().open(admin, com.arayx.bankjago.gui.PlayerListGUI.ListType.EDIT_CASH);
            case 1 -> plugin.getPlayerListGUI().open(admin, com.arayx.bankjago.gui.PlayerListGUI.ListType.EDIT_BANK_BALANCE);
            case 2 -> plugin.getPlayerListGUI().open(admin, com.arayx.bankjago.gui.PlayerListGUI.ListType.EDIT_BANK_NAME);
            case 3 -> plugin.getPlayerListGUI().open(admin, com.arayx.bankjago.gui.PlayerListGUI.ListType.UNSUSPEND_BANK);
            case 4 -> plugin.getHistoryGUI().open(admin);
            case 5 -> {
                plugin.reloadConfig();
                msg.reload();
                admin.sendMessage(msg.getString("admin.reload.rel-success"));
            }
            case 6 -> plugin.getChatListener().startDataDeletion(admin);
            case 7 -> plugin.getPlayerListGUI().open(admin, com.arayx.bankjago.gui.PlayerListGUI.ListType.SUSPEND_BANK);
            case 8 -> admin.closeInventory();
        }
    }
}
