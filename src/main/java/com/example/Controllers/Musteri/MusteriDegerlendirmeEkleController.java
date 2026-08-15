package com.example.Controllers.Musteri;

import com.example.Controllers.baseController;
import com.example.DataAccess.DegerlendirmeDAO;
import com.example.Models.Degerlendirme;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MusteriDegerlendirmeEkleController extends baseController {
    
    @FXML private ComboBox<Integer> cmbPuan;
    @FXML private TextArea txtYorum;
    @FXML private Label lblMesaj;
    @FXML private Button btnGeriDon;
    
    private DegerlendirmeDAO degerlendirmeDAO = new DegerlendirmeDAO();
    
    @FXML
    public void initialize() {
        cmbPuan.setValue(5); // Varsayılan değer
    }
    
    @FXML
    public void degerlendirmeKaydet() {
        if (txtYorum.getText().trim().isEmpty()) {
            lblMesaj.setText("Lütfen bir yorum yazınız!");
            return;
        }
        
        Degerlendirme degerlendirme = new Degerlendirme();
        degerlendirme.setPuan(cmbPuan.getValue());
        degerlendirme.setYorum(txtYorum.getText().trim());
        
        if (degerlendirmeDAO.degerlendirmeEkle(degerlendirme)) {
            showInfoDialog("Değerlendirmeniz başarıyla kaydedildi.");
            geriDon();
        } else {
            lblMesaj.setText("Değerlendirme kaydedilirken bir hata oluştu!");
        }
    }
    
    @FXML
    public void geriDon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterigecmisrezervasyonlar.fxml", btnGeriDon);
    }
} 