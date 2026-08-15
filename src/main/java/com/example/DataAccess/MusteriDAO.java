package com.example.DataAccess;

import com.example.Models.Musteri;
import java.sql.*;

public class MusteriDAO {
    
    // Müşteri girişi kontrolü
    public boolean girisYap(String tcKimlikNo, String sifre) {
        String sql = "SELECT * FROM Musteri WHERE tcKimlikNo = ? AND sifre = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tcKimlikNo);
            pstmt.setString(2, sifre);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Musteri musteri = new Musteri();
                musteri.setTcKimlikNo(rs.getString("tcKimlikNo"));
                musteri.setAd(rs.getString("ad"));
                musteri.setSoyad(rs.getString("soyad"));
                musteri.setEmail(rs.getString("email"));
                musteri.setTelefon(rs.getString("telefon"));
                
                Musteri.oturumAc(musteri);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean musteriEkle(Musteri musteri) {
        String query = "INSERT INTO Musteri (tcKimlikNo, ad, soyad, email, telefon, sifre) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, musteri.getTcKimlikNo());
            pstmt.setString(2, musteri.getAd());
            pstmt.setString(3, musteri.getSoyad());
            pstmt.setString(4, musteri.getEmail());
            pstmt.setString(5, musteri.getTelefon());
            pstmt.setString(6, musteri.getSifre());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Müşteri eklenirken hata: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean tcKimlikNoKontrol(String tcKimlikNo) {
        String query = "SELECT COUNT(*) FROM Musteri WHERE tcKimlikNo = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, tcKimlikNo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("TC Kimlik No kontrolü sırasında hata: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public void musteriBilgileriniGetir(String tcKimlikNo, MusteriBilgileriHandler handler) {
        String query = "SELECT * FROM Musteri WHERE tcKimlikNo = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, tcKimlikNo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                handler.bilgileriAl(
                    rs.getString("ad"),
                    rs.getString("soyad"),
                    rs.getString("email"),
                    rs.getString("telefon")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Müşteri bilgileri alınırken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public interface MusteriBilgileriHandler {
        void bilgileriAl(String ad, String soyad, String email, String telefon);
    }
    
    public boolean sifreYenile(String tcKimlikNo, String email, String telefon, String yeniSifre) {
        String kontrolQuery = "SELECT COUNT(*) FROM Musteri WHERE tcKimlikNo = ? AND email = ? AND telefon = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement kontrolStmt = conn.prepareStatement(kontrolQuery)) {
            
            kontrolStmt.setString(1, tcKimlikNo);
            kontrolStmt.setString(2, email);
            kontrolStmt.setString(3, telefon);
            
            ResultSet rs = kontrolStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Bilgiler doğruysa şifreyi güncelle
                String updateQuery = "UPDATE Musteri SET sifre = ? WHERE tcKimlikNo = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, yeniSifre);
                updateStmt.setString(2, tcKimlikNo);
                
                int affectedRows = updateStmt.executeUpdate();
                return affectedRows > 0;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Şifre yenileme sırasında hata: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean musteriGuncelle(String tcKimlikNo, String ad, String soyad, String email, String telefon, String yeniSifre) {
        String query;
        if (yeniSifre != null) {
            query = "UPDATE Musteri SET ad = ?, soyad = ?, email = ?, telefon = ?, sifre = ? WHERE tcKimlikNo = ?";
        } else {
            query = "UPDATE Musteri SET ad = ?, soyad = ?, email = ?, telefon = ? WHERE tcKimlikNo = ?";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, ad);
            pstmt.setString(2, soyad);
            pstmt.setString(3, email);
            pstmt.setString(4, telefon);
            
            if (yeniSifre != null) {
                pstmt.setString(5, yeniSifre);
                pstmt.setString(6, tcKimlikNo);
            } else {
                pstmt.setString(5, tcKimlikNo);
            }
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean musteriKayit(Musteri musteri) {
        String query = "INSERT INTO Musteri (tcKimlikNo, ad, soyad, email, telefon, sifre) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, musteri.getTcKimlikNo());
            pstmt.setString(2, musteri.getAd());
            pstmt.setString(3, musteri.getSoyad());
            pstmt.setString(4, musteri.getEmail());
            pstmt.setString(5, musteri.getTelefon());
            pstmt.setString(6, musteri.getSifre());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Müşteri kaydı sırasında hata: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean bakiyeGuncelle(String tcKimlikNo, double tutar, String islemTipi) {
        String updateQuery = "UPDATE Musteri SET bakiye = bakiye + ? WHERE tcKimlikNo = ?";
        String insertQuery = "INSERT INTO Bakiye (tcKimlikNo, islemTarihi, islemTipi, tutar, toplamBakiye) " +
                            "VALUES (?, CURDATE(), ?, ?, (SELECT bakiye FROM Musteri WHERE tcKimlikNo = ?))";
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Bakiyeyi güncelle
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setDouble(1, tutar);
                updateStmt.setString(2, tcKimlikNo);
                updateStmt.executeUpdate();

                // Bakiye işlemini kaydet
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, tcKimlikNo);
                insertStmt.setString(2, islemTipi);
                insertStmt.setDouble(3, tutar);
                insertStmt.setString(4, tcKimlikNo);
                insertStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public double bakiyeGetir(String tcKimlikNo) {
        String query = "SELECT bakiye FROM Musteri WHERE tcKimlikNo = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, tcKimlikNo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("bakiye");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public boolean girisKontrol(String tcKimlikNo, String sifre) {
        String sql = "SELECT COUNT(*) FROM Musteri WHERE tcKimlikNo = ? AND sifre = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tcKimlikNo);
            pstmt.setString(2, sifre);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
} 