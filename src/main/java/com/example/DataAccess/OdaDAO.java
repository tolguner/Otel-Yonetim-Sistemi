package com.example.DataAccess;

import com.example.Models.Oda;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

public class OdaDAO {
    
    // Tüm odaları getir
    public ObservableList<Oda> tumOdalariGetir() {
        ObservableList<Oda> odalar = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Oda";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Oda oda = new Oda();
                oda.setOdaNo(rs.getInt("odaNo"));
                oda.setOdaAdi(rs.getString("odaAdi"));
                oda.setOdaTipi(rs.getString("odaTipi"));
                oda.setKapasite(rs.getInt("kapasite"));
                oda.setFiyat(rs.getDouble("fiyat"));
                oda.setMusaitlikDurumu(rs.getBoolean("musaitlikDurumu"));
                oda.setOzellikler(rs.getString("ozellikler"));
                odalar.add(oda);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return odalar;
    }
    
    public boolean odaEkle(Oda oda) {
        String sql = """
            INSERT INTO Oda (odaNo, odaAdi, odaTipi, kapasite, 
                           fiyat, musaitlikDurumu, ozellikler)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, oda.getOdaNo());
            pstmt.setString(2, oda.getOdaAdi());
            pstmt.setString(3, oda.getOdaTipi());
            pstmt.setInt(4, oda.getKapasite());
            pstmt.setDouble(5, oda.getFiyat());
            pstmt.setBoolean(6, oda.isMusaitlikDurumu());
            pstmt.setString(7, oda.getOzellikler());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean odaGuncelle(Oda oda) {
        String sql = """
            UPDATE Oda SET odaAdi = ?, odaTipi = ?, kapasite = ?, 
                          fiyat = ?, musaitlikDurumu = ?, ozellikler = ?
            WHERE odaNo = ?
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, oda.getOdaAdi());
            pstmt.setString(2, oda.getOdaTipi());
            pstmt.setInt(3, oda.getKapasite());
            pstmt.setDouble(4, oda.getFiyat());
            pstmt.setBoolean(5, oda.isMusaitlikDurumu());
            pstmt.setString(6, oda.getOzellikler());
            pstmt.setInt(7, oda.getOdaNo());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean odaSil(int odaNo) {
        String query = "DELETE FROM Oda WHERE odaNo = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, odaNo);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Oda silinirken hata: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public ObservableList<Oda> musaitOdalariGetir(LocalDate baslangicTarihi, LocalDate bitisTarihi, 
                                                 String odaTipi, int kapasite) {
        String query;
        if (baslangicTarihi == null || bitisTarihi == null) {
            query = "SELECT * FROM Oda ORDER BY fiyat ASC";
        } else {
            query = """
                SELECT DISTINCT o.* 
                FROM Oda o 
                WHERE o.kapasite >= ? 
                AND (? IS NULL OR ? = '' OR o.odaTipi = ?) 
                AND o.odaNo NOT IN (
                    SELECT r.odaNo
                    FROM Rezervasyon r
                    WHERE r.durum != 'IPTAL'
                    AND NOT (
                        r.bitisTarihi < ? OR
                        r.baslangicTarihi > ?
                    )
                )
                ORDER BY o.fiyat ASC
            """;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            if (baslangicTarihi != null && bitisTarihi != null) {
                int paramIndex = 1;
                pstmt.setInt(paramIndex++, kapasite);
                pstmt.setString(paramIndex++, odaTipi);
                pstmt.setString(paramIndex++, odaTipi);
                pstmt.setString(paramIndex++, odaTipi);
                pstmt.setDate(paramIndex++, java.sql.Date.valueOf(baslangicTarihi));
                pstmt.setDate(paramIndex++, java.sql.Date.valueOf(bitisTarihi));

                System.out.println("Arama Kriterleri:");
                System.out.println("Kapasite: " + kapasite);
                System.out.println("Oda Tipi: " + odaTipi);
                System.out.println("Başlangıç: " + baslangicTarihi);
                System.out.println("Bitiş: " + bitisTarihi);
            }

            ResultSet rs = pstmt.executeQuery();
            ObservableList<Oda> odalar = FXCollections.observableArrayList();
            
            while (rs.next()) {
                Oda oda = new Oda(
                    rs.getInt("odaNo"),
                    rs.getString("odaAdi"),
                    rs.getString("odaTipi"),
                    rs.getInt("kapasite"),
                    rs.getDouble("fiyat"),
                    rs.getBoolean("musaitlikDurumu"),
                    rs.getString("ozellikler")
                );
                odalar.add(oda);
                System.out.println("Bulunan Oda: No=" + oda.getOdaNo() + 
                                 ", Tip=" + oda.getOdaTipi() + 
                                 ", Kapasite=" + oda.getKapasite());
            }
            return odalar;
        } catch (SQLException e) {
            System.err.println("Oda arama hatası: " + e.getMessage());
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }
    
    public ObservableList<Oda> uygunOdalariGetir(LocalDate girisTarihi, LocalDate cikisTarihi, 
                                                String odaTipi, int kisiSayisi) {
        ObservableList<Oda> odaList = FXCollections.observableArrayList();
        
        String sql = """
            SELECT * FROM Oda 
            WHERE kapasite >= ? 
            AND (?  IS NULL OR odaTipi = ?) 
            AND odaId NOT IN (
                SELECT odaId FROM Rezervasyon 
                WHERE durum = 'ONAYLANDI'
                AND (
                    (baslangicTarihi <= ? AND bitisTarihi >= ?) OR 
                    (baslangicTarihi <= ? AND bitisTarihi >= ?) OR 
                    (baslangicTarihi >= ? AND bitisTarihi <= ?)
                )
            )
            ORDER BY fiyat ASC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, kisiSayisi);
            pstmt.setString(2, odaTipi);
            pstmt.setString(3, odaTipi);
            pstmt.setDate(4, java.sql.Date.valueOf(girisTarihi));
            pstmt.setDate(5, java.sql.Date.valueOf(girisTarihi));
            pstmt.setDate(6, java.sql.Date.valueOf(cikisTarihi));
            pstmt.setDate(7, java.sql.Date.valueOf(cikisTarihi));
            pstmt.setDate(8, java.sql.Date.valueOf(girisTarihi));
            pstmt.setDate(9, java.sql.Date.valueOf(cikisTarihi));
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Oda oda = new Oda(
                    rs.getInt("odaNo"),
                    rs.getString("odaAdi"),
                    rs.getString("odaTipi"),
                    rs.getInt("kapasite"),
                    rs.getDouble("fiyat"),
                    rs.getBoolean("musaitlikDurumu"),
                    rs.getString("ozellikler")
                );
                oda.setOdaId(rs.getInt("odaId"));
                odaList.add(oda);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return odaList;
    }
    
    public ObservableList<String> odaTipleriniGetir() {
        ObservableList<String> odaTipleri = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT odaTipi FROM Oda ORDER BY odaTipi";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                odaTipleri.add(rs.getString("odaTipi"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return odaTipleri;
    }
    
    public ObservableList<Oda> musaitOdalariGetir(LocalDate girisTarihi, LocalDate cikisTarihi, 
                                                 String odaTipi, Double minFiyat, Double maxFiyat) {
        ObservableList<Oda> odalar = FXCollections.observableArrayList();
        
        StringBuilder query = new StringBuilder(
            "SELECT * FROM Oda WHERE odaId NOT IN " +
            "(SELECT odaId FROM Rezervasyon WHERE " +
            "(? BETWEEN girisTarihi AND cikisTarihi) OR " +
            "(? BETWEEN girisTarihi AND cikisTarihi) OR " +
            "(girisTarihi BETWEEN ? AND ?))"
        );
        
        if (odaTipi != null && !odaTipi.isEmpty()) {
            query.append(" AND odaTipi = ?");
        }
        if (minFiyat != null) {
            query.append(" AND fiyat >= ?");
        }
        if (maxFiyat != null) {
            query.append(" AND fiyat <= ?");
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            
            int paramIndex = 1;
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(girisTarihi));
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(cikisTarihi));
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(girisTarihi));
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(cikisTarihi));
            
            if (odaTipi != null && !odaTipi.isEmpty()) {
                pstmt.setString(paramIndex++, odaTipi);
            }
            if (minFiyat != null) {
                pstmt.setDouble(paramIndex++, minFiyat);
            }
            if (maxFiyat != null) {
                pstmt.setDouble(paramIndex++, maxFiyat);
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Oda oda = new Oda();
                oda.setOdaId(rs.getInt("odaId"));
                oda.setOdaNo(rs.getInt("odaNo"));
                oda.setOdaTipi(rs.getString("odaTipi"));
                oda.setKapasite(rs.getInt("kapasite"));
                oda.setFiyat(rs.getDouble("fiyat"));
                oda.setOzellikler(rs.getString("ozellikler"));
                odalar.add(oda);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return odalar;
    }
} 