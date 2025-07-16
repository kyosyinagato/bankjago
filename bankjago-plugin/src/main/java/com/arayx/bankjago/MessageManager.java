package com.arayx.bankjago;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class MessageManager {

    private static FileConfiguration messages;
    private static File messagesFile;  // Store the file object

    /**
     * Inisialisasi MessageManager
     * Memuat messages.yml ke memori
     */
    public static void init(JavaPlugin plugin) {
        // Pastikan messages.yml ada di folder plugin
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");  // Save the file reference
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);  // Jika file tidak ada, buatkan
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);  // Memuat file messages.yml
    }

    /**
     * Mengambil pesan dari messages.yml
     *
     * @param key Kunci pesan
     * @return Pesan dengan kode warna yang sudah diubah
     */
    public static String getMessage(String key) {
        // Mengambil pesan dari messages.yml, jika tidak ditemukan tampilkan pesan error
        String msg = messages.getString(key, "&cMessage not found: " + key);
        return ChatColor.translateAlternateColorCodes('&', msg);  // Mengonversi kode warna '&' ke format warna Bukkit
    }

    /**
     * Mendapatkan pesan dari messages.yml (untuk kompatibilitas dengan kode lama)
     *
     * @param key Kunci pesan
     * @return Pesan dengan kode warna yang sudah diubah
     */
    public static String get(String key) {
        return getMessage(key);  // Call getMessage() internally for backward compatibility
    }

    /**
     * Mendapatkan pesan dari messages.yml (untuk kompatibilitas dengan kode lama)
     *
     * @param key Kunci pesan
     * @return Pesan dengan kode warna yang sudah diubah
     */
    public static String getString(String key) {
        return getMessage(key);  // Reintroduce getString method, calling get internally
    }

    public static List<String> getList(String key) {
        List<String> list = messages.getStringList(key);
        if (list == null || list.isEmpty()) {
            return List.of(ChatColor.translateAlternateColorCodes('&', "&cMessage not found: " + key));
        }
        return list.stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .toList();
    }

    /**
     * Mengirim pesan ke pemain
     *
     * @param player Pemain yang menerima pesan
     * @param key    Kunci pesan yang ingin dikirim
     */
    public static void sendMsg(Player player, String key) {
        // Mengirim pesan ke pemain dengan kunci yang diberikan
        player.sendMessage(getMessage(key));
    }

    /**
     * Reloads the messages.yml and updates the messages configuration.
     */
    public static void reload() {
        // Reload the messages.yml file without needing a plugin reference
        if (messagesFile.exists()) {
            messages = YamlConfiguration.loadConfiguration(messagesFile);
        }
    }
}
