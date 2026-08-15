package com.example.DataAccess;

import com.example.Models.Hizmet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class HizmetDAO {
    
    public ObservableList<Hizmet> tumHizmetleriGetir() {
        ObservableList<Hizmet> hizmetler = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Hizmet";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Hizmet hizmet = new Hizmet();
                hizmet.setHizmetId(rs.getInt("hizmetId"));
                hizmet.setHizmetAdi(rs.getString("hizmetAdi"));
                hizmet.setAciklama(rs.getString("aciklama"));
                hizmet.setFiyat(rs.getDouble("fiyat"));
                hizmet.setAktif(rs.getBoolean("aktif"));
                hizmetler.add(hizmet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hizmetler;
    }

    public boolean hizmetSil(int hizmetId) {
        String sql = "DELETE FROM Hizmet WHERE hizmetId = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, hizmetId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hizmetGuncelle(Hizmet hizmet) {
        String sql = "UPDATE Hizmet SET hizmetAdi = ?, aciklama = ?, fiyat = ?, aktif = ? WHERE hizmetId = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, hizmet.getHizmetAdi());
            pstmt.setString(2, hizmet.getAciklama());
            pstmt.setDouble(3, hizmet.getFiyat());
            pstmt.setBoolean(4, hizmet.isAktif());
            pstmt.setInt(5, hizmet.getHizmetId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hizmetEkle(Hizmet hizmet) {
        String sql = "INSERT INTO Hizmet (hizmetAdi, aciklama, fiyat, aktif) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, hizmet.getHizmetAdi());
            pstmt.setString(2, hizmet.getAciklama());
            pstmt.setDouble(3, hizmet.getFiyat());
            pstmt.setBoolean(4, hizmet.isAktif());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 