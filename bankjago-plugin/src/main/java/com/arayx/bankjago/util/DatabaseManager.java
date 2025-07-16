package com.arayx.bankjago.util;

import com.arayx.bankjago.PlayerBankData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.arayx.bankjago.model.PlayerPinjamanData;
import java.sql.Timestamp;
import com.arayx.bankjago.model.PlayerPinjamanData;
import java.sql.Timestamp;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {

    private final String url;
    private Connection conn;
    private final Map<UUID, PlayerBankData> cache = new HashMap<>();

    public DatabaseManager(String url) {
        this.url = url;
    }

    public void connect() throws SQLException {
        conn = DriverManager.getConnection(url);
    }
    public void disconnect() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
    public Connection getConnection() {
        return conn;
    }

    public void createTables() throws SQLException {
        try (Statement s = conn.createStatement()) {
            s.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS player_bank (" +
                            "uuid VARCHAR(36) PRIMARY KEY, " +
                            "account_number VARCHAR(32), " +
                            "alias VARCHAR(64), " +
                            "balance BIGINT, " +
                            "suspended BOOLEAN" +
                            ")"
            );
            s.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS admin_logs (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "type VARCHAR(32), " +
                            "admin VARCHAR(32), " +
                            "target_uuid VARCHAR(36), " +
                            "amount BIGINT, " +
                            "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );
            s.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS player_pinjaman (" +
                            "uuid VARCHAR(36) PRIMARY KEY, " +
                            "sumber VARCHAR(10) NOT NULL, " +
                            "jumlah BIGINT NOT NULL, " +
                            "cicilan_per_hari BIGINT NOT NULL, " +
                            "tempo INT NOT NULL, " +
                            "jatuh_tempo TIMESTAMP NOT NULL, " +
                            "hari_ke INT NOT NULL, " +
                            "penalty DOUBLE NOT NULL" +
                            ")"
            );
        }
    }

    public void loadAllPlayerData() throws SQLException {
        String sql = "SELECT uuid, account_number, alias, balance, suspended FROM player_bank";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("uuid"));
                String account = rs.getString("account_number");
                String alias = rs.getString("alias");
                long bal = rs.getLong("balance");
                boolean susp = rs.getBoolean("suspended");

                PlayerBankData data = new PlayerBankData(id, account, bal, susp);
                data.setAlias(alias);
                cache.put(id, data);
            }
        }
    }

    public Map<UUID, PlayerBankData> getPlayerDataMap() {
        return cache;
    }

    public void savePlayerBankData(UUID uuid) {
        PlayerBankData d = cache.get(uuid);
        if (d == null) return;

        String sql = "REPLACE INTO player_bank(uuid,account_number,alias,balance,suspended) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, d.getAccountNumber());
            ps.setString(3, d.getAlias());
            ps.setLong(4, d.getBalance());
            ps.setBoolean(5, d.isSuspended());
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public void savePlayerData(UUID uuid, PlayerBankData data) {
        String sql = "REPLACE INTO player_bank(uuid,account_number,alias,balance,suspended) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, data.getAccountNumber());
            ps.setString(3, data.getAlias());
            ps.setLong(4, data.getBalance());
            ps.setBoolean(5, data.isSuspended());
            ps.executeUpdate();
            cache.put(uuid, data);
        } catch (SQLException ignored) {}
    }

    public Map<UUID, PlayerBankData> getAllBankAccounts(boolean onlyOnline) {
        if (!onlyOnline) {
            return cache;
        }
        Map<UUID, PlayerBankData> result = new HashMap<>();
        for (UUID id : cache.keySet()) {
            if (Bukkit.getPlayer(id) != null) {
                result.put(id, cache.get(id));
            }
        }
        return result;
    }

    public void sendLastLogs(Player admin, String type, int count) {
        String sql = "SELECT admin, target_uuid, amount, time " +
                "FROM admin_logs WHERE type=? ORDER BY id DESC LIMIT ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setInt(2, count);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String who = rs.getString("admin");
                    String tgt = rs.getString("target_uuid");
                    long amt = rs.getLong("amount");
                    Timestamp ts = rs.getTimestamp("time");
                    admin.sendMessage(String.format(
                            "[%s] %s → %s : %d at %s",
                            type, who, tgt, amt, ts.toString()
                    ));
                }
            }
        } catch (SQLException ignored) {}
    }

    public void deleteAllData() {
        cache.clear();
        try (Statement s = conn.createStatement()) {
            s.executeUpdate("DELETE FROM player_bank");
            s.executeUpdate("DELETE FROM admin_logs");
        } catch (SQLException ignored) {}
    }

    public void logAdminAction(String type, String admin, UUID target, long amt) {
        String sql = "INSERT INTO admin_logs(type,admin,target_uuid,amount) VALUES(?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, admin);
            ps.setString(3, target.toString());
            ps.setLong(4, amt);
            ps.executeUpdate();
        } catch (SQLException ignored) {}
    }

    public void deletePlayerData(UUID uuid) {
        String sql = "DELETE FROM player_bank WHERE uuid=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to delete player data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public PlayerPinjamanData getPinjamanData(UUID uuid) {
        String sql = "SELECT * FROM player_pinjaman WHERE uuid=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PlayerPinjamanData(
                            uuid,
                            rs.getString("sumber"),
                            rs.getLong("jumlah"),
                            rs.getLong("cicilan_per_hari"),
                            rs.getInt("tempo"),
                            rs.getTimestamp("jatuh_tempo"),
                            rs.getInt("hari_ke"),
                            rs.getDouble("penalty")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void savePinjamanData(UUID uuid, PlayerPinjamanData data) {
        String sql = "REPLACE INTO player_pinjaman(uuid, sumber, jumlah, cicilan_per_hari, tempo, jatuh_tempo, hari_ke, penalty) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, data.getSumber());
            ps.setLong(3, data.getJumlah());
            ps.setLong(4, data.getCicilanPerHari());
            ps.setInt(5, data.getTempo());
            ps.setTimestamp(6, data.getJatuhTempo());
            ps.setInt(7, data.getHariKe());
            ps.setDouble(8, data.getPenalty());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePinjamanData(UUID uuid) {
        String sql = "DELETE FROM player_pinjaman WHERE uuid=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
