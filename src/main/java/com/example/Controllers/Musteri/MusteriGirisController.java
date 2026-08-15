package com.example.Controllers.Musteri;

import com.example.Controllers.baseController;
import com.example.DataAccess.MusteriDAO;
import com.example.Models.Musteri;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class MusteriGirisController extends baseController {
    
    @FXML private TextField txtKimlikNo;
    @FXML private PasswordField txtSifre;
    @FXML private Label lblHata;
    @FXML private Button btnGirisYap;
    @FXML private Button btnKaydol;
    @FXML private Button btnGeriDon;
    @FXML private Button btnSifreUnuttum;
    
    private MusteriDAO musteriDAO = new MusteriDAO();
    
    @FXML
    private void btngirisyap() {
        String tcKimlikNo = txtKimlikNo.getText().trim();
        String sifre = txtSifre.getText().trim();
        
        if (tcKimlikNo.isEmpty() || sifre.isEmpty()) {
            lblHata.setText("Lütfen tüm alanları doldurunuz.");
            return;
        }
        
        if (musteriDAO.girisKontrol(tcKimlikNo, sifre)) {
            musteriDAO.musteriBilgileriniGetir(tcKimlikNo, (ad, soyad, email, telefon) -> {
                Musteri.oturumBilgileriniAyarla(tcKimlikNo, ad, soyad, email, telefon);
            });
            
            ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterianasayfa.fxml", btnGirisYap);
        } else {
            lblHata.setText("TC Kimlik No veya şifre hatalı!");
        }
    }
    
    @FXML
    private void btnkaydol() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterihesapolustur.fxml", btnKaydol);
    }
    
    @FXML
    private void btngeridon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/main.fxml", btnGeriDon);
    }
    
    @FXML
    private void btnsifreunuttum() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterisifreunuttum.fxml", btnSifreUnuttum);
    }
}