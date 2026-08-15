package com.example.Models;

public class Admin {
    private String tcKimlikNo;
    private String ad;
    private String soyad;
    private String email;
    private String sifre;
    private String telefon;

    // Static oturum instance'ı
    private static Admin aktifAdmin;

    // Parametresiz constructor
    public Admin() {}

    // Parametreli constructor
    public Admin(String tcKimlikNo, String ad, String soyad,
                 String email, String sifre, String telefon) {
        this.tcKimlikNo = tcKimlikNo;
        this.ad = ad;
        this.soyad = soyad;
        this.email = email;
        this.sifre = sifre;
        this.telefon = telefon;
    }

    public String getTcKimlikNo() { return tcKimlikNo; }
    public void setTcKimlikNo(String tcKimlikNo) { this.tcKimlikNo = tcKimlikNo; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public String getSoyad() { return soyad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSifre() { return sifre; }
    public void setSifre(String sifre) { this.sifre = sifre; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    // Oturum yönetimi metodları
    public static void oturumAc(Admin admin) {
        aktifAdmin = admin;
    }

    public static void oturumuKapat() {
        aktifAdmin = null;
    }

    public static Admin getAktifAdmin() {
        return aktifAdmin;
    }

    public static boolean oturumVarMi() {
        return aktifAdmin != null;
    }

    public static void oturumBilgileriniAyarla(String tcKimlikNo, String ad, String soyad, String email, String telefon) {
        Admin admin = new Admin(tcKimlikNo, ad, soyad, email, "", telefon);
        oturumAc(admin);
    }
}