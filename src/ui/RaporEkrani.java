package ui;

import controller.IslemDenetleyici;
import model.Kategori;
import model.Rapor;
import util.ParaFormatlayici;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class RaporEkrani {
    private static final Color BG      = new Color(8, 11, 22);
    private static final Color CARD    = new Color(15, 20, 40);
    private static final Color CARD2   = new Color(20, 28, 52);
    private static final Color BORDER  = new Color(40, 52, 80);
    private static final Color BLUE    = new Color(59, 130, 246);
    private static final Color GREEN   = new Color(34, 197, 94);
    private static final Color RED     = new Color(239, 68, 68);
    private static final Color GOLD    = new Color(251, 191, 36);
    private static final Color PURPLE  = new Color(168, 85, 247);
    private static final Color TEXT    = new Color(241, 245, 249);
    private static final Color TEXT_DIM = new Color(100, 116, 139);
    private static final Color TEXT_MID = new Color(148, 163, 184);

    private static final Font F_H1    = new Font("SansSerif", Font.BOLD, 18);
    private static final Font F_BODY  = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font F_SMALL = new Font("SansSerif", Font.BOLD, 11);
    private static final Font F_TINY  = new Font("SansSerif", Font.PLAIN, 10);

    private static final DateTimeFormatter TARIH_FORMATI = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter GOSTER_FMT    = DateTimeFormatter.ofPattern("d MMM yyyy",
            java.util.Locale.of("tr", "TR"));

    private final JDialog dialog;
    private final IslemDenetleyici islemDenetleyici;

    private JPanel sonucPanel;
    private JPanel icerikSargi;

    public RaporEkrani(JFrame parent, IslemDenetleyici islemDenetleyici) {
        this.islemDenetleyici = islemDenetleyici;
        dialog = new JDialog(parent, "Rapor & Analiz", true);
        dialog.setSize(620, 620);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(true);
        dialog.setContentPane(kok());
    }

    private JPanel kok() {
        JPanel root = darkPanel(new BorderLayout(0, 0));
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel baslikPanel = new JPanel(new BorderLayout(0, 4));
        baslikPanel.setOpaque(false);
        baslikPanel.add(lbl("Rapor & Analiz", F_H1, TEXT), BorderLayout.NORTH);
        baslikPanel.add(lbl("Tarih araligi secip finansal ozeti goruntuleyebilirsiniz.", F_TINY, TEXT_DIM), BorderLayout.SOUTH);
        baslikPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JPanel tarihSatir = roundCardPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JTextField basAlan = darkField(LocalDate.now().withDayOfMonth(1).format(TARIH_FORMATI), 110);
        JTextField bitAlan = darkField(LocalDate.now().format(TARIH_FORMATI), 110);
        JLabel hataLbl = lbl("", F_TINY, RED);
        JButton raporBtn = roundBtn("Rapor Olustur", BLUE, Color.WHITE, 140, 34);

        tarihSatir.add(lbl("Baslangic:", F_SMALL, TEXT_MID)); tarihSatir.add(basAlan);
        tarihSatir.add(Box.createHorizontalStrut(4));
        tarihSatir.add(lbl("Bitis:", F_SMALL, TEXT_MID)); tarihSatir.add(bitAlan);
        tarihSatir.add(Box.createHorizontalStrut(8));
        tarihSatir.add(raporBtn);

        JPanel ustBlok = new JPanel(new BorderLayout(0, 6));
        ustBlok.setOpaque(false);
        ustBlok.add(baslikPanel,  BorderLayout.NORTH);
        ustBlok.add(tarihSatir,   BorderLayout.CENTER);
        ustBlok.add(hataLbl,      BorderLayout.SOUTH);

        sonucPanel = darkPanel(new BorderLayout());
        sonucPanel.add(beklemeEtiketi(), BorderLayout.CENTER);

        icerikSargi = darkPanel(new BorderLayout(0, 16));
        icerikSargi.add(ustBlok,    BorderLayout.NORTH);
        icerikSargi.add(sonucPanel, BorderLayout.CENTER);

        root.add(icerikSargi);

        raporBtn.addActionListener(e -> {
            hataLbl.setText("");
            String basMetin = basAlan.getText().trim();
            String bitMetin = bitAlan.getText().trim();
            if (basMetin.isEmpty() || bitMetin.isEmpty()) {
                hataLbl.setText("Baslangic ve bitis tarihlerini giriniz.");
                return;
            }
            try {
                LocalDate bas = LocalDate.parse(basMetin, TARIH_FORMATI);
                LocalDate bit = LocalDate.parse(bitMetin, TARIH_FORMATI);
                if (bas.isAfter(bit)) {
                    hataLbl.setText("Baslangic tarihi bitis tarihinden sonra olamaz.");
                    return;
                }
                Rapor rapor = islemDenetleyici.raporOlustur(bas, bit);
                Map<Kategori, Double> gelirDagilim = islemDenetleyici.gelirKategoriBazliDagilim(bas, bit);
                Map<Kategori, Double> giderDagilim = islemDenetleyici.giderKategoriBazliDagilim(bas, bit);
                icerikSargi.remove(sonucPanel);
                sonucPanel = raporSonucPanel(rapor, gelirDagilim, giderDagilim);
                icerikSargi.add(sonucPanel, BorderLayout.CENTER);
                icerikSargi.revalidate();
                icerikSargi.repaint();
            } catch (DateTimeParseException ex) {
                hataLbl.setText("Tarih formati: gg.aa.yyyy");
            }
        });

        return root;
    }

    private JPanel raporSonucPanel(Rapor rapor,
                                   Map<Kategori, Double> gelirDagilim,
                                   Map<Kategori, Double> giderDagilim) {
        Map<String, Double> ozet = rapor.getOzetKalemler();
        double gelir = ozet.getOrDefault("Toplam Gelir", 0.0);
        double gider = ozet.getOrDefault("Toplam Gider", 0.0);
        double net   = ozet.getOrDefault("Net Bakiye",   0.0);
        int islemSayisi = islemDenetleyici
            .islemleriTarihAraliginaGore(rapor.getBaslangic(), rapor.getBitis()).size();

        JPanel panel = darkPanel(new BorderLayout(0, 14));

        String donem = rapor.getBaslangic().format(GOSTER_FMT) + " - " + rapor.getBitis().format(GOSTER_FMT);
        JLabel donemLbl = lbl(donem, F_SMALL, TEXT_DIM);
        donemLbl.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        panel.add(donemLbl, BorderLayout.NORTH);

        JPanel kartlar = new JPanel(new GridLayout(1, 4, 10, 0));
        kartlar.setOpaque(false);
        kartlar.add(metrikKart("Toplam Gelir", ParaFormatlayici.formatlaKisa(gelir), GREEN, "▲"));
        kartlar.add(metrikKart("Toplam Gider", ParaFormatlayici.formatlaKisa(gider), RED,   "▼"));
        kartlar.add(metrikKart("Net Bakiye",
            (net >= 0 ? "+" : "-") + ParaFormatlayici.formatlaKisa(Math.abs(net)),
            net >= 0 ? BLUE : GOLD, net >= 0 ? "+" : "-"));
        kartlar.add(metrikKart("Islem Sayisi", islemSayisi + " adet", PURPLE, "#"));

        JPanel kategoriler = new JPanel(new GridLayout(1, 2, 12, 0));
        kategoriler.setOpaque(false);
        kategoriler.add(dagilimListesi("Gelir Kategorileri", gelirDagilim, GREEN, gelir));
        kategoriler.add(dagilimListesi("Gider Kategorileri", giderDagilim, RED,   gider));

        JPanel merkez = darkPanel(new BorderLayout(0, 12));
        merkez.add(kartlar,     BorderLayout.NORTH);
        merkez.add(kategoriler, BorderLayout.CENTER);

        panel.add(merkez, BorderLayout.CENTER);
        return panel;
    }

    private JPanel metrikKart(String baslik, String deger, Color aksan, String ikon) {
        JPanel p = new JPanel(new BorderLayout(0, 6)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(aksan);
                g2.fillRoundRect(0, 0, getWidth(), 3, 3, 3);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel ikonLbl   = lbl(ikon,    new Font("SansSerif", Font.BOLD, 18), aksan);
        JLabel degerLbl  = lbl(deger,   new Font("SansSerif", Font.BOLD, 15), TEXT);
        JLabel baslikLbl = lbl(baslik,  F_TINY, TEXT_DIM);

        p.add(ikonLbl,   BorderLayout.NORTH);
        p.add(degerLbl,  BorderLayout.CENTER);
        p.add(baslikLbl, BorderLayout.SOUTH);
        return p;
    }

    /**
     * DUZELTME: Anonim BoxLayout inner-class setTarget(null) hatasi giderildi.
     * Dogrudan panel olusturulup layout ataniyor.
     */
    private JScrollPane dagilimListesi(String baslik, Map<Kategori, Double> dagilim, Color aksan, double toplam) {
        JPanel inner = darkPanel(new BorderLayout(0, 8));
        inner.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        inner.add(lbl(baslik, F_SMALL, aksan), BorderLayout.NORTH);

        // DUZELTME: setTarget(null) yerine dogru BoxLayout kullanimi
        JPanel satirlar = darkPanel(null);
        satirlar.setLayout(new BoxLayout(satirlar, BoxLayout.Y_AXIS));

        if (dagilim.isEmpty()) {
            JLabel yokLbl = lbl("- veri yok -", F_TINY, TEXT_DIM);
            yokLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            satirlar.add(yokLbl);
        } else {
            dagilim.entrySet().stream()
                .sorted(Map.Entry.<Kategori, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    JPanel satir = kategoriSatir(entry.getKey(), entry.getValue(), toplam, aksan);
                    satir.setAlignmentX(Component.LEFT_ALIGNMENT);
                    satirlar.add(satir);
                });
        }

        inner.add(satirlar, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.add(inner);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        return scroll;
    }

    private JPanel kategoriSatir(Kategori kat, double tutar, double toplam, Color defaultRenk) {
        JPanel p = new JPanel(new BorderLayout(6, 2));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        Color katRenk = hexToColor(kat.getRenk(), defaultRenk);
        double pct = toplam > 0 ? tutar / toplam : 0;

        JPanel sol = new JPanel(new BorderLayout(0, 3));
        sol.setOpaque(false);

        JPanel adSatir = new JPanel(new BorderLayout());
        adSatir.setOpaque(false);

        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(katRenk);
                g2.fillOval(0, (getHeight() - 8) / 2, 8, 8);
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(14, 14));

        JLabel adLbl    = lbl(kat.getAd(), F_BODY, TEXT);
        JLabel pctLbl   = lbl(String.format("%.0f%%", pct * 100), F_SMALL, TEXT_DIM);
        // TL/kurus formatinda goster
        JLabel tutarLbl = lbl(ParaFormatlayici.formatla(tutar), F_SMALL, katRenk);

        JPanel adRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        adRow.setOpaque(false);
        adRow.add(dot); adRow.add(adLbl);

        adSatir.add(adRow,    BorderLayout.WEST);
        adSatir.add(tutarLbl, BorderLayout.EAST);

        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD2);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                int w = (int)(getWidth() * Math.min(pct, 1.0));
                if (w > 0) {
                    g2.setColor(katRenk);
                    g2.fillRoundRect(0, 0, w, getHeight(), 4, 4);
                }
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 5));

        JPanel pctPanel = new JPanel(new BorderLayout());
        pctPanel.setOpaque(false);
        pctPanel.add(pctLbl, BorderLayout.EAST);

        sol.add(adSatir,  BorderLayout.NORTH);
        sol.add(bar,      BorderLayout.CENTER);
        sol.add(pctPanel, BorderLayout.SOUTH);
        p.add(sol, BorderLayout.CENTER);
        return p;
    }

    private JLabel beklemeEtiketi() {
        JLabel l = lbl("Tarih araligi secip Rapor Olustur butonuna tiklayin", F_BODY, TEXT_DIM);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    // ── Helper builders ────────────────────────────────────────────────

    private JPanel darkPanel(LayoutManager lm) {
        JPanel p = new JPanel(lm); p.setBackground(BG); p.setOpaque(true); return p;
    }

    private JPanel roundCardPanel(LayoutManager lm) {
        JPanel p = new JPanel(lm) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    private JTextField darkField(String text, int w) {
        JTextField tf = new JTextField(text);
        tf.setBackground(CARD2);
        tf.setForeground(TEXT);
        tf.setCaretColor(TEXT);
        tf.setFont(F_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        tf.setPreferredSize(new Dimension(w, 34));
        return tf;
    }

    private JLabel lbl(String t, Font f, Color c) {
        JLabel l = new JLabel(t); l.setFont(f); l.setForeground(c); l.setOpaque(false); return l;
    }

    private JButton roundBtn(String txt, Color bg, Color fg, int w, int h) {
        JButton b = new JButton(txt) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                FontMetrics fm = g.getFontMetrics(getFont());
                g.setFont(getFont()); g.setColor(fg);
                g.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        b.setFont(F_SMALL); b.setForeground(fg); b.setBackground(bg);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setOpaque(false); b.setPreferredSize(new Dimension(w, h));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static Color hexToColor(String hex, Color fallback) {
        if (hex == null || hex.isEmpty()) return fallback;
        try {
            return Color.decode(hex.startsWith("#") ? hex : "#" + hex);
        } catch (NumberFormatException e) { return fallback; }
    }

    public void goster() { dialog.setVisible(true); }
}
