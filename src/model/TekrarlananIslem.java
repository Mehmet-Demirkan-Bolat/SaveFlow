package model;

import java.time.LocalDate;

public class TekrarlananIslem {

    public enum TekrarTipi { GUNLUK, HAFTALIK, AYLIK }

    private final String id;
    private final String kullaniciId;
    private final String baslik;
    private final double miktar;
    private final Kategori kategori;
    private final IslemTuru tur;
    private final String ekstra;
    private final TekrarTipi tekrarTipi;
    private LocalDate sonrakiTarih;
    private final boolean aktif;

    public TekrarlananIslem(String id, String kullaniciId, String baslik, double miktar,
                            Kategori kategori, IslemTuru tur, String ekstra,
                            TekrarTipi tekrarTipi, LocalDate sonrakiTarih, boolean aktif) {
        this.id = id;
        this.kullaniciId = kullaniciId;
        this.baslik = baslik;
        this.miktar = miktar;
        this.kategori = kategori;
        this.tur = tur;
        this.ekstra = ekstra;
        this.tekrarTipi = tekrarTipi;
        this.sonrakiTarih = sonrakiTarih;
        this.aktif = aktif;
    }

    public String getId()              { return id; }
    public String getKullaniciId()     { return kullaniciId; }
    public String getBaslik()          { return baslik; }
    public double getMiktar()          { return miktar; }
    public Kategori getKategori()      { return kategori; }
    public IslemTuru getTur()          { return tur; }
    public String getEkstra()          { return ekstra != null ? ekstra : ""; }
    public TekrarTipi getTekrarTipi()  { return tekrarTipi; }
    public LocalDate getSonrakiTarih() { return sonrakiTarih; }
    public boolean isAktif()           { return aktif; }

    public void ilerlet() {
        sonrakiTarih = switch (tekrarTipi) {
            case GUNLUK  -> sonrakiTarih.plusDays(1);
            case HAFTALIK -> sonrakiTarih.plusWeeks(1);
            case AYLIK   -> sonrakiTarih.plusMonths(1);
        };
    }
}
