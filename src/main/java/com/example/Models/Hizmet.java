package com.example.Models;

public class Hizmet {
    private int hizmetId;
    private String hizmetAdi;
    private String aciklama;
    private double fiyat;
    private boolean aktif;
    public Hizmet() {}

    public Hizmet(int hizmetId, String hizmetAdi, String aciklama, double fiyat, boolean aktif) {
        this.hizmetId = hizmetId;
        this.hizmetAdi = hizmetAdi;
        this.aciklama = aciklama;
        this.fiyat = fiyat;
        this.aktif = aktif;
    }
    
    public int getHizmetId() { return hizmetId; }
    public void setHizmetId(int hizmetId) { this.hizmetId = hizmetId; }
    
    public String getHizmetAdi() { return hizmetAdi; }
    public void setHizmetAdi(String hizmetAdi) { this.hizmetAdi = hizmetAdi; }
    
    public String getAciklama() { return aciklama; }
    public void setAciklama(String aciklama) { this.aciklama = aciklama; }
    
    public double getFiyat() { return fiyat; }
    public void setFiyat(double fiyat) { this.fiyat = fiyat; }

    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }

    @Override
    public String toString() {
        return hizmetAdi + " (" + fiyat + " TL)";
    }
}
