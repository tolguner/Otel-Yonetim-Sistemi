package com.example.Controllers.Musteri;

import com.example.Controllers.baseController;
import com.example.DataAccess.RezervasyonDAO;
import com.example.DataAccess.HizmetTalebiDAO;
import com.example.DataAccess.BakiyeDAO;
import com.example.Models.Rezervasyon;
import com.example.Models.HizmetTalebi;
import com.example.Models.Musteri;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;

public class MusteriMevcutRezervasyonlarController extends baseController {
    
    @FXML private TableView<Rezervasyon> tblRezervasyonlar;
    @FXML private TableColumn<Rezervasyon, Integer> colOdaNo;
    @FXML private TableColumn<Rezervasyon, String> colOdaAdi;
    @FXML private TableColumn<Rezervasyon, String> colOdaTipi;
    @FXML private TableColumn<Rezervasyon, String> colBaslangicTarihi;
    @FXML private TableColumn<Rezervasyon, String> colBitisTarihi;
    @FXML private TableColumn<Rezervasyon, String> colDurum;
    
    @FXML private Label lblTalepDurumu;
    @FXML private ComboBox<String> cmbHizmetler;
    @FXML private TextArea txtAciklama;
    @FXML private Button btnTalepOlustur;
    @FXML private Button btnTalepGuncelle;
    @FXML private Button btnTalepSil;
    @FXML private Button btnGeriDon;
    @FXML private Label lblMesaj;
    @FXML private Label lblHizmetUcreti;
    @FXML private Label lblHizmetDetay;
    @FXML private Label lblMevcutBakiye;
    
    private RezervasyonDAO rezervasyonDAO = new RezervasyonDAO();
    private HizmetTalebiDAO hizmetTalebiDAO = new HizmetTalebiDAO();
    private BakiyeDAO bakiyeDAO = new BakiyeDAO();
    private Rezervasyon seciliRezervasyon;
    private HizmetTalebi mevcutTalep;
    
    @FXML
    public void initialize() {
        tabloyuAyarla();
        rezervasyonlariYukle();
        tabelSecimDinleyicisiEkle();
        butonlariAyarla(false);
        bakiyeyiGuncelle();
        
        cmbHizmetler.setItems(hizmetTalebiDAO.getHizmetler());
        
        cmbHizmetler.getSelectionModel().selectedItemProperty().addListener((obs, eskiDeger, yeniDeger) -> {
            if (yeniDeger != null) {
                double ucret = hizmetTalebiDAO.getHizmetUcreti(yeniDeger);
                String detay = hizmetTalebiDAO.getHizmetDetay(yeniDeger);
                lblHizmetUcreti.setText(String.format("%.2f TL", ucret));
                lblHizmetDetay.setText(detay);
            } else {
                lblHizmetUcreti.setText("-");
                lblHizmetDetay.setText("-");
            }
        });
    }
    
    private void tabloyuAyarla() {
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
        
        colDurum.setCellValueFactory(new PropertyValueFactory<>("durum"));
    }
    
    private void rezervasyonlariYukle() {
        ObservableList<Rezervasyon> rezervasyonlar = rezervasyonDAO.mevcutRezervasyonlariGetir(
            Musteri.getAktifMusteri().getTcKimlikNo()
        );
        tblRezervasyonlar.setItems(rezervasyonlar);
    }
    
    private void tabelSecimDinleyicisiEkle() {
        tblRezervasyonlar.getSelectionModel().selectedItemProperty().addListener((obs, eskiSecim, yeniSecim) -> {
            if (yeniSecim != null) {
                seciliRezervasyon = yeniSecim;
                hizmetTalebiniYukle(yeniSecim.getRezervasyonId());
            }
        });
    }
    
    private void hizmetTalebiniYukle(int rezervasyonId) {
        mevcutTalep = hizmetTalebiDAO.talepGetir(rezervasyonId);
        if (mevcutTalep != null) {
            lblTalepDurumu.setText(mevcutTalep.getDurum());
            cmbHizmetler.setValue(mevcutTalep.getHizmetAdi());
            txtAciklama.setText(mevcutTalep.getAciklama());
            butonlariAyarla(true);
        } else {
            lblTalepDurumu.setText("Talep Yok");
            cmbHizmetler.setValue(null);
            txtAciklama.clear();
            butonlariAyarla(false);
        }
    }
    
