package com.arayx.bankjago.api;

import java.util.UUID;

public interface BankJagoAPI {
    double getBalance(UUID playerId);
    void deposit(UUID playerId, double amount);
    void withdraw(UUID playerId, double amount);
    boolean transfer(UUID from, UUID to, double amount);
}
