package model;

public class ButceLimiti {
    private final String id;
    private final String kullaniciId;
    private final String kategoriId;
    private final double aylikLimit;

    public ButceLimiti(String id, String kullaniciId, String kategoriId, double aylikLimit) {
        this.id = id;
        this.kullaniciId = kullaniciId;
        this.kategoriId = kategoriId;
        this.aylikLimit = aylikLimit;
    }

    public String getId()          { return id; }
    public String getKullaniciId() { return kullaniciId; }
    public String getKategoriId()  { return kategoriId; }
    public double getAylikLimit()  { return aylikLimit; }
}
