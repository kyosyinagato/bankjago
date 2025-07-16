package com.arayx.bankjago.command;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import com.arayx.bankjago.PlayerBankData;
import com.arayx.bankjago.gui.MainBankGUI;
import com.arayx.bankjago.gui.RegisterBankGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BankCommand implements CommandExecutor {

    private final BankJago plugin;
    private final MessageManager msg;

    public BankCommand(BankJago plugin, MessageManager msg) {
        this.plugin = plugin;
        this.msg = msg;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(msg.get("command.onlyplayer"));
            return true;
        }

        PlayerBankData data = plugin.getPlayerDataMap().get(p.getUniqueId());

//        p.sendMessage("DEBUG: data=" + (data != null ? "not null" : "null") +
  //              ", alias=" + (data != null ? data.getAlias() : "null"));

        if (data == null || data.getAlias() == null || data.getAlias().isEmpty()) {
            new RegisterBankGUI(p, plugin);
        } else {
            plugin.getMainBankGUI().open(p);
        }


        return true;
    }
}
