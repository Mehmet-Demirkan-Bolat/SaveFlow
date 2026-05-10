package controller;

import model.ButceAsimi;
import model.ButceLimiti;
import model.Kategori;
import service.ButceServisi;
import service.OturumYoneticisi;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ButceDenetleyici {
    private final ButceServisi butceServisi;
    private final OturumYoneticisi oturumYoneticisi;

    public ButceDenetleyici(ButceServisi butceServisi, OturumYoneticisi oturumYoneticisi) {
        this.butceServisi = butceServisi;
        this.oturumYoneticisi = oturumYoneticisi;
    }

    private String aktifId() {
        return oturumYoneticisi.getAktifKullanici().getId();
    }

    public void limitBelirle(String kategoriId, double aylikLimit) {
        butceServisi.limitBelirle(aktifId(), kategoriId, aylikLimit);
    }

    public void limitSil(String id) {
        butceServisi.limitSil(id);
    }

    public List<ButceLimiti> limitleriGetir() {
        return butceServisi.limitleriGetir(aktifId());
    }

    public Optional<ButceLimiti> kategoriLimitiGetir(String kategoriId) {
        return butceServisi.kategoriLimitiGetir(aktifId(), kategoriId);
    }

    public List<ButceAsimi> asimlariHesapla(Map<Kategori, Double> dagilim) {
        return butceServisi.asimlariHesapla(aktifId(), dagilim);
    }
}
