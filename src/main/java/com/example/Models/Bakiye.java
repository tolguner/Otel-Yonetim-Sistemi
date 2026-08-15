package com.example.Models;

import java.time.LocalDate;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;
import com.example.DataAccess.BakiyeDAO;

public class Bakiye {
    private static Bakiye instance;
    private final DoubleProperty mevcutBakiye = new SimpleDoubleProperty(0.0);
    
    public DoubleProperty mevcutBakiyeProperty() {
        return mevcutBakiye;
    }
    
    public double getMevcutBakiye() {
        return mevcutBakiye.get();
    }
    
    public void setMevcutBakiye(double value) {
        mevcutBakiye.set(value);
    }
    
    // Singleton pattern
    public static Bakiye getInstance() {
        if (instance == null) {
            instance = new Bakiye();
        }
        return instance;
    }
    
    public void bakiyeGuncelle() {
    }
    
    private int bakiyeId;
    private String tcKimlikNo;
    private LocalDate islemTarihi;
    private String islemTipi;
    private double tutar;
    private double toplamBakiye;
    private double bakiye;

    public Bakiye() {}

    public Bakiye(String tcKimlikNo, LocalDate islemTarihi, String islemTipi, double tutar, double toplamBakiye) {
        this.tcKimlikNo = tcKimlikNo;
        this.islemTarihi = islemTarihi;
        this.islemTipi = islemTipi;
        this.tutar = tutar;
        this.toplamBakiye = toplamBakiye;
    }

    public int getBakiyeId() { return bakiyeId; }
    public void setBakiyeId(int bakiyeId) { this.bakiyeId = bakiyeId; }
    
    public String getTcKimlikNo() { return tcKimlikNo; }
    public void setTcKimlikNo(String tcKimlikNo) { this.tcKimlikNo = tcKimlikNo; }
    
    public LocalDate getIslemTarihi() { return islemTarihi; }
    public void setIslemTarihi(LocalDate islemTarihi) { this.islemTarihi = islemTarihi; }
    
    public String getIslemTipi() { return islemTipi; }
    public void setIslemTipi(String islemTipi) { this.islemTipi = islemTipi; }
    
    public double getTutar() { return tutar; }
    public void setTutar(double tutar) { this.tutar = tutar; }
    
    public double getToplamBakiye() { return toplamBakiye; }
    public void setToplamBakiye(double toplamBakiye) { this.toplamBakiye = toplamBakiye; }

    // İşlem tipleri için sabitler
    public static final String ISLEM_YUKLEME = "BAKİYE YÜKLEME";
    public static final String ISLEM_ODEME = "REZERVASYON ÖDEMESİ";
    public static final String ISLEM_HIZMET = "HİZMET ÖDEMESİ";

    public boolean bakiyeYukle(double tutar) {
        try {
            BakiyeDAO bakiyeDAO = new BakiyeDAO();
            boolean sonuc = bakiyeDAO.bakiyeYukle(tcKimlikNo, tutar);
            if (sonuc) {
                setMevcutBakiye(getMevcutBakiye() + tutar);
            }
            return sonuc;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean bakiyeYeterliMi(double tutar) {
        return this.getMevcutBakiye() >= tutar;
    }

    public double getBakiye() {
        return bakiye;
    }
    
    public void setBakiye(double bakiye) {
        this.bakiye = bakiye;
    }
}