package service;

import model.Kategori;
import java.time.LocalDate;
import java.util.Map;

/**
 * Finansal analiz operasyonlarını tanımlayan arayüz.
 * <p>
 * OOP — Arayüz / Polimorfizm: {@link IslemDenetleyici} bu arayüzü bağımlılık
 * olarak alır; test ortamında sahte (mock) bir uygulama kolayca geçirilebilir.
 */
public interface AnalizYapilabilir {

    /** Verilen dönemde kullanıcının toplam gelirini hesaplar. */
    double toplamGelir(String kullaniciId, LocalDate bas, LocalDate bit);

    /** Verilen dönemde kullanıcının toplam giderini hesaplar. */
    double toplamGider(String kullaniciId, LocalDate bas, LocalDate bit);

    /** Gelir − Gider farkını (net bakiye) döner. */
    double netBakiye(String kullaniciId, LocalDate bas, LocalDate bit);

    /** Tüm işlemlerin kategori bazlı tutarlarını harita olarak döner. */
    Map<Kategori, Double> kategoriBazliDagilim(String kullaniciId, LocalDate bas, LocalDate bit);

    /** Yalnızca gelir işlemlerinin kategori dağılımını döner. */
    Map<Kategori, Double> gelirKategoriBazliDagilim(String kullaniciId, LocalDate bas, LocalDate bit);

    /** Yalnızca gider işlemlerinin kategori dağılımını döner. */
    Map<Kategori, Double> giderKategoriBazliDagilim(String kullaniciId, LocalDate bas, LocalDate bit);
}
