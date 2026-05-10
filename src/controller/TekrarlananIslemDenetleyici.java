package controller;

import model.*;
import service.OturumYoneticisi;
import service.TekrarlananIslemServisi;

import java.time.LocalDate;
import java.util.List;

public class TekrarlananIslemDenetleyici {
    private final TekrarlananIslemServisi tekrarlananIslemServisi;
    private final OturumYoneticisi oturumYoneticisi;

    public TekrarlananIslemDenetleyici(TekrarlananIslemServisi tekrarlananIslemServisi,
                                       OturumYoneticisi oturumYoneticisi) {
        this.tekrarlananIslemServisi = tekrarlananIslemServisi;
        this.oturumYoneticisi = oturumYoneticisi;
    }

    private String aktifId() {
        return oturumYoneticisi.getAktifKullanici().getId();
    }

    public void ekle(String baslik, double miktar, Kategori kategori, IslemTuru tur,
                     String ekstra, TekrarlananIslem.TekrarTipi tekrarTipi, LocalDate sonrakiTarih) {
        tekrarlananIslemServisi.ekle(aktifId(), baslik, miktar, kategori,
            tur, ekstra, tekrarTipi, sonrakiTarih);
    }

    public void sil(String id) {
        tekrarlananIslemServisi.sil(id);
    }

    public List<TekrarlananIslem> listele() {
        return tekrarlananIslemServisi.listele(aktifId());
    }

    public int vadesiGelenIslemleriUygula() {
        return tekrarlananIslemServisi.vadesiGelenIslemleriUygula(aktifId());
    }
}
