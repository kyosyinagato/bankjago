package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import com.arayx.bankjago.PlayerBankData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import com.arayx.bankjago.listener.PinjamanListener;

public class MainBankGUI implements Listener {
    private final BankJago plugin;
    private final MessageManager msg;
    private final String title;

    public MainBankGUI(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg    = msg;
        this.title  = msg.get("gui.main.title");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, title);
        inv.setItem(1, com.arayx.bankjago.util.BankUtil.item(Material.PAPER,       msg.get("gui.main.button.detailbank"), msg.get("gui.main.button.detailbanklore")));
        inv.setItem(3, com.arayx.bankjago.util.BankUtil.item(Material.CHEST,       msg.get("gui.main.button.deposit")));
        inv.setItem(4, com.arayx.bankjago.util.BankUtil.item(Material.GOLD_INGOT,  msg.get("gui.main.button.withdraw")));
        inv.setItem(5, com.arayx.bankjago.util.BankUtil.item(Material.CHEST,       msg.get("gui.main.button.storage")));
        inv.setItem(6, com.arayx.bankjago.util.BankUtil.item(Material.ENDER_PEARL, msg.get("gui.main.button.transfer")));
        inv.setItem(7, com.arayx.bankjago.util.BankUtil.item(Material.BOOK,        msg.get("gui.main.button.history")));
        inv.setItem(8, com.arayx.bankjago.util.BankUtil.item(Material.NAME_TAG,    msg.get("gui.main.button.change-account")));
        inv.setItem(22, com.arayx.bankjago.util.BankUtil.item(Material.BARRIER,   msg.get("gui.main.button.close")));
        inv.setItem(9, com.arayx.bankjago.util.BankUtil.item(Material.EMERALD,     msg.get("gui.main.button.pinjaman")));

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

        if (name.equals(msg.get("gui.main.button.detailbank"))) {
            PlayerBankData data = plugin.getPlayerDataMap().get(p.getUniqueId());
            if (data == null) {
                msg.sendMsg(p, "gui.main.message.no-account");
                return;
            }

            p.closeInventory();
            p.sendMessage(msg.get("gui.main.message.detail.detailbank1"));
            p.sendMessage(msg.get("gui.main.message.detail.detailbank2"));
            p.sendMessage(msg.get("gui.main.message.detail.detail-name").replace("{alias}", data.getAlias()));
            p.sendMessage(msg.get("gui.main.message.detail.detail-norek").replace("{norek}", data.getAccountNumber()));
            p.sendMessage(msg.get("gui.main.message.detail.detail-saldo").replace("{saldo}", plugin.formatRupiah(data.getBalance())));
            p.sendMessage(msg.get("gui.main.message.detail.detailbank3"));
        }

        else if (name.equals(msg.get("gui.main.button.deposit"))) {
            p.closeInventory();
            p.sendMessage(msg.get("prompt.enter-deposit-amount"));
            plugin.getChatListener().startPlayerDeposit(p);
        }

        else if (name.equals(msg.get("gui.main.button.withdraw"))) {
            p.closeInventory();
            p.sendMessage(msg.get("prompt.enter-withdraw-amount"));
            plugin.getChatListener().startPlayerWithdraw(p);
        }

        else if (name.equals(msg.get("gui.main.button.transfer"))) {
            p.closeInventory();
            new TransferGUI(plugin, msg).open(p);
        }

        else if (name.equals(msg.get("gui.main.button.change-account"))) {
            p.closeInventory();
            plugin.getGantiNoRekening().openMain(p);
        }

        else if (name.equals(msg.get("gui.main.button.storage"))) {
            plugin.getStorageGUI().open(p);
        }

        else if (name.equals(msg.get("gui.main.button.history"))) {
            plugin.getHistoryGUI().open(p);
        }

        else if (name.equals(msg.get("gui.main.button.pinjaman"))) {
            p.closeInventory();
            plugin.getPinjamanListener().openMainLoanMenu(p);
        }

        else if (name.equals(msg.get("gui.main.button.close"))) {
            p.closeInventory();
        }
    }
}
