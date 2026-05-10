package service;

import model.Kategori;
import model.Rapor;

import java.time.LocalDate;
import java.util.Map;

public class RaporServisi implements Raporlanabilir {
    private final AnalizYapilabilir analizServisi;

    public RaporServisi(AnalizYapilabilir analizServisi) {
        this.analizServisi = analizServisi;
    }

    @Override
    public Rapor raporOlustur(String kullaniciId, LocalDate bas, LocalDate bit) {
        Rapor rapor = new Rapor(bas, bit);
        double gelir = analizServisi.toplamGelir(kullaniciId, bas, bit);
        double gider = analizServisi.toplamGider(kullaniciId, bas, bit);
        double net   = analizServisi.netBakiye(kullaniciId, bas, bit);

        rapor.ekleKalem("Toplam Gelir", gelir);
        rapor.ekleKalem("Toplam Gider", gider);
        rapor.ekleKalem("Net Bakiye",   net);

        // Kategori dağılımını da raporun içine ekle
        Map<Kategori, Double> dagilim = analizServisi.kategoriBazliDagilim(kullaniciId, bas, bit);
        for (Map.Entry<Kategori, Double> entry : dagilim.entrySet()) {
            rapor.ekleKalem(entry.getKey().getAd(), entry.getValue());
        }
        return rapor;
    }
}
