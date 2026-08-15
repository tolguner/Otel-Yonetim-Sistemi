package com.example.DataAccess;

import java.sql.*;
import com.example.Models.Degerlendirme;

public class DegerlendirmeDAO {
    public String degerlendirmeGetir(int rezervasyonId) {
        String sql = "SELECT yorum FROM Degerlendirme WHERE rezervasyonId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rezervasyonId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("yorum");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean degerlendirmeEkle(Degerlendirme degerlendirme) {
        return degerlendirmeEkle(
            degerlendirme.getRezervasyonId(),
            degerlendirme.getPuan(),
            degerlendirme.getYorum()
        );
    }

    public boolean degerlendirmeEkle(int rezervasyonId, int puan, String yorum) {
        String sql = "INSERT INTO Degerlendirme (rezervasyonId, puan, yorum, tarih) VALUES (?, ?, ?, CURDATE())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rezervasyonId);
            pstmt.setInt(2, puan);
            pstmt.setString(3, yorum);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean degerlendirmeGuncelle(int rezervasyonId, Integer puan, String yorum) {
        String sql = "UPDATE Degerlendirme SET puan = ?, yorum = ? WHERE rezervasyonId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, puan);
            pstmt.setString(2, yorum);
            pstmt.setInt(3, rezervasyonId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean degerlendirmeSil(int rezervasyonId) {
        String sql = "DELETE FROM Degerlendirme WHERE rezervasyonId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rezervasyonId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 