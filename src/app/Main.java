package app;

import controller.*;
import repo.*;
import service.*;
import ui.GirisEkrani;

import javax.swing.*;

/**
 * Uygulamanın giriş noktası.
 * <p>
 * OOP — Bağımlılık Enjeksiyonu (Dependency Injection): tüm repo, servis ve
 * denetleyici nesneleri burada oluşturulur ve birbirine "enjekte" edilir.
 * Hiçbir alt katman kendi bağımlılığını {@code new} ile yaratmaz; bu sayede
 * birimler birbirinden bağımsız test edilebilir ve değiştirilebilir.<br><br>
 * Katman sırası: Repo → Servis → Denetleyici → UI
 */
public class Main {
    public static void main(String[] args) {
        // Swing olayları EDT (Event Dispatch Thread) üzerinde çalışmalıdır.
        SwingUtilities.invokeLater(() -> {

            // ── 1. Katman: Repository (Veri Erişim) ─────────────────
            // Her repo, ilgili arayüzü uygular; servisler arayüz tipini kullanır.
            SQLiteKullaniciRepo        kullaniciRepo      = new SQLiteKullaniciRepo();
            SQLiteIslemRepo            islemRepo          = new SQLiteIslemRepo();
            SQLiteKategoriRepo         kategoriRepo       = new SQLiteKategoriRepo();
            SQLiteButceLimitiRepo      butceLimitiRepo    = new SQLiteButceLimitiRepo();
            SQLiteTekrarlananIslemRepo tekrarlananRepo    = new SQLiteTekrarlananIslemRepo();
            SQLiteHedefTasarrufRepo    hedefRepo          = new SQLiteHedefTasarrufRepo();

            // ── 2. Katman: Servis (İş Mantığı) ──────────────────────
            // Oturum yöneticisi tekil (singleton benzeri) nesne olarak paylaşılır.
            OturumYoneticisi        oturum             = new OturumYoneticisi();
            IslemServisi            islemServisi       = new IslemServisi(islemRepo);
            KategoriServisi         kategoriServisi    = new KategoriServisi(kategoriRepo);
            KullaniciServisi        kullaniciServisi   = new KullaniciServisi(kullaniciRepo, oturum, kategoriServisi);
            AnalizServisi           analizServisi      = new AnalizServisi(islemServisi);
            RaporServisi            raporServisi       = new RaporServisi(analizServisi);
            ButceServisi            butceServisi       = new ButceServisi(butceLimitiRepo);
            TekrarlananIslemServisi tekrarlananServisi = new TekrarlananIslemServisi(tekrarlananRepo, islemServisi);
            HedefTasarrufServisi    hedefServisi       = new HedefTasarrufServisi(hedefRepo);

            // ── 3. Katman: Denetleyici (MVC — Controller) ────────────
            // Denetleyiciler UI'ı servislerden soyutlar; arayüz tipleri kullanılır.
            KullaniciDenetleyici kullaniciDenetleyici =
                new KullaniciDenetleyici(kullaniciServisi, oturum);

            IslemDenetleyici islemDenetleyici =
                new IslemDenetleyici(islemServisi, kategoriServisi,
                                     analizServisi, raporServisi, oturum);

            KategoriDenetleyici kategoriDenetleyici =
                new KategoriDenetleyici(kategoriServisi, oturum);

            ButceDenetleyici butceDenetleyici =
                new ButceDenetleyici(butceServisi, oturum);

            TekrarlananIslemDenetleyici tekrarlananDenetleyici =
                new TekrarlananIslemDenetleyici(tekrarlananServisi, oturum);

            HedefTasarrufDenetleyici hedefDenetleyici =
                new HedefTasarrufDenetleyici(hedefServisi, oturum);

            // ── 4. Katman: UI (View) — giriş ekranını başlat ────────
            GirisEkrani girisEkrani = new GirisEkrani(
                kullaniciDenetleyici,
                islemDenetleyici,
                kategoriDenetleyici
            );
            girisEkrani.setEkDenetleyiciler(
                butceDenetleyici,
                tekrarlananDenetleyici,
                hedefDenetleyici
            );
            girisEkrani.goster();
        });
    }
}
