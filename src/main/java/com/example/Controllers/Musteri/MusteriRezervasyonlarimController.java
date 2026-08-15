package com.example.Controllers.Musteri;

import com.example.Controllers.baseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MusteriRezervasyonlarimController extends baseController {
    
    @FXML private Button btnMevcutRezervasyonlar;
    @FXML private Button btnGelecekRezervasyonlar;
    @FXML private Button btnGecmisRezervasyonlar;
    @FXML private Button btnGeriDon;
    
    @FXML
    private void gecmisRezervasyonlaraGit() {
        System.out.println("Geçmiş rezervasyonlara yönlendiriliyor...");
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterigecmisrezervasyonlar.fxml", btnGecmisRezervasyonlar);
    }
    
    @FXML
    private void mevcutRezervasyonlaraGit() {
        System.out.println("Mevcut rezervasyonlara yönlendiriliyor...");
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterimevcutrezervasyonlar.fxml", btnMevcutRezervasyonlar);
    }
    
    @FXML
    private void gelecekRezervasyonlaraGit() {
        System.out.println("Gelecek rezervasyonlara yönlendiriliyor...");
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterigelecekrezervasyonlar.fxml", btnGelecekRezervasyonlar);
    }
    
    @FXML
    private void geriDon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterianasayfa.fxml", btnGeriDon);
    }
} 