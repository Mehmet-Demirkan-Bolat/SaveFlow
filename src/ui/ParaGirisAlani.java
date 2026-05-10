package ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * TL | , | Kurus seklinde iki parcali para girisi alani.
 *
 * OOP: Yeniden kullanilabilir UI bileseni.
 * - TL kismi: sadece rakam kabul eder, focus kaybedince binlik nokta ekler
 * - Kurus kismi: sadece 2 hane rakam kabul eder (00-99)
 * - getMiktar(): iki alani birlestirir, gecersizde IllegalArgumentException firlatir
 * - setMiktar(double): mevcut degeri alanlara parcalar
 */
public class ParaGirisAlani extends JPanel {

    private static final long serialVersionUID = 1L;

    // ── Stiller ──────────────────────────────────────────────────────
    private static final Color BG_ALAN    = new Color(24, 32, 56);
    private static final Color BORDER_N   = new Color(40, 52, 80);
    private static final Color BORDER_F   = new Color(59, 130, 246);
    private static final Color TEXT       = new Color(241, 245, 249);
    private static final Color TEXT_DIM   = new Color(148, 163, 184);
    private static final Color ALTIN      = new Color(251, 191, 36);

    // ── Binlik ayiraci format ─────────────────────────────────────────
    private static final DecimalFormat TL_FORMAT;
    static {
        DecimalFormatSymbols sym = new DecimalFormatSymbols(Locale.of("tr", "TR"));
        sym.setGroupingSeparator('.');
        TL_FORMAT = new DecimalFormat("#,##0", sym);
    }

    private final JTextField tlAlan;
    private final JTextField kurusAlan;

    /**
     * Bos para giris alani olusturur.
     */
    public ParaGirisAlani() {
        this(0.0);
    }

    /**
     * Baslangiç degeriyle para giris alani olusturur.
     *
     * @param baslangicMiktar Onceki deger (0 veya negatif ise alan bos kalir)
     */
    @SuppressWarnings("this-escape")
    public ParaGirisAlani(double baslangicMiktar) {
        // Alan nesneleri önce yaratılır, ardından layout ve listener'lar eklenir.

        tlAlan     = alanOlustur(10);  // TL kismi (genis)
        kurusAlan = alanOlustur(3);   // Kurus kismi (dar)

        // Layout ayarları — alanlar hazır olduktan sonra
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        // Baslangiç degerini yerles
        if (baslangicMiktar > 0) {
            setMiktar(baslangicMiktar);
        }

        // TL alanina sadece rakam kabul et
        tlAlan.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)
                        && c != java.awt.event.KeyEvent.VK_BACK_SPACE
                        && c != java.awt.event.KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });

        // TL alani focus kaybedince binlik nokta formatla
        tlAlan.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { formatTlAlani(); }
        });

        // Kurus alanina en fazla 2 hane rakam
        kurusAlan.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)
                        && c != java.awt.event.KeyEvent.VK_BACK_SPACE
                        && c != java.awt.event.KeyEvent.VK_DELETE) {
                    e.consume();
                    return;
                }
                if (Character.isDigit(c) && kurusAlan.getText().length() >= 2) {
                    e.consume();
                }
            }
        });

        // Virgul ayiraci
        JLabel virgulLbl = new JLabel(",");
        virgulLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        virgulLbl.setForeground(TEXT_DIM);
        virgulLbl.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

        // TL sembol etiketi
        JLabel tlLbl = new JLabel("₺");
        tlLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        tlLbl.setForeground(ALTIN);
        tlLbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        add(tlAlan);
        add(virgulLbl);
        add(kurusAlan);
        add(tlLbl);
    }

    // ─────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────

    /**
     * Iki alani birlestirir ve double deger dondurur.
     * Gecersiz veya sifir/negatif degerde IllegalArgumentException firlatir.
     */
    public double getMiktar() {
        String tlMetin     = tlAlan.getText().replaceAll("\\.", "").trim();
        String kurusMetin = kurusAlan.getText().trim();

        if (tlMetin.isEmpty() && kurusMetin.isEmpty()) {
            throw new IllegalArgumentException("Miktar bos birakilamaz.");
        }

        long tlKisim = 0;
        if (!tlMetin.isEmpty()) {
            try { tlKisim = Long.parseLong(tlMetin); }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("TL kismi gecersiz rakam iceriyor.");
            }
        }

        int kurusKisim = 0;
        if (!kurusMetin.isEmpty()) {
            try {
                kurusKisim = Integer.parseInt(kurusMetin);
                if (kurusKisim < 0 || kurusKisim > 99) {
                    throw new IllegalArgumentException("Kurus degeri 00-99 arasinda olmalidir.");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Kurus kismi gecersiz rakam iceriyor.");
            }
        }

        if (tlKisim < 0) {
            throw new IllegalArgumentException("Miktar negatif olamaz.");
        }
        double sonuc = tlKisim + (kurusKisim / 100.0);
        if (sonuc <= 0) {
            throw new IllegalArgumentException("Miktar sifirdan buyuk olmalidir.");
        }
        return sonuc;
    }

    /**
     * Verilen double degeri TL ve Kurus alanlarina parcalar ve yerlestirir.
     */
    public void setMiktar(double miktar) {
        if (miktar <= 0) {
            tlAlan.setText("");
            kurusAlan.setText("");
            return;
        }
        long tlKisim     = (long) miktar;
        int  kurusKisim = (int) Math.round((miktar - tlKisim) * 100);
        tlAlan.setText(TL_FORMAT.format(tlKisim));
        kurusAlan.setText(String.format("%02d", kurusKisim));
    }

    // ─────────────────────────────────────────────────────────────────
    // Private yardimci metotlar
    // ─────────────────────────────────────────────────────────────────

    private void formatTlAlani() {
        String metin = tlAlan.getText().replaceAll("\\.", "").trim();
        if (metin.isEmpty()) return;
        try {
            long deger = Long.parseLong(metin);
            tlAlan.setText(TL_FORMAT.format(deger));
        } catch (NumberFormatException ignored) { }
    }

    private JTextField alanOlustur(int kolonSayisi) {
        JTextField tf = new JTextField(kolonSayisi);
        tf.setBackground(BG_ALAN);
        tf.setForeground(TEXT);
        tf.setCaretColor(TEXT);
        tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tf.setHorizontalAlignment(JTextField.RIGHT);

        Border normal = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_N),
            BorderFactory.createEmptyBorder(8, 10, 8, 10));
        Border focus = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_F, 2),
            BorderFactory.createEmptyBorder(7, 9, 7, 9));

        tf.setBorder(normal);
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { tf.setBorder(focus); }
            @Override public void focusLost(FocusEvent e)   { tf.setBorder(normal); }
        });
        return tf;
    }
}
