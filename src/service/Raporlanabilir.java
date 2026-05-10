package service;

import model.Rapor;
import java.time.LocalDate;

/**
 * Rapor oluşturma yeteneğini tanımlayan arayüz.
 * <p>
 * OOP — Arayüz / Soyutlama: denetleyici bu arayüzü kullanır; somut
 * {@link RaporServisi} uygulamasından bağımsızdır.
 */
public interface Raporlanabilir {

    /**
     * Belirtilen kullanıcı ve tarih aralığı için finansal rapor oluşturur.
     *
     * @param kullaniciId raporu oluşturulacak kullanıcı
     * @param bas         raporun başlangıç tarihi (dahil)
     * @param bit         raporun bitiş tarihi (dahil)
     * @return doldurulmuş {@link Rapor} nesnesi
     */
    Rapor raporOlustur(String kullaniciId, LocalDate bas, LocalDate bit);
}
