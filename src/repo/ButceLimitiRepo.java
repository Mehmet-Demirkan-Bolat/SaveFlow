package repo;

import model.ButceLimiti;
import java.util.List;
import java.util.Optional;

public interface ButceLimitiRepo {
    void kaydetYaDaGuncelle(ButceLimiti limit);
    void sil(String id);
    Optional<ButceLimiti> kategoriIleGetir(String kullaniciId, String kategoriId);
    List<ButceLimiti> kullaniciyaGore(String kullaniciId);
}
