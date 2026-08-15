package com.example.Models;

public class Oda {
    private int odaId;
    private int odaNo;
    private String odaAdi;
    private String odaTipi;
    private int kapasite;
    private double fiyat;
    private boolean musaitlikDurumu;
    private String ozellikler;

    public Oda() {}

    public Oda(int odaNo, String odaAdi, String odaTipi, int kapasite, double fiyat, boolean musaitlikDurumu, String ozellikler) {
        this.odaNo = odaNo;
        this.odaAdi = odaAdi;
        this.odaTipi = odaTipi;
        this.kapasite = kapasite;
        this.fiyat = fiyat;
        this.musaitlikDurumu = musaitlikDurumu;
        this.ozellikler = ozellikler;
    }

    public int getOdaId() { return odaId; }
    public void setOdaId(int odaId) { this.odaId = odaId; }
    
    public int getOdaNo() { return odaNo; }
    public void setOdaNo(int odaNo) { this.odaNo = odaNo; }
    
    public String getOdaAdi() { return odaAdi; }
    public void setOdaAdi(String odaAdi) { this.odaAdi = odaAdi; }
    
    public String getOdaTipi() { return odaTipi; }
    public void setOdaTipi(String odaTipi) { this.odaTipi = odaTipi; }
    
    public int getKapasite() { return kapasite; }
    public void setKapasite(int kapasite) { this.kapasite = kapasite; }
    
    public double getFiyat() { return fiyat; }
    public void setFiyat(double fiyat) { this.fiyat = fiyat; }
    
    public boolean isMusaitlikDurumu() { return musaitlikDurumu; }
    public void setMusaitlikDurumu(boolean musaitlikDurumu) { this.musaitlikDurumu = musaitlikDurumu; }
    
    public String getOzellikler() { return ozellikler; }
    public void setOzellikler(String ozellikler) { this.ozellikler = ozellikler; }
}