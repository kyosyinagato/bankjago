package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CustomAnvilGUI {

    private final BankJago plugin;
    private final Player player;
    private final String norekTujuan;
    private final String aliasTujuan;

    public CustomAnvilGUI(BankJago plugin, Player player, String norekTujuan, String aliasTujuan) {
        this.plugin = plugin;
        this.player = player;
        this.norekTujuan = norekTujuan;
        this.aliasTujuan = aliasTujuan;
        open();
    }

    private void open() {
        ItemStack slot0 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta0 = slot0.getItemMeta();
        meta0.setDisplayName(plugin.getMessageManager().get("gui.transfer.confirm-button"));
        meta0.setLore(List.of(plugin.getMessageManager().get("gui.transfer.confirm-lore")));
        slot0.setItemMeta(meta0);

        ItemStack slot1 = new ItemStack(Material.BARRIER);
        ItemMeta meta1 = slot1.getItemMeta();
        meta1.setDisplayName(plugin.getMessageManager().get("gui.transfer.close-button"));

        List<String> lore = List.of(
                plugin.getMessageManager().get("gui.transfer.info-lore-1"),
                plugin.getMessageManager().get("gui.transfer.info-lore-2"),
                plugin.getMessageManager().get("gui.transfer.info-lore-3").replace("{norek}", norekTujuan),
                plugin.getMessageManager().get("gui.transfer.info-lore-4").replace("{alias}", aliasTujuan),
                plugin.getMessageManager().get("gui.transfer.info-lore-5"),
                plugin.getMessageManager().get("gui.transfer.info-lore-6"),
                plugin.getMessageManager().get("gui.transfer.info-lore-7"),
                plugin.getMessageManager().get("gui.transfer.info-lore-8")
        );

        meta1.setLore(lore);
        slot1.setItemMeta(meta1);

        new AnvilGUI.Builder()
                .plugin(plugin)
                .title(plugin.getMessageManager().get("gui.transfer.title"))
                .text(" ")
                .itemLeft(slot0)
                .itemRight(slot1)
                .onClick((slot, state) -> {
                    if (slot == AnvilGUI.Slot.OUTPUT) {
                        String inputText = state.getText().trim();

                        if (inputText.equalsIgnoreCase("cancel")) {
                            player.sendMessage(plugin.getMessageManager().get("transfer.cancelled"));
                            return AnvilGUI.Response.close();
                        }

                        if (inputText.isEmpty() || !inputText.matches("\\d+")) {
                            String msg = plugin.getMessageManager().get("error.not-a-number");
                            player.sendMessage(msg);
                            return AnvilGUI.Response.text(msg);
                        }

                        int nominal = Integer.parseInt(inputText);
                        if (nominal < 10000) {
                            String msg = plugin.getMessageManager()
                                    .get("error.minimal-transfer")
                                    .replace("{amount}", "10000");
                            player.sendMessage(msg);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1.0f, 1.0f);
                            return AnvilGUI.Response.text(msg);
                        }

                        plugin.getTransferChatListener().transfer(player, norekTujuan, aliasTujuan, nominal);
                        return AnvilGUI.Response.close();
                    } else if (slot == AnvilGUI.Slot.INPUT_RIGHT) {
                        player.closeInventory();
                        return AnvilGUI.Response.close();
                    }

                    return AnvilGUI.Response.text("");
                })
                .open(player);
    }
}
