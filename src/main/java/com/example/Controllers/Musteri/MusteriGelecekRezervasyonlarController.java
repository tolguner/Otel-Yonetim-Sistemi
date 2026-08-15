package com.example.Controllers.Musteri;

import com.example.Controllers.baseController;
import com.example.Models.Rezervasyon;
import com.example.Models.Musteri;
import com.example.DataAccess.RezervasyonDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;

public class MusteriGelecekRezervasyonlarController extends baseController {
    
    @FXML private TableView<Rezervasyon> tblRezervasyonlar;
    @FXML private TableColumn<Rezervasyon, Integer> colOdaNo;
    @FXML private TableColumn<Rezervasyon, String> colOdaAdi;
    @FXML private TableColumn<Rezervasyon, String> colOdaTipi;
    @FXML private TableColumn<Rezervasyon, LocalDate> colBaslangic;
    @FXML private TableColumn<Rezervasyon, LocalDate> colBitis;
    @FXML private TableColumn<Rezervasyon, Double> colTutar;
    
    @FXML private Label lblSecilenRezervasyon;
    @FXML private Label lblIadeTutari;
    @FXML private Button btnIptalEt;
    @FXML private Button btnGeriDon;
    
    private RezervasyonDAO rezervasyonDAO = new RezervasyonDAO();
    
    @FXML
    public void initialize() {
        colOdaNo.setCellValueFactory(new PropertyValueFactory<>("odaNo"));
        colOdaAdi.setCellValueFactory(new PropertyValueFactory<>("odaAdi"));
        colOdaTipi.setCellValueFactory(new PropertyValueFactory<>("odaTipi"));
        colBaslangic.setCellValueFactory(new PropertyValueFactory<>("baslangicTarihi"));
        colBitis.setCellValueFactory(new PropertyValueFactory<>("bitisTarihi"));
        colTutar.setCellValueFactory(new PropertyValueFactory<>("toplamTutar"));
        
        btnIptalEt.setDisable(true);
        
        tblRezervasyonlar.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                lblSecilenRezervasyon.setText(String.format("Oda: %d\nGiriş: %s\nÇıkış: %s", 
                    newSelection.getOdaNo(), 
                    newSelection.getBaslangicTarihi(), 
                    newSelection.getBitisTarihi()));
                lblIadeTutari.setText(String.format("İade Tutarı: %.2f TL", newSelection.getToplamTutar()));
                btnIptalEt.setDisable(false);
            } else {
                lblSecilenRezervasyon.setText("Lütfen iptal etmek istediğiniz rezervasyonu seçin");
                lblIadeTutari.setText("İade Tutarı: 0 TL");
                btnIptalEt.setDisable(true);
            }
        });
        
        rezervasyonlariYukle();
    }
    
    @FXML
    private void rezervasyonIptalEt() {
        Rezervasyon seciliRezervasyon = tblRezervasyonlar.getSelectionModel().getSelectedItem();
        if (seciliRezervasyon != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Rezervasyon İptali");
            alert.setHeaderText(null);
            alert.setContentText("Seçili rezervasyonu iptal etmek istediğinize emin misiniz?");
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                if (rezervasyonDAO.rezervasyonIptalEt(seciliRezervasyon.getRezervasyonId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Rezervasyon başarıyla iptal edildi.");
                    rezervasyonlariYukle();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Hata", "Rezervasyon iptal edilirken bir hata oluştu!");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen iptal edilecek rezervasyonu seçin!");
        }
    }
    
    @FXML
    private void geriDon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterirezervasyonlarim.fxml", btnGeriDon);
    }
    
    private void rezervasyonlariYukle() {
        tblRezervasyonlar.setItems(rezervasyonDAO.gelecekRezervasyonlariGetir(
            Musteri.getAktifMusteri().getTcKimlikNo()
        ));
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 