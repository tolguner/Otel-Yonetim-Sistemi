package com.example.Controllers.Yonetici;

import com.example.Controllers.baseController;
import com.example.DataAccess.HizmetTalebiDAO;
import com.example.Models.HizmetTalebi;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;

public class YoneticiHizmetTalebiController extends baseController {
    
    @FXML private TableView<HizmetTalebi> tblTalepler;
    @FXML private TableColumn<HizmetTalebi, Integer> colOdaNo;
    @FXML private TableColumn<HizmetTalebi, String> colHizmet;
    @FXML private TableColumn<HizmetTalebi, String> colAciklama;
    @FXML private TableColumn<HizmetTalebi, String> colTarih;
    @FXML private TableColumn<HizmetTalebi, String> colDurum;
    
    @FXML private Label lblOdaNo;
    @FXML private Label lblHizmet;
    @FXML private Label lblAciklama;
    @FXML private Label lblTalepTarihi;
    @FXML private ComboBox<String> cmbDurum;
    
    @FXML private Button btnGeriDon;
    
    private HizmetTalebiDAO hizmetTalebiDAO = new HizmetTalebiDAO();
    private HizmetTalebi seciliTalep;
    
    @FXML
    public void initialize() {
        tabloyuAyarla();
        talepleriYukle();
        durumSecenekleriniYukle();
        
        tblTalepler.getSelectionModel().selectedItemProperty().addListener((obs, eskiSecim, yeniSecim) -> {
            if (yeniSecim != null) {
                seciliTalep = yeniSecim;
                detaylariGoster(yeniSecim);
            }
        });
    }
    
    private void tabloyuAyarla() {
        colOdaNo.setCellValueFactory(new PropertyValueFactory<>("odaNo"));
        colHizmet.setCellValueFactory(new PropertyValueFactory<>("hizmetAdi"));
        colAciklama.setCellValueFactory(new PropertyValueFactory<>("aciklama"));
        
        colTarih.setCellValueFactory(cellData -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue(cellData.getValue().getTalepTarihi()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            return property;
        });
        
        colDurum.setCellValueFactory(new PropertyValueFactory<>("durum"));
    }
    
    private void durumSecenekleriniYukle() {
        cmbDurum.getItems().clear();
        cmbDurum.getItems().addAll(
            "Talebiniz İletildi",
            "Talebiniz Onaylandı"
        );
    }
    
    private void detaylariGoster(HizmetTalebi talep) {
        lblOdaNo.setText(String.valueOf(talep.getOdaNo()));
        lblHizmet.setText(talep.getHizmetAdi());
        lblAciklama.setText(talep.getAciklama());
        lblTalepTarihi.setText(talep.getTalepTarihi()
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        cmbDurum.setValue(talep.getDurum());
    }
    
    @FXML
    private void durumGuncelle() {
        if (seciliTalep == null || cmbDurum.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen bir durum seçiniz!");
            return;
        }
        
        if (hizmetTalebiDAO.durumGuncelle(seciliTalep.getTalepId(), cmbDurum.getValue())) {
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Talep durumu güncellendi!");
            talepleriYukle();
        } else {
            showAlert(Alert.AlertType.ERROR, "Hata", "Talep durumu güncellenirken bir hata oluştu!");
        }
    }
    
    private void talepleriYukle() {
        ObservableList<HizmetTalebi> talepler = hizmetTalebiDAO.tumTalepleriGetir();
        tblTalepler.setItems(talepler);
    }
    
    @FXML
    private void geriDon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Yonetici/yoneticianasayfa.fxml", btnGeriDon);
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 