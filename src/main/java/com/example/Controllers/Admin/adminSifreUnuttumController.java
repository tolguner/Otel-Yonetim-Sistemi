package com.example.Controllers.Admin;

import com.example.Controllers.baseController;
import com.example.DataAccess.AdminDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class adminSifreUnuttumController extends baseController {

    @FXML
    private TextField tcNoField;
    
    @FXML
    private TextField telefonField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField yeniSifreField;

    @FXML
    private Button btnsifreyenile;

    @FXML
    private Button btngeridon;

    @FXML
    public void gosifreyenile() {
        String tcKimlikNo = tcNoField.getText();
        String telefon = telefonField.getText();
        String email = emailField.getText();
        String yeniSifre = yeniSifreField.getText();
        

        if (tcKimlikNo.isEmpty() || telefon.isEmpty() || email.isEmpty() || yeniSifre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uyarı");
            alert.setHeaderText(null);
            alert.setContentText("Lütfen tüm bilgileri doldurunuz!");
            alert.showAndWait();
            return;
        }
        

        if (AdminDAO.bilgileriKontrolEt(tcKimlikNo, telefon, email)) {

            if (AdminDAO.sifreGuncelle(email, yeniSifre)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Bilgilendirme");
                alert.setHeaderText(null);
                alert.setContentText("Şifreniz başarı ile yenilenmiştir.");
                alert.showAndWait();
                
                ilgiliSayfayaGit("/com/example/otelsistemi/Admin/admingirisekrani.fxml", btnsifreyenile);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Hata");
                alert.setHeaderText(null);
                alert.setContentText("Şifre güncellenirken bir hata oluştu!");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText(null);
            alert.setContentText("Bilgilerinizi kontrol ediniz!");
            alert.showAndWait();
        }
    }

    @FXML
    public void gogeridon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Admin/admingirisekrani.fxml", btngeridon);
    }
}
