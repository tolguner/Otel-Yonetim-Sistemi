package com.example.Controllers.Musteri;

import com.example.Controllers.baseController;
import com.example.DataAccess.MusteriDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class MusteriSifreUnuttumController extends baseController {
    
    @FXML private TextField txtTcKimlikNo;
    @FXML private TextField txtTelefon;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtYeniSifre;
    @FXML private Label lblMesaj;
    @FXML private Button btnsifreyenile;
    @FXML private Button btngeridon;
    
    private MusteriDAO musteriDAO = new MusteriDAO();
    
    @FXML
    private void sifreYenile() {
        if (!formGecerliMi()) return;
        
        String tcKimlikNo = txtTcKimlikNo.getText().trim();
        String email = txtEmail.getText().trim();
        String telefon = txtTelefon.getText().trim();
        String yeniSifre = txtYeniSifre.getText();
        
        if (musteriDAO.sifreYenile(tcKimlikNo, email, telefon, yeniSifre)) {
            lblMesaj.setStyle("-fx-text-fill: green;");
            lblMesaj.setText("Şifreniz başarıyla güncellendi! Giriş sayfasına yönlendiriliyorsunuz...");
            
            bekleVeYonlendir("/com/example/otelsistemi/Musteri/musterigirisekrani.fxml", btnsifreyenile, 2);
        } else {
            lblMesaj.setStyle("-fx-text-fill: red;");
            lblMesaj.setText("Bilgileriniz hatalı! Lütfen kontrol ediniz.");
        }
    }
    
    private boolean formGecerliMi() {
        if (txtTcKimlikNo.getText().isEmpty() || txtEmail.getText().isEmpty() || 
            txtTelefon.getText().isEmpty() || txtYeniSifre.getText().isEmpty()) {
            
            lblMesaj.setText("Lütfen tüm alanları doldurun!");
            return false;
        }
        
        if (txtTcKimlikNo.getText().length() != 11) {
            lblMesaj.setText("TC Kimlik No 11 haneli olmalıdır!");
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
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterigirisekrani.fxml", btngeridon);
    }
} 