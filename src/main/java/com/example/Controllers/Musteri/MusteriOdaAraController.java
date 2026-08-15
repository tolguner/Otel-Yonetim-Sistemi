package com.example.Controllers.Musteri;

import com.example.Controllers.baseController;
import com.example.DataAccess.OdaDAO;
import com.example.DataAccess.RezervasyonDAO;
import com.example.DataAccess.BakiyeDAO;
import com.example.Models.Oda;
import com.example.Models.Musteri;
import com.example.Models.Rezervasyon;
import com.example.Models.Bakiye;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;

public class MusteriOdaAraController extends baseController {
    
    @FXML private DatePicker dpGirisTarihi;
    @FXML private DatePicker dpCikisTarihi;
    @FXML private ComboBox<String> cmbOdaTipi;
    @FXML private ComboBox<Integer> cmbKisiSayisi;
    @FXML private Label lblMesaj;
    
    @FXML private TableView<Oda> tblOdalar;
    @FXML private TableColumn<Oda, Integer> colOdaNo;
    @FXML private TableColumn<Oda, String> colOdaTipi;
    @FXML private TableColumn<Oda, Integer> colKapasite;
    @FXML private TableColumn<Oda, Double> colFiyat;
    @FXML private TableColumn<Oda, String> colOzellikler;
    
    @FXML private Button btnGeriDon;
    @FXML private Button btnRezervasyonYap;
    
    @FXML private Label lblBakiye;
    @FXML private Label lblToplamTutar;
    
    private OdaDAO odaDAO = new OdaDAO();
    private RezervasyonDAO rezervasyonDAO = new RezervasyonDAO();
    
