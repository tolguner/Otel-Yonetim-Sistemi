package com.example.Controllers.Admin;

import com.example.Controllers.baseController;
import com.example.DataAccess.YoneticiDAO;
import com.example.Models.Yonetici;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;

public class AdminYoneticiIslemleriController extends baseController {

    @FXML private TableView<Yonetici> yoneticiTablo;
    @FXML private TableColumn<Yonetici, String> tcNoColumn;
    @FXML private TableColumn<Yonetici, String> adColumn;
    @FXML private TableColumn<Yonetici, String> soyadColumn;
    @FXML private TableColumn<Yonetici, String> emailColumn;
    @FXML private TableColumn<Yonetici, String> telefonColumn;
    
    @FXML private TextField tcNoField;
    @FXML private TextField adField;
    @FXML private TextField soyadField;
    @FXML private TextField emailField;
    @FXML private TextField telefonField;
    @FXML private PasswordField sifreField;
    
    @FXML private Button btnEkle;
    @FXML private Button btnGuncelle;
    @FXML private Button btnSil;
    @FXML private Button btnGeriDon;

    private YoneticiDAO yoneticiDAO = new YoneticiDAO();

    @FXML
    public void initialize() {
        try {
            System.out.println("Yönetici tablosu yükleniyor...");
            
            tcNoColumn.setCellValueFactory(new PropertyValueFactory<>("tcKimlikNo"));
            adColumn.setCellValueFactory(new PropertyValueFactory<>("ad"));
            soyadColumn.setCellValueFactory(new PropertyValueFactory<>("soyad"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            telefonColumn.setCellValueFactory(new PropertyValueFactory<>("telefon"));
            
            tabloyuGuncelle();
            
            yoneticiTablo.getSelectionModel().selectedItemProperty().addListener((_, _, yeniSecim) -> {
                if (yeniSecim != null) {
                    System.out.println("Seçilen yönetici: " + yeniSecim.getAd() + " " + yeniSecim.getSoyad());
                    tcNoField.setText(yeniSecim.getTcKimlikNo());
                    adField.setText(yeniSecim.getAd());
                    soyadField.setText(yeniSecim.getSoyad());
                    emailField.setText(yeniSecim.getEmail());
                    telefonField.setText(yeniSecim.getTelefon());
                    sifreField.setText(yeniSecim.getSifre());
                }
            });
            
            System.out.println("Initialize tamamlandı.");
        } catch (Exception e) {
            System.err.println("Initialize hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void tabloyuGuncelle() {
        try {
            System.out.println("Tablo güncelleniyor...");
            ObservableList<Yonetici> yoneticiList = YoneticiDAO.tumYoneticileriGetir();
            yoneticiTablo.setItems(yoneticiList);
            System.out.println("Tablo güncellendi. Kayıt sayısı: " + yoneticiList.size());
        } catch (Exception e) {
            System.err.println("Tablo güncelleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    public void yoneticiEkle() {
        try {
            if (alanlariBosmu()) return;
            
            Yonetici yeniYonetici = new Yonetici(
                tcNoField.getText(),
                adField.getText(),
                soyadField.getText(),
                emailField.getText(),
                telefonField.getText(),
                sifreField.getText()
            );
            
            System.out.println("Yönetici ekleniyor: " + yeniYonetici.getAd() + " " + yeniYonetici.getSoyad());
            
            if (YoneticiDAO.yoneticiEkle(yeniYonetici)) {
                tabloyuGuncelle();
                alanlariTemizle();
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Yönetici başarıyla eklendi.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "Yönetici eklenirken bir hata oluştu!");
            }
        } catch (Exception e) {
            System.err.println("Yönetici ekleme hatası: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", "Beklenmeyen bir hata oluştu!");
        }
    }
    
    @FXML
    public void yoneticiGuncelle() {
        if (alanlariBosmu()) return;
        
        Yonetici guncelYonetici = new Yonetici(
            tcNoField.getText(),
            adField.getText(),
            soyadField.getText(),
            emailField.getText(),
            telefonField.getText(),
            sifreField.getText()
        );
        
        if (yoneticiDAO.yoneticiGuncelle(guncelYonetici)) {
            tabloyuGuncelle();
            alanlariTemizle();
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Yönetici başarıyla güncellendi.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Hata", "Yönetici güncellenirken bir hata oluştu.");
        }
    }
    
    @FXML
    public void yoneticiSil() {
        Yonetici seciliYonetici = yoneticiTablo.getSelectionModel().getSelectedItem();
        if (seciliYonetici == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen silinecek yöneticiyi seçin.");
            return;
        }
        
        if (YoneticiDAO.yoneticiSil(seciliYonetici.getTcKimlikNo())) {
            tabloyuGuncelle();
            alanlariTemizle();
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Yönetici başarıyla silindi.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Hata", "Yönetici silinirken bir hata oluştu.");
        }
    }
    
    private boolean alanlariBosmu() {
        if (tcNoField.getText().isEmpty() || adField.getText().isEmpty() || 
            soyadField.getText().isEmpty() || emailField.getText().isEmpty() || 
            telefonField.getText().isEmpty() || sifreField.getText().isEmpty()) {
            
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen tüm alanları doldurun.");
            return true;
        }
        return false;
    }
    
    private void alanlariTemizle() {
        tcNoField.clear();
        adField.clear();
        soyadField.clear();
        emailField.clear();
        telefonField.clear();
        sifreField.clear();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    public void geriDon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Admin/adminanasayfa.fxml", btnGeriDon);
    }
} 