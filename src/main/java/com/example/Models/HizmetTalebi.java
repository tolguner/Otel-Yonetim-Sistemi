package com.example.Models;

import java.time.LocalDateTime;

public class HizmetTalebi {
    private int talepId;
    private int rezervasyonId;
    private int hizmetId;
    private String hizmetAdi;
    private String aciklama;
    private String durum;
    private LocalDateTime talepTarihi;
    private double fiyat;
    private int odaNo;
    private String musteriAdi;

    public HizmetTalebi() {}

    public HizmetTalebi(int rezervasyonId, int hizmetId, String hizmetAdi, String aciklama, String durum, LocalDateTime talepTarihi, double fiyat) {
        this.rezervasyonId = rezervasyonId;
        this.hizmetId = hizmetId;
        this.hizmetAdi = hizmetAdi;
        this.aciklama = aciklama;
        this.durum = durum;
        this.talepTarihi = talepTarihi;
        this.fiyat = fiyat;
    }

    public int getTalepId() { return talepId; }
    public void setTalepId(int talepId) { this.talepId = talepId; }
    
    public int getRezervasyonId() { return rezervasyonId; }
    public void setRezervasyonId(int rezervasyonId) { this.rezervasyonId = rezervasyonId; }
    
    public int getHizmetId() { return hizmetId; }
    public void setHizmetId(int hizmetId) { this.hizmetId = hizmetId; }
    
    public String getHizmetAdi() { return hizmetAdi; }
    public void setHizmetAdi(String hizmetAdi) { this.hizmetAdi = hizmetAdi; }
    
    public String getAciklama() { return aciklama; }
    public void setAciklama(String aciklama) { this.aciklama = aciklama; }
    
    public String getDurum() { return durum; }
    public void setDurum(String durum) { this.durum = durum; }
    
    public LocalDateTime getTalepTarihi() { return talepTarihi; }
    public void setTalepTarihi(LocalDateTime talepTarihi) { this.talepTarihi = talepTarihi; }
    
    public double getFiyat() { return fiyat; }
    public void setFiyat(double fiyat) { this.fiyat = fiyat; }

    public int getOdaNo() {
        return odaNo;
    }
    
    public void setOdaNo(int odaNo) {
        this.odaNo = odaNo;
    }

    public String getMusteriAdi() { return musteriAdi; }
    public void setMusteriAdi(String musteriAdi) { this.musteriAdi = musteriAdi; }
} 