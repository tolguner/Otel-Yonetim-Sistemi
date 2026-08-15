package com.example.Models;

public class Musteri {
    private String tcKimlikNo;
    private String ad;
    private String soyad;
    private String email;
    private String telefon;
    private String sifre;
    
    private static Musteri aktifMusteri;
    public Musteri() {}
    
    public Musteri(String tcKimlikNo, String ad, String soyad, String email, String telefon, String sifre, double bakiye) {
        this.tcKimlikNo = tcKimlikNo;
        this.ad = ad;
        this.soyad = soyad;
        this.email = email;
        this.telefon = telefon;
        this.sifre = sifre;
    }

    public Musteri(String tcKimlikNo, String ad, String soyad, String email, String telefon, String sifre) {
        this.tcKimlikNo = tcKimlikNo;
        this.ad = ad;
        this.soyad = soyad;
        this.email = email;
        this.telefon = telefon;
        this.sifre = sifre;
    }

    public static void oturumAc(Musteri musteri) {
        aktifMusteri = musteri;
    }
    
    public static void oturumuKapat() {
        aktifMusteri = null;
    }   

    public static Musteri getAktifMusteri() {
        return aktifMusteri;
    }
    
    public static boolean oturumVarMi() {
        return aktifMusteri != null;
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
    
    public static void oturumBilgileriniAyarla(String tcKimlikNo, String ad, String soyad, String email, String telefon) {
        aktifMusteri = new Musteri();
        aktifMusteri.setTcKimlikNo(tcKimlikNo);
        aktifMusteri.setAd(ad);
        aktifMusteri.setSoyad(soyad);
        aktifMusteri.setEmail(email);
        aktifMusteri.setTelefon(telefon);
    }
}
