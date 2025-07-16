package com.arayx.bankjago.listener;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import com.arayx.bankjago.util.GantiNorekManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GantiNorekListener implements Listener {

    private final BankJago plugin;
    private final MessageManager msg;

    public GantiNorekListener(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;

        Player player = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) return;

        String title = e.getView().getTitle();
        String name = e.getCurrentItem().getItemMeta().getDisplayName();

        e.setCancelled(true);

        if (title.equals(msg.get("change_account.title"))) {
            if (name.equals(msg.get("change_account.option_random"))) {
                GantiNorekManager.modeCache.put(player.getUniqueId(), "random");
                plugin.getGantiNoRekening().openSource(player, false);
            } else if (name.equals(msg.get("change_account.option_custom"))) {
                GantiNorekManager.modeCache.put(player.getUniqueId(), "custom");
                plugin.getGantiNoRekening().openSource(player, true);
            } else if (name.equals(msg.get("change_account.option_back"))) {
                plugin.getMainBankGUI().open(player);
            }

        } else if (title.equals(msg.get("change_account.choose_source"))) {
            String mode = GantiNorekManager.modeCache.get(player.getUniqueId());
            if (name.contains("Uang Cash")) {
                GantiNorekManager.modeCache.put(player.getUniqueId(), mode + "_cash");
                if (mode.contains("custom")) {
                    plugin.getGantiNoRekening().openAnvilInput(player);
                } else {
                    plugin.getGantiNoRekening().openConfirm(player);
                }
            } else if (name.contains("Saldo Bank")) {
                GantiNorekManager.modeCache.put(player.getUniqueId(), mode + "_bank");
                if (mode.contains("custom")) {
                    plugin.getGantiNoRekening().openAnvilInput(player);
                } else {
                    plugin.getGantiNoRekening().openConfirm(player);
                }
            } else {
                plugin.getGantiNoRekening().openMain(player);
            }

        } else if (title.equals(msg.get("change_account.confirm_title"))) {
            if (name.contains("YA")) {
                GantiNorekManager.konfirmasi(plugin, msg, player, true);
                plugin.getMainBankGUI().open(player);
            } else if (name.contains("TIDAK")) {
                GantiNorekManager.konfirmasi(plugin, msg, player, false);
                plugin.getGantiNoRekening().openMain(player);
            }
        }
    }
}
