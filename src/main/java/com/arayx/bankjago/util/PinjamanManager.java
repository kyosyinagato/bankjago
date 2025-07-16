package com.arayx.bankjago.util;

import com.arayx.bankjago.BankJago;
import com.arayx.bankjago.model.PlayerPinjamanData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

public class PinjamanManager {

    private final DatabaseManager db;
    private final Map<UUID, Long> nominalMap = new HashMap<>();
    private final Map<UUID, String> sumberMap = new HashMap<>();

    public PinjamanManager(DatabaseManager db) {
        this.db = db;
    }

    public PlayerPinjamanData getPinjaman(UUID uuid) {
        try (PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT * FROM player_pinjaman WHERE uuid=?")) {
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

    public void savePinjaman(PlayerPinjamanData data) {
        try (PreparedStatement ps = db.getConnection().prepareStatement(
                "REPLACE INTO player_pinjaman(uuid,sumber,jumlah,cicilan_per_hari,tempo,jatuh_tempo,hari_ke,penalty) VALUES(?,?,?,?,?,?,?,?)")) {
            ps.setString(1, data.getUuid().toString());
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

    public void deletePinjaman(UUID uuid) {
        try (PreparedStatement ps = db.getConnection().prepareStatement(
                "DELETE FROM player_pinjaman WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PlayerPinjamanData> getAllPinjaman() {
        List<PlayerPinjamanData> list = new ArrayList<>();
        try (PreparedStatement ps = db.getConnection().prepareStatement("SELECT * FROM player_pinjaman")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PlayerPinjamanData(
                            UUID.fromString(rs.getString("uuid")),
                            rs.getString("sumber"),
                            rs.getLong("jumlah"),
                            rs.getLong("cicilan_per_hari"),
                            rs.getInt("tempo"),
                            rs.getTimestamp("jatuh_tempo"),
                            rs.getInt("hari_ke"),
                            rs.getDouble("penalty")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void startReminderTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(BankJago.getInstance(), () -> {
            for (PlayerPinjamanData pinjaman : getAllPinjaman()) {
                UUID uuid = pinjaman.getUuid();
                Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline()) continue;

                Timestamp jatuhTempo = pinjaman.getJatuhTempo();
                if (jatuhTempo == null) continue;

                long millisRemaining = jatuhTempo.getTime() - System.currentTimeMillis();
                long hoursRemaining = millisRemaining / (1000 * 60 * 60);

                if (hoursRemaining <= 2 && hoursRemaining >= 0) {
                    String cicilanStr = BankJago.getInstance().formatRupiah(pinjaman.getCicilanPerHari());
                    String msgText = BankJago.getInstance().getMessageManager()
                            .get("loan.reminder").replace("{cicilan}", cicilanStr);
                    BankJago.getInstance().getMessageManager().sendMsg(player, msgText);
                }
            }
        }, 0L, 20L * 60L * 60L); // setiap 1 jam
    }

    public void startPenaltyTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(BankJago.getInstance(), () -> {
            for (PlayerPinjamanData pinjaman : getAllPinjaman()) {
                UUID uuid = pinjaman.getUuid();

                if (pinjaman.getHariKe() >= pinjaman.getTempo()) continue; // sudah selesai

                pinjaman.setHariKe(pinjaman.getHariKe() + 1);

                if (pinjaman.getHariKe() > 1) {
                    double extraPenalty = pinjaman.getPenalty() + 0.02; // +2% per hari
                    pinjaman.setPenalty(extraPenalty);

                    long nominal = pinjaman.getJumlah();
                    double totalBunga = nominal * extraPenalty;
                    long totalPinjaman = nominal + Math.round(totalBunga);
                    long cicilanHarian = totalPinjaman / pinjaman.getTempo();

                    pinjaman.setCicilanPerHari(cicilanHarian);
                }

                savePinjaman(pinjaman);
            }
        }, 0L, 20L * 60L * 60L * 24L); // setiap 24 jam
    }

    // ===== sementara di memory =====

    public void setNominal(UUID uuid, long nominal) {
        nominalMap.put(uuid, nominal);
    }

    public long getNominal(UUID uuid) {
        return nominalMap.getOrDefault(uuid, 0L);
    }

    public void setSumber(UUID uuid, String sumber) {
        sumberMap.put(uuid, sumber);
    }

    public String getSumber(UUID uuid) {
        return sumberMap.getOrDefault(uuid, "");
    }
}
