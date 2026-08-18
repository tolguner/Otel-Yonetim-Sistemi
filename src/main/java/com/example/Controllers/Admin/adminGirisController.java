package com.example.Controllers.Admin;

import com.example.Controllers.baseController;
import com.example.DataAccess.AdminDAO;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.example.Models.Admin;

public class adminGirisController extends baseController {

    @FXML
    private Button btngirisyap;

    @FXML
    private Button btnsifremiunuttum;

    @FXML
    private Button btngeridon;

    @FXML
    public void gosifremiunuttum() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Admin/adminssifreunuttum.fxml", btnsifremiunuttum);
    }

    @FXML
    public void gogeridon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/main.fxml", btngeridon);
    }

    @FXML
    private TextField tcKimlikNumarasi;

    @FXML
    private PasswordField sifre;

    @FXML
    private Label gerimesaj;

    private final AdminDAO adminDAO = new AdminDAO();

    @FXML
    public void gogirisyap() {
        String giristcKimlikNumarasi = tcKimlikNumarasi.getText();
        String girissifre = sifre.getText();

        // Detaylı log
        System.out.println("===== Giriş İşlemi =====");
        System.out.println("TC Kimlik No: " + giristcKimlikNumarasi);

        // Boş alan kontrolü
        if (giristcKimlikNumarasi.isEmpty() || girissifre.isEmpty()) {
            Platform.runLater(() -> {
                gerimesaj.setText("Lütfen Kullanıcı veya Şifre Alanını Boş Bırakmayınız.");
                gerimesaj.setStyle("-fx-text-fill: red;");
            });
            return;
        }

        boolean girisBasarili = adminDAO.girisYap(giristcKimlikNumarasi, girissifre);

        if (girisBasarili) {
            System.out.println("Giriş başarılı, admin bilgileri alınıyor...");
            
            // Admin bilgilerini al ve sakla
            AdminDAO.getAdmin(giristcKimlikNumarasi, (ad, soyad, email, telefon) -> {
                System.out.println("Admin bilgileri oturuma kaydediliyor:");
                System.out.println("TC: " + giristcKimlikNumarasi);
                System.out.println("Ad: " + ad);
                System.out.println("Soyad: " + soyad);
                System.out.println("Email: " + email);
                System.out.println("Telefon: " + telefon);
                
                Admin.oturumBilgileriniAyarla(giristcKimlikNumarasi, ad, soyad, email, telefon);
            });

            Platform.runLater(() -> {
                gerimesaj.setText("Giriş Başarılı!");
                gerimesaj.setStyle("-fx-text-fill: green;");

                PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(2));
                pause.setOnFinished(_ -> {
                    try {
                        // FXML yükleme ile ilgili detaylı log
                        System.out.println("Yönlendirilen Sayfa: /com/example/otelsistemi/Admin/adminanasayfa.fxml");
                        ilgiliSayfayaGit("/com/example/otelsistemi/Admin/adminanasayfa.fxml", btngirisyap);
                    } catch (Exception ex) {
                        System.err.println("Sayfa Yönlendirme Hatası: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
                pause.play();
            });
        } else {
            Platform.runLater(() -> {
                gerimesaj.setText("Kullanıcı Adı veya Şifre Hatalı!");
                gerimesaj.setStyle("-fx-text-fill: red;");
            });
        }
    }
}
