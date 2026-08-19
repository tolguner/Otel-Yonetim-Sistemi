-- Otel Yönetim Sistemi - Başlangıç Verisi (opsiyonel)
-- Odaları ve hizmetleri doldurur ki proje ilk kurulduğunda oda arama /
-- hizmet talebi ekranları boş görünmesin. Kullanıcı hesapları (müşteri,
-- yönetici, admin) içermez — bunları uygulamadaki kayıt ekranlarından
-- oluşturun (böylece şifreler BCrypt ile hash'lenir).
--
-- Kullanım (önce sql/schema.sql çalıştırılmış olmalı):
--   mysql -u root -p rezervasyonotomasyonu < sql/seed.sql

USE rezervasyonotomasyonu;

INSERT INTO Oda (odaNo, odaAdi, odaTipi, kapasite, fiyat, musaitlikDurumu, ozellikler) VALUES
    (101, 'Lavanta Suite',      'Suite',    2, 2000.00, 1, 'Özel balkon, Jakuzi, Şömine, Deniz manzarası, Smart TV, Minibar'),
    (102, 'Begonvil Bahçe',     'Deluxe',   2, 1500.00, 1, 'Özel bahçe, Hamak, Açık oturma alanı, Smart TV, Minibar'),
    (103, 'Yasemin Teraslı',    'Suite',    3, 2200.00, 1, 'Geniş teras, Lounge alanı, Jakuzi, Deniz manzarası, Smart TV'),
    (104, 'Defne Aile Suite',   'Aile',     4, 2800.00, 1, '2 Yatak odası, Oturma odası, Mutfak, Teras, Deniz manzarası'),
    (105, 'Mercan Köşe Suite',  'Suite',    2, 2400.00, 1, 'Köşe suite, 180° Deniz manzarası, Jakuzi, Özel balkon'),
    (106, 'Mimoza Bahçe',       'Standart', 2, 1200.00, 1, 'Bahçe manzarası, Balkon, Smart TV, Minibar'),
    (107, 'Sardunya Tek',       'Standart', 1,  800.00, 1, 'Şehir manzarası, Balkon, Smart TV, Çalışma masası'),
    (108, 'Manolya Suite',      'Suite',    2, 1800.00, 1, 'Bahçe manzarası, Jakuzi, Oturma alanı, Smart TV, Minibar');

INSERT INTO Hizmet (hizmetAdi, aciklama, fiyat, aktif) VALUES
    ('Oda Servisi',   'Yemek ve içecek servisi',       50.00,  1),
    ('Çamaşırhane',   'Kıyafet yıkama ve ütüleme',     30.00,  1),
    ('Spa',           'Masaj ve spa hizmetleri',      100.00,  1),
    ('Araç Kiralama', 'Günlük araç kiralama hizmeti', 200.00,  1),
    ('Rehberlik',     'Şehir turu ve rehberlik hizmeti', 150.00, 1);
