package service;

import exception.GecersizMiktarHatasi;
import model.*;
import repo.TekrarlananIslemRepo;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TekrarlananIslemServisi {
    private final TekrarlananIslemRepo tekrarlananIslemRepo;
    private final IslemServisi islemServisi;

    public TekrarlananIslemServisi(TekrarlananIslemRepo tekrarlananIslemRepo,
                                   IslemServisi islemServisi) {
        this.tekrarlananIslemRepo = tekrarlananIslemRepo;
        this.islemServisi = islemServisi;
    }

    public void ekle(String kullaniciId, String baslik, double miktar, Kategori kategori,
                     IslemTuru tur, String ekstra, TekrarlananIslem.TekrarTipi tekrarTipi,
                     LocalDate sonrakiTarih) {
        String id = UUID.randomUUID().toString();
        TekrarlananIslem ti = new TekrarlananIslem(id, kullaniciId, baslik, miktar,
            kategori, tur, ekstra, tekrarTipi, sonrakiTarih, true);
        tekrarlananIslemRepo.kaydet(ti);
    }

    public void sil(String id) {
        tekrarlananIslemRepo.sil(id);
    }

    public List<TekrarlananIslem> listele(String kullaniciId) {
        return tekrarlananIslemRepo.kullaniciyaGore(kullaniciId);
    }

    /** Vadesi gelmiş tekrarlanan işlemleri otomatik olarak uygular. Uygulanan sayısını döner. */
    public int vadesiGelenIslemleriUygula(String kullaniciId) {
        List<TekrarlananIslem> vadesGelenler =
            tekrarlananIslemRepo.vadesiGelenler(kullaniciId, LocalDate.now());
        int sayac = 0;
        for (TekrarlananIslem ti : vadesGelenler) {
            // Catch-up: eğer uygulama uzun süre açılmadıysa birden fazla uygula
            while (!ti.getSonrakiTarih().isAfter(LocalDate.now())) {
                try {
                    if (ti.getTur() == IslemTuru.GELIR) {
                        islemServisi.gelirEkle(kullaniciId, ti.getMiktar(),
                            ti.getSonrakiTarih(), ti.getBaslik(), ti.getKategori(), ti.getEkstra());
                    } else {
                        islemServisi.giderEkle(kullaniciId, ti.getMiktar(),
                            ti.getSonrakiTarih(), ti.getBaslik(), ti.getKategori(), ti.getEkstra());
                    }
                    sayac++;
                } catch (GecersizMiktarHatasi e) {
                    System.err.println("Tekrarlanan işlem uygulanamadı: " + e.getMessage());
                }
                ti.ilerlet();
            }
            tekrarlananIslemRepo.guncelle(ti);
        }
        return sayac;
    }
}
