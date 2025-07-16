// src/main/java/com/arayx/bankjago/util/BankScheduler.java
package com.arayx.bankjago.util;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.MessageManager;
import com.arayx.bankjago.PlayerBankData;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;

public class BankScheduler {
    private final BankJago plugin;
    private final MessageManager msg;
    private final Map<UUID, PlayerBankData> dataMap;
    private BukkitTask task;

    public BankScheduler(BankJago plugin, MessageManager msg, Map<UUID, PlayerBankData> dataMap) {
        this.plugin  = plugin;
        this.msg     = msg;
        this.dataMap = dataMap;
    }

    /** Start the repeating task (every 5 minutes) */
    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    // 1) Save every player's bank data
                    for (UUID uuid : dataMap.keySet()) {
                        plugin.savePlayerBankData(uuid);
                    }
                    // 2) Optional: log or notify console
                    plugin.getLogger().info("Bank data autosave complete for " + dataMap.size() + " players.");
                },
                /* initial delay */ 20L * 60 * 5,
                /* repeat every */ 20L * 60 * 5);
    }

    /** Stop and restart the scheduler */
    public void restart() {
        if (task != null) {
            task.cancel();
        }
        start();
    }
}
