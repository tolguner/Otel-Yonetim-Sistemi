package com.example.Controllers.Yonetici;

import com.example.Controllers.baseController;
import com.example.Models.Yonetici;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class yoneticiAnasayfaController extends baseController {

    @FXML
    private Button btnprofilim;

    @FXML
    private Button btnodaislemleri;

    @FXML
    private Button btnhizmettalebi;

    @FXML
    private Button btncikisyap;

    @FXML
    private Button btnHizmetIslemleri;

    @FXML
    private Button btnMusteriIslemleri;

    @FXML
    public void goprofilim() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Yonetici/yoneticiprofilim.fxml", btnprofilim);
    }

    @FXML
    public void goodaislemleri() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Yonetici/yoneticiodaislemleri.fxml", btnodaislemleri);
    }

    @FXML
    public void gohizmettalebi() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Yonetici/yoneticihizmettalebi.fxml", btnhizmettalebi);
    }

    @FXML
    public void gocikisyap() {
        Yonetici.oturumuKapat();
        ilgiliSayfayaGit("/com/example/otelsistemi/Yonetici/yoneticigirisekrani.fxml", btncikisyap);
    }

    @FXML
    private void goHizmetIslemleri() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Yonetici/YoneticiHizmetIslemleri.fxml", btnHizmetIslemleri);
    }

    @FXML
    private void goMusteriIslemleri() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Yonetici/yoneticimusteriislemleri.fxml", btnMusteriIslemleri);
    }
} 