package ui;

import controller.KategoriDenetleyici;
import controller.TekrarlananIslemDenetleyici;
import model.*;
import util.ParaFormatlayici; // Tablo gosterimi icin kullanilabilir

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.Dialog;
import java.time.LocalDate;
import java.util.List;

public class TekrarlananIslemFormu {

    private static final Color BG    = new Color(8, 11, 22);
    private static final Color CARD  = new Color(15, 20, 40);
    private static final Color CARD2 = new Color(20, 28, 52);
    private static final Color BORDER_C = new Color(40, 52, 80);
    private static final Color BLUE  = new Color(59, 130, 246);
    private static final Color GREEN = new Color(34, 197, 94);
    private static final Color RED   = new Color(239, 68, 68);
    private static final Color GOLD  = new Color(251, 191, 36);
    private static final Color TEXT  = new Color(241, 245, 249);
    private static final Color TEXT_MID = new Color(148, 163, 184);

    private final JDialog dialog;
    private final TekrarlananIslemDenetleyici tekrarlananIslemDenetleyici;
    private final KategoriDenetleyici kategoriDenetleyici;
    private final Runnable yenileCallback;

    public TekrarlananIslemFormu(Window parent,
                                 TekrarlananIslemDenetleyici tekrarlananIslemDenetleyici,
                                 KategoriDenetleyici kategoriDenetleyici,
                                 Runnable yenileCallback) {
        this.tekrarlananIslemDenetleyici = tekrarlananIslemDenetleyici;
        this.kategoriDenetleyici = kategoriDenetleyici;
        this.yenileCallback = yenileCallback;

        dialog = new JDialog(parent, "🔄 Tekrarlanan İşlem Ekle", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(420, 500);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setContentPane(icerikOlustur());
    }

    private JPanel icerikOlustur() {
        JPanel root = darkPanel(new BorderLayout(0, 0));
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        // Başlık
        JTextField txtBaslik = alan("", "Örn: Kira, Netflix...");
        // Tutar — TL | , | Kurus ikili giris alani
        ParaGirisAlani txtMiktar = new ParaGirisAlani();
        // Tür
        JComboBox<String> cmbTur = combo(new String[]{"Gider", "Gelir"});
        // Kategori
        List<Kategori> kategoriler = kategoriDenetleyici.kategorileriGetir();
        JComboBox<Kategori> cmbKategori = new JComboBox<>(kategoriler.toArray(new Kategori[0]));
        stilCombo(cmbKategori);
        cmbKategori.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel l = new JLabel(value != null ? value.getAd() : "");
            l.setFont(new Font("SansSerif", Font.PLAIN, 13));
            l.setForeground(TEXT); l.setBackground(isSelected ? CARD2 : CARD); l.setOpaque(true);
            l.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            return l;
        });
        // Ekstra
        JTextField txtEkstra = alan("", "Kaynak / Ödeme Yöntemi");
        // Tekrar tipi
        JComboBox<String> cmbTekrar = combo(new String[]{"Aylık", "Haftalık", "Günlük"});
        // Başlangıç tarihi
        JTextField txtTarih = alan(LocalDate.now().toString(), "yyyy-aa-gg");

        JLabel hataLbl = lbl("", new Font("SansSerif", Font.PLAIN, 12), RED);
        hataLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(satirOlustur("Başlık", txtBaslik));
        form.add(Box.createVerticalStrut(10));
        form.add(satirOlustur("Tür", cmbTur));
        form.add(Box.createVerticalStrut(10));
        form.add(satirOlustur("Tutar  ( TL , Kurus )", txtMiktar));
        form.add(Box.createVerticalStrut(10));
        form.add(satirOlustur("Kategori", cmbKategori));
        form.add(Box.createVerticalStrut(10));
        form.add(satirOlustur("Ekstra Bilgi", txtEkstra));
        form.add(Box.createVerticalStrut(10));
        form.add(satirOlustur("Tekrar", cmbTekrar));
        form.add(Box.createVerticalStrut(10));
        form.add(satirOlustur("İlk Tarih", txtTarih));
        form.add(Box.createVerticalStrut(10));
        form.add(hataLbl);

        root.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        JButton iptalBtn = roundBtn("İptal", CARD2, TEXT_MID);
        JButton kaydetBtn = roundBtn("Kaydet", BLUE, Color.WHITE);

        iptalBtn.addActionListener(e -> dialog.dispose());
        kaydetBtn.addActionListener(e -> {
            hataLbl.setText("");
            String baslik = txtBaslik.getText().trim();
            
            String tarihStr = txtTarih.getText().trim();

            if (baslik.isEmpty()) { hataLbl.setText("Baslik bos olamaz."); return; }
            double miktar;
            try {
                miktar = txtMiktar.getMiktar();
            } catch (IllegalArgumentException ex) {
                hataLbl.setText(ex.getMessage());
                return;
            }
            LocalDate tarih;
            try { tarih = LocalDate.parse(tarihStr); }
            catch (Exception ex) { hataLbl.setText("Tarih yyyy-aa-gg formatında olmalı."); return; }

            Kategori kat = (Kategori) cmbKategori.getSelectedItem();
            if (kat == null) { hataLbl.setText("Kategori seçin."); return; }

            IslemTuru tur = cmbTur.getSelectedIndex() == 0 ? IslemTuru.GIDER : IslemTuru.GELIR;
            TekrarlananIslem.TekrarTipi tekrarTipi = switch (cmbTekrar.getSelectedIndex()) {
                case 1 -> TekrarlananIslem.TekrarTipi.HAFTALIK;
                case 2 -> TekrarlananIslem.TekrarTipi.GUNLUK;
                default -> TekrarlananIslem.TekrarTipi.AYLIK;
            };

            tekrarlananIslemDenetleyici.ekle(baslik, miktar, kat, tur,
                txtEkstra.getText().trim(), tekrarTipi, tarih);
            // Eklenen işlemin vadesi bugün veya geçmişse hemen uygula
            tekrarlananIslemDenetleyici.vadesiGelenIslemleriUygula();
            yenileCallback.run();
            dialog.dispose();
        });

        btnPanel.add(iptalBtn);
        btnPanel.add(kaydetBtn);
        root.add(btnPanel, BorderLayout.SOUTH);

        return root;
    }

    private JPanel satirOlustur(String etiket, JComponent alan) {
        JPanel p = new JPanel(new BorderLayout(8, 4));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        p.add(lbl(etiket, new Font("SansSerif", Font.BOLD, 11), new Color(100, 116, 139)), BorderLayout.NORTH);
        p.add(alan, BorderLayout.CENTER);
        return p;
    }

    private JTextField alan(String deger, String ipucu) {
        JTextField tf = new JTextField(deger);
        tf.setBackground(CARD2);
        tf.setForeground(TEXT);
        tf.setCaretColor(TEXT);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_C),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setToolTipText(ipucu);
        return tf;
    }

    private JComboBox<String> combo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        stilCombo(cb);
        return cb;
    }

    private void stilCombo(JComboBox<?> cb) {
        cb.setBackground(CARD2);
        cb.setForeground(TEXT);
        cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cb.setBorder(BorderFactory.createLineBorder(BORDER_C));
    }

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

    public void goster() { dialog.setVisible(true); }
}
