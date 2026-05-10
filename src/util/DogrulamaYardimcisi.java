package util;

import java.time.LocalDate;

/**
 * Uygulama genelinde kullanilan dogrulama yardimci sinifi.
 * Tek sorumluluk: veri gecerlilik kontrolleri.
 */
public class DogrulamaYardimcisi {

    private DogrulamaYardimcisi() { /* yardimci sinif */ }

    public static boolean emailGecerliMi(String email) {
        if (email == null || email.isBlank()) return false;
        return email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    public static boolean miktarGecerliMi(double miktar) {
        return miktar > 0 && Double.isFinite(miktar);
    }

    public static boolean bosDegilMi(String deger) {
        return deger != null && !deger.isBlank();
    }

    /** Uzunluk sinirini asip asmadigi kontrolu */
    public static boolean uzunlukGecerliMi(String deger, int maxUzunluk) {
        return deger != null && deger.length() <= maxUzunluk;
    }

    /** Tarih araliginin gecerli olup olmadigini kontrol eder */
    public static boolean tarihAraligiGecerliMi(LocalDate bas, LocalDate bit) {
        return bas != null && bit != null && !bas.isAfter(bit);
    }

    /** Sifreyi dogrular: en az 6 karakter */
    public static boolean sifreGecerliMi(String sifre) {
        return sifre != null && sifre.length() >= 6;
    }
}

