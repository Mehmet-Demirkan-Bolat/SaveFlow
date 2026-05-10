package controller;

import exception.GecersizMiktarHatasi;
import model.Islem;
import model.Kategori;
import model.Rapor;
import service.AnalizYapilabilir;
import service.IslemServisi;
import service.KategoriServisi;
import service.OturumYoneticisi;
import service.Raporlanabilir;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Kullanıcı arayüzü ile iş mantığı katmanı arasındaki köprü (MVC — Controller).
 * <p>
 * OOP — Bağımlılık Enjeksiyonu: servisler arayüz tipinden alınır; UI somut
 * sınıfları doğrudan çağırmaz.<br>
 * OOP — Sorumluluk Ayrımı: aktif kullanıcı bilgisini {@link OturumYoneticisi}'nden
 * alır; UI'ın oturum detaylarını bilmesine gerek kalmaz.
 */
public class IslemDenetleyici {

    private final IslemServisi islemServisi;
    private final KategoriServisi kategoriServisi;

    /** Analiz işlemleri için arayüz üzerinden erişim — polimorfik kullanım. */
    private final AnalizYapilabilir analizServisi;

    /** Rapor üretimi için arayüz üzerinden erişim. */
    private final Raporlanabilir raporServisi;

    /** Aktif kullanıcı kimliğini sağlar. */
    private final OturumYoneticisi oturumYoneticisi;

    public IslemDenetleyici(IslemServisi islemServisi,
                            KategoriServisi kategoriServisi,
                            AnalizYapilabilir analizServisi,
                            Raporlanabilir raporServisi,
                            OturumYoneticisi oturumYoneticisi) {
        this.islemServisi = islemServisi;
        this.kategoriServisi = kategoriServisi;
        this.analizServisi = analizServisi;
        this.raporServisi = raporServisi;
        this.oturumYoneticisi = oturumYoneticisi;
    }

    /** Aktif kullanıcının ID'sini kısa yoldan alır. */
    private String aktifId() {
        return oturumYoneticisi.getAktifKullanici().getId();
    }

    public void gelirEkle(double miktar, LocalDate tarih, String aciklama,
                          Kategori kategori, String kaynak) throws GecersizMiktarHatasi {
        islemServisi.gelirEkle(aktifId(), miktar, tarih, aciklama, kategori, kaynak);
    }

    public void giderEkle(double miktar, LocalDate tarih, String aciklama,
                          Kategori kategori, String odemeYontemi) throws GecersizMiktarHatasi {
        islemServisi.giderEkle(aktifId(), miktar, tarih, aciklama, kategori, odemeYontemi);
    }

    public void islemGuncelle(String islemId, double miktar, LocalDate tarih,
                              String aciklama, Kategori kategori, String ekstra, model.IslemTuru tur) {
        islemServisi.guncelle(islemId, miktar, tarih, aciklama, kategori, ekstra, tur);
    }

    public void islemSil(String islemId) {
        islemServisi.sil(islemId);
    }

    public List<Islem> islemleriGetir() {
        return islemServisi.listele(aktifId());
    }

    public List<Islem> islemleriTarihAraliginaGore(LocalDate bas, LocalDate bit) {
        return islemServisi.tarihAraliginaGore(aktifId(), bas, bit);
    }

    public double toplamGelir(LocalDate bas, LocalDate bit) {
        return analizServisi.toplamGelir(aktifId(), bas, bit);
    }

    public double toplamGider(LocalDate bas, LocalDate bit) {
        return analizServisi.toplamGider(aktifId(), bas, bit);
    }

    public double netBakiye(LocalDate bas, LocalDate bit) {
        return analizServisi.netBakiye(aktifId(), bas, bit);
    }

    public Map<Kategori, Double> kategoriBazliDagilim(LocalDate bas, LocalDate bit) {
        return analizServisi.kategoriBazliDagilim(aktifId(), bas, bit);
    }

    public Map<Kategori, Double> gelirKategoriBazliDagilim(LocalDate bas, LocalDate bit) {
        return analizServisi.gelirKategoriBazliDagilim(aktifId(), bas, bit);
    }

    public Map<Kategori, Double> giderKategoriBazliDagilim(LocalDate bas, LocalDate bit) {
        return analizServisi.giderKategoriBazliDagilim(aktifId(), bas, bit);
    }

    public Rapor raporOlustur(LocalDate bas, LocalDate bit) {
        return raporServisi.raporOlustur(aktifId(), bas, bit);
    }
}
