package model;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class Rapor {
    private final LocalDate baslangic;
    private final LocalDate bitis;
    private final Map<String, Double> ozetKalemler = new LinkedHashMap<>();

    public Rapor(LocalDate baslangic, LocalDate bitis) {
        this.baslangic = baslangic;
        this.bitis = bitis;
    }

    public LocalDate getBaslangic()              { return baslangic; }
    public LocalDate getBitis()                  { return bitis; }
    public Map<String, Double> getOzetKalemler() { return ozetKalemler; }

    public void ekleKalem(String ad, double deger) {
        ozetKalemler.put(ad, deger);
    }

    public String olusturMetin() {
        StringBuilder sb = new StringBuilder();
        sb.append("RAPOR (").append(baslangic).append(" - ").append(bitis).append(")\n");
        sb.append("----------------------------------\n");
        for (var e : ozetKalemler.entrySet()) {
            sb.append(String.format("%-20s : %.2f TL%n", e.getKey(), e.getValue()));
        }
        return sb.toString();
    }
}
