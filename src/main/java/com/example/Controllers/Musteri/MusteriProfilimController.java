package com.example.Controllers.Musteri;

import com.example.Controllers.baseController;
import com.example.DataAccess.MusteriDAO;
import com.example.Models.Musteri;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class MusteriProfilimController extends baseController {
    
    @FXML private TextField txtTcKimlikNo;
    @FXML private TextField txtAd;
    @FXML private TextField txtSoyad;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefon;
    @FXML private Label lblMesaj;
    @FXML private Button btnGuncelle;
    @FXML private Button btnGeriDon;
    @FXML private Button btnSifreDegistir;
    @FXML private PasswordField txtYeniSifre;
    @FXML private PasswordField txtSifreTekrar;
    
    private MusteriDAO musteriDAO = new MusteriDAO();
    
    @FXML
    public void initialize() {
        // Mevcut bilgileri form alanlarına doldur
        txtTcKimlikNo.setText(Musteri.getAktifMusteri().getTcKimlikNo());
        txtAd.setText(Musteri.getAktifMusteri().getAd());
        txtSoyad.setText(Musteri.getAktifMusteri().getSoyad());
        txtEmail.setText(Musteri.getAktifMusteri().getEmail());
        txtTelefon.setText(Musteri.getAktifMusteri().getTelefon());
        
        txtTcKimlikNo.setEditable(false);
    }
    
    @FXML
    private void bilgileriGuncelle() {
        if (!formGecerliMi()) {
            return;
        }
        
        String yeniSifre = txtYeniSifre.getText().trim();
        String sifreTekrar = txtSifreTekrar.getText().trim();
        
        if (!yeniSifre.isEmpty() || !sifreTekrar.isEmpty()) {
            if (!yeniSifre.equals(sifreTekrar)) {
                lblMesaj.setText("Şifreler eşleşmiyor!");
                return;
            }
            if (yeniSifre.length() < 6) {
                lblMesaj.setText("Şifre en az 6 karakter olmalıdır!");
                return;
            }
        }
        
        if (musteriDAO.musteriGuncelle(
                Musteri.getAktifMusteri().getTcKimlikNo(),
                txtAd.getText(),
                txtSoyad.getText(),
                txtEmail.getText(),
                txtTelefon.getText(),
                yeniSifre.isEmpty() ? null : yeniSifre)) {
            
            Musteri.oturumBilgileriniAyarla(
                    Musteri.getAktifMusteri().getTcKimlikNo(),
                    txtAd.getText(),
                    txtSoyad.getText(),
                    txtEmail.getText(),
                    txtTelefon.getText()
            );
            
            lblMesaj.setStyle("-fx-text-fill: green;");
            lblMesaj.setText("Bilgileriniz başarıyla güncellendi!");
            
            txtYeniSifre.clear();
            txtSifreTekrar.clear();
        } else {
            lblMesaj.setStyle("-fx-text-fill: red;");
            lblMesaj.setText("Güncelleme sırasında bir hata oluştu!");
        }
    }
    
    private boolean formGecerliMi() {
        if (txtAd.getText().isEmpty() || txtSoyad.getText().isEmpty() || 
            txtEmail.getText().isEmpty() || txtTelefon.getText().isEmpty()) {
            
            lblMesaj.setText("Lütfen tüm alanları doldurun!");
            return false;
        }
        
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            lblMesaj.setText("Geçerli bir email adresi giriniz!");
            return false;
        }
        
        if (!txtTelefon.getText().matches("\\d{10}")) {
            lblMesaj.setText("Telefon numarası 10 haneli olmalıdır!");
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void geriDon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterianasayfa.fxml", btnGeriDon);
    }
} 