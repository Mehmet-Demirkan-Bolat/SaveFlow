package model;

import java.time.LocalDate;

/**
 * Tüm finansal işlemlerin (gelir/gider) ortak özelliklerini tanımlayan soyut temel sınıf.
 * <p>
 * OOP — Kalıtım: {@link Gelir} ve {@link Gider} bu sınıfı genişletir.<br>
 * OOP — Soyutlama: {@link #getTur()} ve {@link #toString()} alt sınıflara bırakılmıştır.<br>
 * OOP — Kapsülleme: tüm alanlar {@code private}; getter ve setter ile erişilir.
 * {@code id} ve {@code kullaniciId} işlem kaydının bütünlüğü için değiştirilemez.
 */
public abstract class Islem {

    /** Benzersiz işlem kimliği (UUID) — değiştirilemez. */
    private final String id;

    /** Bu işlemin sahibi olan kullanıcının kimliği — değiştirilemez. */
    private final String kullaniciId;

    /** İşlemin gerçekleştiği tarih. */
    private LocalDate tarih;

    /** İşlem tutarı — her zaman pozitiftir. */
    private double miktar;

    /** Kullanıcı tarafından girilen serbest metin açıklaması (null olabilir). */
    private String aciklama;

    /** İşlemin ait olduğu kategori. */
    private Kategori kategori;

    /**
     * Alt sınıfların çağırdığı temel kurucu.
     *
     * @param id          benzersiz işlem kimliği
     * @param kullaniciId işlem sahibinin kimliği
     * @param tarih       işlem tarihi
     * @param miktar      pozitif işlem tutarı
     * @param aciklama    serbest metin açıklama (null geçebilir)
     * @param kategori    ilgili kategori
     */
    protected Islem(String id, String kullaniciId, LocalDate tarih,
                    double miktar, String aciklama, Kategori kategori) {
        this.id          = id;
        this.kullaniciId = kullaniciId;
        this.tarih       = tarih;
        this.miktar      = miktar;
        this.aciklama    = aciklama;
        this.kategori    = kategori;
    }

    // ── Getter'lar ───────────────────────────────────────────────────
    public String getId()          { return id; }
    public String getKullaniciId() { return kullaniciId; }
    public LocalDate getTarih()    { return tarih; }
    public double getMiktar()      { return miktar; }
    public String getAciklama()    { return aciklama; }
    public Kategori getKategori()  { return kategori; }

    // ── Setter'lar ───────────────────────────────────────────────────

    /**
     * İşlem tarihini günceller.
     *
     * @param tarih yeni tarih (null olamaz)
     */
    public void setTarih(LocalDate tarih) {
        if (tarih == null) throw new IllegalArgumentException("Tarih boş olamaz.");
        this.tarih = tarih;
    }

    /**
     * İşlem tutarını günceller.
     *
     * @param miktar yeni tutar (sıfırdan büyük olmalıdır)
     */
    public void setMiktar(double miktar) {
        if (miktar <= 0) throw new IllegalArgumentException("Miktar sıfırdan büyük olmalıdır.");
        this.miktar = miktar;
    }

    /**
     * İşlem açıklamasını günceller.
     *
     * @param aciklama yeni açıklama (null olabilir)
     */
    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    /**
     * İşlemin kategorisini günceller.
     *
     * @param kategori yeni kategori (null olamaz)
     */
    public void setKategori(Kategori kategori) {
        if (kategori == null) throw new IllegalArgumentException("Kategori boş olamaz.");
        this.kategori = kategori;
    }

    /**
     * İşlem türünü döner (GELIR / GIDER).
     * OOP — Polimorfizm: her alt sınıf kendi türünü bildirir.
     *
     * @return {@link IslemTuru}
     */
    public abstract IslemTuru getTur();

    /** Alt sınıf tarafından insanca okunabilir biçimde uygulanır. */
    @Override
    public abstract String toString();
}
