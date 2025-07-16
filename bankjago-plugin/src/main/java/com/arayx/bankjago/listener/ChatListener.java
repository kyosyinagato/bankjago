package com.arayx.bankjago.listener;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import com.arayx.bankjago.PlayerBankData;
import com.arayx.bankjago.gui.PlayerListGUI;
import com.arayx.bankjago.gui.UniversalConfirmationGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import com.arayx.bankjago.util.BankUtil;

import java.util.*;

public class ChatListener implements Listener {
    private final BankJago plugin;
    private final MessageManager msg;

    private enum Stage {
        ENTER_AMOUNT,
        ENTER_ALIAS,
        ENTER_CODE
    }

    private enum Action {
        ADD_CASH,
        REDUCE_CASH,
        SET_CASH,
        ADD_BALANCE,
        REDUCE_BALANCE,
        PLAYER_DEPOSIT,
        PLAYER_WITHDRAW,
        CHANGE_ALIAS,
        UNSUSPEND_BANK,
        SUSPEND_BANK,
        REGISTER_BANK,
        DELETE_ALL
    }

    private static class Pending {
        Action action;
        UUID targetUuid;
        String alias;
        long amount;
        Stage stage;

        Pending(Action action) {
            this.action = action;
        }
    }

    private final Map<UUID, Pending> pending = new HashMap<>();

