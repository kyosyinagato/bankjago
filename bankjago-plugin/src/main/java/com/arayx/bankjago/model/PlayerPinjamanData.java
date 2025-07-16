package com.arayx.bankjago.model;

import java.sql.Timestamp;
import java.util.UUID;

public class PlayerPinjamanData {
    private UUID uuid;
    private String sumber;
    private long jumlah;
    private long cicilanPerHari;
    private int tempo;
    private Timestamp jatuhTempo;
    private int hariKe;
    private double penalty;

    public PlayerPinjamanData(UUID uuid, String sumber, long jumlah, long cicilanPerHari, int tempo, Timestamp jatuhTempo, int hariKe, double penalty) {
        this.uuid = uuid;
        this.sumber = sumber;
        this.jumlah = jumlah;
        this.cicilanPerHari = cicilanPerHari;
        this.tempo = tempo;
        this.jatuhTempo = jatuhTempo;
        this.hariKe = hariKe;
        this.penalty = penalty;
    }

    public UUID getUuid() { return uuid; }
    public String getSumber() { return sumber; }
    public long getJumlah() { return jumlah; }
    public long getCicilanPerHari() { return cicilanPerHari; }
    public int getTempo() { return tempo; }
    public Timestamp getJatuhTempo() { return jatuhTempo; }
    public int getHariKe() { return hariKe; }
    public double getPenalty() { return penalty; }

    public void setJumlah(long jumlah) { this.jumlah = jumlah; }
    public void setSumber(String sumber) { this.sumber = sumber; }
    public void setJatuhTempo(Timestamp jatuhTempo) { this.jatuhTempo = jatuhTempo; }
    public void setHariKe(int hariKe) { this.hariKe = hariKe; }

    // ✅ tambahan biar compile:
    public void setTempo(int tempo) { this.tempo = tempo; }
    public void setCicilanPerHari(long cicilanPerHari) { this.cicilanPerHari = cicilanPerHari; }
    public void setPenalty(double penalty) { this.penalty = penalty; }
}
