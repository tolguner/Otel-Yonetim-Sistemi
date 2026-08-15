package com.example.Models;

import java.time.LocalDate;

public class Degerlendirme {
    private int degerlendirmeId;
    private int rezervasyonId;
    private int puan;
    private String yorum;
    private LocalDate tarih;
    private String tcKimlikNo;

    public Degerlendirme() {}

    public Degerlendirme(int rezervasyonId, int puan, String yorum, LocalDate tarih) {
        this.rezervasyonId = rezervasyonId;
        this.puan = puan;
        this.yorum = yorum;
        this.tarih = tarih;
    }

    public int getDegerlendirmeId() { return degerlendirmeId; }
    public void setDegerlendirmeId(int degerlendirmeId) { this.degerlendirmeId = degerlendirmeId; }
    
    public int getRezervasyonId() { return rezervasyonId; }
    public void setRezervasyonId(int rezervasyonId) { this.rezervasyonId = rezervasyonId; }
    
    public int getPuan() { return puan; }
    public void setPuan(int puan) { this.puan = puan; }
    
    public String getYorum() { return yorum; }
    public void setYorum(String yorum) { this.yorum = yorum; }
    
    public LocalDate getTarih() { return tarih; }
    public void setTarih(LocalDate tarih) { this.tarih = tarih; }
    
    public String getTcKimlikNo() {
        return tcKimlikNo;
    }
    
    public void setTcKimlikNo(String tcKimlikNo) {
        this.tcKimlikNo = tcKimlikNo;
    }
}