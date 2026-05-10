package service;

import model.Kullanici;

public class OturumYoneticisi {
    private Kullanici aktifKullanici;

    public void oturumAc(Kullanici kullanici) {
        this.aktifKullanici = kullanici;
    }

    public void oturumKapat() {
        this.aktifKullanici = null;
    }

    public Kullanici getAktifKullanici() {
        return aktifKullanici;
    }

    public boolean oturumAcikMi() {
        return aktifKullanici != null;
    }
}
