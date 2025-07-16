package com.arayx.bankjago;

import java.util.UUID;

public class PlayerBankData {
    private final UUID uuid;
    private String accountNumber;
    private String alias;
    private long balance;
    private long cash;
    private boolean suspended;

    public PlayerBankData(UUID uuid, String accountNumber, long balance, boolean suspended) {
        this.uuid = uuid;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.suspended = suspended;
        this.cash = 0;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getCash() {
        return cash;
    }

    public void setCash(long cash) {
        this.cash = cash;
    }

    public boolean isSuspended() {
        return suspended;
    }
    public void deposit(long amount) {this.balance += amount;}

    public void withdraw(long amount) {this.balance -= amount;}


    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }
}
