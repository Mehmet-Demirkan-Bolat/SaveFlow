package util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Para birimi formatlama yardımcı sınıfı.
 * TL/kuruş ayrımı yapar, her 3 basamakta bir nokta koyar.
 * Örnek: 1234567.89 → "1.234.567,89 ₺"
 */
public final class ParaFormatlayici {

    private static final DecimalFormatSymbols SEMBOLLER;
    private static final DecimalFormat TAM_FORMAT;    // 1.234.567,89 ₺
    private static final DecimalFormat KISA_FORMAT;   // 1.234.567 ₺  (kuruşsuz)

    static {
        SEMBOLLER = new DecimalFormatSymbols(Locale.of("tr", "TR"));
        SEMBOLLER.setGroupingSeparator('.');
        SEMBOLLER.setDecimalSeparator(',');

        TAM_FORMAT  = new DecimalFormat("#,##0.00", SEMBOLLER);
        KISA_FORMAT = new DecimalFormat("#,##0",    SEMBOLLER);
    }

    private ParaFormatlayici() { /* yardımcı sınıf */ }

    /** "1.234.567,89 ₺" biçiminde döner */
    public static String formatla(double miktar) {
        return TAM_FORMAT.format(miktar) + " ₺";
    }

    /** Artı işaretli gelir için: "+1.234.567,89 ₺" */
    public static String formatlaGelir(double miktar) {
        return "+" + TAM_FORMAT.format(miktar) + " ₺";
    }

    /** Eksi işaretli gider için: "−1.234.567,89 ₺" */
    public static String formatlaGider(double miktar) {
        return "−" + TAM_FORMAT.format(miktar) + " ₺";
    }

    /** Kuruş göstermeden: "1.234.567 ₺" */
    public static String formatlaKisa(double miktar) {
        return KISA_FORMAT.format(miktar) + " ₺";
    }

    /** Sadece sayı kısmı, TL sembolü yok: "1.234.567,89" */
    public static String formatlaSayi(double miktar) {
        return TAM_FORMAT.format(miktar);
    }

    /**
     * Kullanıcının girdiği metni double'a çevirir.
     * Hem "1.234,56" hem "1234.56" hem "1234,56" formatlarını kabul eder.
     */
    public static double parse(String metin) throws NumberFormatException {
        if (metin == null || metin.isBlank()) throw new NumberFormatException("Boş değer");
        // Binlik nokta ayracı → kaldır, ondalık virgül → nokta
        String temiz = metin.trim()
            .replace("₺", "")
            .replace(" ", "")
            .trim();
        // Türkçe format: 1.234,56
        if (temiz.contains(",")) {
            temiz = temiz.replace(".", "").replace(",", ".");
        }
        // İngilizce format: 1,234.56
        else if (temiz.indexOf('.') != temiz.lastIndexOf('.')) {
            temiz = temiz.replace(".", "");
        }
        return Double.parseDouble(temiz);
    }
}
