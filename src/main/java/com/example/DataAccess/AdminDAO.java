package com.example.DataAccess;

import com.example.Models.Admin;
import com.example.Models.Yonetici;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdminDAO {
    
    public boolean girisYap(String tcKimlikNo, String sifre) {
        String sql = "SELECT * FROM Admin WHERE tcKimlikNo = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tcKimlikNo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() && PasswordHasher.dogrula(sifre, rs.getString("sifre"))) {
                Admin admin = new Admin();
                admin.setTcKimlikNo(rs.getString("tcKimlikNo"));
                admin.setAd(rs.getString("ad"));
                admin.setSoyad(rs.getString("soyad"));
                admin.setEmail(rs.getString("email"));
                admin.setTelefon(rs.getString("telefon"));
                Admin.oturumAc(admin);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean adminGuncelle(Admin admin) {
        String sql = "UPDATE Admin SET ad = ?, soyad = ?, email = ?, telefon = ?, sifre = ? WHERE tcKimlikNo = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, admin.getAd());
            pstmt.setString(2, admin.getSoyad());
            pstmt.setString(3, admin.getEmail());
            pstmt.setString(4, admin.getTelefon());
            pstmt.setString(5, PasswordHasher.hashle(admin.getSifre()));
            pstmt.setString(6, admin.getTcKimlikNo());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean adminEkle(Admin admin) {
        String sql = "INSERT INTO Admin (tcKimlikNo, ad, soyad, email, telefon, sifre) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, admin.getTcKimlikNo());
            pstmt.setString(2, admin.getAd());
            pstmt.setString(3, admin.getSoyad());
            pstmt.setString(4, admin.getEmail());
            pstmt.setString(5, admin.getTelefon());
            pstmt.setString(6, PasswordHasher.hashle(admin.getSifre()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public interface AdminBilgileriCallback {
        void onBilgiAl(String ad, String soyad, String email, String telefon);
    }

    public static void getAdmin(String tcKimlikNo, AdminBilgileriCallback callback) {
        String sql = "SELECT ad, soyad, email, telefon FROM Admin WHERE tcKimlikNo = ?";
        
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

    public static boolean profilGuncelle(String tcKimlikNo, String ad, String soyad, String email, String telefon) {
        String sql = "UPDATE Admin SET ad = ?, soyad = ?, email = ?, telefon = ? WHERE tcKimlikNo = ?";
        
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

    public static boolean sifreSifirla(String tcKimlikNo, String yeniSifre) {
        String sql = "UPDATE Admin SET sifre = ? WHERE tcKimlikNo = ?";

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

    public static boolean bilgileriKontrolEt(String tcKimlikNo, String telefon, String email) {
        String sql = "SELECT * FROM Admin WHERE tcKimlikNo = ? AND telefon = ? AND email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tcKimlikNo);
            pstmt.setString(2, telefon);
            pstmt.setString(3, email);
            
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sifreGuncelle(String email, String yeniSifre) {
        String sql = "UPDATE Admin SET sifre = ? WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, PasswordHasher.hashle(yeniSifre));
            pstmt.setString(2, email);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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
} 