package com.arayx.bankjago.command;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BankAdminCommand implements CommandExecutor {
    private final BankJago plugin;
    private final MessageManager msg;

    public BankAdminCommand(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg    = msg;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg.getString("command.onlyplayer"));
            return true;
        }
        Player admin = (Player) sender;
        if (!admin.hasPermission("bank.admin")) {
            admin.sendMessage(msg.getString("command.noperm"));
            return true;
        }
        plugin.getAdminBankGUI().open(admin);
        return true;
    }
}
