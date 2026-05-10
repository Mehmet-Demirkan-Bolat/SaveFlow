package repo;

import model.HedefTasarruf;
import java.util.List;

public interface HedefTasarrufRepo {
    void kaydet(HedefTasarruf hedef);
    void guncelle(HedefTasarruf hedef);
    void katki(String hedefId, double miktar);
    void sil(String id);
    List<HedefTasarruf> kullaniciyaGore(String kullaniciId);
}
