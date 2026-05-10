package ui;

import controller.ButceDenetleyici;
import controller.KategoriDenetleyici;
import model.ButceLimiti;
import model.Kategori;
import model.IslemTuru;


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class ButceLimitiFormu {

    private static final Color BG    = new Color(8, 11, 22);
    private static final Color CARD  = new Color(15, 20, 40);
    private static final Color CARD2 = new Color(20, 28, 52);
    private static final Color BORDER_C = new Color(40, 52, 80);
    private static final Color BLUE  = new Color(59, 130, 246);
    private static final Color GREEN = new Color(34, 197, 94);
    private static final Color RED   = new Color(239, 68, 68);
    private static final Color GOLD  = new Color(251, 191, 36);
    private static final Color TEXT  = new Color(241, 245, 249);
    private static final Color TEXT_DIM = new Color(100, 116, 139);
    private static final Color TEXT_MID = new Color(148, 163, 184);

    private final JDialog dialog;
    private final ButceDenetleyici butceDenetleyici;
    private final KategoriDenetleyici kategoriDenetleyici;
    private final Runnable yenileCallback;

    public ButceLimitiFormu(JFrame parent, ButceDenetleyici butceDenetleyici,
                            KategoriDenetleyici kategoriDenetleyici, Runnable yenileCallback) {
        this.butceDenetleyici = butceDenetleyici;
        this.kategoriDenetleyici = kategoriDenetleyici;
        this.yenileCallback = yenileCallback;

        dialog = new JDialog(parent, "💰 Bütçe Limitleri", true);
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setContentPane(icerikOlustur());
    }

    private JPanel icerikOlustur() {
        JPanel root = darkPanel(new BorderLayout(0, 0));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel baslik = lbl("Aylık Gider Limitleri", new Font("SansSerif", Font.BOLD, 18), TEXT);
        JLabel aciklama = lbl("Her kategori için aylık harcama limiti belirleyin.", new Font("SansSerif", Font.PLAIN, 12), TEXT_MID);

        JPanel ustPanel = darkPanel(new BorderLayout(0, 6));
        ustPanel.add(baslik, BorderLayout.NORTH);
        ustPanel.add(aciklama, BorderLayout.CENTER);
        ustPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        root.add(ustPanel, BorderLayout.NORTH);

        List<Kategori> kategoriler = kategoriDenetleyici.kategorileriGetir();
        List<ButceLimiti> mevcutLimitler = butceDenetleyici.limitleriGetir();

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG);

        JTextField[] alanlar = null; // kullanilmiyor, gecisle icin
        ParaGirisAlani[] paraAlanlar = new ParaGirisAlani[kategoriler.size()];
        Kategori[] katArray = kategoriler.toArray(new Kategori[0]);

        for (int i = 0; i < katArray.length; i++) {
            Kategori kat = katArray[i];
            if (kat.getTur() != IslemTuru.GIDER) continue;

            Optional<ButceLimiti> mevcutLimit = mevcutLimitler.stream()
                .filter(l -> l.getKategoriId().equals(kat.getId()))
                .findFirst();

            JPanel satir = new JPanel(new BorderLayout(12, 0));
            satir.setBackground(CARD);
            satir.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_C),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
            ));
            satir.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

            JLabel katLbl = lbl("📁 " + kat.getAd(), new Font("SansSerif", Font.BOLD, 13), TEXT);

            // TL | , | Kurus ikili giris alani
            ParaGirisAlani paraAlan = new ParaGirisAlani();
            mevcutLimit.ifPresent(l -> paraAlan.setMiktar(l.getAylikLimit()));
            paraAlanlar[i] = paraAlan;

            satir.add(katLbl,   BorderLayout.CENTER);
            satir.add(paraAlan, BorderLayout.EAST);
            listPanel.add(satir);
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_C));
        root.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        JButton iptalBtn = roundBtn("İptal", CARD2, TEXT_MID);
        JButton kaydetBtn = roundBtn("Kaydet", BLUE, Color.WHITE);

        iptalBtn.addActionListener(e -> dialog.dispose());
        kaydetBtn.addActionListener(e -> {
            for (int i = 0; i < katArray.length; i++) {
                if (paraAlanlar[i] == null) continue;
                Kategori kat = katArray[i];
                try {
                    double limit = paraAlanlar[i].getMiktar();
                    butceDenetleyici.limitBelirle(kat.getId(), limit);
                } catch (IllegalArgumentException ex) {
                    // Alan bos birakildiysa mevcut limiti sil
                    String mesaj = ex.getMessage();
                    if (mesaj.contains("bos")) {
                        butceDenetleyici.limitleriGetir().stream()
                            .filter(l -> l.getKategoriId().equals(kat.getId()))
                            .findFirst()
                            .ifPresent(l -> butceDenetleyici.limitSil(l.getId()));
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            kat.getAd() + " icin gecersiz miktar.\n" + mesaj,
                            "Hata", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            yenileCallback.run();
            dialog.dispose();
        });

        btnPanel.add(iptalBtn);
        btnPanel.add(kaydetBtn);
        root.add(btnPanel, BorderLayout.SOUTH);

        return root;
    }

    public void goster() { dialog.setVisible(true); }

    private JPanel darkPanel(LayoutManager lm) {
        JPanel p = new JPanel(lm); p.setBackground(BG); p.setOpaque(true); return p;
    }

    private JLabel lbl(String t, Font f, Color c) {
        JLabel l = new JLabel(t); l.setFont(f); l.setForeground(c); l.setOpaque(false); return l;
    }

    private JButton roundBtn(String txt, Color bg, Color fg) {
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
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(fg); b.setBackground(bg);
        b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setOpaque(false);
        b.setPreferredSize(new Dimension(90, 36));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
