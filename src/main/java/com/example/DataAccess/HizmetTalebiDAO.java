package com.example.DataAccess;

import com.example.Models.HizmetTalebi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class HizmetTalebiDAO {
    
    public boolean talepOlustur(HizmetTalebi talep) {
        // Önce hizmetId'yi al
        String getHizmetIdSql = "SELECT hizmetId FROM Hizmet WHERE hizmetAdi = ?";
        int hizmetId = 0;
        
        try (Connection conn = DBConnection.getConnection()) {
            // Debug için hizmet adını yazdır
            System.out.println("Aranan hizmet adı: " + talep.getHizmetAdi());
            
            // Hizmet ID'sini al
            try (PreparedStatement pstmt = conn.prepareStatement(getHizmetIdSql)) {
                pstmt.setString(1, talep.getHizmetAdi());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    hizmetId = rs.getInt("hizmetId");
                    System.out.println("Bulunan hizmetId: " + hizmetId);
                } else {
                    System.out.println("Hizmet bulunamadı!");
                    return false;
                }
            }
            
            // Talebi oluştur
            String insertSql = """
                INSERT INTO HizmetTalebi (rezervasyonId, hizmetId, aciklama, 
                                        talepTarihi, durum, fiyat, odaNo)
                VALUES (?, ?, ?, CURRENT_TIMESTAMP, 'Talebiniz İletildi', ?, ?)
            """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, talep.getRezervasyonId());
                pstmt.setInt(2, hizmetId);
                pstmt.setString(3, talep.getAciklama());
                pstmt.setDouble(4, talep.getFiyat());
                pstmt.setInt(5, talep.getOdaNo());
                
                // Debug için SQL'i yazdır
                System.out.println("Çalıştırılan SQL: " + pstmt.toString());
                
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Hata detayı: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public ObservableList<HizmetTalebi> talepleriGetir(int rezervasyonId) {
        ObservableList<HizmetTalebi> talepler = FXCollections.observableArrayList();
        String sql = """
            SELECT ht.*, h.hizmetAdi 
            FROM HizmetTalebi ht
            JOIN Hizmet h ON ht.hizmetId = h.hizmetId
            WHERE ht.rezervasyonId = ?
            ORDER BY ht.talepTarihi DESC
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rezervasyonId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                HizmetTalebi talep = new HizmetTalebi();
                talep.setTalepId(rs.getInt("talepId"));
                talep.setRezervasyonId(rs.getInt("rezervasyonId"));
                talep.setHizmetId(rs.getInt("hizmetId"));
                talep.setHizmetAdi(rs.getString("hizmetAdi"));
                talep.setAciklama(rs.getString("aciklama"));
                talep.setDurum(rs.getString("durum"));
                talep.setTalepTarihi(rs.getTimestamp("talepTarihi").toLocalDateTime());
                talep.setFiyat(rs.getDouble("fiyat"));
                talepler.add(talep);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return talepler;
    }

    public boolean durumGuncelle(int talepId, String yeniDurum) {
        // Sadece izin verilen durumları kontrol et
        if (!yeniDurum.equals("Talebiniz İletildi") && !yeniDurum.equals("Talebiniz Onaylandı")) {
            return false;
        }

        String sql = "UPDATE HizmetTalebi SET durum = ? WHERE talepId = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, yeniDurum);
            pstmt.setInt(2, talepId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getHizmetUcreti(String hizmetAdi) {
        String sql = "SELECT fiyat FROM Hizmet WHERE hizmetAdi = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, hizmetAdi);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("fiyat");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public String getHizmetDetay(String hizmetAdi) {
        String sql = "SELECT aciklama FROM Hizmet WHERE hizmetAdi = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, hizmetAdi);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("aciklama");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return "Detay bulunamadı";
    }

    public HizmetTalebi talepGetir(int rezervasyonId) {
        String sql = """
            SELECT ht.*, h.hizmetAdi 
            FROM HizmetTalebi ht
            JOIN Hizmet h ON ht.hizmetId = h.hizmetId
            WHERE ht.rezervasyonId = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rezervasyonId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                HizmetTalebi talep = new HizmetTalebi();
                talep.setTalepId(rs.getInt("talepId"));
                talep.setRezervasyonId(rs.getInt("rezervasyonId"));
                talep.setHizmetId(rs.getInt("hizmetId"));
                talep.setHizmetAdi(rs.getString("hizmetAdi"));
                talep.setAciklama(rs.getString("aciklama"));
                talep.setDurum(rs.getString("durum"));
                talep.setFiyat(rs.getDouble("fiyat"));
                talep.setOdaNo(rs.getInt("odaNo"));
                return talep;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Hizmet talebini günceller (hizmet türü değişmiş olabilir). Yeni/eski fiyat
     * farkı varsa müşterinin bakiyesinden düşülür veya iade edilir.
     */
    public boolean talepGuncelle(int talepId, String yeniHizmetAdi, String yeniAciklama,
                                  double eskiFiyat, String tcKimlikNo) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int hizmetId = 0;
            double yeniFiyat = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT hizmetId, fiyat FROM Hizmet WHERE hizmetAdi = ?")) {
                pstmt.setString(1, yeniHizmetAdi);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    hizmetId = rs.getInt("hizmetId");
                    yeniFiyat = rs.getDouble("fiyat");
                } else {
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE HizmetTalebi SET hizmetId = ?, aciklama = ?, fiyat = ? WHERE talepId = ?")) {
                pstmt.setInt(1, hizmetId);
                pstmt.setString(2, yeniAciklama);
                pstmt.setDouble(3, yeniFiyat);
                pstmt.setInt(4, talepId);
                if (pstmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            double fark = yeniFiyat - eskiFiyat;
            if (fark != 0) {
                BakiyeDAO bakiyeDAO = new BakiyeDAO();
                bakiyeDAO.bakiyeGuncelle(tcKimlikNo, -fark,
                        fark > 0 ? "HIZMET_ODEMESI" : "HIZMET_IADESI");
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
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

    public boolean talepSil(int talepId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);  // Transaction başlat
            
            // Önce hizmet tutarını ve müşteri bilgisini al
            String sqlTutar = """
                SELECT ht.fiyat, r.tcKimlikNo 
                FROM HizmetTalebi ht
                JOIN Rezervasyon r ON ht.rezervasyonId = r.rezervasyonId
                WHERE ht.talepId = ?
            """;
            
            double iadeTutari = 0;
            String tcKimlikNo = "";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sqlTutar)) {
                pstmt.setInt(1, talepId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    iadeTutari = rs.getDouble("fiyat");
                    tcKimlikNo = rs.getString("tcKimlikNo");
                }
            }
            
            // Hizmet talebini sil
            String sqlSil = "DELETE FROM HizmetTalebi WHERE talepId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlSil)) {
                pstmt.setInt(1, talepId);
                pstmt.executeUpdate();
            }
            
            // Bakiyeyi güncelle
            if (iadeTutari > 0 && !tcKimlikNo.isEmpty()) {
                BakiyeDAO bakiyeDAO = new BakiyeDAO();
                bakiyeDAO.bakiyeGuncelle(tcKimlikNo, iadeTutari, "HIZMET_IPTAL_IADESI");
            }
            
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

    public ObservableList<HizmetTalebi> tumTalepleriGetir() {
        ObservableList<HizmetTalebi> talepler = FXCollections.observableArrayList();
        String sql = """
            SELECT ht.*, h.hizmetAdi, r.tcKimlikNo, m.ad, m.soyad
            FROM HizmetTalebi ht
            JOIN Hizmet h ON ht.hizmetId = h.hizmetId
            JOIN Rezervasyon r ON ht.rezervasyonId = r.rezervasyonId
            JOIN Musteri m ON r.tcKimlikNo = m.tcKimlikNo
            ORDER BY ht.talepTarihi DESC
        """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                HizmetTalebi talep = new HizmetTalebi();
                talep.setTalepId(rs.getInt("talepId"));
                talep.setRezervasyonId(rs.getInt("rezervasyonId"));
                talep.setHizmetId(rs.getInt("hizmetId"));
                talep.setHizmetAdi(rs.getString("hizmetAdi"));
                talep.setAciklama(rs.getString("aciklama"));
                talep.setTalepTarihi(rs.getTimestamp("talepTarihi").toLocalDateTime());
                talep.setDurum(rs.getString("durum"));
                talep.setFiyat(rs.getDouble("fiyat"));
                talep.setOdaNo(rs.getInt("odaNo"));
                talepler.add(talep);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return talepler;
    }

    public ObservableList<String> getHizmetler() {
        ObservableList<String> hizmetler = FXCollections.observableArrayList();
        String sql = "SELECT hizmetAdi FROM Hizmet ORDER BY hizmetAdi";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // Debug için
            System.out.println("Hizmetler sorgusu çalıştırılıyor...");
            
            while (rs.next()) {
                String hizmetAdi = rs.getString("hizmetAdi");
                hizmetler.add(hizmetAdi);
                // Debug için
                System.out.println("Bulunan hizmet: " + hizmetAdi);
            }
            
            // Debug için
            System.out.println("Toplam " + hizmetler.size() + " hizmet bulundu.");
            
        } catch (SQLException e) {
            System.err.println("Hizmetler getirilirken hata: " + e.getMessage());
            e.printStackTrace();
        }
        
        return hizmetler;
    }
} 