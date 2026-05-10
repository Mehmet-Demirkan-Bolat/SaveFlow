package service;

import model.Islem;
import model.IslemTuru;
import model.Kategori;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnalizServisi implements AnalizYapilabilir {
    private final IslemServisi islemServisi;

    public AnalizServisi(IslemServisi islemServisi) {
        this.islemServisi = islemServisi;
    }

    @Override
    public double toplamGelir(String kullaniciId, LocalDate bas, LocalDate bit) {
        return islemServisi.tarihAraliginaGore(kullaniciId, bas, bit).stream()
            .filter(i -> i.getTur() == IslemTuru.GELIR)
            .mapToDouble(Islem::getMiktar)
            .sum();
    }

    @Override
    public double toplamGider(String kullaniciId, LocalDate bas, LocalDate bit) {
        return islemServisi.tarihAraliginaGore(kullaniciId, bas, bit).stream()
            .filter(i -> i.getTur() == IslemTuru.GIDER)
            .mapToDouble(Islem::getMiktar)
            .sum();
    }

    @Override
    public double netBakiye(String kullaniciId, LocalDate bas, LocalDate bit) {
        return toplamGelir(kullaniciId, bas, bit) - toplamGider(kullaniciId, bas, bit);
    }

    @Override
    public Map<Kategori, Double> kategoriBazliDagilim(String kullaniciId, LocalDate bas, LocalDate bit) {
        return dagilimHesapla(kullaniciId, bas, bit, null);
    }

    @Override
    public Map<Kategori, Double> gelirKategoriBazliDagilim(String kullaniciId, LocalDate bas, LocalDate bit) {
        return dagilimHesapla(kullaniciId, bas, bit, IslemTuru.GELIR);
    }

    @Override
    public Map<Kategori, Double> giderKategoriBazliDagilim(String kullaniciId, LocalDate bas, LocalDate bit) {
        return dagilimHesapla(kullaniciId, bas, bit, IslemTuru.GIDER);
    }

    private Map<Kategori, Double> dagilimHesapla(String kullaniciId, LocalDate bas, LocalDate bit,
                                                   IslemTuru filtre) {
        List<Islem> islemler = islemServisi.tarihAraliginaGore(kullaniciId, bas, bit);
        Map<Kategori, Double> dagilim = new LinkedHashMap<>();
        for (Islem islem : islemler) {
            if (filtre != null && islem.getTur() != filtre) continue;
            dagilim.merge(islem.getKategori(), islem.getMiktar(), Double::sum);
        }
        return dagilim;
    }
}
