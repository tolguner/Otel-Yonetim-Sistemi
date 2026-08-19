-- Otel Yönetim Sistemi - Veritabanı Şeması
-- Bu dosya mevcut geliştirme veritabanının (rezervasyonotomasyonu) yapısal
-- dökümüdür (mysqldump --no-data ile alınıp elle düzenlenmiştir). Veri
-- içermez, sadece tablo/sütun/ilişki tanımlarını içerir.
--
-- Kullanım:
--   mysql -u root -p < sql/schema.sql
-- ardından sql/migrations/ altındaki betikleri sırayla çalıştırın
-- (bu dosyaya zaten dahil edilmiş olan düzeltmeler hariç, ileride
-- eklenecek yeni migration'lar için).

CREATE DATABASE IF NOT EXISTS rezervasyonotomasyonu
    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE rezervasyonotomasyonu;

-- Müşteri hesapları. `bakiye` sütunu şemada bulunur ancak uygulama
-- güncel bakiyeyi Bakiye tablosundaki son işlem kaydından hesaplar
-- (bkz. BakiyeDAO); bu sütun MusteriDAO içindeki kullanılmayan
-- yardımcı metodlar dışında canlı akışlarda okunmaz/yazılmaz.
CREATE TABLE IF NOT EXISTS Musteri (
    tcKimlikNo varchar(11) NOT NULL,
    ad varchar(50) NOT NULL,
    soyad varchar(50) NOT NULL,
    email varchar(100) NOT NULL,
    sifre varchar(100) NOT NULL,
    telefon varchar(15) NOT NULL,
    bakiye decimal(10,2) DEFAULT '0.00',
    PRIMARY KEY (tcKimlikNo),
    UNIQUE KEY email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Yönetici hesapları (oda/hizmet/hizmet talebi işlemlerini yürütür).
CREATE TABLE IF NOT EXISTS Yonetici (
    tcKimlikNo varchar(11) NOT NULL,
    ad varchar(50) DEFAULT NULL,
    soyad varchar(50) DEFAULT NULL,
    email varchar(100) DEFAULT NULL,
    sifre varchar(100) DEFAULT NULL,
    telefon varchar(15) DEFAULT NULL,
    PRIMARY KEY (tcKimlikNo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Admin hesapları (yönetici hesaplarını yönetir).
CREATE TABLE IF NOT EXISTS Admin (
    tcKimlikNo varchar(11) NOT NULL,
    ad varchar(50) DEFAULT NULL,
    soyad varchar(50) DEFAULT NULL,
    email varchar(100) DEFAULT NULL,
    sifre varchar(100) DEFAULT NULL,
    telefon varchar(15) DEFAULT NULL,
    PRIMARY KEY (tcKimlikNo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Otel odaları.
CREATE TABLE IF NOT EXISTS Oda (
    odaNo int NOT NULL,
    odaAdi varchar(50) DEFAULT NULL,
    odaTipi varchar(50) DEFAULT NULL,
    kapasite int DEFAULT NULL,
    fiyat decimal(10,2) DEFAULT NULL,
    musaitlikDurumu tinyint(1) DEFAULT NULL,
    ozellikler text,
    PRIMARY KEY (odaNo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Sunulan hizmetler (oda servisi, çamaşırhane, spa vb.).
CREATE TABLE IF NOT EXISTS Hizmet (
    hizmetId int NOT NULL AUTO_INCREMENT,
    hizmetAdi varchar(100) DEFAULT NULL,
    aciklama text,
    fiyat decimal(10,2) DEFAULT NULL,
    aktif tinyint(1) NOT NULL DEFAULT '1',
    PRIMARY KEY (hizmetId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Rezervasyonlar. durum: 'ONAYLANDI' | 'AKTIF' | 'IPTAL'
CREATE TABLE IF NOT EXISTS Rezervasyon (
    rezervasyonId int NOT NULL AUTO_INCREMENT,
    tcKimlikNo varchar(11) DEFAULT NULL,
    odaNo int DEFAULT NULL,
    baslangicTarihi date DEFAULT NULL,
    bitisTarihi date DEFAULT NULL,
    toplamTutar decimal(10,2) DEFAULT NULL,
    durum varchar(20) DEFAULT 'AKTIF',
    PRIMARY KEY (rezervasyonId),
    KEY tcKimlikNo (tcKimlikNo),
    KEY rezervasyon_ibfk_2 (odaNo),
    CONSTRAINT rezervasyon_ibfk_1 FOREIGN KEY (tcKimlikNo) REFERENCES Musteri (tcKimlikNo),
    CONSTRAINT rezervasyon_ibfk_2 FOREIGN KEY (odaNo) REFERENCES Oda (odaNo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Bakiye hareketleri (defter/ledger). Güncel bakiye, bir müşterinin
-- en son kaydındaki toplamBakiye alanından okunur (MAX değil, en
-- son bakiyeId'ye göre) — bkz. BakiyeDAO.bakiyeGetir.
CREATE TABLE IF NOT EXISTS Bakiye (
    bakiyeId int NOT NULL AUTO_INCREMENT,
    tcKimlikNo varchar(11) DEFAULT NULL,
    islemTarihi date DEFAULT NULL,
    islemTipi varchar(50) DEFAULT NULL,
    tutar decimal(10,2) DEFAULT NULL,
    toplamBakiye decimal(10,2) DEFAULT NULL,
    PRIMARY KEY (bakiyeId),
    KEY tcKimlikNo (tcKimlikNo),
    CONSTRAINT bakiye_ibfk_1 FOREIGN KEY (tcKimlikNo) REFERENCES Musteri (tcKimlikNo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Konaklama değerlendirmeleri (1 rezervasyon -> en fazla 1 değerlendirme,
-- uygulama katmanında kontrol edilir).
CREATE TABLE IF NOT EXISTS Degerlendirme (
    degerlendirmeId int NOT NULL AUTO_INCREMENT,
    rezervasyonId int DEFAULT NULL,
    puan int DEFAULT NULL,
    yorum text,
    tarih date DEFAULT NULL,
    PRIMARY KEY (degerlendirmeId),
    KEY rezervasyonId (rezervasyonId),
    CONSTRAINT degerlendirme_ibfk_1 FOREIGN KEY (rezervasyonId) REFERENCES Rezervasyon (rezervasyonId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Rezervasyona bağlı hizmet talepleri. durum: 'Talebiniz İletildi' | 'Talebiniz Onaylandı'
CREATE TABLE IF NOT EXISTS HizmetTalebi (
    talepId int NOT NULL AUTO_INCREMENT,
    rezervasyonId int DEFAULT NULL,
    hizmetId int DEFAULT NULL,
    aciklama text,
    talepTarihi datetime DEFAULT CURRENT_TIMESTAMP,
    durum varchar(50) DEFAULT 'Talebiniz İletildi',
    fiyat decimal(10,2) DEFAULT NULL,
    odaNo int DEFAULT NULL,
    PRIMARY KEY (talepId),
    KEY rezervasyonId (rezervasyonId),
    KEY hizmetId (hizmetId),
    KEY odaNo (odaNo),
    CONSTRAINT hizmettalebi_ibfk_1 FOREIGN KEY (rezervasyonId) REFERENCES Rezervasyon (rezervasyonId),
    CONSTRAINT hizmettalebi_ibfk_2 FOREIGN KEY (hizmetId) REFERENCES Hizmet (hizmetId),
    CONSTRAINT hizmettalebi_ibfk_3 FOREIGN KEY (odaNo) REFERENCES Oda (odaNo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
