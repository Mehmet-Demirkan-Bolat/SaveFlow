package controller;

import model.IslemTuru;
import model.Kategori;
import service.KategoriServisi;
import service.OturumYoneticisi;

import java.util.List;

public class KategoriDenetleyici {
    private final KategoriServisi kategoriServisi;
    private final OturumYoneticisi oturumYoneticisi;

    public KategoriDenetleyici(KategoriServisi kategoriServisi,
                               OturumYoneticisi oturumYoneticisi) {
        this.kategoriServisi = kategoriServisi;
        this.oturumYoneticisi = oturumYoneticisi;
    }

    public void kategoriEkle(String ad, IslemTuru tur, String renk) {
        String kullaniciId = oturumYoneticisi.getAktifKullanici().getId();
        kategoriServisi.ekle(kullaniciId, ad, tur, renk);
    }

    public void kategoriEkle(String ad, IslemTuru tur) {
        kategoriEkle(ad, tur, "#3B82F6");
    }

    public void kategoriSil(String kategoriId) {
        kategoriServisi.sil(kategoriId);
    }

    public List<Kategori> kategorileriGetir() {
        String kullaniciId = oturumYoneticisi.getAktifKullanici().getId();
        return kategoriServisi.listele(kullaniciId);
    }
}
