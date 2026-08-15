package com.example.Controllers.Admin;

import com.example.Controllers.baseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class adminAnasayfaController extends baseController {

    @FXML
    private Button btnprofilim;

    @FXML
    private Button btnyoneticiislemleri;

    @FXML
    private Button btncikisyap;

    @FXML
    public void goprofilim(){
        ilgiliSayfayaGit("/com/example/otelsistemi/Admin/adminprofilim.fxml", btnprofilim);
    }

    @FXML
    public void goyoneticiislemleri(){
        System.out.println("Yönetici işlemleri sayfasına yönlendiriliyor...");
        ilgiliSayfayaGit("/com/example/otelsistemi/Admin/adminyoneticiislemleri.fxml", btnyoneticiislemleri);
    }

    @FXML
    public void gocikisyap(){
        ilgiliSayfayaGit("/com/example/otelsistemi/Admin/admingirisekrani.fxml", btncikisyap);
    }
}
