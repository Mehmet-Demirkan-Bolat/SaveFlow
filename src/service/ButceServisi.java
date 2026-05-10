package service;

import model.*;
import repo.ButceLimitiRepo;

import java.util.*;
import java.util.stream.Collectors;

public class ButceServisi {
    private final ButceLimitiRepo butceLimitiRepo;

    public ButceServisi(ButceLimitiRepo butceLimitiRepo) {
        this.butceLimitiRepo = butceLimitiRepo;
    }

    public void limitBelirle(String kullaniciId, String kategoriId, double aylikLimit) {
        if (kullaniciId == null || kullaniciId.isBlank())
            throw new IllegalArgumentException("Kullanici ID bos olamaz.");
        if (kategoriId == null || kategoriId.isBlank())
            throw new IllegalArgumentException("Kategori ID bos olamaz.");
        if (aylikLimit <= 0)
            throw new IllegalArgumentException("Aylik limit sifirdan buyuk olmalidir.");
        String id = UUID.randomUUID().toString();
        ButceLimiti limit = new ButceLimiti(id, kullaniciId, kategoriId, aylikLimit);
        butceLimitiRepo.kaydetYaDaGuncelle(limit);
    }

    public void limitSil(String id) {
        butceLimitiRepo.sil(id);
    }

    public List<ButceLimiti> limitleriGetir(String kullaniciId) {
        return butceLimitiRepo.kullaniciyaGore(kullaniciId);
    }

    public Optional<ButceLimiti> kategoriLimitiGetir(String kullaniciId, String kategoriId) {
        return butceLimitiRepo.kategoriIleGetir(kullaniciId, kategoriId);
    }

    public List<ButceAsimi> asimlariHesapla(String kullaniciId, Map<Kategori, Double> dagilim) {
        List<ButceLimiti> limitler = butceLimitiRepo.kullaniciyaGore(kullaniciId);
        Map<String, Double> limitMap = limitler.stream()
            .collect(Collectors.toMap(ButceLimiti::getKategoriId, ButceLimiti::getAylikLimit));

        return dagilim.entrySet().stream()
            .filter(e -> e.getKey().getTur() == IslemTuru.GIDER)
            .filter(e -> limitMap.containsKey(e.getKey().getId()))
            .filter(e -> e.getValue() > limitMap.get(e.getKey().getId()))
            .map(e -> new ButceAsimi(e.getKey(), limitMap.get(e.getKey().getId()), e.getValue()))
            .collect(Collectors.toList());
    }
}
