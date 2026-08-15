package com.example.Controllers.Musteri;

import com.example.Controllers.baseController;
import com.example.Models.Musteri;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MusteriAnasayfaController extends baseController {
    
    @FXML private Label lblHosgeldin;
    @FXML private Button btnProfilim;
    @FXML private Button btnOdaAra;
    @FXML private Button btnRezervasyonlarim;
    @FXML private Button btnCikisYap;
    @FXML private Button btnBakiye;
    
    @FXML
    public void initialize() {
        Musteri aktifMusteri = Musteri.getAktifMusteri();
        lblHosgeldin.setText("Hoş Geldiniz, " + aktifMusteri.getAd() + " " + aktifMusteri.getSoyad());
    }
    
    @FXML
    private void profilimGit() {
        System.out.println("Profilim sayfasına yönlendiriliyor..."); // Debug için
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musteriprofilim.fxml", btnProfilim);
    }
    
    @FXML
    private void odaAra() {
        System.out.println("Oda arama sayfasına yönlendiriliyor...");
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musteriodaara.fxml", btnOdaAra);
    }
    
    @FXML
    private void rezervasyonlaraGit() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterirezervasyonlarim.fxml", btnRezervasyonlarim);
    }
    
    @FXML
    private void cikisYap() {
        Musteri.oturumuKapat();
        ilgiliSayfayaGit("/com/example/otelsistemi/main.fxml", btnCikisYap);
    }
    
    @FXML
    private void bakiyeGit() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musteribakiye.fxml", btnBakiye);
    }
} 