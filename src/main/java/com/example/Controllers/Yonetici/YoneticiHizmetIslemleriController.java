package com.example.Controllers.Yonetici;

import com.example.Controllers.baseController;
import com.example.DataAccess.HizmetDAO;
import com.example.Models.Hizmet;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class YoneticiHizmetIslemleriController extends baseController {
    
    @FXML private Button btnGeriDon;
    @FXML private TableView<Hizmet> tblHizmetler;
    @FXML private TableColumn<Hizmet, String> colHizmetAdi;
    @FXML private TableColumn<Hizmet, String> colAciklama;
    @FXML private TableColumn<Hizmet, Double> colFiyat;
    @FXML private TableColumn<Hizmet, Boolean> colDurum;
    
    @FXML private TextField txtHizmetAdi;
    @FXML private TextArea txtAciklama;
    @FXML private TextField txtFiyat;
    @FXML private CheckBox chkAktif;
    @FXML private Label lblMesaj;
    
    private HizmetDAO hizmetDAO = new HizmetDAO();
    private Hizmet seciliHizmet;
    
    @FXML
    public void initialize() {
        tabloyuAyarla();
        tabloyuGuncelle();
        tabloSecimDinleyicisiEkle();
    }
    
    private void tabloyuAyarla() {
        colHizmetAdi.setCellValueFactory(new PropertyValueFactory<>("hizmetAdi"));
        colAciklama.setCellValueFactory(new PropertyValueFactory<>("aciklama"));
        colFiyat.setCellValueFactory(new PropertyValueFactory<>("fiyat"));
        colDurum.setCellValueFactory(new PropertyValueFactory<>("aktif"));
        
        colDurum.setCellFactory(col -> new TableCell<Hizmet, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Aktif" : "Pasif");
                }
            }
        });
    }
    
    private void tabloyuGuncelle() {
        tblHizmetler.setItems(hizmetDAO.tumHizmetleriGetir());
    }
    
    private void tabloSecimDinleyicisiEkle() {
        tblHizmetler.getSelectionModel().selectedItemProperty().addListener((obs, eskiSecim, yeniSecim) -> {
            if (yeniSecim != null) {
                seciliHizmet = yeniSecim;
                formuDoldur(yeniSecim);
            }
        });
    }
    
    private void formuDoldur(Hizmet hizmet) {
        txtHizmetAdi.setText(hizmet.getHizmetAdi());
        txtAciklama.setText(hizmet.getAciklama());
        txtFiyat.setText(String.valueOf(hizmet.getFiyat()));
        chkAktif.setSelected(hizmet.isAktif());
    }
    
    @FXML
    private void hizmetEkle() {
        if (!formGecerliMi()) return;
        
        Hizmet yeniHizmet = new Hizmet();
        yeniHizmet.setHizmetAdi(txtHizmetAdi.getText());
        yeniHizmet.setAciklama(txtAciklama.getText());
        yeniHizmet.setFiyat(Double.parseDouble(txtFiyat.getText()));
        yeniHizmet.setAktif(chkAktif.isSelected());
        
        if (hizmetDAO.hizmetEkle(yeniHizmet)) {
            lblMesaj.setText("Hizmet başarıyla eklendi.");
            tabloyuGuncelle();
            formuTemizle();
        } else {
            lblMesaj.setText("Hizmet eklenirken bir hata oluştu!");
        }
    }
    
    @FXML
    private void hizmetGuncelle() {
        if (seciliHizmet == null || !formGecerliMi()) return;
        
        seciliHizmet.setHizmetAdi(txtHizmetAdi.getText());
        seciliHizmet.setAciklama(txtAciklama.getText());
        seciliHizmet.setFiyat(Double.parseDouble(txtFiyat.getText()));
        seciliHizmet.setAktif(chkAktif.isSelected());
        
        if (hizmetDAO.hizmetGuncelle(seciliHizmet)) {
            lblMesaj.setText("Hizmet başarıyla güncellendi.");
            tabloyuGuncelle();
        } else {
            lblMesaj.setText("Hizmet güncellenirken bir hata oluştu!");
        }
    }
    
    @FXML
    private void hizmetSil() {
        if (seciliHizmet == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Silme Onayı");
        alert.setHeaderText(null);
        alert.setContentText("Seçili hizmeti silmek istediğinize emin misiniz?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            if (hizmetDAO.hizmetSil(seciliHizmet.getHizmetId())) {
                lblMesaj.setText("Hizmet başarıyla silindi.");
                tabloyuGuncelle();
                formuTemizle();
            } else {
                lblMesaj.setText("Hizmet silinirken bir hata oluştu!");
            }
        }
    }
    
    @FXML
    private void formuTemizle() {
        txtHizmetAdi.clear();
        txtAciklama.clear();
        txtFiyat.clear();
        chkAktif.setSelected(true);
        seciliHizmet = null;
        lblMesaj.setText("");
    }
    
    private boolean formGecerliMi() {
        if (txtHizmetAdi.getText().isEmpty() || txtAciklama.getText().isEmpty() || txtFiyat.getText().isEmpty()) {
            lblMesaj.setText("Lütfen tüm alanları doldurunuz!");
            return false;
        }
        
        try {
            double fiyat = Double.parseDouble(txtFiyat.getText());
            if (fiyat < 0) {
                lblMesaj.setText("Fiyat negatif olamaz!");
                return false;
            }
        } catch (NumberFormatException e) {
            lblMesaj.setText("Lütfen geçerli bir fiyat giriniz!");
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void geriDon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Yonetici/yoneticianasayfa.fxml", btnGeriDon);
    }
} 