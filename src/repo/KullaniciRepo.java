package repo;

import exception.KayitHatasi;
import model.Kullanici;
import java.util.Optional;

public interface KullaniciRepo {
    void kaydet(Kullanici kullanici) throws KayitHatasi;
    Optional<Kullanici> emailIleGetir(String email);
    boolean emailMevcutMu(String email);
    void sil(String kullaniciId);
}
