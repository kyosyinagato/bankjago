package com.arayx.bankjago.listener;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import com.arayx.bankjago.PlayerBankData;
import com.arayx.bankjago.gui.KonfirmasiPinjamanGUI;
import com.arayx.bankjago.model.PlayerPinjamanData;
import com.arayx.bankjago.util.PinjamanManager;
import com.arayx.bankjago.gui.PinjamanGUI;
import com.arayx.bankjago.gui.TempoPinjamanGUI;
import com.arayx.bankjago.gui.NominalPinjamanGUI;
import com.arayx.bankjago.gui.AjukanPinjamanGUI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class PinjamanListener implements Listener {

    private final BankJago plugin;
    private final MessageManager msg;
    private final PinjamanManager pinjamanManager;
    private final String title;

    public PinjamanListener(BankJago plugin, MessageManager msg, PinjamanManager pinjamanManager) {
        this.plugin = plugin;
        this.msg = msg;
        this.pinjamanManager = pinjamanManager;
        this.title = msg.get("loan.gui.title");

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void openMainLoanMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, title);

        inv.setItem(11, plugin.getItemBuilder(Material.EMERALD).name(msg.get("loan.gui.button.apply")).build());
        inv.setItem(13, plugin.getItemBuilder(Material.BOOK).name(msg.get("loan.gui.button.current")).build());
        inv.setItem(15, plugin.getItemBuilder(Material.PAPER).name(msg.get("loan.gui.button.pay")).build());
        inv.setItem(22, plugin.getItemBuilder(Material.BARRIER).name(msg.get("loan.gui.button.close")).build());

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
            String viewTitle = e.getView().getTitle();
            UUID uuid = player.getUniqueId();

            e.setCancelled(true);

            if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) return;

            String name = e.getCurrentItem().getItemMeta().getDisplayName();

            // === Main Loan Menu ===
            if (viewTitle.equals(title)) {

                if (name.equals(msg.get("loan.gui.button.apply"))) {
                    player.closeInventory();
                    new PinjamanGUI(plugin, msg).open(player);
                    return;
                }

                if (name.equals(msg.get("loan.gui.button.current"))) {
                    player.closeInventory();
                    PlayerPinjamanData pinjaman = pinjamanManager.getPinjaman(uuid);
                    if (pinjaman == null) {
                        msg.sendMsg(player, "loan.no-current");
                        return;
                    }

                    player.sendMessage(msg.get("loan.current.header"));
                    player.sendMessage(msg.get("loan.current.jumlah").replace("{jumlah}", plugin.formatRupiah(pinjaman.getJumlah())));
                    player.sendMessage(msg.get("loan.current.cicilan").replace("{cicilan}", plugin.formatRupiah(pinjaman.getCicilanPerHari())));
                    player.sendMessage(msg.get("loan.current.tempo").replace("{tempo}", String.valueOf(pinjaman.getTempo())));
                    player.sendMessage(msg.get("loan.current.hari").replace("{hari}", String.valueOf(pinjaman.getHariKe())));
                    player.sendMessage(msg.get("loan.current.footer"));
                    return;
                }

                if (name.equals(msg.get("loan.gui.button.pay"))) {
                    player.closeInventory();
                    PlayerPinjamanData pinjaman = pinjamanManager.getPinjaman(uuid);
                    if (pinjaman == null) {
                        msg.sendMsg(player, "loan.no-current");
                        return;
                    }

                    PlayerBankData bankData = plugin.getPlayerDataMap().get(uuid);
                    long cicilan = pinjaman.getCicilanPerHari();

                    if (bankData.getBalance() >= cicilan) {
                        bankData.setBalance(bankData.getBalance() - cicilan);
                        pinjamanManager.deletePinjaman(uuid);
                        plugin.savePlayerBankData(uuid);
                        msg.sendMsg(player, "loan.paid-success");
                    } else {
                        msg.sendMsg(player, "loan.not-enough-balance");
                    }
                    return;
                }

                if (name.equals(msg.get("loan.gui.button.pay_all"))) {
                    player.closeInventory();

                    PlayerPinjamanData pinjaman = pinjamanManager.getPinjaman(uuid);
                    if (pinjaman == null) {
                        msg.sendMsg(player, "loan.no-current");
                        return;
                    }

                    PlayerBankData bankData = plugin.getPlayerDataMap().get(uuid);

                    long totalTagihan = pinjaman.getCicilanPerHari() * pinjaman.getTempo();

                    if (bankData.getBalance() >= totalTagihan) {
                        bankData.setBalance(bankData.getBalance() - totalTagihan);
                        plugin.savePlayerBankData(uuid);

                        pinjamanManager.deletePinjaman(uuid);

                        msg.sendMsg(player, "loan.all-paid-success");
                    } else {
                        msg.sendMsg(player, "loan.not-enough-balance");
                    }
                    return;
                }

                if (name.equals(msg.get("loan.gui.button.close"))) {
                    player.closeInventory();
                    return;
                }
            }

            // === Ajukan Pinjaman Menu ===
            if (viewTitle.equals(msg.get("pinjaman_ajukan_title"))) {

                if (name.equals(msg.get("pinjaman_ajukan_cash_name"))) {
                    player.closeInventory();
                    plugin.getPinjamanManager().setSumber(uuid, "cash");
                    new NominalPinjamanGUI(plugin, msg).open(player);
                    return;
                }

                if (name.equals(msg.get("pinjaman_ajukan_saldo_name"))) {
                    player.closeInventory();
                    plugin.getPinjamanManager().setSumber(uuid, "bank");
                    new NominalPinjamanGUI(plugin, msg).open(player);
                    return;
                }

                if (name.equals(msg.get("back_name"))) {
                    player.closeInventory();
                    new PinjamanGUI(plugin, msg).open(player);
                    return;
                }
            }

            // === Nominal Pinjaman Menu ===
            if (viewTitle.equals(msg.get("pinjaman_nominal_title"))) {

                long nominal = 0;

                if (name.equals(msg.get("pinjaman_nominal_250k"))) nominal = 250_000;
                if (name.equals(msg.get("pinjaman_nominal_500k"))) nominal = 500_000;
                if (name.equals(msg.get("pinjaman_nominal_1m"))) nominal = 1_000_000;
                if (name.equals(msg.get("pinjaman_nominal_2_5m"))) nominal = 2_500_000;

                if (nominal > 0) {
                    player.closeInventory();
                    plugin.getPinjamanManager().setNominal(uuid, nominal);
                    new TempoPinjamanGUI(plugin, msg).open(player);
                    return;
                }

                if (name.equals(msg.get("back_name"))) {
                    player.closeInventory();
                    new AjukanPinjamanGUI(plugin, msg).open(player);
                    return;
                }
            }

            // === Tempo Pinjaman Menu ===
            if (viewTitle.equals(msg.get("pinjaman_tempo_title"))) {

                if (name.equals(msg.get("pinjaman_tempo_7"))) {
                    handleTempoSelection(player, uuid, 7, 0.02);
                    return;
                }

                if (name.equals(msg.get("pinjaman_tempo_14"))) {
                    handleTempoSelection(player, uuid, 14, 0.035);
                    return;
                }

                if (name.equals(msg.get("pinjaman_tempo_30"))) {
                    handleTempoSelection(player, uuid, 30, 0.05);
                    return;
                }

                if (name.equals(msg.get("back_name"))) {
                    player.closeInventory();
                    return;
                }
            }

            // === Konfirmasi Pinjaman Menu ===
            if (viewTitle.equals(msg.get("pinjaman_konfirmasi_title"))) {

                if (name.equals(msg.get("confirm_yes_name"))) {
                    player.closeInventory();

                    PlayerPinjamanData pinjaman = pinjamanManager.getPinjaman(uuid);
                    if (pinjaman == null) {
                        msg.sendMsg(player, "loan.no-current");
                        return;
                    }

                    long nominal = pinjamanManager.getNominal(uuid);
                    String sumber = pinjamanManager.getSumber(uuid);

                    java.sql.Timestamp jatuhTempo = new java.sql.Timestamp(System.currentTimeMillis() + (24L * 60 * 60 * 1000)); // +24 jam
                    pinjaman.setJumlah(nominal);
                    pinjaman.setSumber(sumber);
                    pinjaman.setJatuhTempo(jatuhTempo);
                    pinjaman.setHariKe(1);

                    pinjamanManager.savePinjaman(pinjaman);

                    msg.sendMsg(player, "loan.confirmed");
                    return;
                }

                if (name.equals(msg.get("confirm_no_name"))) {
                    player.closeInventory();
                    msg.sendMsg(player, "loan.cancelled");
                    plugin.getMainBankGUI().open(player);
                    return;
                }
            }
    }


    private void handleTempoSelection(Player player, UUID uuid, int tempo, double bunga) {
        PlayerPinjamanData pinjaman = pinjamanManager.getPinjaman(uuid);
        if (pinjaman == null) {
            msg.sendMsg(player, "loan.no-current");
            player.closeInventory();
            return;
        }

        long nominal = pinjamanManager.getNominal(uuid);
        String sumber = pinjamanManager.getSumber(uuid);

        double totalBunga = nominal * bunga;
        long totalPinjaman = nominal + Math.round(totalBunga);
        long cicilanHarian = totalPinjaman / tempo;

        pinjaman.setTempo(tempo);
        pinjaman.setCicilanPerHari(cicilanHarian);
        pinjaman.setPenalty(bunga);

        pinjamanManager.savePinjaman(pinjaman);

        new KonfirmasiPinjamanGUI(plugin, msg).open(player, "Pinjaman Rp" + plugin.formatRupiah(totalPinjaman));
    }
}
