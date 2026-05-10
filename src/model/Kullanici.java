package model;

/**
 * Sisteme kayıtlı kullanıcıyı temsil eden model sınıfı.
 * <p>
 * OOP — Kapsülleme: tüm alanlar {@code private}; dışarıdan erişim yalnızca
 * getter ve setter metodları aracılığıyla sağlanır.<br>
 * {@code id} ve {@code eposta} kimlik bilgisi olduğundan setter sunulmaz;
 * {@code ad} ve {@code sifre} güncellenebilir.
 */
public class Kullanici {

    /** Benzersiz kullanıcı kimliği (UUID) — değiştirilemez. */
    private final String id;

    /** Kullanıcının görünen adı. */
    private String ad;

    /** Giriş için kullanılan e-posta adresi — değiştirilemez. */
    private final String eposta;

    /** Hashlenmiş şifre. */
    private String sifre;

    public Kullanici(String id, String ad, String eposta, String sifre) {
        this.id     = id;
        this.ad     = ad;
        this.eposta = eposta;
        this.sifre  = sifre;
    }

    // ── Getter'lar ───────────────────────────────────────────────────
    public String getId()     { return id; }
    public String getAd()     { return ad; }
    public String getEposta() { return eposta; }
    public String getSifre()  { return sifre; }

    // ── Setter'lar ───────────────────────────────────────────────────

    /**
     * Kullanıcının görünen adını günceller.
     *
     * @param ad yeni ad (boş olamaz)
     */
    public void setAd(String ad) {
        if (ad == null || ad.isBlank()) throw new IllegalArgumentException("Ad boş olamaz.");
        this.ad = ad;
    }

    /**
     * Kullanıcının şifresini günceller.
     *
     * @param sifre yeni hashlenmiş şifre (boş olamaz)
     */
    public void setSifre(String sifre) {
        if (sifre == null || sifre.isBlank()) throw new IllegalArgumentException("Şifre boş olamaz.");
        this.sifre = sifre;
    }
}
