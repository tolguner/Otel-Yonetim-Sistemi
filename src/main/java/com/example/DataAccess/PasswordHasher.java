package com.example.DataAccess;

import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Pattern;

/**
 * Şifreleri BCrypt ile hash'ler ve doğrular.
 * dogrula(): veritabanında hâlâ eski düz metin şifre bulunan hesaplarla
 * geriye dönük uyumluluk için, hash görünmeyen değerlerle düz metin
 * karşılaştırması yapar.
 */
public class PasswordHasher {

    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[aby]\\$\\d{2}\\$.{53}$");

    private PasswordHasher() {}

    public static String hashle(String duzMetin) {
        if (duzMetin == null) return null;
        if (zatenHashli(duzMetin)) return duzMetin;
        return BCrypt.hashpw(duzMetin, BCrypt.gensalt());
    }

    public static boolean dogrula(String girilenSifre, String saklananDeger) {
        if (girilenSifre == null || saklananDeger == null) return false;
        if (!zatenHashli(saklananDeger)) {
            return girilenSifre.equals(saklananDeger);
        }
        try {
            return BCrypt.checkpw(girilenSifre, saklananDeger);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean zatenHashli(String deger) {
        return deger != null && BCRYPT_PATTERN.matcher(deger).matches();
    }
}
