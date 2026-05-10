package controller;

import exception.GirisHatasi;
import exception.KayitHatasi;
import model.Kullanici;
import service.KullaniciServisi;
import service.OturumYoneticisi;

public class KullaniciDenetleyici {
    private final KullaniciServisi kullaniciServisi;
    private final OturumYoneticisi oturumYoneticisi;

    public KullaniciDenetleyici(KullaniciServisi kullaniciServisi,
                                OturumYoneticisi oturumYoneticisi) {
        this.kullaniciServisi = kullaniciServisi;
        this.oturumYoneticisi = oturumYoneticisi;
    }

    public void kayitOl(String ad, String email, String sifre) throws KayitHatasi {
        kullaniciServisi.kayitOl(ad, email, sifre);
    }

    public void girisYap(String email, String sifre) throws GirisHatasi {
        kullaniciServisi.girisYap(email, sifre);
        // oturumYoneticisi.oturumAc zaten KullaniciServisi içinde çağrılıyor
    }

    public void cikisYap() {
        oturumYoneticisi.oturumKapat();
    }

    public Kullanici getAktifKullanici() {
        return oturumYoneticisi.getAktifKullanici();
    }
}