    private void butonlariAyarla(boolean talepVar) {
        btnTalepOlustur.setVisible(!talepVar);
        btnTalepGuncelle.setVisible(talepVar);
        btnTalepSil.setVisible(talepVar);
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void geriDon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterirezervasyonlarim.fxml", btnGeriDon);
    }
    
    @FXML
    private void hizmetTalebiOlustur() {
        if (seciliRezervasyon == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen bir rezervasyon seçiniz!");
            return;
        }

        String secilenHizmet = cmbHizmetler.getValue();
        if (secilenHizmet == null || secilenHizmet.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen bir hizmet seçiniz!");
            return;
        }

        double hizmetUcreti = hizmetTalebiDAO.getHizmetUcreti(secilenHizmet);
        double mevcutBakiye = bakiyeDAO.bakiyeGetir(Musteri.getAktifMusteri().getTcKimlikNo());
        
        System.out.println("Hizmet ücreti: " + hizmetUcreti);
        System.out.println("Mevcut bakiye: " + mevcutBakiye);

        if (mevcutBakiye < hizmetUcreti) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", 
                     String.format("Bakiyeniz yetersiz! Hizmet ücreti: %.2f TL, Bakiyeniz: %.2f TL", 
                                 hizmetUcreti, mevcutBakiye));
            return;
        }

        HizmetTalebi yeniTalep = new HizmetTalebi();
        yeniTalep.setRezervasyonId(seciliRezervasyon.getRezervasyonId());
        yeniTalep.setHizmetAdi(secilenHizmet);
        yeniTalep.setAciklama(txtAciklama.getText().trim());
        yeniTalep.setFiyat(hizmetUcreti);  // Fiyatı da ekle
        yeniTalep.setOdaNo(seciliRezervasyon.getOdaNo());  // Oda numarasını da ekle

        if (hizmetTalebiDAO.talepOlustur(yeniTalep)) {
            // Bakiyeyi güncelle
            bakiyeDAO.bakiyeGuncelle(Musteri.getAktifMusteri().getTcKimlikNo(), -hizmetUcreti, "HIZMET_ODEMESI");
            bakiyeyiGuncelle();
            
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Hizmet talebi oluşturuldu!");
            rezervasyonlariYukle();
            hizmetTalebiniYukle(seciliRezervasyon.getRezervasyonId());
        } else {
            showAlert(Alert.AlertType.ERROR, "Hata", "Hizmet talebi oluşturulamadı!");
        }
    }
    
    @FXML
    private void hizmetTalebiGuncelle() {
        if (mevcutTalep == null) return;
        
        mevcutTalep.setHizmetAdi(cmbHizmetler.getValue());
        mevcutTalep.setAciklama(txtAciklama.getText().trim());
        
        if (hizmetTalebiDAO.talepGuncelle(mevcutTalep)) {
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Hizmet talebi güncellendi!");
            rezervasyonlariYukle();
            hizmetTalebiniYukle(seciliRezervasyon.getRezervasyonId());
        } else {
            showAlert(Alert.AlertType.ERROR, "Hata", "Hizmet talebi güncellenemedi!");
        }
    }
    
    @FXML
    private void hizmetTalebiSil() {
        if (mevcutTalep == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Onay");
        alert.setHeaderText(null);
        alert.setContentText("Hizmet talebini silmek istediğinize emin misiniz?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            if (hizmetTalebiDAO.talepSil(mevcutTalep.getTalepId())) {
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Hizmet talebi silindi!");
                rezervasyonlariYukle();
                hizmetTalebiniYukle(seciliRezervasyon.getRezervasyonId());
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "Hizmet talebi silinemedi!");
            }
        }
    }
    
    private void bakiyeyiGuncelle() {
        double mevcutBakiye = bakiyeDAO.bakiyeGetir(Musteri.getAktifMusteri().getTcKimlikNo());
        lblMevcutBakiye.setText(String.format("Mevcut Bakiye: %.2f TL", mevcutBakiye));
    }
} 