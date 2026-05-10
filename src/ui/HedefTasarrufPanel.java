package ui;

import controller.HedefTasarrufDenetleyici;
import model.HedefTasarruf;
import util.ParaFormatlayici;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class HedefTasarrufPanel {

    private static final Color BG     = new Color(8, 11, 22);
    private static final Color CARD   = new Color(15, 20, 40);
    private static final Color CARD2  = new Color(20, 28, 52);
    private static final Color BORDER_C = new Color(40, 52, 80);
    private static final Color BLUE   = new Color(59, 130, 246);
    private static final Color BLUE_L = new Color(99, 179, 255);
    private static final Color GREEN  = new Color(34, 197, 94);
    private static final Color GREEN_D = new Color(21, 128, 61);
    private static final Color RED    = new Color(239, 68, 68);
    private static final Color GOLD   = new Color(251, 191, 36);
    private static final Color PURPLE = new Color(168, 85, 247);
    private static final Color TEXT   = new Color(241, 245, 249);
    private static final Color TEXT_DIM = new Color(100, 116, 139);
    private static final Color TEXT_MID = new Color(148, 163, 184);

    private static final Font F_H1    = new Font("SansSerif", Font.BOLD, 20);
    private static final Font F_H2    = new Font("SansSerif", Font.BOLD, 14);
    private static final Font F_BODY  = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font F_SMALL = new Font("SansSerif", Font.BOLD, 11);
    private static final Font F_TINY  = new Font("SansSerif", Font.PLAIN, 10);

    private static final Color[] HEDEF_RENKLER = {
        BLUE, GREEN, GOLD, PURPLE, new Color(249, 115, 22), new Color(20, 184, 166)
    };

    private final HedefTasarrufDenetleyici hedefDenetleyici;
    private final Runnable anaYenile;
    private final JFrame parentFrame;

    private JPanel hedefListePanel;
    private final JPanel mainPanel;

    @SuppressWarnings("this-escape")
    public HedefTasarrufPanel(JFrame parentFrame, HedefTasarrufDenetleyici hedefDenetleyici,
                               Runnable anaYenile) {
        this.parentFrame = parentFrame;
        this.hedefDenetleyici = hedefDenetleyici;
        this.anaYenile = anaYenile;
        this.mainPanel = olustur();
    }

    public JPanel getPanel() { return mainPanel; }

    private JPanel olustur() {
        JPanel root = darkPanel(new BorderLayout(0, 16));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Üst: başlık + ekle butonu
        JPanel ust = new JPanel(new BorderLayout());
        ust.setOpaque(false);

        JPanel baslikBlok = new JPanel(new BorderLayout(0, 4));
        baslikBlok.setOpaque(false);
        baslikBlok.add(lbl("🎯 Tasarruf Hedefleri", F_H1, TEXT), BorderLayout.NORTH);
        baslikBlok.add(lbl("Finansal hedeflerinizi belirleyin ve ilerlemenizi takip edin.",
            F_BODY, TEXT_MID), BorderLayout.CENTER);

        JButton ekleBtn = roundBtn("+ Yeni Hedef", GREEN, new Color(10, 30, 15), 120, 38);
        ekleBtn.addActionListener(e -> yeniHedefFormu());

        ust.add(baslikBlok, BorderLayout.WEST);
        ust.add(ekleBtn, BorderLayout.EAST);
        root.add(ust, BorderLayout.NORTH);

        // Hedef kartları
        hedefListePanel = new JPanel();
        hedefListePanel.setLayout(new BoxLayout(hedefListePanel, BoxLayout.Y_AXIS));
        hedefListePanel.setBackground(BG);

        JScrollPane scroll = new JScrollPane(hedefListePanel);
        scroll.setBorder(null);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);
        scroll.setOpaque(false);
        root.add(scroll, BorderLayout.CENTER);

        yenile();
        return root;
    }

    public void yenile() {
        hedefListePanel.removeAll();
        List<HedefTasarruf> hedefler = hedefDenetleyici.listele();

        if (hedefler.isEmpty()) {
            JPanel bosPanel = new JPanel(new GridBagLayout());
            bosPanel.setBackground(BG);
            JLabel bosLbl = lbl("Henüz hedef oluşturmadınız.\n+ Yeni Hedef butonuna tıklayın.",
                new Font("SansSerif", Font.PLAIN, 14), TEXT_DIM);
            bosLbl.setHorizontalAlignment(SwingConstants.CENTER);
            bosPanel.add(bosLbl);
            hedefListePanel.add(bosPanel);
        } else {
            for (int i = 0; i < hedefler.size(); i++) {
                hedefListePanel.add(hedefKarti(hedefler.get(i), HEDEF_RENKLER[i % HEDEF_RENKLER.length]));
                hedefListePanel.add(Box.createVerticalStrut(12));
            }
        }

        hedefListePanel.revalidate();
        hedefListePanel.repaint();
    }

    private JPanel hedefKarti(HedefTasarruf hedef, Color renk) {
        JPanel kart = new JPanel(new BorderLayout(0, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(renk.getRed(), renk.getGreen(), renk.getBlue(), 60));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(renk);
                g2.fillRoundRect(0, 20, 4, getHeight() - 40, 4, 4);
                g2.dispose();
            }
        };
        kart.setOpaque(false);
        kart.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        kart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        kart.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Üst satır: isim + durum + butonlar
        JPanel ustSatir = new JPanel(new BorderLayout(8, 0));
        ustSatir.setOpaque(false);

        JLabel adLbl = lbl("🎯 " + hedef.getAd(), F_H2, TEXT);
        ustSatir.add(adLbl, BorderLayout.WEST);

        JPanel sagPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        sagPanel.setOpaque(false);

        if (hedef.tamamlandi()) {
            sagPanel.add(badge("✅ Tamamlandı", GREEN));
        } else if (hedef.getBitisTarihi().isBefore(LocalDate.now())) {
            sagPanel.add(badge("⏰ Süre Doldu", RED));
        } else {
            long kalanGun = LocalDate.now().until(hedef.getBitisTarihi()).getDays();
            sagPanel.add(badge(kalanGun + " gün kaldı", BLUE));
        }

        JButton katkilaBtn = smallBtn("+ Katkı", GREEN);
        JButton silBtn     = smallBtn("Sil", new Color(80, 20, 20));
        katkilaBtn.setForeground(new Color(10, 30, 15));
        silBtn.setForeground(new Color(255, 150, 150));

        katkilaBtn.addActionListener(e -> katkilaDialog(hedef));
        silBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(parentFrame,
                "\"" + hedef.getAd() + "\" hedefini silmek istiyor musunuz?",
                "Onayla", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                hedefDenetleyici.sil(hedef.getId());
                yenile();
            }
        });

        sagPanel.add(katkilaBtn);
        sagPanel.add(silBtn);
        ustSatir.add(sagPanel, BorderLayout.EAST);
        kart.add(ustSatir, BorderLayout.NORTH);

        // İlerleme çubuğu
        JPanel progressBlok = new JPanel(new BorderLayout(0, 6));
        progressBlok.setOpaque(false);

        double yuzde = hedef.yuzde();
        JPanel barPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int h = getHeight();
                // Arka plan
                g2.setColor(CARD2);
                g2.fillRoundRect(0, 0, getWidth(), h, h, h);
                // Doluluk
                int doluGenislik = (int) (getWidth() * yuzde / 100.0);
                if (doluGenislik > 0) {
                    GradientPaint gp = new GradientPaint(0, 0, renk, doluGenislik, 0,
                        renk.brighter());
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, doluGenislik, h, h, h);
                }
                g2.dispose();
            }
        };
        barPanel.setOpaque(false);
        barPanel.setPreferredSize(new Dimension(0, 12));

        JPanel miktarSatir = new JPanel(new BorderLayout());
        miktarSatir.setOpaque(false);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.of("tr", "TR"));
        miktarSatir.add(lbl(
            ParaFormatlayici.formatla(hedef.getBirikilenMiktar()) + " / " +
            ParaFormatlayici.formatla(hedef.getHedefMiktar()) +
            String.format("   (%.0f%%)", yuzde),
            new Font("SansSerif", Font.BOLD, 13), renk), BorderLayout.WEST);
        miktarSatir.add(lbl("Hedef: " + hedef.getBitisTarihi().format(fmt), F_TINY, TEXT_DIM),
            BorderLayout.EAST);

        progressBlok.add(miktarSatir, BorderLayout.NORTH);
        progressBlok.add(barPanel, BorderLayout.CENTER);
        kart.add(progressBlok, BorderLayout.CENTER);

        return kart;
    }

    private void katkilaDialog(HedefTasarruf hedef) {
        // Ozel dialog: TL | , | Kurus giris alani ile
        JDialog katDialog = new JDialog(parentFrame, "Katki Ekle", true);
        katDialog.setSize(360, 180);
        katDialog.setLocationRelativeTo(parentFrame);
        katDialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(CARD);
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        JLabel aciklamaLbl = new JLabel("\"" + hedef.getAd() + "\" hedefine katki miktari:");
        aciklamaLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        aciklamaLbl.setForeground(TEXT);

        ParaGirisAlani paraAlani = new ParaGirisAlani();
        JLabel hataLbl = new JLabel("");
        hataLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hataLbl.setForeground(RED);

        JPanel merkez = new JPanel();
        merkez.setOpaque(false);
        merkez.setLayout(new BoxLayout(merkez, BoxLayout.Y_AXIS));
        merkez.add(aciklamaLbl);
        merkez.add(Box.createVerticalStrut(10));
        merkez.add(paraAlani);
        merkez.add(Box.createVerticalStrut(4));
        merkez.add(hataLbl);

        JButton iptalBtn = new JButton("Iptal");
        iptalBtn.setBackground(CARD2);
        iptalBtn.setForeground(TEXT_DIM);
        iptalBtn.setFocusPainted(false);
        iptalBtn.addActionListener(e -> katDialog.dispose());

        JButton ekleBtn = new JButton("Ekle");
        ekleBtn.setBackground(BLUE);
        ekleBtn.setForeground(Color.WHITE);
        ekleBtn.setFocusPainted(false);
        ekleBtn.addActionListener(e -> {
            try {
                double miktar = paraAlani.getMiktar();
                hedefDenetleyici.katki(hedef.getId(), miktar);
                katDialog.dispose();
                yenile();
                anaYenile.run();
            } catch (IllegalArgumentException ex) {
                hataLbl.setText(ex.getMessage());
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(iptalBtn);
        btnPanel.add(ekleBtn);

        root.add(merkez,   BorderLayout.CENTER);
        root.add(btnPanel, BorderLayout.SOUTH);

        katDialog.setContentPane(root);
        katDialog.setVisible(true);
    }

    private void yeniHedefFormu() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(CARD);

        JTextField txtAd    = new JTextField(20);
        ParaGirisAlani txtMiktar = new ParaGirisAlani();   // TL | , | Kurus
        JTextField txtBitis = new JTextField(LocalDate.now().plusMonths(6).toString(), 20);

        form.add(yForm("Hedef Adi", txtAd));
        form.add(Box.createVerticalStrut(10));
        form.add(yForm("Hedef Miktar  ( TL , Kurus )", txtMiktar));
        form.add(Box.createVerticalStrut(10));
        form.add(yForm("Bitis Tarihi (yyyy-aa-gg)", txtBitis));

        int r = JOptionPane.showConfirmDialog(parentFrame, form, "Yeni Tasarruf Hedefi",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        String ad = txtAd.getText().trim();
        if (ad.isEmpty()) { JOptionPane.showMessageDialog(parentFrame, "Hedef adi bos olamaz."); return; }

        double miktar;
        try {
            miktar = txtMiktar.getMiktar();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(parentFrame, e.getMessage());
            return;
        }

        LocalDate bitis;
        try { bitis = LocalDate.parse(txtBitis.getText().trim()); }
        catch (Exception e) {
            JOptionPane.showMessageDialog(parentFrame, "Tarih yyyy-aa-gg formatinda olmali.");
            return;
        }
        if (bitis.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(parentFrame, "Bitis tarihi bugun veya sonraki bir tarih olmalidir.");
            return;
        }

        hedefDenetleyici.ekle(ad, miktar, LocalDate.now(), bitis);
        yenile();
        anaYenile.run();
    }

    private JPanel yForm(String etiket, JTextField alan) {
        return yForm(etiket, (JComponent) alan);
    }

    private JPanel yForm(String etiket, JComponent alan) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(etiket);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(TEXT_DIM);
        p.add(l, BorderLayout.NORTH);
        p.add(alan, BorderLayout.CENTER);
        return p;
    }

    private JLabel badge(String txt, Color renk) {
        JLabel l = new JLabel(txt) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(renk.getRed(), renk.getGreen(), renk.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setFont(F_TINY);
        l.setForeground(renk);
        l.setOpaque(false);
        l.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return l;
    }

    private JButton smallBtn(String txt, Color bg) {
        JButton b = new JButton(txt) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                FontMetrics fm = g.getFontMetrics(getFont());
                g.setFont(getFont()); g.setColor(getForeground());
                g.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        b.setFont(F_TINY); b.setBackground(bg);
        b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setOpaque(false);
        b.setPreferredSize(new Dimension(70, 26));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
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
        b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setOpaque(false);
        b.setPreferredSize(new Dimension(w, h));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel darkPanel(LayoutManager lm) {
        JPanel p = new JPanel(lm); p.setBackground(BG); p.setOpaque(true); return p;
    }

    private JLabel lbl(String t, Font f, Color c) {
        JLabel l = new JLabel(t); l.setFont(f); l.setForeground(c); l.setOpaque(false); return l;
    }
}
