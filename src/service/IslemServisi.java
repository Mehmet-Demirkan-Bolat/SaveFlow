package service;

import exception.GecersizMiktarHatasi;
import model.Gelir;
import model.Gider;
import model.Islem;
import model.Kategori;
import repo.IslemRepo;
import util.DogrulamaYardimcisi;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * İşlem iş mantığını yöneten servis sınıfı.
 * <p>
 * OOP — Bağımlılık Enjeksiyonu: {@link IslemRepo} arayüzü constructor ile
 * enjekte edilir; somut SQLite implementasyonundan habersizdir.<br>
 * OOP — Sorumluluk Ayrımı: doğrulama bu katmanda yapılır, UI bunu bilmez.
 */
public class IslemServisi {

    /** İşlem kalıcılığı için kullanılan repo arayüzü. */
    private final IslemRepo islemRepo;

    /**
     * @param islemRepo işlem deposu (SQLite veya başka bir uygulama)
     */
    public IslemServisi(IslemRepo islemRepo) {
        this.islemRepo = islemRepo;
    }

    /**
     * Yeni bir gelir işlemi oluşturup kaydeder.
     *
     * @throws GecersizMiktarHatasi miktar sıfır veya negatifse fırlatılır
     */
    public void gelirEkle(String kullaniciId, double miktar, LocalDate tarih,
                          String aciklama, Kategori kategori, String kaynak) throws GecersizMiktarHatasi {
        if (!DogrulamaYardimcisi.miktarGecerliMi(miktar)) {
            throw new GecersizMiktarHatasi("Miktar sıfırdan büyük olmalıdır.");
        }
        String id = UUID.randomUUID().toString();
        Gelir gelir = new Gelir(id, kullaniciId, tarih, miktar, aciklama, kategori, kaynak);
        islemRepo.kaydet(gelir);
    }

    /**
     * Yeni bir gider işlemi oluşturup kaydeder.
     *
     * @throws GecersizMiktarHatasi miktar sıfır veya negatifse fırlatılır
     */
    public void giderEkle(String kullaniciId, double miktar, LocalDate tarih,
                          String aciklama, Kategori kategori, String odemeYontemi) throws GecersizMiktarHatasi {
        if (!DogrulamaYardimcisi.miktarGecerliMi(miktar)) {
            throw new GecersizMiktarHatasi("Miktar sıfırdan büyük olmalıdır.");
        }
        String id = UUID.randomUUID().toString();
        Gider gider = new Gider(id, kullaniciId, tarih, miktar, aciklama, kategori, odemeYontemi);
        islemRepo.kaydet(gider);
    }

    /**
     * Mevcut bir işlemi günceller; temel doğrulamaları çalıştırır.
     *
     * @param ekstra gelirde kaynak, giderde ödeme yöntemi
     */
    public void guncelle(String islemId, double miktar, LocalDate tarih,
                         String aciklama, Kategori kategori, String ekstra, model.IslemTuru tur) {
        if (islemId == null || islemId.isBlank())
            throw new IllegalArgumentException("Islem ID bos olamaz.");
        if (!DogrulamaYardimcisi.miktarGecerliMi(miktar))
            throw new IllegalArgumentException("Miktar sifirdan buyuk olmalidir.");
        if (tarih == null)
            throw new IllegalArgumentException("Tarih bos olamaz.");
        if (kategori == null)
            throw new IllegalArgumentException("Kategori bos olamaz.");
        islemRepo.guncelle(islemId, miktar, tarih, aciklama, kategori, ekstra, tur);
    }

    /** Verilen kimliğe sahip işlemi siler. */
    public void sil(String islemId) {
        islemRepo.sil(islemId);
    }

    /** Kullanıcıya ait tüm işlemleri listeler. */
    public List<Islem> listele(String kullaniciId) {
        return islemRepo.kullaniciyaGore(kullaniciId);
    }

    /** Kullanıcıya ait, belirtilen tarih aralığındaki işlemleri listeler. */
    public List<Islem> tarihAraliginaGore(String kullaniciId, LocalDate bas, LocalDate bit) {
        return islemRepo.tarihAraliginaGore(kullaniciId, bas, bit);
    }
}
