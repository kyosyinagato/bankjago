package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.util.BankUtil;
import com.arayx.bankjago.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RegisterBankGUI implements Listener {

    private final Player player;
    private final BankJago plugin;

    public RegisterBankGUI(Player player, BankJago plugin) {
        this.player = player;
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        open();
    }

    public void open() {
        Inventory inv = Bukkit.createInventory(null, 27, plugin.getMessageManager().get("register.gui.title"));

        ItemStack daftar = new ItemBuilder(Material.EMERALD_BLOCK)
                .name(plugin.getMessageManager().get("register.gui.button.confirm"))
                .lore(plugin.getMessageManager().get("register.gui.lore.confirm"))
                .build();

        String formattedCash = BankUtil.formatRupiah((long) plugin.getEconomy().getBalance(player));

        ItemStack cash = new ItemBuilder(Material.GOLD_INGOT)
                .name(plugin.getMessageManager().get("register.gui.button.cash").replace("{cash}", formattedCash))
                .build();

        ItemStack close = new ItemBuilder(Material.BARRIER)
                .name(plugin.getMessageManager().get("register.gui.button.close"))
                .build();

        inv.setItem(11, daftar);
        inv.setItem(13, cash);
        inv.setItem(15, close);

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!p.equals(player)) return;
        if (!e.getView().getTitle().equals(plugin.getMessageManager().get("register.gui.title"))) return;

        e.setCancelled(true);

        switch (e.getRawSlot()) {
            case 11 -> {
                plugin.getChatListener().startRegisterBank(p);
                p.closeInventory();
            }
            case 15 -> p.closeInventory();
        }
    }
}
