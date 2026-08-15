package com.example.Controllers.Yonetici;

import com.example.Controllers.baseController;
import com.example.DataAccess.YoneticiDAO;
import com.example.Models.Yonetici;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class yoneticiProfilController extends baseController {

    @FXML
    private TextField txtTcKimlikNo;
    
    @FXML
    private TextField txtAd;
    
    @FXML
    private TextField txtSoyad;
    
    @FXML
    private TextField txtEmail;
    
    @FXML
    private TextField txtTelefon;
    
    @FXML
    private PasswordField txtSifre;
    
    @FXML
    private Label lblMesaj;
    
    @FXML
    private Button btnGuncelle;
    
    @FXML
    private Button btnGeriDon;

    private YoneticiDAO yoneticiDAO = new YoneticiDAO();

    @FXML
    public void initialize() {
        // Oturum bilgilerini form alanlarına doldur
        txtTcKimlikNo.setText(Yonetici.getAktifYonetici().getTcKimlikNo());
        txtAd.setText(Yonetici.getAktifYonetici().getAd());
        txtSoyad.setText(Yonetici.getAktifYonetici().getSoyad());
        txtEmail.setText(Yonetici.getAktifYonetici().getEmail());
        txtTelefon.setText(Yonetici.getAktifYonetici().getTelefon());
        
        // TC No değiştirilemez
        txtTcKimlikNo.setEditable(false);
    }

    @FXML
    public void guncelle() {
        String tcKimlikNo = txtTcKimlikNo.getText();
        String ad = txtAd.getText();
        String soyad = txtSoyad.getText();
        String email = txtEmail.getText();
        String telefon = txtTelefon.getText();
        String sifre = txtSifre.getText();

        Yonetici.getAktifYonetici().setSifre(sifre);
        
        if (yoneticiDAO.yoneticiGuncelle(Yonetici.getAktifYonetici())) {
            // Oturum bilgilerini güncelle
            Yonetici.oturumBilgileriniAyarla(tcKimlikNo, ad, soyad, email, telefon);
            
            lblMesaj.setStyle("-fx-text-fill: green;");
            lblMesaj.setText("Bilgiler başarıyla güncellendi!");
        } else {
            lblMesaj.setStyle("-fx-text-fill: red;");
            lblMesaj.setText("Güncelleme işlemi başarısız!");
        }
    }

    @FXML
    public void geriDon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Yonetici/yoneticianasayfa.fxml", btnGeriDon);
    }
} 