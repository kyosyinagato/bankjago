package com.arayx.bankjago;

import com.arayx.bankjago.api.BankJagoAPI;

import java.util.UUID;

public class BankJagoAPIImpl implements BankJagoAPI {

    private final BankJago plugin;

    public BankJagoAPIImpl(BankJago plugin) {
        this.plugin = plugin;
    }

    @Override
    public double getBalance(UUID playerId) {
        return plugin.getPlayerData(playerId).getBalance();
    }

    @Override
    public void deposit(UUID playerId, double amount) {
        plugin.getPlayerData(playerId).deposit((long) amount);
    }

    @Override
    public void withdraw(UUID playerId, double amount) {
        plugin.getPlayerData(playerId).withdraw((long) amount);
    }

    @Override
    public boolean transfer(UUID from, UUID to, double amount) {
        return plugin.transfer(from, to, amount);
    }
}
