package com.example.Controllers.Yonetici;

import com.example.Controllers.baseController;
import com.example.DataAccess.YoneticiDAO;
import com.example.Models.Yonetici;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class yoneticiGirisController extends baseController {
    @FXML
    private TextField txtTcKimlikNo;
    
    @FXML
    private PasswordField txtSifre;
    
    @FXML
    private Label gerimesaj;
    
    @FXML
    private Button btngeridon;
    
    @FXML
    private Button btnsifremiunuttum;

    private YoneticiDAO yoneticiDAO = new YoneticiDAO();

    @FXML
    public void gogeridon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/main.fxml", btngeridon);
    }

    @FXML
    public void gosifremiunuttum() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Yonetici/yoneticisifreunuttum.fxml", btnsifremiunuttum);
    }

    @FXML
    public void girisYap() {
        String tcKimlikNo = txtTcKimlikNo.getText();
        String sifre = txtSifre.getText();

        System.out.println("===== Giriş İşlemi =====");
        System.out.println("TC Kimlik No: " + tcKimlikNo);

        if (yoneticiDAO.yoneticiGiris(tcKimlikNo, sifre)) {
            System.out.println("Giriş başarılı, yönetici bilgileri alınıyor...");
            
            yoneticiDAO.yoneticiBilgileriniGetir(tcKimlikNo, (ad, soyad, email, telefon) -> {
                System.out.println("Yönetici bilgileri oturuma kaydediliyor:");
                System.out.println("TC: " + tcKimlikNo);
                System.out.println("Ad: " + ad);
                System.out.println("Soyad: " + soyad);
                System.out.println("Email: " + email);
                System.out.println("Telefon: " + telefon);

                Yonetici.oturumBilgileriniAyarla(tcKimlikNo, ad, soyad, email, telefon);

                Platform.runLater(() -> {
                    gerimesaj.setStyle("-fx-text-fill: green;");
                    gerimesaj.setText("Giriş Başarılı! Yönlendiriliyorsunuz...");

                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(_ -> ilgiliSayfayaGit("/com/example/otelsistemi/Yonetici/yoneticianasayfa.fxml", btngeridon));
                    pause.play();
                });
            });
        } else {
            Platform.runLater(() -> {
                gerimesaj.setText("Kullanıcı Adı veya Şifre Hatalı!");
                gerimesaj.setStyle("-fx-text-fill: red;");
            });
        }
    }
}