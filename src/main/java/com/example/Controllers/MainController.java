package com.example.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainController extends baseController {

    @FXML
    private Button btnmusterigiris;

    @FXML
    private Button btnyoneticigiris;

    @FXML
    private Button btnadmingiris;

    @FXML
    private void btnmusterigiris() {
        System.out.println("Müşteri girişine yönlendiriliyor...");
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterigirisekrani.fxml", btnmusterigiris);
    }

    @FXML
    public void btnyoneticigiris() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Yonetici/yoneticigirisekrani.fxml",btnyoneticigiris);
    }

    @FXML
    public void btnadmingiris() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Admin/admingirisekrani.fxml", btnadmingiris);
    }
}