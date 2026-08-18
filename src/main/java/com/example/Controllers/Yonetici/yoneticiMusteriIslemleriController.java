package com.example.Controllers.Yonetici;

import com.example.Controllers.baseController;
import com.example.DataAccess.MusteriDAO;
import com.example.Models.Musteri;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class yoneticiMusteriIslemleriController extends baseController {

    @FXML private TableView<Musteri> tableMusteriler;
    @FXML private TableColumn<Musteri, String> colTcKimlikNo;
    @FXML private TableColumn<Musteri, String> colAd;
    @FXML private TableColumn<Musteri, String> colSoyad;
    @FXML private TableColumn<Musteri, String> colEmail;
    @FXML private TableColumn<Musteri, String> colTelefon;

    @FXML private TextField txtTcKimlikNo;
    @FXML private TextField txtAd;
    @FXML private TextField txtSoyad;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefon;
    @FXML private PasswordField txtSifre;
    @FXML private Label lblMesaj;
    @FXML private Button btnGeriDon;

    private final MusteriDAO musteriDAO = new MusteriDAO();

    @FXML
    public void initialize() {
        tabloyuAyarla();
        tabloyuGuncelle();
        tabloSecimDinleyicisiEkle();
    }

    private void tabloyuAyarla() {
        colTcKimlikNo.setCellValueFactory(new PropertyValueFactory<>("tcKimlikNo"));
        colAd.setCellValueFactory(new PropertyValueFactory<>("ad"));
        colSoyad.setCellValueFactory(new PropertyValueFactory<>("soyad"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefon.setCellValueFactory(new PropertyValueFactory<>("telefon"));
    }

    private void tabloyuGuncelle() {
        ObservableList<Musteri> musteriListesi = musteriDAO.tumMusterileriGetir();
        tableMusteriler.setItems(musteriListesi);
    }

    private void tabloSecimDinleyicisiEkle() {
        tableMusteriler.getSelectionModel().selectedItemProperty().addListener((obs, eskiSecim, yeniSecim) -> {
            if (yeniSecim != null) {
                txtTcKimlikNo.setText(yeniSecim.getTcKimlikNo());
                txtTcKimlikNo.setEditable(false);
                txtAd.setText(yeniSecim.getAd());
                txtSoyad.setText(yeniSecim.getSoyad());
                txtEmail.setText(yeniSecim.getEmail());
                txtTelefon.setText(yeniSecim.getTelefon());
                txtSifre.clear();
            }
        });
    }

    @FXML
    private void musteriEkle() {
        if (!formGecerliMi()) return;

        Musteri yeniMusteri = new Musteri(
                txtTcKimlikNo.getText(),
                txtAd.getText(),
                txtSoyad.getText(),
                txtEmail.getText(),
                txtTelefon.getText(),
                txtSifre.getText()
        );

        if (musteriDAO.musteriEkle(yeniMusteri)) {
            mesajGoster("Müşteri başarıyla eklendi.", "success");
            tabloyuGuncelle();
            formuTemizle();
        } else {
            mesajGoster("Müşteri eklenirken bir hata oluştu!", "error");
        }
    }

    @FXML
    private void musteriGuncelle() {
        if (txtTcKimlikNo.getText().isEmpty() || txtAd.getText().isEmpty() ||
                txtSoyad.getText().isEmpty() || txtEmail.getText().isEmpty() || txtTelefon.getText().isEmpty()) {
            mesajGoster("Lütfen tüm alanları doldurun!", "error");
            return;
        }

        String yeniSifre = txtSifre.getText().isEmpty() ? null : txtSifre.getText();

        if (musteriDAO.musteriGuncelle(txtTcKimlikNo.getText(), txtAd.getText(), txtSoyad.getText(),
                txtEmail.getText(), txtTelefon.getText(), yeniSifre)) {
            mesajGoster("Müşteri başarıyla güncellendi.", "success");
            tabloyuGuncelle();
            formuTemizle();
        } else {
            mesajGoster("Müşteri güncellenirken bir hata oluştu!", "error");
        }
    }

    @FXML
    private void musteriSil() {
        Musteri seciliMusteri = tableMusteriler.getSelectionModel().getSelectedItem();
        if (seciliMusteri == null) {
            mesajGoster("Lütfen silinecek müşteriyi seçin!", "error");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Silme Onayı");
        alert.setHeaderText(null);
        alert.setContentText("Seçili müşteriyi silmek istediğinize emin misiniz?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (musteriDAO.musteriSil(seciliMusteri.getTcKimlikNo())) {
                mesajGoster("Müşteri başarıyla silindi.", "success");
                tabloyuGuncelle();
                formuTemizle();
            } else {
                mesajGoster("Müşteri silinirken bir hata oluştu!", "error");
            }
        }
    }

    private boolean formGecerliMi() {
        if (txtTcKimlikNo.getText().isEmpty() || txtAd.getText().isEmpty() ||
                txtSoyad.getText().isEmpty() || txtEmail.getText().isEmpty() ||
                txtTelefon.getText().isEmpty() || txtSifre.getText().isEmpty()) {
            mesajGoster("Lütfen tüm alanları doldurun!", "error");
            return false;
        }
        return true;
    }

    @FXML
    private void formuTemizle() {
        txtTcKimlikNo.clear();
        txtTcKimlikNo.setEditable(true);
        txtAd.clear();
        txtSoyad.clear();
        txtEmail.clear();
        txtTelefon.clear();
        txtSifre.clear();
        lblMesaj.setText("");
        tableMusteriler.getSelectionModel().clearSelection();
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
