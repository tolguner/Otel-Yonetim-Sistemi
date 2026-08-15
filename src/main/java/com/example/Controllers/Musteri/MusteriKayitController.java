package com.example.Controllers.Musteri;

import com.example.Controllers.baseController;
import com.example.DataAccess.MusteriDAO;
import com.example.Models.Musteri;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class MusteriKayitController extends baseController {
    
    @FXML private TextField txtTcKimlikNo;
    @FXML private TextField txtAd;
    @FXML private TextField txtSoyad;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefon;
    @FXML private PasswordField txtSifre;
    @FXML private PasswordField txtSifreTekrar;
    @FXML private Label lblMesaj;
    @FXML private Button btnKayitOl;
    @FXML private Button btnGeriDon;
    
    private MusteriDAO musteriDAO = new MusteriDAO();
    
    @FXML
    private void kayitOl() {
        if (!formGecerliMi()) return;
        
        Musteri yeniMusteri = new Musteri(
            txtTcKimlikNo.getText(),
            txtAd.getText(),
            txtSoyad.getText(),
            txtEmail.getText(),
            txtTelefon.getText(),
            txtSifre.getText()
        );
        
        if (musteriDAO.musteriEkle(yeniMusteri)) {
            lblMesaj.setStyle("-fx-text-fill: green;");
            lblMesaj.setText("Kayıt başarılı! Giriş sayfasına yönlendiriliyorsunuz...");
            
            bekleVeYonlendir("/com/example/otelsistemi/Musteri/musterigirisekrani.fxml", btnKayitOl, 1);
        } else {
            lblMesaj.setStyle("-fx-text-fill: red;");
            lblMesaj.setText("Kayıt sırasında bir hata oluştu!");
        }
    }
    
    private boolean formGecerliMi() {
        if (txtTcKimlikNo.getText().isEmpty() || txtAd.getText().isEmpty() || 
            txtSoyad.getText().isEmpty() || txtEmail.getText().isEmpty() || 
            txtTelefon.getText().isEmpty() || txtSifre.getText().isEmpty() || 
            txtSifreTekrar.getText().isEmpty()) {
            
            lblMesaj.setText("Lütfen tüm alanları doldurun!");
            return false;
        }
        
        if (!txtSifre.getText().equals(txtSifreTekrar.getText())) {
            lblMesaj.setText("Şifreler eşleşmiyor!");
            return false;
        }
        
        if (txtTcKimlikNo.getText().length() != 11) {
            lblMesaj.setText("TC Kimlik No 11 haneli olmalıdır!");
            return false;
        }
        
        if (musteriDAO.tcKimlikNoKontrol(txtTcKimlikNo.getText())) {
            lblMesaj.setText("Bu TC Kimlik No ile kayıtlı bir müşteri zaten var!");
            return false;
        }
        
        if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            lblMesaj.setText("Geçerli bir email adresi giriniz!");
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void geriDon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterigirisekrani.fxml", btnGeriDon);
    }
} 