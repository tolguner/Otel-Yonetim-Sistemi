package com.example.DataAccess;

import com.example.Models.Bakiye;
import com.example.Models.HizmetTalebi;
import com.example.Models.Rezervasyon;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bakiye, rezervasyon ve hizmet talebi arasındaki para akışını gerçek
 * MySQL veritabanına karşı doğrular. Kendi test verisini oluşturur ve
 * her testten sonra temizler; mevcut kullanıcı verisine dokunmaz.
 *
 * Çalıştırmak için proje README'sindeki DB_URL/DB_USER/DB_PASSWORD
 * ortam değişkenleriyle erişilebilir bir MySQL sunucusu gerekir.
 */
class RezervasyonBakiyeEntegrasyonTest {

    private static final String TC = "00000000001";
    private static final int ODA_NO = 99999;

    private final BakiyeDAO bakiyeDAO = new BakiyeDAO();
    private final RezervasyonDAO rezervasyonDAO = new RezervasyonDAO();
    private final OdaDAO odaDAO = new OdaDAO();
    private final HizmetTalebiDAO hizmetTalebiDAO = new HizmetTalebiDAO();

    @BeforeEach
    void setUp() throws Exception {
        temizle();
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement p = conn.prepareStatement(
                    "INSERT INTO Musteri (tcKimlikNo, ad, soyad, email, sifre, telefon) VALUES (?, 'Test', 'Kullanici', ?, '123456', '5550000000')")) {
                p.setString(1, TC);
                p.setString(2, TC + "@test.local");
                p.executeUpdate();
            }
            try (PreparedStatement p = conn.prepareStatement(
                    "INSERT INTO Oda (odaNo, odaAdi, odaTipi, kapasite, fiyat, musaitlikDurumu, ozellikler) VALUES (?, 'Test Oda', 'Standart', 2, 100, 1, 'test')")) {
                p.setInt(1, ODA_NO);
                p.executeUpdate();
            }
        }
        bakiyeDAO.bakiyeYukle(TC, 1000);
    }

    @AfterEach
    void tearDown() throws Exception {
        temizle();
    }

    private void temizle() throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement p = conn.prepareStatement(
                    "DELETE FROM HizmetTalebi WHERE rezervasyonId IN (SELECT rezervasyonId FROM Rezervasyon WHERE tcKimlikNo = ?)")) {
                p.setString(1, TC); p.executeUpdate();
            }
            try (PreparedStatement p = conn.prepareStatement("DELETE FROM Rezervasyon WHERE tcKimlikNo = ?")) {
                p.setString(1, TC); p.executeUpdate();
            }
            try (PreparedStatement p = conn.prepareStatement("DELETE FROM Bakiye WHERE tcKimlikNo = ?")) {
                p.setString(1, TC); p.executeUpdate();
            }
            try (PreparedStatement p = conn.prepareStatement("DELETE FROM Musteri WHERE tcKimlikNo = ?")) {
                p.setString(1, TC); p.executeUpdate();
            }
            try (PreparedStatement p = conn.prepareStatement("DELETE FROM Oda WHERE odaNo = ?")) {
                p.setInt(1, ODA_NO); p.executeUpdate();
            }
        }
    }

    private int sonRezervasyonuOlustur(double tutar, LocalDate giris, LocalDate cikis, String durum) {
        Rezervasyon r = new Rezervasyon();
        r.setTcKimlikNo(TC);
        r.setOdaNo(ODA_NO);
        r.setBaslangicTarihi(giris);
        r.setBitisTarihi(cikis);
        r.setToplamTutar(tutar);
        r.setDurum(durum);
        assertTrue(rezervasyonDAO.rezervasyonEkle(r));

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement p = conn.prepareStatement(
                     "SELECT rezervasyonId FROM Rezervasyon WHERE tcKimlikNo = ? ORDER BY rezervasyonId DESC LIMIT 1")) {
            p.setString(1, TC);
            var rs = p.executeQuery();
            assertTrue(rs.next());
            return rs.getInt("rezervasyonId");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void rezervasyonUcretiBakiyedenDusulurIadeEdilmezOnceGeriYuklenmez() {
        assertEquals(1000.0, bakiyeDAO.bakiyeGetir(TC), 0.001);

        boolean odemeBasarili = bakiyeDAO.bakiyeGuncelle(TC, -300.0, Bakiye.ISLEM_ODEME);
        assertTrue(odemeBasarili);

        assertEquals(700.0, bakiyeDAO.bakiyeGetir(TC), 0.001,
                "Rezervasyon ücreti bakiyeden düşülmeli, artırılmamalı");
    }

    @Test
    void iptalIadesiSadeceBirKezYapilirCiftIadeEngellenir() {
        double tutar = 300.0;
        bakiyeDAO.bakiyeGuncelle(TC, -tutar, Bakiye.ISLEM_ODEME);
        int rezervasyonId = sonRezervasyonuOlustur(tutar, LocalDate.now().plusDays(5), LocalDate.now().plusDays(8), "ONAYLANDI");

        assertTrue(rezervasyonDAO.rezervasyonIptalEt(rezervasyonId), "İlk iptal başarılı olmalı");
        assertEquals(1000.0, bakiyeDAO.bakiyeGetir(TC), 0.001, "İptal sonrası tutar tam iade edilmeli");

        assertFalse(rezervasyonDAO.rezervasyonIptalEt(rezervasyonId), "Aynı rezervasyon ikinci kez iptal edilememeli");
        assertEquals(1000.0, bakiyeDAO.bakiyeGetir(TC), 0.001, "İkinci iptal denemesi tekrar iade yapmamalı");
    }

    @Test
    void iptalEdilenRezervasyonOdayiTekrarMusaitYapar() {
        LocalDate giris = LocalDate.now().plusDays(10);
        LocalDate cikis = LocalDate.now().plusDays(12);
        int rezervasyonId = sonRezervasyonuOlustur(200.0, giris, cikis, "ONAYLANDI");

        boolean doluyken = odaDAO.musaitOdalariGetir(giris, cikis, null, 2).stream()
                .anyMatch(o -> o.getOdaNo() == ODA_NO);
        assertFalse(doluyken, "Aktif rezervasyon varken oda müsait listede görünmemeli");

        assertTrue(rezervasyonDAO.rezervasyonIptalEt(rezervasyonId));

        boolean iptalSonrasi = odaDAO.musaitOdalariGetir(giris, cikis, null, 2).stream()
                .anyMatch(o -> o.getOdaNo() == ODA_NO);
        assertTrue(iptalSonrasi, "İptal edilen rezervasyon odayı tekrar müsait yapmalı");
    }

    @Test
    void hizmetTalebiGuncellemesiFiyatFarkiniBakiyeyeYansitir() {
        int rezervasyonId = sonRezervasyonuOlustur(100.0, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "ONAYLANDI");

        HizmetTalebi talep = new HizmetTalebi();
        talep.setRezervasyonId(rezervasyonId);
        talep.setHizmetAdi("Oda Servisi"); // 50 TL
        talep.setAciklama("ilk talep");
        talep.setFiyat(50.0);
        talep.setOdaNo(ODA_NO);
        assertTrue(hizmetTalebiDAO.talepOlustur(talep));
        bakiyeDAO.bakiyeGuncelle(TC, -50.0, "HIZMET_ODEMESI");
        assertEquals(950.0, bakiyeDAO.bakiyeGetir(TC), 0.001);

        HizmetTalebi yuklenen = hizmetTalebiDAO.talepGetir(rezervasyonId);
        assertNotNull(yuklenen);
        assertEquals(50.0, yuklenen.getFiyat(), 0.001, "talepGetir fiyatı da yüklemeli");

        // Spa'ya (100 TL) yükselt -> 50 TL fark bakiyeden düşülmeli
        assertTrue(hizmetTalebiDAO.talepGuncelle(yuklenen.getTalepId(), "Spa", "guncellendi", yuklenen.getFiyat(), TC));
        assertEquals(900.0, bakiyeDAO.bakiyeGetir(TC), 0.001, "Fiyat farkı (100-50=50) bakiyeden düşülmeli");
    }
}
