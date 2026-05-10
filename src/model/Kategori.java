package model;

import java.util.Objects;

/**
 * Gelir veya gider işlemlerini sınıflandırmak için kullanılan kategori modeli.
 * <p>
 * OOP — Kapsülleme: tüm alanlar {@code private}; dışarıdan erişim yalnızca
 * getter ve setter metodları aracılığıyla sağlanır.<br>
 * {@code id}, {@code kullaniciId} ve {@code tur} değiştirilemez;
 * {@code ad} ve {@code renk} sonradan güncellenebilir.
 */
public class Kategori {

    /** Benzersiz kategori kimliği (UUID) — değiştirilemez. */
    private final String id;

    /** Bu kategorinin sahibi olan kullanıcının kimliği — değiştirilemez. */
    private final String kullaniciId;

    /** Kategori görünen adı (örn. "Market", "Kira"). */
    private String ad;

    /** Kategorinin türü: GELIR veya GIDER — değiştirilemez. */
    private final IslemTuru tur;

    /** Arayüzde kullanılan hex renk kodu (örn. "#3B82F6"). */
    private String renk;

    public Kategori(String id, String kullaniciId, String ad, IslemTuru tur, String renk) {
        this.id          = id;
        this.kullaniciId = kullaniciId;
        this.ad          = ad;
        this.tur         = tur;
        this.renk        = renk;
    }

    /** Varsayılan renk (#3B82F6 — mavi) ile kategori oluşturur. */
    public Kategori(String id, String kullaniciId, String ad, IslemTuru tur) {
        this(id, kullaniciId, ad, tur, "#3B82F6");
    }

    // ── Getter'lar ───────────────────────────────────────────────────
    public String getId()          { return id; }
    public String getKullaniciId() { return kullaniciId; }
    public String getAd()          { return ad; }
    public IslemTuru getTur()      { return tur; }
    public String getRenk()        { return renk != null && !renk.isBlank() ? renk : "#3B82F6"; }

    // ── Setter'lar ───────────────────────────────────────────────────

    /**
     * Kategorinin görünen adını günceller.
     *
     * @param ad yeni kategori adı (boş olamaz)
     */
    public void setAd(String ad) {
        if (ad == null || ad.isBlank()) throw new IllegalArgumentException("Kategori adı boş olamaz.");
        this.ad = ad;
    }

    /**
     * Kategorinin renk kodunu günceller.
     *
     * @param renk yeni hex renk kodu (örn. "#FF5733")
     */
    public void setRenk(String renk) {
        if (renk == null || renk.isBlank()) throw new IllegalArgumentException("Renk boş olamaz.");
        this.renk = renk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Kategori)) return false;
        return Objects.equals(id, ((Kategori) o).id);
    }

    @Override
    public int hashCode() { return Objects.hashCode(id); }

    @Override
    public String toString() { return ad; }
}
