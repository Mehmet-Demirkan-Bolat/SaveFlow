package service;

import model.IslemTuru;
import model.Kategori;
import repo.KategoriRepo;

import java.util.List;
import java.util.UUID;

public class KategoriServisi {
    private final KategoriRepo kategoriRepo;

    public KategoriServisi(KategoriRepo kategoriRepo) {
        this.kategoriRepo = kategoriRepo;
    }

    public void ekle(String kullaniciId, String ad, IslemTuru tur, String renk) {
        String id = UUID.randomUUID().toString();
        Kategori kategori = new Kategori(id, kullaniciId, ad, tur, renk);
        kategoriRepo.kaydet(kategori);
    }

    /** Geriye dönük uyumluluk — varsayılan renk ile */
    public void ekle(String kullaniciId, String ad, IslemTuru tur) {
        ekle(kullaniciId, ad, tur, "#3B82F6");
    }

    public void sil(String kategoriId) {
        kategoriRepo.sil(kategoriId);
    }

    public List<Kategori> listele(String kullaniciId) {
        return kategoriRepo.kullaniciyaGore(kullaniciId);
    }

    /** Yeni kayıt olan kullanıcıya hazır kategoriler ekler */
    public void varsayilanKategorileriEkle(String kullaniciId) {
        // Gider kategorileri
        ekle(kullaniciId, "Yemek & İçecek", IslemTuru.GIDER, "#F97316");
        ekle(kullaniciId, "Ev & Kira",       IslemTuru.GIDER, "#3B82F6");
        ekle(kullaniciId, "Giyim",           IslemTuru.GIDER, "#A855F7");
        ekle(kullaniciId, "Eğlence",         IslemTuru.GIDER, "#22C55E");
        ekle(kullaniciId, "Ulaşım",          IslemTuru.GIDER, "#14B8A6");
        // Gelir kategorileri
        ekle(kullaniciId, "Maaş",            IslemTuru.GELIR, "#FBBF24");
        ekle(kullaniciId, "Ek Gelir",        IslemTuru.GELIR, "#60A5FA");
    }
}
