package controller;

import model.HedefTasarruf;
import service.HedefTasarrufServisi;
import service.OturumYoneticisi;

import java.time.LocalDate;
import java.util.List;

public class HedefTasarrufDenetleyici {
    private final HedefTasarrufServisi hedefTasarrufServisi;
    private final OturumYoneticisi oturumYoneticisi;

    public HedefTasarrufDenetleyici(HedefTasarrufServisi hedefTasarrufServisi,
                                    OturumYoneticisi oturumYoneticisi) {
        this.hedefTasarrufServisi = hedefTasarrufServisi;
        this.oturumYoneticisi = oturumYoneticisi;
    }

    private String aktifId() {
        return oturumYoneticisi.getAktifKullanici().getId();
    }

    public void ekle(String ad, double hedefMiktar, LocalDate baslangicTarihi, LocalDate bitisTarihi) {
        hedefTasarrufServisi.ekle(aktifId(), ad, hedefMiktar, baslangicTarihi, bitisTarihi);
    }

    public void katki(String hedefId, double miktar) {
        hedefTasarrufServisi.katki(hedefId, miktar);
    }

    public void sil(String id) {
        hedefTasarrufServisi.sil(id);
    }

    public List<HedefTasarruf> listele() {
        return hedefTasarrufServisi.listele(aktifId());
    }
}
