package repo;

import model.TekrarlananIslem;
import java.time.LocalDate;
import java.util.List;

public interface TekrarlananIslemRepo {
    void kaydet(TekrarlananIslem islem);
    void guncelle(TekrarlananIslem islem);
    void sil(String id);
    List<TekrarlananIslem> kullaniciyaGore(String kullaniciId);
    List<TekrarlananIslem> vadesiGelenler(String kullaniciId, LocalDate bugun);
}
