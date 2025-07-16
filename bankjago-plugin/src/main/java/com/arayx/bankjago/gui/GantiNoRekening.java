package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import com.arayx.bankjago.listener.GantiNorekListener;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GantiNoRekening {

    private final BankJago plugin;
    private final MessageManager msg;

    public GantiNoRekening(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
        Bukkit.getPluginManager().registerEvents(new GantiNorekListener(plugin, msg), plugin);
    }

    /**
     * GUI utama: pilih Random / Custom / Back
     */
    public void openMain(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, msg.get("change_account.title"));

        inv.setItem(11, plugin.getItemBuilder(Material.NAME_TAG)
                .name(msg.get("change_account.option_random")).build());

        inv.setItem(15, plugin.getItemBuilder(Material.BOOK)
                .name(msg.get("change_account.option_custom")).build());

        inv.setItem(22, plugin.getItemBuilder(Material.ARROW)
                .name(msg.get("change_account.option_back")).build());

        player.openInventory(inv);
    }

    /**
     * GUI untuk memilih sumber biaya: Cash / Saldo Bank / Back
     */
    public void openSource(Player player, boolean custom) {
        Inventory inv = Bukkit.createInventory(null, 27, msg.get("change_account.choose_source"));

        inv.setItem(11, plugin.getItemBuilder(Material.EMERALD)
                .name(msg.get("change_account.choose_cash")).build());

        inv.setItem(15, plugin.getItemBuilder(Material.GOLD_INGOT)
                .name(msg.get("change_account.choose_bank")).build());

        inv.setItem(22, plugin.getItemBuilder(Material.ARROW)
                .name(msg.get("change_account.choose_back")).build());

        player.openInventory(inv);
    }

    /**
     * GUI konfirmasi YA/TIDAK
     */
    public void openConfirm(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, msg.get("change_account.confirm_title"));

        inv.setItem(11, plugin.getItemBuilder(Material.LIME_WOOL)
                .name(msg.get("change_account.confirm_yes")).build());

        inv.setItem(13, plugin.getItemBuilder(Material.PAPER)
                .name(msg.get("change_account.confirm_paper_name"))
                .lore(msg.get("change_account.confirm_lore")).build());

        inv.setItem(15, plugin.getItemBuilder(Material.RED_WOOL)
                .name(msg.get("change_account.confirm_no")).build());

        player.openInventory(inv);
    }

    /**
     * AnvilGUI untuk input custom rekening
     */
    public void openAnvilInput(Player player) {
        new AnvilGUI.Builder()
                .plugin(plugin)
                .title(msg.get("change_account.anvil_title"))
                .text("")
                .itemLeft(plugin.getItemBuilder(Material.PAPER)
                        .name("&aGanti")
                        .lore("&7Klik untuk mengganti nomor rekening").build())
                .itemRight(plugin.getItemBuilder(Material.ARROW)
                        .name("&cKembali")
                        .lore("&7Kembali ke menu sebelumnya").build())
                .onClick((slot, state) -> {
                    if (slot == AnvilGUI.Slot.OUTPUT) {
                        String input = state.getText().trim();

                        if (input.isEmpty() || !input.matches("\\d{6,12}")) {
                            String errorMsg = msg.get("error.invalid-rekening");
                            player.sendMessage(errorMsg);
                            return AnvilGUI.Response.text(errorMsg);
                        }

                        // Simpan custom input sementara
                        com.arayx.bankjago.util.GantiNorekManager.customInput.put(player.getUniqueId(), input);
                        openConfirm(player);
                        return AnvilGUI.Response.close();

                    } else if (slot == AnvilGUI.Slot.INPUT_RIGHT) {
                        openMain(player);
                        return AnvilGUI.Response.close();
                    }

                    return AnvilGUI.Response.text("");
                })
                .open(player);
    }
}
