package repo;

import model.Islem;
import model.IslemTuru;
import model.Kategori;
import java.time.LocalDate;
import java.util.List;

/**
 * İşlem veri erişim katmanının sözleşmesini tanımlayan arayüz.
 * <p>
 * OOP — Soyutlama / Arayüz: UI ve servis katmanı bu arayüzü kullanır;
 * somut uygulama ({@link SQLiteIslemRepo}) bağımlılıktan ayrıştırılmıştır.
 * Farklı bir veritabanına geçmek için yalnızca yeni bir uygulama yazmak yeterlidir.
 */
public interface IslemRepo {

    /** Yeni bir işlemi kalıcı depoya kaydeder. */
    void kaydet(Islem islem);

    /**
     * Mevcut bir işlemin alanlarını günceller.
     *
     * @param ekstra gelirde kaynak, giderde ödeme yöntemi
     */
    void guncelle(String islemId, double miktar, LocalDate tarih,
                  String aciklama, Kategori kategori, String ekstra, IslemTuru tur);

    /** Verilen kimliğe sahip işlemi kalıcı depodan siler. */
    void sil(String islemId);

    /** Belirtilen kullanıcıya ait tüm işlemleri döner. */
    List<Islem> kullaniciyaGore(String kullaniciId);

    /**
     * Belirtilen kullanıcıya ait, tarih aralığındaki işlemleri döner.
     *
     * @param bas aralık başlangıcı (dahil)
     * @param bit aralık sonu (dahil)
     */
    List<Islem> tarihAraliginaGore(String kullaniciId, LocalDate bas, LocalDate bit);
}
