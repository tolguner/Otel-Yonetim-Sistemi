-- Hizmet tablosunda "aktif" sütunu eksikti; HizmetDAO ve Yönetici hizmet
-- yönetimi ekranı bu sütunu okuyup yazdığı için tüm hizmet ekranı
-- (listeleme, ekleme, güncelleme) SQLException ile başarısız oluyordu.
-- Bu betik eksik sütunu ekler; mevcut hizmetler varsayılan olarak aktif kalır.

ALTER TABLE Hizmet
    ADD COLUMN aktif TINYINT(1) NOT NULL DEFAULT 1;
