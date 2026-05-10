package model;

import java.time.LocalDate;

/**
 * Gelir türündeki finansal işlemi temsil eder.
 * <p>
 * OOP — Kalıtım: {@link Islem} soyut sınıfını genişletir ve
 * "GELIR" türüne özgü {@code kaynak} alanını ekler.<br>
 * OOP — Polimorfizm: {@link #getTur()} {@code GELIR} sabitini döner,
 * böylece çalışma zamanında tür kontrolü yapılabilir.
 */
public class Gelir extends Islem {

    /** Gelirin kaynağı (maaş, kira vb.). */
    private final String kaynak;

    /**
     * Yeni bir Gelir nesnesi oluşturur.
     *
     * @param kaynak gelirin kaynağı (null geçebilir)
     */
    public Gelir(String id, String kullaniciId, LocalDate tarih,
                 double miktar, String aciklama, Kategori kategori, String kaynak) {
        super(id, kullaniciId, tarih, miktar, aciklama, kategori);
        this.kaynak = kaynak;
    }

    public String getKaynak() { return kaynak; }

    /** {@inheritDoc} — Gelir için her zaman {@code IslemTuru.GELIR} döner. */
    @Override
    public IslemTuru getTur() { return IslemTuru.GELIR; }

    @Override
    public String toString() {
        return "[GELİR] " + getTarih() + " | " + getMiktar() +
               " TL | " + getKategori().getAd() + " | " + kaynak;
    }
}
