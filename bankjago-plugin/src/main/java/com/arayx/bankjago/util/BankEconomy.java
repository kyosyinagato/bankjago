package com.arayx.bankjago.util;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.PlayerBankData;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BankEconomy implements Economy {
    private final BankJago plugin;

    public BankEconomy(BankJago plugin) {
        this.plugin = plugin;
    }

    @Override public boolean isEnabled()                   { return true; }
    @Override public String  getName()                     { return plugin.getDescription().getName(); }
    @Override public int     fractionalDigits()            { return 0; }
    @Override public String  currencyNameSingular()        { return "Rupiah"; }
    @Override public String  currencyNamePlural()          { return "Rupiahs"; }
    @Override public String  format(double amount)         { return plugin.formatRupiah(Math.round(amount)); }

    @Override public boolean hasBankSupport()              { return false; }
    @Override public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }
    @Override public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }
    @Override public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }
    @Override public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }
    @Override public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }
    @Override public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }
    @Override public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }
    @Override public List<String> getBanks()               { return Collections.emptyList(); }

    @Override public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }
    @Override public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
    }
    @Override public EconomyResponse isBankOwner(String bankName, String playerName) {
        return isBankOwner(bankName, plugin.getServer().getOfflinePlayer(playerName));
    }
    @Override public EconomyResponse isBankMember(String bankName, String playerName) {
        return isBankMember(bankName, plugin.getServer().getOfflinePlayer(playerName));
    }

    @Override public boolean hasAccount(String playerName) {
        return hasAccount(plugin.getServer().getOfflinePlayer(playerName));
    }
    @Override public boolean hasAccount(OfflinePlayer player) {
        return plugin.getPlayerDataMap().containsKey(player.getUniqueId());
    }
    @Override public boolean createPlayerAccount(String playerName) {
        return createPlayerAccount(plugin.getServer().getOfflinePlayer(playerName));
    }
    @Override public boolean createPlayerAccount(OfflinePlayer player) {
        UUID id = player.getUniqueId();
        if (!plugin.getPlayerDataMap().containsKey(id)) {
            PlayerBankData d = new PlayerBankData(id, plugin.generateRandomNoRek(), 0L, false);
            plugin.getPlayerDataMap().put(id, d);
            plugin.savePlayerBankData(id);
        }
        return true;
    }
    @Override public boolean hasAccount(String playerName, String world) { return hasAccount(playerName); }
    @Override public boolean hasAccount(OfflinePlayer player, String world) { return hasAccount(player); }
    @Override public boolean createPlayerAccount(String playerName, String world) {
        return createPlayerAccount(playerName);
    }
    @Override public boolean createPlayerAccount(OfflinePlayer player, String world) {
        return createPlayerAccount(player);
    }

    @Override public double getBalance(String playerName) {
        return getBalance(plugin.getServer().getOfflinePlayer(playerName));
    }
    @Override public double getBalance(OfflinePlayer player) {
        PlayerBankData d = plugin.getPlayerDataMap().get(player.getUniqueId());
        return d == null ? 0 : d.getCash();
    }

    @Override public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawPlayer(plugin.getServer().getOfflinePlayer(playerName), amount);
    }
    @Override public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        UUID id = player.getUniqueId();
        PlayerBankData d = plugin.getPlayerDataMap().get(id);
        if (d == null) return new EconomyResponse(0, 0, ResponseType.FAILURE, "No account");
        long amt = Math.round(amount);
        if (d.getCash() < amt) {
            return new EconomyResponse(0, d.getCash(), ResponseType.FAILURE, "Insufficient funds");
        }
        d.setCash(d.getCash() - amt);
        plugin.savePlayerBankData(id);
        return new EconomyResponse(amt, d.getCash(), ResponseType.SUCCESS, "Withdrawal successful");
    }

    @Override public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositPlayer(plugin.getServer().getOfflinePlayer(playerName), amount);
    }
    @Override public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        UUID id = player.getUniqueId();
        PlayerBankData d = plugin.getPlayerDataMap().get(id);
        if (d == null) {
            d = new PlayerBankData(id, plugin.generateRandomNoRek(), 0L, false);
            plugin.getPlayerDataMap().put(id, d);
        }
        long amt = Math.round(amount);
        d.setCash(d.getCash() + amt);
        plugin.savePlayerBankData(id);
        return new EconomyResponse(amt, d.getCash(), ResponseType.SUCCESS, "Deposit successful");
    }

    @Override public EconomyResponse withdrawPlayer(String world, String playerName, double amount) {
        return withdrawPlayer(playerName, amount);
    }
    @Override public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount) {
        return withdrawPlayer(player, amount);
    }
    @Override public EconomyResponse depositPlayer(String world, String playerName, double amount) {
        return depositPlayer(playerName, amount);
    }
    @Override public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount) {
        return depositPlayer(player, amount);
    }

    @Override public boolean has(String playerName, double amount) {
        return has(plugin.getServer().getOfflinePlayer(playerName), amount);
    }
    @Override public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }
    @Override public boolean has(String playerName, String world, double amount) {
        return has(plugin.getServer().getOfflinePlayer(playerName), world, amount);
    }
    @Override public boolean has(OfflinePlayer player, String world, double amount) {
        return has(player, amount);
    }

    @Override public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }
    @Override public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }
}
