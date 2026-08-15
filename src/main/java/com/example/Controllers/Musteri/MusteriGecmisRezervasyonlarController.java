package com.example.Controllers.Musteri;

import com.example.Controllers.baseController;
import com.example.Models.Rezervasyon;
import com.example.Models.Musteri;
import com.example.DataAccess.RezervasyonDAO;
import com.example.DataAccess.DegerlendirmeDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;

public class MusteriGecmisRezervasyonlarController extends baseController {
    
    @FXML private TableView<Rezervasyon> tblRezervasyonlar;
    @FXML private TableColumn<Rezervasyon, Integer> colOdaNo;
    @FXML private TableColumn<Rezervasyon, String> colOdaAdi;
    @FXML private TableColumn<Rezervasyon, String> colOdaTipi;
    @FXML private TableColumn<Rezervasyon, String> colBaslangicTarihi;
    @FXML private TableColumn<Rezervasyon, String> colBitisTarihi;
    @FXML private TableColumn<Rezervasyon, Double> colTutar;
    @FXML private TableColumn<Rezervasyon, String> colDegerlendirme;
    
    @FXML private TextArea txtDegerlendirme;
    @FXML private ComboBox<Integer> cmbPuan;
    @FXML private Button btnDegerlendir;
    @FXML private Button btnGuncelle;
    @FXML private Button btnSil;
    @FXML private Button btnGeriDon;
    
    private RezervasyonDAO rezervasyonDAO = new RezervasyonDAO();
    private DegerlendirmeDAO degerlendirmeDAO = new DegerlendirmeDAO();
    
    @FXML
    public void initialize() {
        cmbPuan.getItems().addAll(1, 2, 3, 4, 5);
        cmbPuan.setValue(5);
        
        colOdaNo.setCellValueFactory(new PropertyValueFactory<>("odaNo"));
        colOdaAdi.setCellValueFactory(new PropertyValueFactory<>("odaAdi"));
        colOdaTipi.setCellValueFactory(new PropertyValueFactory<>("odaTipi"));
        
        colBaslangicTarihi.setCellValueFactory(cellData -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue(cellData.getValue().getBaslangicTarihi().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            return property;
        });
        
        colBitisTarihi.setCellValueFactory(cellData -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue(cellData.getValue().getBitisTarihi().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            return property;
        });
        
        colDegerlendirme.setCellValueFactory(new PropertyValueFactory<>("degerlendirme"));
        
        btnGuncelle.setDisable(true);
        btnSil.setDisable(true);
        
        tblRezervasyonlar.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String degerlendirme = degerlendirmeDAO.degerlendirmeGetir(newSelection.getRezervasyonId());
                txtDegerlendirme.setText(degerlendirme);

                boolean degerlendirmeVar = degerlendirme != null && !degerlendirme.isEmpty();
                btnDegerlendir.setDisable(degerlendirmeVar);
                btnGuncelle.setDisable(!degerlendirmeVar);
                btnSil.setDisable(!degerlendirmeVar);
            }
        });
        
        rezervasyonlariYukle();
    }
    
    @FXML
    private void degerlendirmeEkle() {
        Rezervasyon seciliRezervasyon = tblRezervasyonlar.getSelectionModel().getSelectedItem();
        if (seciliRezervasyon != null) {
            String yorum = txtDegerlendirme.getText().trim();
            if (yorum.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen değerlendirme yazınız!");
                return;
            }
            
            if (degerlendirmeDAO.degerlendirmeEkle(
                seciliRezervasyon.getRezervasyonId(),
                cmbPuan.getValue(),
                txtDegerlendirme.getText()
            )) {
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Değerlendirmeniz kaydedildi.");
                rezervasyonlariYukle();
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "Değerlendirme eklenirken bir hata oluştu!");
            }
        }
    }
    
    @FXML
    private void degerlendirmeGuncelle() {
        Rezervasyon seciliRezervasyon = tblRezervasyonlar.getSelectionModel().getSelectedItem();
        if (seciliRezervasyon != null) {
            String yorum = txtDegerlendirme.getText().trim();
            if (yorum.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen değerlendirme yazınız!");
                return;
            }
            
            if (degerlendirmeDAO.degerlendirmeGuncelle(seciliRezervasyon.getRezervasyonId(),
                                                   cmbPuan.getValue(),
                                                   yorum)) {
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Değerlendirmeniz güncellendi.");
                rezervasyonlariYukle();
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "Değerlendirme güncellenirken bir hata oluştu!");
            }
        }
    }
    
    @FXML
    private void degerlendirmeSil() {
        Rezervasyon seciliRezervasyon = tblRezervasyonlar.getSelectionModel().getSelectedItem();
        if (seciliRezervasyon != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Onay");
            alert.setHeaderText(null);
            alert.setContentText("Değerlendirmeyi silmek istediğinizden emin misiniz?");
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                if (degerlendirmeDAO.degerlendirmeSil(seciliRezervasyon.getRezervasyonId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Değerlendirme silindi.");
                    txtDegerlendirme.clear();
                    rezervasyonlariYukle();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Hata", "Değerlendirme silinirken bir hata oluştu!");
                }
            }
        }
    }
    
    @FXML
    private void geriDon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterirezervasyonlarim.fxml", btnGeriDon);
    }
    
    private void rezervasyonlariYukle() {
        tblRezervasyonlar.setItems(rezervasyonDAO.gecmisRezervasyonlariGetir(
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