    public ChatListener(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // 🎯 Semua helper method yg dipanggil GUI
    public void startAliasInput(Player player) { startChangeAlias(player); }
    public void startPlayerDeposit(Player player) { startDeposit(player); }
    public void startPlayerWithdraw(Player player) { startWithdraw(player); }
    public void startDataDeletion(Player player) {
        UUID target = player.getUniqueId();
        startDeleteAll(player, target);
    }
    public void startBankBalanceOperation(Player player, UUID targetUuid) {
        startBalanceOperation(player, targetUuid, true);
    }
    public void startCashOperation(Player player, UUID targetUuid) {
        startCashOperation(player, targetUuid, true);
    }

    public void startBankNameChange(Player player, UUID targetUuid) {
        Pending p = new Pending(Action.CHANGE_ALIAS);
        p.targetUuid = targetUuid;
        p.stage = Stage.ENTER_ALIAS;
        pending.put(player.getUniqueId(), p);
        player.sendTitle(msg.get("prompt.enter-alias"), "", 10, 70, 20);
    }

    public void startRegisterBank(Player player) {
        Pending p = new Pending(Action.REGISTER_BANK);
        p.stage = Stage.ENTER_ALIAS;
        pending.put(player.getUniqueId(), p);
        player.sendTitle(msg.get("register.input-alias-title"), msg.get("register.input-alias-subtitle"), 10, 70, 20);
    }

    public void startCashOperation(Player player, UUID targetUuid, boolean add) {
        Pending p = new Pending(add ? Action.ADD_CASH : Action.REDUCE_CASH);
        p.targetUuid = targetUuid;
        p.stage = Stage.ENTER_AMOUNT;
        pending.put(player.getUniqueId(), p);
        player.sendTitle(msg.get("prompt.enter-cash-amount"), "", 10, 70, 20);
    }
    public void startSetCashOperation(Player player, UUID targetUuid) {
        Pending p = new Pending(Action.SET_CASH);
        p.targetUuid = targetUuid;
        p.stage = Stage.ENTER_AMOUNT;
        pending.put(player.getUniqueId(), p);
        player.sendTitle(msg.get("prompt.enter-cash-amount"), "", 10, 70, 20);
    }
    public void startBalanceOperation(Player player, UUID targetUuid, boolean add) {
        Pending p = new Pending(add ? Action.ADD_BALANCE : Action.REDUCE_BALANCE);
        p.targetUuid = targetUuid;
        p.stage = Stage.ENTER_AMOUNT;
        pending.put(player.getUniqueId(), p);
        player.sendTitle(msg.get("prompt.enter-balance-amount"), "", 10, 70, 20);
    }
    public void startSuspendOperation(Player admin, UUID targetUuid) {
        Pending p = new Pending(Action.SUSPEND_BANK);
        p.targetUuid = targetUuid;
        pending.put(admin.getUniqueId(), p);
        openConfirmGUI(admin, p);
    }

    public void startDeposit(Player player) {
        Pending p = new Pending(Action.PLAYER_DEPOSIT);
        p.stage = Stage.ENTER_AMOUNT;
        pending.put(player.getUniqueId(), p);
        player.sendTitle(msg.get("prompt.enter-deposit-amount"), "", 10, 70, 20);
    }

    public void startWithdraw(Player player) {
        Pending p = new Pending(Action.PLAYER_WITHDRAW);
        p.stage = Stage.ENTER_AMOUNT;
        pending.put(player.getUniqueId(), p);
        player.sendTitle(msg.get("prompt.enter-withdraw-amount"), "", 10, 70, 20);
    }

    public void startChangeAlias(Player player) {
        Pending p = new Pending(Action.CHANGE_ALIAS);
        p.stage = Stage.ENTER_ALIAS;
        pending.put(player.getUniqueId(), p);
        player.sendTitle(msg.get("prompt.enter-alias"), "", 10, 70, 20);
    }

    public void startUnsuspendOperation(Player admin, UUID targetUuid) {
        Pending p = new Pending(Action.UNSUSPEND_BANK);
        p.targetUuid = targetUuid;
        pending.put(admin.getUniqueId(), p);
        openConfirmGUI(admin, p);
    }

    public void startDeleteAll(Player player, UUID targetUuid) {
        Pending p = new Pending(Action.DELETE_ALL);
        p.targetUuid = targetUuid;
        p.stage = Stage.ENTER_CODE;
        pending.put(player.getUniqueId(), p);
        player.sendTitle(msg.get("prompt.enter-delete-code"), "", 10, 70, 20);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!pending.containsKey(uuid)) return;

        e.setCancelled(true);

        Pending p = pending.remove(uuid);
        String m = e.getMessage().trim();

        switch (p.stage) {
            case ENTER_AMOUNT -> {
                try {
                    p.amount = Long.parseLong(m);
                    openConfirmGUI(player, p);
                } catch (NumberFormatException ex) {
                    player.sendMessage(msg.get("error.not-a-number"));
                }
            }
            case ENTER_ALIAS -> {
                p.alias = m;
                openConfirmGUI(player, p);
            }
            case ENTER_CODE -> {
                if (m.equalsIgnoreCase("moonlight")) {
                    plugin.getPlayerDataMap().remove(p.targetUuid);
                    plugin.getDatabaseManager().deletePlayerData(p.targetUuid);
                    player.sendMessage(msg.get("admin.delete.del-success"));
                } else {
                    player.sendMessage(msg.get("admin.delete.wrong"));
                }
            }
        }
    }

