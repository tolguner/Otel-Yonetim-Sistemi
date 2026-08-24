# Otel Yönetim Sistemi

JavaFX ve MySQL ile geliştirilmiş masaüstü otel rezervasyon ve yönetim uygulaması. Üç ayrı kullanıcı rolü (Admin, Yönetici, Müşteri) için ayrı arayüzler sunar.

**Bilişim Sistemleri İçin Java** dersi kapsamında geliştirilmiş bir dönem projesidir. Amaç, JavaFX ile masaüstü arayüz tasarımı, JDBC üzerinden ilişkisel veritabanı erişimi ve katmanlı mimari (Model–DAO–Controller) uygulamaktır.

## Özellikler

**Müşteri**
- Kayıt olma, giriş, şifre sıfırlama
- Oda arama ve rezervasyon oluşturma
- Mevcut / geçmiş / gelecek rezervasyonları görüntüleme
- Bakiye yönetimi
- Konaklama değerlendirmesi ekleme

**Yönetici**
- Oda işlemleri (ekleme, güncelleme, silme)
- Hizmet tanımlama ve hizmet taleplerini karşılama
- Müşteri işlemleri

**Admin**
- Yönetici hesaplarının yönetimi
- Profil işlemleri

## Teknolojiler

| Katman | Teknoloji |
|---|---|
| Arayüz | JavaFX, FXML, ControlsFX, BootstrapFX |
| Veri erişimi | JDBC (DAO deseni) |
| Veritabanı | MySQL |
| Şifre güvenliği | BCrypt (jBCrypt) |
| Derleme | Maven |
| Test | JUnit 5 |

## Mimari

Katmanlı yapı kullanılır:

- `Models/` — veri sınıfları (Musteri, Oda, Rezervasyon, Hizmet, Bakiye, Degerlendirme)
- `DataAccess/` — her varlık için DAO sınıfları ve `DBConnection`
- `Controllers/` — her ekran için FXML controller'ı
- `resources/.../*.fxml` — arayüz tanımları
- `sql/` — veritabanı şeması (`schema.sql`), örnek veri (`seed.sql`) ve şema güncellemeleri (`migrations/`)

## Kurulum

1. Veritabanını ve tablolarını oluşturun. Şema betiği veritabanını (yoksa)
   ve tüm tabloları oluşturur:

```bash
mysql -u root -p < sql/schema.sql
```

   İsteğe bağlı olarak örnek oda ve hizmet verisini yükleyin (arama/hizmet
   ekranlarının boş görünmemesi için):

```bash
mysql -u root -p rezervasyonotomasyonu < sql/seed.sql
```

   (`sql/migrations/` altındaki betikler, veritabanını daha eski bir sürümle
   kurmuş olanlar içindir; `schema.sql` bu düzeltmeleri zaten içerir.)

2. Bağlantı bilgilerini ortam değişkenleriyle verin. Kaynak koda şifre
   gömülmemesi için `DB_PASSWORD` **zorunludur**; `DB_URL` ve `DB_USER`
   verilmezse `localhost:3306/rezervasyonotomasyonu` ve `root` varsayılır:

```bash
export DB_URL="jdbc:mysql://localhost:3306/rezervasyonotomasyonu?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export DB_USER="root"
export DB_PASSWORD="şifreniz"
```

3. Uygulamayı çalıştırın:

```bash
./mvnw clean javafx:run
```

## Test

```bash
./mvnw test
```

`PasswordHasherTest` saf birim testidir (veritabanı gerekmez).
`RezervasyonBakiyeEntegrasyonTest` bakiye/rezervasyon/hizmet talebi
para akışını gerçek bir MySQL bağlantısına karşı doğrular (2. maddedeki
ortam değişkenleri gerekir); kendi test verisini oluşturur ve her
testten sonra temizler, mevcut verilere dokunmaz.

## Gereksinimler

- JDK 17+
- MySQL 8+
- Maven (proje ile gelen `mvnw` sarmalayıcısı kullanılabilir)
