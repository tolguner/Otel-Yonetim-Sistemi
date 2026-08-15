package com.example.Models;

public class Yonetici {
    private String tcKimlikNo;
    private String ad;
    private String soyad;
    private String email;
    private String telefon;
    private String sifre;
    private static Yonetici aktifYonetici;

    public Yonetici() {}

    public Yonetici(String tcKimlikNo, String ad, String soyad, String email, String telefon, String sifre) {
        this.tcKimlikNo = tcKimlikNo;
        this.ad = ad;
        this.soyad = soyad;
        this.email = email;
        this.telefon = telefon;
        this.sifre = sifre;
    }   
    
    public String getTcKimlikNo() { return tcKimlikNo; }
    public void setTcKimlikNo(String tcKimlikNo) { this.tcKimlikNo = tcKimlikNo; }
    
    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }
    
    public String getSoyad() { return soyad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    
    public String getSifre() { return sifre; }
    public void setSifre(String sifre) { this.sifre = sifre; }

    public static void oturumAc(Yonetici yonetici) {
        aktifYonetici = yonetici;
    }
    
    public static Yonetici getAktifYonetici() {
        return aktifYonetici;
    }
    
    public static void oturumuKapat() {
        aktifYonetici = null;
    }

    public static void oturumBilgileriniAyarla(String tcKimlikNo, String ad, String soyad, String email, String telefon) {
        aktifYonetici = new Yonetici();
        aktifYonetici.tcKimlikNo = tcKimlikNo;
        aktifYonetici.ad = ad;
        aktifYonetici.soyad = soyad;
        aktifYonetici.email = email;
        aktifYonetici.telefon = telefon;
    }
}
