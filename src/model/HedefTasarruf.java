package model;

import java.time.LocalDate;

public class HedefTasarruf {
    private final String id;
    private final String kullaniciId;
    private final String ad;
    private final double hedefMiktar;
    private final double birikilenMiktar;
    private final LocalDate baslangicTarihi;
    private final LocalDate bitisTarihi;

    public HedefTasarruf(String id, String kullaniciId, String ad, double hedefMiktar,
                         double birikilenMiktar, LocalDate baslangicTarihi, LocalDate bitisTarihi) {
        this.id = id;
        this.kullaniciId = kullaniciId;
        this.ad = ad;
        this.hedefMiktar = hedefMiktar;
        this.birikilenMiktar = birikilenMiktar;
        this.baslangicTarihi = baslangicTarihi;
        this.bitisTarihi = bitisTarihi;
    }

    public String getId()                 { return id; }
    public String getKullaniciId()        { return kullaniciId; }
    public String getAd()                 { return ad; }
    public double getHedefMiktar()        { return hedefMiktar; }
    public double getBirikilenMiktar()    { return birikilenMiktar; }
    public LocalDate getBaslangicTarihi() { return baslangicTarihi; }
    public LocalDate getBitisTarihi()     { return bitisTarihi; }

    public double yuzde() {
        if (hedefMiktar <= 0) return 0;
        return Math.min(100.0, (birikilenMiktar / hedefMiktar) * 100.0);
    }

    public boolean tamamlandi() { return birikilenMiktar >= hedefMiktar; }
}
