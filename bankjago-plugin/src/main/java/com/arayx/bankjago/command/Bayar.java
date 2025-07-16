package com.arayx.bankjago.command;

import com.arayx.bankjago.BankJago;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Bayar implements CommandExecutor {

    private final BankJago plugin;

    public Bayar(BankJago plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Perintah ini hanya bisa digunakan oleh pemain.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 2) {
            int idTarget;
            double amount;
            try {
                idTarget = Integer.parseInt(args[0]);
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "ID atau jumlah tidak valid.");
                return true;
            }

            Plugin citizenIdPlugin = Bukkit.getPluginManager().getPlugin("CitizenID");
            if (citizenIdPlugin == null) {
                player.sendMessage(ChatColor.RED + "Plugin CitizenID tidak ditemukan.");
                return true;
            }

            Player target = null;
            for (Player p : Bukkit.getOnlinePlayers()) {
                try {
                    Class<?> apiClass = Class.forName("com.arayx.citizenid.CitizenID$CitizenIDAPI");
                    Object result = apiClass.getMethod("getCurrentID", Player.class).invoke(null, p);
                    int currentId = (int) result;
                    if (currentId == idTarget) {
                        target = p;
                        break;
                    }
                } catch (Exception ex) {
                    player.sendMessage(ChatColor.RED + "Gagal mengambil ID pemain.");
                    ex.printStackTrace();
                    return true;
                }
            }

            if (target == null) {
                player.sendMessage(ChatColor.RED + "Pemain dengan ID " + idTarget + " tidak ditemukan.");
                return true;
            }

            bayarCash(player, target, amount);

        } else if (args.length == 1) {
            double amount;
            try {
                amount = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Jumlah tidak valid.");
                return true;
            }

            Player target = null;
            double nearestDistance = Double.MAX_VALUE;

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.equals(player)) continue;

                double distance = p.getLocation().distance(player.getLocation());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    target = p;
                }
            }

            if (target == null || nearestDistance > 3.0) {
                player.sendMessage(ChatColor.RED + "Tidak ada pemain lain di dekatmu (maksimal 3 block).");
                return true;
            }

            bayarCash(player, target, amount);

        } else {
            player.sendMessage(ChatColor.YELLOW + "Usage: /bayar <id> <jumlah> atau /bayar <jumlah>");
        }

        return true;
    }

    private void bayarCash(Player sender, Player receiver, double amount) {
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Jumlah tidak boleh nol atau negatif.");
            return;
        }

        double senderCash = plugin.getEconomy().getBalance(sender);
        if (senderCash < amount) {
            sender.sendMessage(ChatColor.RED + "Uang cash kamu tidak cukup untuk membayar " + plugin.formatRupiah((long) amount));
            return;
        }

        plugin.getEconomy().withdrawPlayer(sender, amount);
        plugin.getEconomy().depositPlayer(receiver, amount);

        sender.sendMessage(ChatColor.GREEN + "✅ Kamu membayar " + plugin.formatRupiah((long) amount) + " ke " + receiver.getName());
        receiver.sendMessage(ChatColor.GREEN + "✅ Kamu menerima " + plugin.formatRupiah((long) amount) + " dari " + sender.getName());

        sender.playSound(sender.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        receiver.playSound(receiver.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
    }
}
