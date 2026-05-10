package ui;

import controller.IslemDenetleyici;
import controller.KategoriDenetleyici;
import exception.GecersizMiktarHatasi;
import model.Islem;
import model.IslemTuru;
import model.Kategori;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Yeni islem ekleme veya mevcut islemi duzenleme formu.
 *
 * OOP ilkeleri:
 *  - Tek sorumluluk: yalnizca islem formu UI yonetimi
 *  - Para girisi ParaGirisAlani bileseni uzerinden yapilir (TL | , | Kurus)
 *  - Tur secimi kategorileri filtreler (gelir -> gelir kategorileri, vb.)
 */
public class IslemFormu {

    private static final DateTimeFormatter TARIH_FORMATI = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final JDialog dialog;
    private final IslemDenetleyici islemDenetleyici;
    private final KategoriDenetleyici kategoriDenetleyici;
    private final Islem islemDuzenle;
    private final Runnable yenileCallback;

    public IslemFormu(JFrame parent, IslemDenetleyici islemDenetleyici,
                      KategoriDenetleyici kategoriDenetleyici, Islem islemDuzenle,
                      Runnable yenileCallback) {
        this.islemDenetleyici    = islemDenetleyici;
        this.kategoriDenetleyici = kategoriDenetleyici;
        this.islemDuzenle        = islemDuzenle;
        this.yenileCallback      = yenileCallback;

        dialog = new JDialog(parent,
            islemDuzenle == null ? "Yeni Islem" : "Islemi Duzenle", true);
        dialog.setSize(480, 640);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);

