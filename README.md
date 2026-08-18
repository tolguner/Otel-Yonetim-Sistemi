# Otel Yönetim Sistemi

JavaFX ve MySQL ile geliştirilmiş masaüstü otel rezervasyon ve yönetim uygulaması. Üç ayrı kullanıcı rolü (Admin, Yönetici, Müşteri) için ayrı arayüzler sunar.

**Durum:** Çalışır durumda — temel akışlar tamamlandı. Rezervasyon/bakiye/iptal mantığındaki hatalar giderildi, şifreler artık BCrypt ile hash'leniyor.

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

## Kurulum

1. MySQL'de veritabanını oluşturun.
2. Bağlantı bilgilerini ortam değişkenleriyle verin (varsayılan: `root` / `123456` / `localhost:3306/rezervasyonotomasyonu`):

```bash
export DB_URL="jdbc:mysql://localhost:3306/rezervasyonotomasyonu?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export DB_USER="root"
export DB_PASSWORD="şifreniz"
```

3. Uygulamayı çalıştırın:

```bash
./mvnw clean javafx:run
```

## Bilinen kısıtlar

- `OdaDAO` içinde `odaId` sütununa referans veren iki yardımcı metod (`uygunOdalariGetir`,
  `musaitOdalariGetir(..., Double, Double)`) hiçbir ekrandan çağrılmıyor ve gerçek şemada
  `odaId` diye bir sütun yok — çağrılırsa hata verir. Kullanılan tek arama metodu
  `musaitOdalariGetir(LocalDate, LocalDate, String, int)`.
- "Şifremi unuttum" akışları (Müşteri/Yönetici/Admin) TC-e-posta-telefon eşleşmesini
  kontrol ediyor ama gerçek bir doğrulama kodu/e-posta gönderimi yok.
- Arayüz bu oturumda görsel olarak (javafx:run ile) çalıştırılıp test edilmedi;
  değişiklikler DAO katmanında gerçek MySQL veritabanına karşı JUnit testleriyle doğrulandı.

## Gereksinimler

- JDK 17+
- MySQL 8+
- Maven (proje ile gelen `mvnw` sarmalayıcısı kullanılabilir)
