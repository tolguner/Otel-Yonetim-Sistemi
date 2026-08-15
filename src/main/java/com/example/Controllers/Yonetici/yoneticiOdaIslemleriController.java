package com.example.Controllers.Yonetici;

import com.example.Controllers.baseController;
import com.example.DataAccess.OdaDAO;
import com.example.Models.Oda;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

public class yoneticiOdaIslemleriController extends baseController {

    @FXML private TableView<Oda> tableOdalar;
    @FXML private TableColumn<Oda, Integer> colOdaNo;
    @FXML private TableColumn<Oda, String> colOdaAdi;
    @FXML private TableColumn<Oda, String> colOdaTipi;
    @FXML private TableColumn<Oda, Integer> colKapasite;
    @FXML private TableColumn<Oda, Double> colFiyat;
    @FXML private TableColumn<Oda, Boolean> colDurum;

    @FXML private TextField txtOdaNo;
    @FXML private TextField txtOdaAdi;
    @FXML private TextField txtOdaTipi;
    @FXML private TextField txtKapasite;
    @FXML private TextField txtFiyat;
    @FXML private CheckBox chkDurum;
    @FXML private TextArea txtOzellikler;
    @FXML private Label lblMesaj;
    @FXML private Button btnGeriDon;

    private OdaDAO odaDAO = new OdaDAO();

    @FXML
    public void initialize() {
        tabloyuAyarla();
        tabloyuGuncelle();
        tabloSecimDinleyicisiEkle();
        txtOdaNo.setEditable(true); // Yeni oda eklerken oda no girilebilsin
    }

    private void tabloyuAyarla() {
        colOdaNo.setCellValueFactory(new PropertyValueFactory<>("odaNo"));
        colOdaAdi.setCellValueFactory(new PropertyValueFactory<>("odaAdi"));
        colOdaTipi.setCellValueFactory(new PropertyValueFactory<>("odaTipi"));
        colKapasite.setCellValueFactory(new PropertyValueFactory<>("kapasite"));
        colFiyat.setCellValueFactory(new PropertyValueFactory<>("fiyat"));
        colDurum.setCellValueFactory(new PropertyValueFactory<>("musaitlikDurumu"));

        colDurum.setCellFactory(_ -> new TableCell<Oda, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Müsait" : "Dolu");
                    setTextFill(item ? Color.GREEN : Color.RED);
                }
            }
        });
    }

    private void tabloSecimDinleyicisiEkle() {
        tableOdalar.getSelectionModel().selectedItemProperty().addListener((_, _, yeniSecim) -> {
            if (yeniSecim != null) {
                odayiFormaYukle(yeniSecim);
                txtOdaNo.setEditable(false);
            }
        });
    }

    private void tabloyuGuncelle() {
        ObservableList<Oda> odaListesi = odaDAO.tumOdalariGetir();
        tableOdalar.setItems(odaListesi);
    }

    private void odayiFormaYukle(Oda oda) {
        txtOdaNo.setText(String.valueOf(oda.getOdaNo()));
        txtOdaAdi.setText(oda.getOdaAdi());
        txtOdaTipi.setText(oda.getOdaTipi());
        txtKapasite.setText(String.valueOf(oda.getKapasite()));
        txtFiyat.setText(String.valueOf(oda.getFiyat()));
        chkDurum.setSelected(oda.isMusaitlikDurumu());
        txtOzellikler.setText(oda.getOzellikler());
    }

    @FXML
    private void odaEkle() {
        try {
            int odaNo = Integer.parseInt(txtOdaNo.getText());
            String odaAdi = txtOdaAdi.getText();
            String odaTipi = txtOdaTipi.getText();
            int kapasite = Integer.parseInt(txtKapasite.getText());
            double fiyat = Double.parseDouble(txtFiyat.getText()); // String'den BigDecimal'e çevirme
            boolean musaitlik = chkDurum.isSelected();
            String ozellikler = txtOzellikler.getText();

            Oda yeniOda = new Oda(odaNo, odaAdi, odaTipi, kapasite, fiyat, musaitlik, ozellikler);
            
            if (odaDAO.odaEkle(yeniOda)) {
                lblMesaj.setText("Oda başarıyla eklendi.");
                tabloyuGuncelle();
            } else {
                lblMesaj.setText("Oda eklenirken bir hata oluştu!");
            }
        } catch (NumberFormatException e) {
            lblMesaj.setText("Lütfen sayısal değerleri doğru formatta girin!");
        }
    }

    @FXML
    private void odaGuncelle() {
        if (!formGecerliMi()) return;

        try {
            Oda oda = formdanOdaOlustur();
            if (odaDAO.odaGuncelle(oda)) {
                mesajGoster("Oda başarıyla güncellendi.", "success");
                tabloyuGuncelle();
            } else {
                mesajGoster("Oda güncellenirken bir hata oluştu!", "error");
            }
        } catch (Exception e) {
            mesajGoster("Lütfen tüm alanları doğru formatta doldurun!", "error");
        }
    }

    @FXML
    private void odaSil() {
        Oda secilenOda = tableOdalar.getSelectionModel().getSelectedItem();
        if (secilenOda != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Silme Onayı");
            alert.setHeaderText("Oda Silme İşlemi");
            alert.setContentText("Seçili odayı silmek istediğinizden emin misiniz?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                if (odaDAO.odaSil(secilenOda.getOdaNo())) {
                    mesajGoster("Oda başarıyla silindi.", "success");
                    tabloyuGuncelle();
                    formuTemizle();
                } else {
                    mesajGoster("Oda silinirken bir hata oluştu!", "error");
                }
            }
        } else {
            mesajGoster("Lütfen silinecek odayı seçin!", "error");
        }
    }

    private boolean formGecerliMi() {
        if (txtOdaNo.getText().isEmpty() || txtOdaAdi.getText().isEmpty() || 
            txtOdaTipi.getText().isEmpty() || txtKapasite.getText().isEmpty() || 
            txtFiyat.getText().isEmpty()) {
            mesajGoster("Lütfen tüm alanları doldurun!", "error");
            return false;
        }
        return true;
    }

    private Oda formdanOdaOlustur() {
        int odaNo = Integer.parseInt(txtOdaNo.getText());
        String odaAdi = txtOdaAdi.getText();
        String odaTipi = txtOdaTipi.getText();
        int kapasite = Integer.parseInt(txtKapasite.getText());
        double fiyat = Double.parseDouble(txtFiyat.getText());  // BigDecimal yerine double
        boolean musaitlik = chkDurum.isSelected();
        String ozellikler = txtOzellikler.getText();
        
        return new Oda(odaNo, odaAdi, odaTipi, kapasite, fiyat, musaitlik, ozellikler);
    }

    @FXML
    private void formuTemizle() {
        txtOdaNo.clear();
        txtOdaAdi.clear();
        txtOdaTipi.clear();
        txtKapasite.clear();
        txtFiyat.clear();
        chkDurum.setSelected(true);
        txtOzellikler.clear();
        lblMesaj.setText("");
        tableOdalar.getSelectionModel().clearSelection();
        txtOdaNo.setEditable(true); // Yeni oda eklemek için oda no girilebilir olsun
    }

    private void mesajGoster(String mesaj, String tip) {
        lblMesaj.setText(mesaj);
        lblMesaj.setStyle(tip.equals("success") ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }

    @FXML
    private void geriDon() {
        ilgiliSayfayaGit("/com/example/otelsistemi/Yonetici/yoneticianasayfa.fxml", btnGeriDon);
    }
} 