        JPanel cp = new JPanel(new BorderLayout());
        cp.setBackground(ModernTema.BG_KOYU);
        cp.setOpaque(true);
        cp.add(icerikOlustur());
        dialog.setContentPane(cp);
    }

    private JPanel icerikOlustur() {
        JScrollPane scroll = new JScrollPane(formOlustur());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(ModernTema.BG_KOYU); g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        wrapper.setOpaque(true);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel formOlustur() {
        JPanel ana = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(ModernTema.BG_KOYU); g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        ana.setOpaque(true);
        ana.setLayout(new BoxLayout(ana, BoxLayout.Y_AXIS));
        ana.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Baslik
        JLabel baslik = ModernTema.etiket(
            islemDuzenle == null ? "Yeni Islem Ekle" : "Islemi Duzenle",
            ModernTema.FONT_BASLIK, ModernTema.METIN_BEYAZ);
        baslik.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel altYazi = ModernTema.etiket(
            "Tum alanlari doldurunuz", ModernTema.FONT_KUCUK, ModernTema.METIN_GRI);
        altYazi.setAlignmentX(Component.LEFT_ALIGNMENT);
        altYazi.setBorder(BorderFactory.createEmptyBorder(4, 0, 20, 0));

        // Islem turu
        JComboBox<IslemTuru> turCombo = ModernTema.modernComboBox(IslemTuru.values());
        if (islemDuzenle != null) turCombo.setSelectedItem(islemDuzenle.getTur());
        turCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        turCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Para giris alani: TL | , | Kurus ─────────────────────────
        double baslangicMiktar = islemDuzenle != null ? islemDuzenle.getMiktar() : 0.0;
        ParaGirisAlani paraAlani = new ParaGirisAlani(baslangicMiktar);

        // Tarih
        JTextField tarihAlan = ModernTema.modernTextField("");
        tarihAlan.setText(islemDuzenle != null
            ? islemDuzenle.getTarih().format(TARIH_FORMATI)
            : LocalDate.now().format(TARIH_FORMATI));
        tarihAlan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        tarihAlan.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Aciklama
        JTextField aciklamaAlan = ModernTema.modernTextField("");
        if (islemDuzenle != null) aciklamaAlan.setText(islemDuzenle.getAciklama());
        aciklamaAlan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        aciklamaAlan.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Kategoriler
        List<Kategori> tumKategoriler = kategoriDenetleyici.kategorileriGetir();

        JComboBox<Kategori> kategoriCombo = new JComboBox<>();
        kategoriCombo.setFont(ModernTema.FONT_NORMAL);
        kategoriCombo.setBackground(ModernTema.BG_PANEL);
        kategoriCombo.setForeground(ModernTema.METIN_BEYAZ);
        kategoriCombo.setOpaque(true);
        kategoriCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        kategoriCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        kategoriCombo.setRenderer((list, val, idx, sel, foc) -> {
            JLabel lbl = new JLabel(val == null ? "" : val.getAd());
            lbl.setFont(ModernTema.FONT_NORMAL);
            lbl.setForeground(ModernTema.METIN_BEYAZ);
            lbl.setBackground(sel ? ModernTema.VURGU_MAVI : ModernTema.BG_PANEL);
            lbl.setOpaque(true);
            lbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            return lbl;
        });

        // Ekstra alan (Kaynak / Odeme Yontemi)
        JLabel ekstraLabel = etiket("KAYNAK");
        JTextField ekstraAlan = ModernTema.modernTextField("");
        if (islemDuzenle instanceof model.Gelir g)       ekstraAlan.setText(g.getKaynak());
        else if (islemDuzenle instanceof model.Gider gd) ekstraAlan.setText(gd.getOdemeYontemi());
        ekstraAlan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        ekstraAlan.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Hata etiketi
        JLabel hataLabel = ModernTema.etiket("", ModernTema.FONT_KUCUK, ModernTema.GIDER_KIRMIZI);
        hataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Kaydet butonu
        JButton kaydetBtn = ModernTema.modernButon(
            islemDuzenle == null ? "  Ekle  " : "  Guncelle  ", ModernTema.VURGU_MAVI);
        kaydetBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        kaydetBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        // ── Kategori filtreleme (tur seciminden sonra) ────────────────
        Runnable kategorileriFiltrele = () -> {
            IslemTuru secilenTur = (IslemTuru) turCombo.getSelectedItem();
            Kategori oncekiSecim = (Kategori) kategoriCombo.getSelectedItem();

            kategoriCombo.removeAllItems();
            tumKategoriler.stream()
                .filter(k -> k.getTur() == secilenTur)
                .collect(Collectors.toList())
                .forEach(kategoriCombo::addItem);

            if (oncekiSecim != null && oncekiSecim.getTur() == secilenTur) {
                for (int i = 0; i < kategoriCombo.getItemCount(); i++) {
                    if (kategoriCombo.getItemAt(i).getId().equals(oncekiSecim.getId())) {
                        kategoriCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            ekstraLabel.setText(
                secilenTur == IslemTuru.GELIR ? "KAYNAK" : "ODEME YONTEMI");
        };

        turCombo.addActionListener(e -> kategorileriFiltrele.run());
        kategorileriFiltrele.run();

        // Duzenleme modunda onceki kategoriyi sec
        if (islemDuzenle != null) {
            for (int i = 0; i < kategoriCombo.getItemCount(); i++) {
                if (kategoriCombo.getItemAt(i).getId().equals(islemDuzenle.getKategori().getId())) {
                    kategoriCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        // ── Panel yerlesimi ───────────────────────────────────────────
        ana.add(baslik);
        ana.add(altYazi);

        ana.add(etiket("ISLEM TURU"));             ana.add(Box.createVerticalStrut(5));
        ana.add(turCombo);                          ana.add(Box.createVerticalStrut(14));

        ana.add(etiket("MIKTAR  ( TL , Kurus )")); ana.add(Box.createVerticalStrut(5));
        ana.add(paraAlani);                          ana.add(Box.createVerticalStrut(14));

        ana.add(etiket("TARIH (GG.AA.YYYY)"));     ana.add(Box.createVerticalStrut(5));
        ana.add(tarihAlan);                          ana.add(Box.createVerticalStrut(14));

        ana.add(etiket("ACIKLAMA"));                ana.add(Box.createVerticalStrut(5));
        ana.add(aciklamaAlan);                       ana.add(Box.createVerticalStrut(14));

        ana.add(etiket("KATEGORI"));                ana.add(Box.createVerticalStrut(5));
        ana.add(kategoriCombo);                      ana.add(Box.createVerticalStrut(14));

        ana.add(ekstraLabel);                        ana.add(Box.createVerticalStrut(5));
        ana.add(ekstraAlan);                         ana.add(Box.createVerticalStrut(10));

        ana.add(hataLabel);                          ana.add(Box.createVerticalStrut(16));
        ana.add(kaydetBtn);

        // ── Kaydet / Guncelle eylemi ──────────────────────────────────
        kaydetBtn.addActionListener(e -> {
            hataLabel.setText("");
            try {
                // Para miktarini ParaGirisAlani'ndan al
                double miktar;
                try {
                    miktar = paraAlani.getMiktar();
                } catch (IllegalArgumentException ex) {
                    hataLabel.setText(ex.getMessage());
                    return;
                }

                // Tarih
                String tarihMetin = tarihAlan.getText().trim();
                if (tarihMetin.isEmpty()) {
                    hataLabel.setText("Tarih bos birakilamaz.");
                    return;
                }
                LocalDate tarih;
                try {
                    tarih = LocalDate.parse(tarihMetin, TARIH_FORMATI);
                } catch (DateTimeParseException ex) {
                    hataLabel.setText("Tarih formati: gg.aa.yyyy");
                    return;
                }
                if (tarih.isAfter(LocalDate.now().plusYears(1))) {
                    hataLabel.setText("Tarih cok ileri bir tarih olamaz.");
                    return;
                }

                String aciklama    = aciklamaAlan.getText().trim();
                Kategori seciliKat = (Kategori) kategoriCombo.getSelectedItem();
                String ekstra      = ekstraAlan.getText().trim();
                IslemTuru tur      = (IslemTuru) turCombo.getSelectedItem();

                if (seciliKat == null) {
                    hataLabel.setText("Lutfen bir kategori secin.");
                    return;
                }
                if (seciliKat.getTur() != tur) {
                    hataLabel.setText("Secilen kategori islem turuyle uyusmuyor.");
                    return;
                }

                if (islemDuzenle == null) {
                    if (tur == IslemTuru.GELIR)
                        islemDenetleyici.gelirEkle(miktar, tarih, aciklama, seciliKat, ekstra);
                    else
                        islemDenetleyici.giderEkle(miktar, tarih, aciklama, seciliKat, ekstra);
                } else {
                    islemDenetleyici.islemGuncelle(
                        islemDuzenle.getId(), miktar, tarih, aciklama, seciliKat, ekstra, tur);
                }
                yenileCallback.run();
                dialog.dispose();

            } catch (GecersizMiktarHatasi ex) {
                hataLabel.setText(ex.getMessage());
            }
        });

        return ana;
    }

    private JLabel etiket(String metin) {
        JLabel lbl = ModernTema.etiket(
            metin, new Font("SansSerif", Font.BOLD, 10), ModernTema.METIN_GRI);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    public void goster() { dialog.setVisible(true); }
}
