package service;

import model.HedefTasarruf;
import repo.HedefTasarrufRepo;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class HedefTasarrufServisi {
    private final HedefTasarrufRepo hedefTasarrufRepo;

    public HedefTasarrufServisi(HedefTasarrufRepo hedefTasarrufRepo) {
        this.hedefTasarrufRepo = hedefTasarrufRepo;
    }

    public void ekle(String kullaniciId, String ad, double hedefMiktar,
                     LocalDate baslangicTarihi, LocalDate bitisTarihi) {
        if (kullaniciId == null || kullaniciId.isBlank())
            throw new IllegalArgumentException("Kullanici ID bos olamaz.");
        if (ad == null || ad.isBlank())
            throw new IllegalArgumentException("Hedef adi bos olamaz.");
        if (hedefMiktar <= 0)
            throw new IllegalArgumentException("Hedef miktar sifirdan buyuk olmalidir.");
        if (baslangicTarihi == null || bitisTarihi == null)
            throw new IllegalArgumentException("Tarihler bos olamaz.");
        if (baslangicTarihi.isAfter(bitisTarihi))
            throw new IllegalArgumentException("Baslangic tarihi bitis tarihinden sonra olamaz.");
        String id = UUID.randomUUID().toString();
        HedefTasarruf hedef = new HedefTasarruf(id, kullaniciId, ad,
            hedefMiktar, 0, baslangicTarihi, bitisTarihi);
        hedefTasarrufRepo.kaydet(hedef);
    }

    public void katki(String hedefId, double miktar) {
        if (hedefId == null || hedefId.isBlank())
            throw new IllegalArgumentException("Hedef ID bos olamaz.");
        if (miktar <= 0)
            throw new IllegalArgumentException("Katki miktari sifirdan buyuk olmalidir.");
        hedefTasarrufRepo.katki(hedefId, miktar);
    }

    public void sil(String id) {
        hedefTasarrufRepo.sil(id);
    }

    public List<HedefTasarruf> listele(String kullaniciId) {
        return hedefTasarrufRepo.kullaniciyaGore(kullaniciId);
    }
}
