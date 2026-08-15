package com.example.DataAccess;

import com.example.Models.Rezervasyon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class RezervasyonDAO {
    
    public boolean rezervasyonOlustur(Rezervasyon rezervasyon) {
        String sql = """
            INSERT INTO Rezervasyon (tcKimlikNo, odaNo, baslangicTarihi, 
                                   bitisTarihi, toplamTutar, durum)
            VALUES (?, ?, ?, ?, ?, 'AKTIF')
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rezervasyon.getTcKimlikNo());
            pstmt.setInt(2, rezervasyon.getOdaNo());
            pstmt.setDate(3, Date.valueOf(rezervasyon.getBaslangicTarihi()));
            pstmt.setDate(4, Date.valueOf(rezervasyon.getBitisTarihi()));
            pstmt.setDouble(5, rezervasyon.getToplamTutar());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<Rezervasyon> musteriRezervasyonlariniGetir(String tcKimlikNo) {
        ObservableList<Rezervasyon> rezervasyonlar = FXCollections.observableArrayList();
        String sql = """
            SELECT r.*, o.odaAdi, o.odaTipi 
            FROM Rezervasyon r
            JOIN Oda o ON r.odaNo = o.odaNo
            WHERE r.tcKimlikNo = ?
            ORDER BY r.baslangicTarihi DESC
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tcKimlikNo);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Rezervasyon rezervasyon = new Rezervasyon();
                rezervasyon.setRezervasyonId(rs.getInt("rezervasyonId"));
                rezervasyon.setTcKimlikNo(rs.getString("tcKimlikNo"));
                rezervasyon.setOdaNo(rs.getInt("odaNo"));
                rezervasyon.setBaslangicTarihi(rs.getDate("baslangicTarihi").toLocalDate());
                rezervasyon.setBitisTarihi(rs.getDate("bitisTarihi").toLocalDate());
                rezervasyon.setToplamTutar(rs.getDouble("toplamTutar"));
                rezervasyon.setDurum(rs.getString("durum"));
                rezervasyonlar.add(rezervasyon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezervasyonlar;
    }

    public boolean durumGuncelle(int rezervasyonId, String yeniDurum) {
        String sql = "UPDATE Rezervasyon SET durum = ? WHERE rezervasyonId = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Sadece izin verilen durumları kontrol et
            if (!yeniDurum.equals("Onaylandı") && !yeniDurum.equals("İptal Edildi")) {
                return false;
            }
            
            pstmt.setString(1, yeniDurum);
            pstmt.setInt(2, rezervasyonId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0 && yeniDurum.equals("İptal Edildi")) {
                String sqlRezervasyon = """
                    SELECT r.tcKimlikNo, r.toplamTutar 
                    FROM Rezervasyon r 
                    WHERE r.rezervasyonId = ?
                """;
                
                try (PreparedStatement pstmtRezervasyon = conn.prepareStatement(sqlRezervasyon)) {
                    pstmtRezervasyon.setInt(1, rezervasyonId);
                    ResultSet rs = pstmtRezervasyon.executeQuery();
                    
                    if (rs.next()) {
                        String tcKimlikNo = rs.getString("tcKimlikNo");
                        double iadeTutari = rs.getDouble("toplamTutar");
                        
                        // Bakiye iadesini yap
                        BakiyeDAO bakiyeDAO = new BakiyeDAO();
                        bakiyeDAO.bakiyeGuncelle(tcKimlikNo, iadeTutari, "REZERVASYON_IPTAL_IADESI");
                    }
                }
            }
            
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ObservableList<Rezervasyon> aktifRezervasyonlariGetir(String tcKimlikNo) {
        ObservableList<Rezervasyon> rezervasyonlar = FXCollections.observableArrayList();
        String sql = """
            SELECT r.*, o.odaAdi, o.odaTipi 
            FROM Rezervasyon r
            JOIN Oda o ON r.odaNo = o.odaNo
            WHERE r.tcKimlikNo = ? 
            AND r.durum = 'AKTIF'
            AND r.bitisTarihi >= CURDATE()
            ORDER BY r.baslangicTarihi
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tcKimlikNo);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Rezervasyon rezervasyon = new Rezervasyon();
                rezervasyon.setRezervasyonId(rs.getInt("rezervasyonId"));
                rezervasyon.setTcKimlikNo(rs.getString("tcKimlikNo"));
                rezervasyon.setOdaNo(rs.getInt("odaNo"));
                rezervasyon.setBaslangicTarihi(rs.getDate("baslangicTarihi").toLocalDate());
                rezervasyon.setBitisTarihi(rs.getDate("bitisTarihi").toLocalDate());
                rezervasyon.setToplamTutar(rs.getDouble("toplamTutar"));
                rezervasyon.setDurum(rs.getString("durum"));
                rezervasyonlar.add(rezervasyon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezervasyonlar;
    }

    public ObservableList<Rezervasyon> gecmisRezervasyonlariGetir(String tcKimlikNo) {
        ObservableList<Rezervasyon> rezervasyonlar = FXCollections.observableArrayList();
        String sql = """
            SELECT r.*, o.odaTipi, o.odaAdi,
                   CASE 
                       WHEN d.degerlendirmeId IS NOT NULL THEN 'Değerlendirme Var'
                       ELSE 'Değerlendirme Yok'
                   END as degerlendirmeDurumu
            FROM Rezervasyon r
            JOIN Oda o ON r.odaNo = o.odaNo
            LEFT JOIN Degerlendirme d ON r.rezervasyonId = d.rezervasyonId
            WHERE r.tcKimlikNo = ? 
            AND r.bitisTarihi < CURRENT_DATE
            ORDER BY r.baslangicTarihi DESC
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tcKimlikNo);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Rezervasyon rezervasyon = new Rezervasyon();
                rezervasyon.setRezervasyonId(rs.getInt("rezervasyonId"));
                rezervasyon.setOdaNo(rs.getInt("odaNo"));
                rezervasyon.setOdaTipi(rs.getString("odaTipi"));
                rezervasyon.setOdaAdi(rs.getString("odaAdi"));
                rezervasyon.setBaslangicTarihi(rs.getDate("baslangicTarihi").toLocalDate());
                rezervasyon.setBitisTarihi(rs.getDate("bitisTarihi").toLocalDate());
                rezervasyon.setToplamTutar(rs.getDouble("toplamTutar"));
                rezervasyon.setDegerlendirme(rs.getString("degerlendirmeDurumu"));
                rezervasyonlar.add(rezervasyon);
            }
        } catch (SQLException e) {
            System.err.println("Geçmiş rezervasyonlar getirilirken hata: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rezervasyonlar;
    }

    public boolean rezervasyonIptalEt(int rezervasyonId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);  // Transaction başlat
            
            // Önce rezervasyon tutarını al
            String sqlTutar = "SELECT toplamTutar, tcKimlikNo FROM Rezervasyon WHERE rezervasyonId = ?";
            double iadeTutari = 0;
            String tcKimlikNo = "";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sqlTutar)) {
                pstmt.setInt(1, rezervasyonId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    iadeTutari = rs.getDouble("toplamTutar");
                    tcKimlikNo = rs.getString("tcKimlikNo");
                }
            }
            
            String sqlIptal = "UPDATE Rezervasyon SET durum = 'IPTAL' WHERE rezervasyonId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlIptal)) {
                pstmt.setInt(1, rezervasyonId);
                pstmt.executeUpdate();
            }
            
            BakiyeDAO bakiyeDAO = new BakiyeDAO();
            bakiyeDAO.bakiyeGuncelle(tcKimlikNo, iadeTutari, "REZERVASYON_IPTAL_IADESI");
            
            conn.commit();  // Transaction'ı onayla
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();  // Hata durumunda geri al
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ObservableList<Rezervasyon> gelecekRezervasyonlariGetir(String tcKimlikNo) {
        ObservableList<Rezervasyon> rezervasyonlar = FXCollections.observableArrayList();
        String sql = """
            SELECT r.*, o.odaTipi, o.odaAdi 
            FROM Rezervasyon r
            JOIN Oda o ON r.odaNo = o.odaNo
            WHERE r.tcKimlikNo = ? 
            AND r.baslangicTarihi > CURRENT_DATE
            AND r.durum != 'IPTAL'
            ORDER BY r.baslangicTarihi ASC
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tcKimlikNo);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Rezervasyon rezervasyon = new Rezervasyon();
                rezervasyon.setRezervasyonId(rs.getInt("rezervasyonId"));
                rezervasyon.setOdaNo(rs.getInt("odaNo"));
                rezervasyon.setOdaTipi(rs.getString("odaTipi"));
                rezervasyon.setOdaAdi(rs.getString("odaAdi"));
                rezervasyon.setBaslangicTarihi(rs.getDate("baslangicTarihi").toLocalDate());
                rezervasyon.setBitisTarihi(rs.getDate("bitisTarihi").toLocalDate());
                rezervasyon.setToplamTutar(rs.getDouble("toplamTutar"));
                rezervasyonlar.add(rezervasyon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezervasyonlar;
    }

    public ObservableList<Rezervasyon> mevcutRezervasyonlariGetir(String tcKimlikNo) {
        ObservableList<Rezervasyon> rezervasyonlar = FXCollections.observableArrayList();
        String sql = """
            SELECT r.*, o.odaTipi, o.odaAdi,
                   COALESCE(ht.durum, 'Talep Yok') as talepDurumu
            FROM Rezervasyon r
            JOIN Oda o ON r.odaNo = o.odaNo
            LEFT JOIN HizmetTalebi ht ON r.rezervasyonId = ht.rezervasyonId
            WHERE r.tcKimlikNo = ? 
            AND r.baslangicTarihi <= CURRENT_DATE 
            AND r.bitisTarihi >= CURRENT_DATE
            AND r.durum != 'IPTAL'
            ORDER BY r.baslangicTarihi DESC
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tcKimlikNo);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Rezervasyon rezervasyon = new Rezervasyon();
                rezervasyon.setRezervasyonId(rs.getInt("rezervasyonId"));
                rezervasyon.setOdaNo(rs.getInt("odaNo"));
                rezervasyon.setOdaTipi(rs.getString("odaTipi"));
                rezervasyon.setOdaAdi(rs.getString("odaAdi"));
                rezervasyon.setBaslangicTarihi(rs.getDate("baslangicTarihi").toLocalDate());
                rezervasyon.setBitisTarihi(rs.getDate("bitisTarihi").toLocalDate());
                rezervasyon.setToplamTutar(rs.getDouble("toplamTutar"));
                rezervasyon.setDurum(rs.getString("talepDurumu"));
                rezervasyonlar.add(rezervasyon);
            }
        } catch (SQLException e) {
            System.err.println("Mevcut rezervasyonlar getirilirken hata: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rezervasyonlar;
    }

    public boolean rezervasyonEkle(Rezervasyon rezervasyon) {
        String sql = "INSERT INTO Rezervasyon (tcKimlikNo, odaNo, baslangicTarihi, bitisTarihi, toplamTutar, durum) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rezervasyon.getTcKimlikNo());
            pstmt.setInt(2, rezervasyon.getOdaNo());
            pstmt.setDate(3, java.sql.Date.valueOf(rezervasyon.getBaslangicTarihi()));
            pstmt.setDate(4, java.sql.Date.valueOf(rezervasyon.getBitisTarihi()));
            pstmt.setDouble(5, rezervasyon.getToplamTutar());
            pstmt.setString(6, rezervasyon.getDurum());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 