    @FXML
    public void initialize() {
        // Tablo sütunlarını ayarla
        colOdaNo.setCellValueFactory(new PropertyValueFactory<>("odaNo"));
        colOdaTipi.setCellValueFactory(new PropertyValueFactory<>("odaTipi"));
        colKapasite.setCellValueFactory(new PropertyValueFactory<>("kapasite"));
        colFiyat.setCellValueFactory(new PropertyValueFactory<>("fiyat"));
        colOzellikler.setCellValueFactory(new PropertyValueFactory<>("ozellikler"));
        
        cmbOdaTipi.setItems(odaDAO.odaTipleriniGetir());
        
        dpGirisTarihi.setValue(LocalDate.now());
        dpCikisTarihi.setValue(LocalDate.now().plusDays(1));
        
        cmbKisiSayisi.getItems().addAll(1, 2, 3, 4, 5);
        cmbKisiSayisi.setValue(1); // Varsayılan değer
        
        tblOdalar.setItems(odaDAO.musaitOdalariGetir(null, null, null, 0));
        
        Bakiye.getInstance().mevcutBakiyeProperty().addListener((obs, oldVal, newVal) -> {
            lblBakiye.setText(String.format("Mevcut Bakiye: %.2f TL", newVal));
        });
        
        Bakiye.getInstance().bakiyeGuncelle();
        
        tblOdalar.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && dpGirisTarihi.getValue() != null && dpCikisTarihi.getValue() != null) {
                long gunSayisi = ChronoUnit.DAYS.between(dpGirisTarihi.getValue(), dpCikisTarihi.getValue());
                BigDecimal toplamTutar = BigDecimal.valueOf(newSelection.getFiyat()).multiply(BigDecimal.valueOf(gunSayisi));
                lblToplamTutar.setText(String.format("Toplam Tutar: %.2f TL", toplamTutar));
                btnRezervasyonYap.setDisable(false); // Oda seçildiğinde butonu aktif et
            } else {
                lblToplamTutar.setText("Toplam Tutar: 0.00 TL");
                btnRezervasyonYap.setDisable(true); // Oda seçili değilse butonu deaktif et
            }
        });
    }
    
    @FXML
    private void odalariAra() {
        // Tarih kontrolü
        LocalDate bugun = LocalDate.now();
        
        if (dpGirisTarihi.getValue() == null || dpCikisTarihi.getValue() == null) {
            lblMesaj.setText("Lütfen tarih seçiniz!");
            return;
        }
        
        if (dpGirisTarihi.getValue().isBefore(bugun)) {
            lblMesaj.setText("Giriş tarihi bugünden önce olamaz!");
            return;
        }

        if (dpGirisTarihi.getValue().isAfter(dpCikisTarihi.getValue())) {
            lblMesaj.setText("Giriş tarihi çıkış tarihinden sonra olamaz!");
            return;
        }

        if (dpGirisTarihi.getValue().equals(dpCikisTarihi.getValue())) {
            lblMesaj.setText("Giriş ve çıkış tarihi aynı olamaz!");
            return;
        }

        // Maksimum rezervasyon süresi kontrolü (örn: 30 gün)
        long gunFarki = ChronoUnit.DAYS.between(dpGirisTarihi.getValue(), dpCikisTarihi.getValue());
        if (gunFarki > 30) {
            lblMesaj.setText("Maksimum rezervasyon süresi 30 gündür!");
            return;
        }
        
        if (cmbKisiSayisi.getValue() == null) {
            lblMesaj.setText("Lütfen kişi sayısı seçiniz!");
            return;
        }

        try {
            ObservableList<Oda> musaitOdalar = odaDAO.musaitOdalariGetir(
                dpGirisTarihi.getValue(),
                dpCikisTarihi.getValue(),
                cmbOdaTipi.getValue(),
                cmbKisiSayisi.getValue()
            );
            
            tblOdalar.setItems(musaitOdalar);
            
            lblToplamTutar.setText("Toplam Tutar: 0 TL");
            
            if (musaitOdalar.isEmpty()) {
                lblMesaj.setText("Seçilen kriterlere uygun oda bulunamadı!");
            } else {
                lblMesaj.setText(musaitOdalar.size() + " adet uygun oda bulundu.");
            }
            
            System.out.println("Arama kriterleri: " +
                "Giriş: " + dpGirisTarihi.getValue() + ", " +
                "Çıkış: " + dpCikisTarihi.getValue() + ", " +
                "Oda Tipi: " + cmbOdaTipi.getValue() + ", " +
                "Kişi Sayısı: " + cmbKisiSayisi.getValue());
                
        } catch (Exception e) {
            lblMesaj.setText("Oda arama sırasında bir hata oluştu!");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void rezervasyonYap() {
        try {
            Oda secilenOda = tblOdalar.getSelectionModel().getSelectedItem();
            if (secilenOda == null) {
                lblMesaj.setText("Lütfen bir oda seçiniz!");
                return;
            }

            LocalDate bugun = LocalDate.now();
            if (dpGirisTarihi.getValue().isBefore(bugun)) {
                lblMesaj.setText("Geçmiş tarihe rezervasyon yapılamaz!");
                return;
            }

            long gunSayisi = ChronoUnit.DAYS.between(dpGirisTarihi.getValue(), dpCikisTarihi.getValue());
            double toplamTutar = secilenOda.getFiyat() * gunSayisi;

            BakiyeDAO bakiyeDAO = new BakiyeDAO();
            
            System.out.println("Toplam tutar: " + toplamTutar);
            System.out.println("TC: " + Musteri.getAktifMusteri().getTcKimlikNo());
            
            if (!bakiyeDAO.bakiyeYeterliMi(Musteri.getAktifMusteri().getTcKimlikNo(), toplamTutar)) {
                lblMesaj.setText("Yetersiz bakiye! Gerekli tutar: " + toplamTutar + " TL");
                return;
            }

            if (Bakiye.getInstance().bakiyeYukle(toplamTutar)) {
                Rezervasyon rezervasyon = new Rezervasyon();
                rezervasyon.setTcKimlikNo(Musteri.getAktifMusteri().getTcKimlikNo());
                rezervasyon.setOdaNo(secilenOda.getOdaNo());
                rezervasyon.setBaslangicTarihi(dpGirisTarihi.getValue());
                rezervasyon.setBitisTarihi(dpCikisTarihi.getValue());
                rezervasyon.setToplamTutar(toplamTutar);
                rezervasyon.setDurum("ONAYLANDI");

                System.out.println("Rezervasyon yapılıyor...");
                boolean sonuc = rezervasyonDAO.rezervasyonEkle(rezervasyon);
                
                if (sonuc) {
                    // Başarı mesajı göster
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Rezervasyon Başarılı");
                    alert.setHeaderText(null);
                    alert.setContentText("Rezervasyonunuz başarıyla oluşturuldu!\n\n" +
                                        "Oda No: " + secilenOda.getOdaNo() + "\n" +
                                        "Giriş Tarihi: " + dpGirisTarihi.getValue() + "\n" +
                                        "Çıkış Tarihi: " + dpCikisTarihi.getValue() + "\n" +
                                        "Toplam Tutar: " + String.format("%.2f TL", toplamTutar));
                    alert.showAndWait();

                    // Rezervasyonlarım sayfasına yönlendir
                    ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterirezervasyonlarim.fxml", btnRezervasyonYap);
                } else {
                    // Rezervasyon başarısız olursa bakiyeyi geri yükle
                    Bakiye.getInstance().bakiyeYukle(toplamTutar);
                    lblMesaj.setText("Rezervasyon oluşturulurken bir hata oluştu!");
                }
            }
        } catch (Exception e) {
            System.err.println("Rezervasyon yapılırken hata: " + e.getMessage());
            e.printStackTrace();
            lblMesaj.setText("Beklenmeyen bir hata oluştu!");
        }
    }
    
    @FXML
    private void geriDon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterianasayfa.fxml", btnGeriDon);
    }
} 