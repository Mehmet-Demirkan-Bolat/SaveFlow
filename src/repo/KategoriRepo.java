package repo;

import model.Kategori;
import java.util.List;

public interface KategoriRepo {
    void kaydet(Kategori kategori);
    void sil(String kategoriId);
    List<Kategori> kullaniciyaGore(String kullaniciId);
}
