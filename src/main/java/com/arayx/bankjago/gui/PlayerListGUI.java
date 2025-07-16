package com.arayx.bankjago.gui;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import com.arayx.bankjago.PlayerBankData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.GameMode;

import java.util.*;

public class PlayerListGUI implements Listener {
    public enum ListType {EDIT_CASH, EDIT_BANK_BALANCE, EDIT_BANK_NAME, UNSUSPEND_BANK, SUSPEND_BANK}

    private final BankJago plugin;
    private final MessageManager msg;
    private final String title;

    private final Map<UUID, ListType> currentType = new HashMap<>();
    private final Map<UUID, Map<Integer, UUID>> slotToUuid = new HashMap<>();

    public PlayerListGUI(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
        this.title = msg.get("gui.players.title");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player admin, ListType type) {
        Inventory inv = Bukkit.createInventory(null, 54, title);
        Map<Integer, UUID> map = new HashMap<>();
        int slot = 0;

        if (type == ListType.EDIT_CASH) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (slot >= 54) break;
                UUID uuid = p.getUniqueId();
                PlayerBankData data = plugin.getPlayerDataMap().get(uuid);
                inv.setItem(slot, plugin.getItemBuilder(Material.PLAYER_HEAD)
                        .skullOwner(p.getName())
                        .name(msg.get("gui.players.entry").replace("%1%", p.getName()))
                        .lore(List.of(
                                msg.get("gui.players.lore.cash")
                                        .replace("{cash}", plugin.formatRupiah(data != null ? data.getCash() : 0)),
                                "",
                                msg.get("gui.players.lore.click-left"),
                                msg.get("gui.players.lore.click-right"),
                                msg.get("gui.players.lore.click-shift")
                        ))
                        .build());
                map.put(slot++, uuid);
            }
        } else {
            for (var entry : plugin.getPlayerDataMap().entrySet()) {
                if (slot >= 54) break;
                PlayerBankData data = entry.getValue();
                boolean show = switch (type) {
                    case EDIT_BANK_BALANCE, EDIT_BANK_NAME -> true;
                    case UNSUSPEND_BANK -> data.isSuspended();
                    default -> false;
                };
                if (!show) continue;
                inv.setItem(slot, plugin.getItemBuilder(Material.PLAYER_HEAD)
                        .skullOwner(Bukkit.getOfflinePlayer(entry.getKey()).getName())
                        .name(data.getAlias())
                        .lore(List.of(
                                msg.get("gui.players.lore.balance")
                                        .replace("{balance}", plugin.formatRupiah(data.getBalance())),
                                "",
                                msg.get("gui.players.lore.click-left"),
                                msg.get("gui.players.lore.click-right"),
                                msg.get("gui.players.lore.click-shift")
                        ))
                        .build());
                map.put(slot++, entry.getKey());
            }
        }

        currentType.put(admin.getUniqueId(), type);
        slotToUuid.put(admin.getUniqueId(), map);
        admin.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        // Kalau creative, biarin aja
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (!e.getView().getTitle().equals(title)) return;

        e.setCancelled(true);

        Player admin = player;
        ListType type = currentType.get(admin.getUniqueId());
        UUID target = slotToUuid.get(admin.getUniqueId()).get(e.getRawSlot());
        if (type == null || target == null) return;

        switch (type) {
            case EDIT_CASH -> {
                switch (e.getClick()) {
                    case LEFT:
                        plugin.getChatListener().startCashOperation(admin, target, true);
                        admin.closeInventory();
                        break;
                    case RIGHT:
                        plugin.getChatListener().startCashOperation(admin, target, false);
                        admin.closeInventory();
                        break;
                    case SHIFT_LEFT, SHIFT_RIGHT:
                        plugin.getChatListener().startSetCashOperation(admin, target);
                        admin.closeInventory();
                        break;
                }
            }
            case EDIT_BANK_BALANCE -> {
                switch (e.getClick()) {
                    case LEFT:
                        plugin.getChatListener().startBalanceOperation(admin, target, true);
                        admin.closeInventory();
                        break;
                    case RIGHT:
                        plugin.getChatListener().startBalanceOperation(admin, target, false);
                        admin.closeInventory();
                        break;
                    case SHIFT_LEFT, SHIFT_RIGHT:
                        plugin.getChatListener().startSuspendOperation(admin, target);
                        admin.closeInventory();
                        break;
                }
            }
            case EDIT_BANK_NAME -> {
                plugin.getChatListener().startBankNameChange(admin, target);
                admin.closeInventory();
            }
            case UNSUSPEND_BANK -> {
                plugin.getChatListener().startUnsuspendOperation(admin, target);
                admin.closeInventory();
            }
        }
    }
}
