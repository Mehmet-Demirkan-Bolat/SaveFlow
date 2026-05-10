package model;

import java.time.LocalDate;

/**
 * Gider türündeki finansal işlemi temsil eder.
 * <p>
 * OOP — Kalıtım: {@link Islem} soyut sınıfını genişletir ve
 * "GIDER" türüne özgü {@code odemeYontemi} alanını ekler.<br>
 * OOP — Polimorfizm: {@link #getTur()} {@code GIDER} sabitini döner.
 */
public class Gider extends Islem {

    /** Ödeme yöntemi (nakit, kredi kartı vb.). */
    private final String odemeYontemi;

    /**
     * Yeni bir Gider nesnesi oluşturur.
     *
     * @param odemeYontemi ödeme yöntemi (null geçebilir)
     */
    public Gider(String id, String kullaniciId, LocalDate tarih,
                 double miktar, String aciklama, Kategori kategori, String odemeYontemi) {
        super(id, kullaniciId, tarih, miktar, aciklama, kategori);
        this.odemeYontemi = odemeYontemi;
    }

    public String getOdemeYontemi() { return odemeYontemi; }

    /** {@inheritDoc} — Gider için her zaman {@code IslemTuru.GIDER} döner. */
    @Override
    public IslemTuru getTur() { return IslemTuru.GIDER; }

    @Override
    public String toString() {
        return "[GİDER] " + getTarih() + " | " + getMiktar() +
               " TL | " + getKategori().getAd() + " | " + odemeYontemi;
    }
}