    private void openConfirmGUI(Player player, Pending p) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            new UniversalConfirmationGUI(player,
                    () -> {
                        PlayerBankData data = plugin.getPlayerDataMap().computeIfAbsent(
                                (p.targetUuid != null ? p.targetUuid : player.getUniqueId()),
                                id -> new PlayerBankData(id, "AUTO", 0, false)
                        );
                        switch (p.action) {
                            case ADD_CASH -> {
                                data.setCash(data.getCash() + p.amount);
                                String formatted = BankUtil.formatRupiah(data.getCash());
                                player.sendMessage(msg.get("admin.cash.added-admin")
                                        .replace("{player}", player.getName())
                                        .replace("{amount}", BankUtil.formatRupiah(p.amount)));
                            }
                            case REDUCE_CASH -> {
                                data.setCash(data.getCash() - p.amount);
                                player.sendMessage(msg.get("admin.cash.reduced-admin")
                                        .replace("{player}", player.getName())
                                        .replace("{amount}", BankUtil.formatRupiah(p.amount)));
                            }
                            case ADD_BALANCE -> {
                                data.setBalance(data.getBalance() + p.amount);
                                player.sendMessage(msg.get("admin.balance.added-admin")
                                        .replace("{player}", player.getName())
                                        .replace("{amount}", BankUtil.formatRupiah(p.amount)));
                            }
                            case REDUCE_BALANCE -> {
                                data.setBalance(data.getBalance() - p.amount);
                                player.sendMessage(msg.get("admin.balance.reduced-admin")
                                        .replace("{player}", player.getName())
                                        .replace("{amount}", BankUtil.formatRupiah(p.amount)));
                            }
                            case PLAYER_DEPOSIT -> {
                                data.setCash(data.getCash() - p.amount);
                                data.setBalance(data.getBalance() + p.amount);
                                String saldoString = BankUtil.formatRupiah(data.getBalance());
                                player.sendMessage(msg.get("notification.deposit-success").replace("{saldosaatini}", saldoString));
                            }
                            case PLAYER_WITHDRAW -> {
                                data.setCash(data.getCash() + p.amount);
                                data.setBalance(data.getBalance() - p.amount);
                                String saldoString = BankUtil.formatRupiah(data.getBalance());
                                player.sendMessage(msg.get("notification.withdraw-success").replace("{saldosaatini}", saldoString));
                            }
                            case REGISTER_BANK -> {
                                double cash = plugin.getEconomy().getBalance(player);
                                if (cash < 50000) {
                                    player.sendMessage(msg.get("error.insufficient-cash"));
                                    return;
                                }

                                plugin.getEconomy().withdrawPlayer(player, 50000);
                                String noRek = plugin.generateRandomNoRek();

                                // Data yang sudah ada di variabel 'data' hasil computeIfAbsent
                                data.setAccountNumber(noRek);
                                data.setAlias(p.alias);
                                data.setBalance(25000);

                                plugin.getPlayerDataMap().put(player.getUniqueId(), data);
                                plugin.getDatabaseManager().getPlayerDataMap().put(player.getUniqueId(), data);
                                plugin.getDatabaseManager().savePlayerData(player.getUniqueId(), data);

                                String msgText = msg.get("register.reg-success")
                                        .replace("{alias}", p.alias)
                                        .replace("{norek}", noRek)
                                        .replace("{saldo}", plugin.formatRupiah(25000));
                                player.sendMessage(msgText);
                            }
                            case SUSPEND_BANK -> {
                                data.setSuspended(true);
                                plugin.savePlayerBankData(data.getUuid());
                                player.sendMessage(msg.get("notification.suspend"));
                                plugin.getPlayerListGUI().open(player, PlayerListGUI.ListType.SUSPEND_BANK);
                            }
                            case CHANGE_ALIAS -> {
                                String oldAlias = data.getAlias();
                                data.setAlias(p.alias);
                                plugin.savePlayerBankData(data.getUuid());
                                player.sendMessage(
                                        msg.get("notification.alias-changed")
                                                .replace("{old}", oldAlias != null ? oldAlias : "N/A")
                                                .replace("{new}", p.alias)
                                );
                            }
                            case UNSUSPEND_BANK -> {
                                data.setSuspended(false);
                                plugin.savePlayerBankData(data.getUuid());
                                player.sendMessage(msg.get("notification.unsuspend"));
                                plugin.getPlayerListGUI().open(player, PlayerListGUI.ListType.UNSUSPEND_BANK);
                            }
                        }
                        plugin.savePlayerBankData(data.getUuid());
                    },
                    () -> {
                        // onCancel, bisa lakukan apa saja kalau mau
                    },
                    msg.get("confirm.title"),
                    msg.get("confirm.subtitle"),
                    plugin
            );
        });
    }
}
