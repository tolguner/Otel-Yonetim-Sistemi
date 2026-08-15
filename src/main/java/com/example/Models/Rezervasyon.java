package com.example.Models;

import java.time.LocalDate;

public class Rezervasyon {
    private int rezervasyonId;
    private String tcKimlikNo;
    private int odaNo;
    private String odaTipi;
    private String odaAdi;
    private LocalDate baslangicTarihi;
    private LocalDate bitisTarihi;
    private double toplamTutar;
    private String durum;
    private String degerlendirme;

    public Rezervasyon() {}

    public Rezervasyon(int rezervasyonId, String tcKimlikNo, int odaNo, String odaTipi, LocalDate baslangicTarihi, LocalDate bitisTarihi, double toplamTutar, String durum) {
        this.rezervasyonId = rezervasyonId;
        this.tcKimlikNo = tcKimlikNo;
        this.odaNo = odaNo;
        this.odaTipi = odaTipi;
        this.baslangicTarihi = baslangicTarihi;
        this.bitisTarihi = bitisTarihi;
        this.toplamTutar = toplamTutar;
        this.durum = durum;
    }

    public int getRezervasyonId() { return rezervasyonId; }
    public void setRezervasyonId(int rezervasyonId) { this.rezervasyonId = rezervasyonId; }
    
    public String getTcKimlikNo() { return tcKimlikNo; }
    public void setTcKimlikNo(String tcKimlikNo) { this.tcKimlikNo = tcKimlikNo; }
    
    public int getOdaNo() { return odaNo; }
    public void setOdaNo(int odaNo) { this.odaNo = odaNo; }
    
    public String getOdaTipi() { return odaTipi; }
    public void setOdaTipi(String odaTipi) { this.odaTipi = odaTipi; }
    
    public LocalDate getBaslangicTarihi() { return baslangicTarihi; }
    public void setBaslangicTarihi(LocalDate baslangicTarihi) { this.baslangicTarihi = baslangicTarihi; }
    
    public LocalDate getBitisTarihi() { return bitisTarihi; }
    public void setBitisTarihi(LocalDate bitisTarihi) { this.bitisTarihi = bitisTarihi; }
    
    public double getToplamTutar() { return toplamTutar; }
    public void setToplamTutar(double toplamTutar) { this.toplamTutar = toplamTutar; }
    
    public String getDurum() { return durum; }
    public void setDurum(String durum) { this.durum = durum; }
    
    public String getOdaAdi() { return odaAdi; }
    public void setOdaAdi(String odaAdi) { this.odaAdi = odaAdi; }
    
    public String getDegerlendirme() { return degerlendirme; }
    public void setDegerlendirme(String degerlendirme) { this.degerlendirme = degerlendirme; }
}
