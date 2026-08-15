package com.example.Controllers.Admin;

import com.example.Controllers.baseController;
import com.example.Models.Admin;
import com.example.DataAccess.AdminDAO;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class adminProfilimController extends baseController {

    @FXML
    private TextField tcNoField;
    
    @FXML
    private TextField adField;
    
    @FXML
    private TextField soyadField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField telefonField;
    
    @FXML
    private Button btnprofilguncelle;
    
    @FXML
    private Button btngeridon;

    @FXML
    public void initialize() {
        try {
            System.out.println("\nAdmin Profili Yükleniyor...");
            Admin aktifAdmin = Admin.getAktifAdmin();
            System.out.println("TC: " + aktifAdmin.getTcKimlikNo());
            System.out.println("Ad: " + aktifAdmin.getAd());
            System.out.println("Soyad: " + aktifAdmin.getSoyad());
            System.out.println("Email: " + aktifAdmin.getEmail());
            System.out.println("Telefon: " + aktifAdmin.getTelefon());
            
            Platform.runLater(() -> {
                try {
                    // TC Kimlik No alanını devre dışı bırak
                    tcNoField.setText(aktifAdmin.getTcKimlikNo());
                    tcNoField.setEditable(false);
                    tcNoField.setStyle("-fx-background-radius: 10; -fx-background-color: #f0f0f0;");
                    
                    // Diğer alanları doldur
                    adField.setText(aktifAdmin.getAd());
                    soyadField.setText(aktifAdmin.getSoyad());
                    emailField.setText(aktifAdmin.getEmail());
                    telefonField.setText(aktifAdmin.getTelefon());
                    
                    System.out.println("Text field'lar dolduruldu!");
                } catch (Exception e) {
                    System.err.println("Text field doldurma hatası: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("Initialize hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void goprofilguncelle() {
        String tcNo = tcNoField.getText();
        String ad = adField.getText();
        String soyad = soyadField.getText();
        String email = emailField.getText();
        String telefon = telefonField.getText();
        

        if (ad.isEmpty() || soyad.isEmpty() || email.isEmpty() || telefon.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uyarı");
            alert.setHeaderText(null);
            alert.setContentText("Lütfen tüm alanları doldurunuz!");
            alert.showAndWait();
            return;
        }
        
        if (AdminDAO.profilGuncelle(tcNo, ad, soyad, email, telefon)) {
            // Oturum bilgilerini güncelle
            Admin.oturumBilgileriniAyarla(tcNo, ad, soyad, email, telefon);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Başarılı");
            alert.setHeaderText(null);
            alert.setContentText("Profiliniz başarıyla güncellendi!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Profil güncellenirken bir hata oluştu!");
            alert.showAndWait();
        }
    }

    @FXML
    public void gogeridon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Admin/adminanasayfa.fxml", btngeridon);
    }
} 