package com.example.Controllers.Musteri;

import com.example.Controllers.baseController;
import com.example.DataAccess.BakiyeDAO;
import com.example.Models.Musteri;
import com.example.Models.Bakiye;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import javafx.collections.ObservableList;
import javafx.application.Platform;

public class MusteriBakiyeController extends baseController {
    
    @FXML private Label lblMevcutBakiye;
    @FXML private TextField txtYuklenecekTutar;
    @FXML private Button btnBakiyeYukle;
    @FXML private Button btnGeriDon;
    @FXML private Label lblMesaj;
    
    @FXML private TableView<Bakiye> tblIslemGecmisi;
    @FXML private TableColumn<Bakiye, LocalDate> colTarih;
    @FXML private TableColumn<Bakiye, String> colIslemTipi;
    @FXML private TableColumn<Bakiye, Double> colTutar;
    @FXML private TableColumn<Bakiye, Double> colBakiye;
    
    private BakiyeDAO bakiyeDAO = new BakiyeDAO();
    
    @FXML
    public void initialize() {
        colTarih.setCellValueFactory(new PropertyValueFactory<>("islemTarihi"));
        colIslemTipi.setCellValueFactory(new PropertyValueFactory<>("islemTipi"));
        colTutar.setCellValueFactory(new PropertyValueFactory<>("tutar"));
        colBakiye.setCellValueFactory(new PropertyValueFactory<>("toplamBakiye"));

        colTutar.setCellFactory(tc -> new TableCell<Bakiye, Double>() {
            @Override
            protected void updateItem(Double tutar, boolean empty) {
                super.updateItem(tutar, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f TL", tutar));
                }
            }
        });

        colBakiye.setCellFactory(tc -> new TableCell<Bakiye, Double>() {
            @Override
            protected void updateItem(Double bakiye, boolean empty) {
                super.updateItem(bakiye, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f TL", bakiye));
                }
            }
        });

        bakiyeDAO.addBakiyeListener((tcKimlikNo, yeniBakiye) -> {
            if (tcKimlikNo.equals(Musteri.getAktifMusteri().getTcKimlikNo())) {
                Platform.runLater(() -> {
                    lblMevcutBakiye.setText(String.format("%.2f TL", yeniBakiye));
                    islemGecmisiniGuncelle();
                });
            }
        });

        double mevcutBakiye = bakiyeDAO.bakiyeGetir(Musteri.getAktifMusteri().getTcKimlikNo());
        lblMevcutBakiye.setText(String.format("%.2f TL", mevcutBakiye));

        islemGecmisiniGuncelle();
    }
    
    private void islemGecmisiniGuncelle() {
        BakiyeDAO bakiyeDAO = new BakiyeDAO();
        ObservableList<Bakiye> islemler = bakiyeDAO.islemGecmisiGetir(
            Musteri.getAktifMusteri().getTcKimlikNo()
        );
        

        tblIslemGecmisi.setItems(islemler);
        
        double guncelBakiye = bakiyeDAO.bakiyeGetir(Musteri.getAktifMusteri().getTcKimlikNo());
        lblMevcutBakiye.setText(String.format("%.2f TL", guncelBakiye));
    }
    
    @FXML
    private void bakiyeYukle() {
        try {
            double tutar = Double.parseDouble(txtYuklenecekTutar.getText());
            
            if (tutar <= 0) {
                lblMesaj.setText("Lütfen geçerli bir tutar giriniz!");
                return;
            }
            
            // Bakiye yükleme işlemini doğrudan BakiyeDAO ile yap
            if (bakiyeDAO.bakiyeYukle(Musteri.getAktifMusteri().getTcKimlikNo(), tutar)) {
                lblMesaj.setText("Bakiye başarıyla yüklendi!");
                txtYuklenecekTutar.clear();
                islemGecmisiniGuncelle();
            } else {
                lblMesaj.setText("Bakiye yükleme işlemi başarısız!");
            }
        } catch (NumberFormatException e) {
            lblMesaj.setText("Lütfen geçerli bir tutar giriniz!");
        }
    }
    
    @FXML
    private void geriDon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Musteri/musterianasayfa.fxml", btnGeriDon);
    }
} 