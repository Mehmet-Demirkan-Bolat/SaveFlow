package model;

public class ButceAsimi {
    private final Kategori kategori;
    private final double limit;
    private final double harcanan;

    public ButceAsimi(Kategori kategori, double limit, double harcanan) {
        this.kategori = kategori;
        this.limit = limit;
        this.harcanan = harcanan;
    }

    public Kategori getKategori() { return kategori; }
    public double getLimit()      { return limit; }
    public double getHarcanan()   { return harcanan; }

    public double asimMiktari()   { return harcanan - limit; }
    public double asimYuzdesi()   { return ((harcanan / limit) - 1.0) * 100.0; }
}
