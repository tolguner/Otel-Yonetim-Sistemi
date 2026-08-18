package com.example.DataAccess;

import com.example.Models.Yonetici;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class YoneticiDAO {
    public static ObservableList<Yonetici> tumYoneticileriGetir() {
        ObservableList<Yonetici> yoneticiList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Yonetici";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Yonetici yonetici = new Yonetici();
                yonetici.setTcKimlikNo(rs.getString("tcKimlikNo"));
                yonetici.setAd(rs.getString("ad"));
                yonetici.setSoyad(rs.getString("soyad"));
                yonetici.setEmail(rs.getString("email"));
                yonetici.setTelefon(rs.getString("telefon"));
                yonetici.setSifre(rs.getString("sifre"));
                yoneticiList.add(yonetici);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return yoneticiList;
    }
    
    public static boolean yoneticiSil(String tcKimlikNo) {
        String sql = "DELETE FROM Yonetici WHERE tcKimlikNo = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
             pstmt.setString(1, tcKimlikNo);
             return pstmt.executeUpdate() > 0;
             
         } catch (SQLException e) {
             e.printStackTrace();
             return false;
         }
    }
    
    public boolean yoneticiGuncelle(Yonetici yonetici) {
        // Şifre boş/null gönderilmişse mevcut şifre korunur (yanlışlıkla silinmesin diye)
        boolean sifreDegisecek = yonetici.getSifre() != null && !yonetici.getSifre().isEmpty();
        String sql = sifreDegisecek
            ? "UPDATE Yonetici SET ad = ?, soyad = ?, email = ?, telefon = ?, sifre = ? WHERE tcKimlikNo = ?"
            : "UPDATE Yonetici SET ad = ?, soyad = ?, email = ?, telefon = ? WHERE tcKimlikNo = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, yonetici.getAd());
            pstmt.setString(2, yonetici.getSoyad());
            pstmt.setString(3, yonetici.getEmail());
            pstmt.setString(4, yonetici.getTelefon());

            if (sifreDegisecek) {
                pstmt.setString(5, PasswordHasher.hashle(yonetici.getSifre()));
                pstmt.setString(6, yonetici.getTcKimlikNo());
            } else {
                pstmt.setString(5, yonetici.getTcKimlikNo());
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean yoneticiEkle(Yonetici yonetici) {
        String sql = "INSERT INTO Yonetici (tcKimlikNo, ad, soyad, email, telefon, sifre) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
             pstmt.setString(1, yonetici.getTcKimlikNo());
             pstmt.setString(2, yonetici.getAd());
             pstmt.setString(3, yonetici.getSoyad());
             pstmt.setString(4, yonetici.getEmail());
             pstmt.setString(5, yonetici.getTelefon());
             pstmt.setString(6, PasswordHasher.hashle(yonetici.getSifre()));

             return pstmt.executeUpdate() > 0;
         } catch (SQLException e) {
             e.printStackTrace();
             return false;
         }
    }

    public static boolean girisYap(String tcKimlikNo, String sifre) {
        String sql = "SELECT * FROM Yonetici WHERE tcKimlikNo = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

             pstmt.setString(1, tcKimlikNo);
             ResultSet rs = pstmt.executeQuery();

             if (rs.next() && PasswordHasher.dogrula(sifre, rs.getString("sifre"))) {
                 Yonetici yonetici = new Yonetici();
                 yonetici.setTcKimlikNo(rs.getString("tcKimlikNo"));
                 yonetici.setAd(rs.getString("ad"));
                 yonetici.setSoyad(rs.getString("soyad"));
                 yonetici.setEmail(rs.getString("email"));
                 yonetici.setTelefon(rs.getString("telefon"));
                 Yonetici.oturumAc(yonetici);
                 return true;
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
         return false;
    }
    
    public static boolean profilGuncelle(String tcKimlikNo, String ad, String soyad, String email, String telefon) {
        String sql = "UPDATE Yonetici SET ad = ?, soyad = ?, email = ?, telefon = ? WHERE tcKimlikNo = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
             pstmt.setString(1, ad);
             pstmt.setString(2, soyad);
             pstmt.setString(3, email);
             pstmt.setString(4, telefon);
             pstmt.setString(5, tcKimlikNo);
             
             return pstmt.executeUpdate() > 0;
         } catch (SQLException e) {
             e.printStackTrace();
             return false;
         }
    }
    
    public static boolean sifreGuncelle(String tcKimlikNo, String yeniSifre) {
        String sql = "UPDATE Yonetici SET sifre = ? WHERE tcKimlikNo = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

             pstmt.setString(1, PasswordHasher.hashle(yeniSifre));
             pstmt.setString(2, tcKimlikNo);

             return pstmt.executeUpdate() > 0;
         } catch (SQLException e) {
             e.printStackTrace();
             return false;
         }
    }

    public boolean yoneticiGiris(String tcKimlikNo, String sifre) {
        String sql = "SELECT sifre FROM Yonetici WHERE tcKimlikNo = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tcKimlikNo);

            ResultSet rs = pstmt.executeQuery();
            return rs.next() && PasswordHasher.dogrula(sifre, rs.getString("sifre"));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public interface YoneticiBilgileriCallback {
        void onBilgiAl(String ad, String soyad, String email, String telefon);
    }

    public void yoneticiBilgileriniGetir(String tcKimlikNo, YoneticiBilgileriCallback callback) {
        String sql = "SELECT ad, soyad, email, telefon FROM Yonetici WHERE tcKimlikNo = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tcKimlikNo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                callback.onBilgiAl(
                    rs.getString("ad"),
                    rs.getString("soyad"),
                    rs.getString("email"),
                    rs.getString("telefon")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 