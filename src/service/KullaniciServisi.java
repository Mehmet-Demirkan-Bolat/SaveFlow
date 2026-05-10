package service;

import exception.GirisHatasi;
import exception.KayitHatasi;
import model.Kullanici;
import repo.KullaniciRepo;
import util.DogrulamaYardimcisi;

import java.util.UUID;

public class KullaniciServisi {
    private final KullaniciRepo kullaniciRepo;
    private final OturumYoneticisi oturumYoneticisi;
    private final KategoriServisi kategoriServisi;

    public KullaniciServisi(KullaniciRepo kullaniciRepo, OturumYoneticisi oturumYoneticisi,
                            KategoriServisi kategoriServisi) {
        this.kullaniciRepo = kullaniciRepo;
        this.oturumYoneticisi = oturumYoneticisi;
        this.kategoriServisi = kategoriServisi;
    }

    public void kayitOl(String ad, String email, String sifre) throws KayitHatasi {
        if (!DogrulamaYardimcisi.bosDegilMi(ad)) {
            throw new KayitHatasi("Ad boş olamaz.");
        }
        if (!DogrulamaYardimcisi.emailGecerliMi(email)) {
            throw new KayitHatasi("Geçersiz e-posta formatı.");
        }
        if (!DogrulamaYardimcisi.sifreGecerliMi(sifre)) {
            throw new KayitHatasi("Sifre en az 6 karakter olmalidir.");
        }
        if (kullaniciRepo.emailMevcutMu(email)) {
            throw new KayitHatasi("Bu e-posta zaten kayıtlı.");
        }
        String id = UUID.randomUUID().toString();
        Kullanici yeni = new Kullanici(id, ad, email, sifre);
        kullaniciRepo.kaydet(yeni);
        // Yeni kullanıcıya hazır kategoriler ekle
        if (kategoriServisi != null) {
            kategoriServisi.varsayilanKategorileriEkle(id);
        }
    }

    public Kullanici girisYap(String email, String sifre) throws GirisHatasi {
        if (!DogrulamaYardimcisi.emailGecerliMi(email)) {
            throw new GirisHatasi("Geçersiz e-posta formatı.");
        }
        Kullanici kullanici = kullaniciRepo.emailIleGetir(email)
            .orElseThrow(() -> new GirisHatasi("Bu e-posta adresiyle kayıt bulunamadı."));
        if (!kullanici.getSifre().equals(sifre)) {
            throw new GirisHatasi("Şifre hatalı. Lütfen tekrar deneyin.");
        }
        oturumYoneticisi.oturumAc(kullanici);
        return kullanici;
    }
}
