package com.example.DataAccess;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    void hashleDuzMetniHicBirZamanGeriDondurmez() {
        String hash = PasswordHasher.hashle("gizliSifre1");
        assertNotEquals("gizliSifre1", hash);
        assertTrue(PasswordHasher.zatenHashli(hash));
    }

    @Test
    void dogrulaDogruSifreyleBasarili() {
        String hash = PasswordHasher.hashle("gizliSifre1");
        assertTrue(PasswordHasher.dogrula("gizliSifre1", hash));
    }

    @Test
    void dogrulaYanlisSifreyleBasarisiz() {
        String hash = PasswordHasher.hashle("gizliSifre1");
        assertFalse(PasswordHasher.dogrula("yanlisSifre", hash));
    }

    @Test
    void hashleZatenHashliDegeriTekrarHashlemez() {
        String hash = PasswordHasher.hashle("gizliSifre1");
        assertEquals(hash, PasswordHasher.hashle(hash),
                "Zaten hash'li bir değer tekrar hash'lenmemeli (çift hash'leme hesabı kilitler)");
    }

    @Test
    void dogrulaEskiDuzMetinSifrelerleGeriyeDonukUyumlu() {
        // Hash'lenmeden önce eklenmiş eski hesaplar hâlâ giriş yapabilmeli
        assertTrue(PasswordHasher.dogrula("eskiDuzMetinSifre", "eskiDuzMetinSifre"));
        assertFalse(PasswordHasher.dogrula("yanlis", "eskiDuzMetinSifre"));
    }
}
