package com.example.DataAccess;

import com.example.Models.Bakiye;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BakiyeDAO {
    
    private List<BakiyeListener> listeners = new ArrayList<>();

    public interface BakiyeListener {
        void bakiyeGuncellendi(String tcKimlikNo, double yeniBakiye);
    }

    public void addBakiyeListener(BakiyeListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(String tcKimlikNo, double yeniBakiye) {
        for (BakiyeListener listener : listeners) {
            listener.bakiyeGuncellendi(tcKimlikNo, yeniBakiye);
        }
    }

    // Bakiye getir
    public double bakiyeGetir(String tcKimlikNo) {
        String sql = "SELECT COALESCE(MAX(toplamBakiye), 0) as toplamBakiye FROM Bakiye WHERE tcKimlikNo = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tcKimlikNo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double bakiye = rs.getDouble("toplamBakiye");
                System.out.println("Bakiye getirildi: " + bakiye + " TL (TC: " + tcKimlikNo + ")");
                return bakiye;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public boolean bakiyeIslemiYap(String tcKimlikNo, String islemTipi, double tutar) {
        String sql = """
            INSERT INTO Bakiye (tcKimlikNo, islemTarihi, islemTipi, tutar, toplamBakiye)
            VALUES (?, CURDATE(), ?, ?, 
                   (SELECT COALESCE(MAX(toplamBakiye), 0) + ? 
                    FROM Bakiye WHERE tcKimlikNo = ?))
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tcKimlikNo);
            pstmt.setString(2, islemTipi);
            pstmt.setDouble(3, tutar);
            pstmt.setDouble(4, tutar);
            pstmt.setString(5, tcKimlikNo);
            
            if (pstmt.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getBakiye(String tcKimlikNo) {
        String sql = """
            SELECT COALESCE(MAX(toplamBakiye), 0) as bakiye 
            FROM Bakiye 
            WHERE tcKimlikNo = ?
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

    public ObservableList<Bakiye> islemGecmisiGetir(String tcKimlikNo) {
        ObservableList<Bakiye> islemler = FXCollections.observableArrayList();
        String sql = "SELECT islemTarihi, islemTipi, tutar, toplamBakiye " +
                    "FROM Bakiye WHERE tcKimlikNo = ? ORDER BY bakiyeId DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tcKimlikNo);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Bakiye islem = new Bakiye();
                islem.setTcKimlikNo(tcKimlikNo);
                islem.setIslemTarihi(rs.getDate("islemTarihi").toLocalDate());
                islem.setIslemTipi(rs.getString("islemTipi"));
                islem.setTutar(rs.getDouble("tutar"));
                islem.setToplamBakiye(rs.getDouble("toplamBakiye"));
                islemler.add(islem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return islemler;
    }

    public boolean bakiyeYeterliMi(String tcKimlikNo, double gerekliTutar) {
        double mevcutBakiye = bakiyeGetir(tcKimlikNo);
        System.out.println("Bakiye kontrolü: Mevcut=" + mevcutBakiye + ", Gerekli=" + gerekliTutar);
        return mevcutBakiye >= gerekliTutar;
    }

    public boolean bakiyeYukle(String tcKimlikNo, double tutar) {
        String sql = "INSERT INTO Bakiye (tcKimlikNo, islemTarihi, islemTipi, tutar, toplamBakiye) " +
                     "VALUES (?, CURRENT_TIMESTAMP, 'YUKLEME', ?, " +
                     "(SELECT COALESCE(MAX(toplamBakiye), 0) + ? FROM Bakiye b2 WHERE tcKimlikNo = ?))";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tcKimlikNo);
            pstmt.setDouble(2, tutar);
            pstmt.setDouble(3, tutar);
            pstmt.setString(4, tcKimlikNo);
            
            if (pstmt.executeUpdate() > 0) {
                // İşlem başarılı olduğunda dinleyicileri bilgilendir
                double yeniBakiye = bakiyeGetir(tcKimlikNo);
                notifyListeners(tcKimlikNo, yeniBakiye);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean bakiyeGuncelle(String tcKimlikNo, double tutar, String islemTipi) {
        String sql = """
            INSERT INTO Bakiye (tcKimlikNo, islemTarihi, islemTipi, tutar, toplamBakiye) 
            VALUES (?, CURRENT_TIMESTAMP, ?, ?, 
                (SELECT COALESCE(MAX(toplamBakiye), 0) + ? 
                 FROM Bakiye b2 
                 WHERE tcKimlikNo = ?))
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tcKimlikNo);
            pstmt.setString(2, islemTipi);
            pstmt.setDouble(3, tutar);
            pstmt.setDouble(4, tutar);
            pstmt.setString(5, tcKimlikNo);
            
            if (pstmt.executeUpdate() > 0) {
                // İşlem başarılı olduğunda dinleyicileri bilgilendir
                double yeniBakiye = bakiyeGetir(tcKimlikNo);
                notifyListeners(tcKimlikNo, yeniBakiye);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<Bakiye> getBakiyeGecmisi(String tcKimlikNo) {
        ObservableList<Bakiye> bakiyeListesi = FXCollections.observableArrayList();
        String sql = """
            SELECT * FROM Bakiye 
            WHERE tcKimlikNo = ? 
            ORDER BY islemTarihi DESC, bakiyeId DESC
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tcKimlikNo);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Bakiye bakiye = new Bakiye();
                bakiye.setBakiyeId(rs.getInt("bakiyeId"));
                bakiye.setTcKimlikNo(rs.getString("tcKimlikNo"));
                bakiye.setIslemTarihi(rs.getDate("islemTarihi").toLocalDate());
                bakiye.setIslemTipi(rs.getString("islemTipi"));
                bakiye.setTutar(rs.getDouble("tutar"));
                bakiye.setToplamBakiye(rs.getDouble("toplamBakiye"));
                bakiyeListesi.add(bakiye);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bakiyeListesi;
    }
} 