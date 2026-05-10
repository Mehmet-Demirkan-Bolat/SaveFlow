package ui;

import controller.*;
import util.ParaFormatlayici;
import model.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SaveFlow ana ekranı — tüm içerik tek sayfada, scroll olmadan görünür.
 * Layout: BorderLayout kök → NORTH(kartlar) | CENTER(3 sütun split) | SOUTH(aksiyon bar)
 */
public class AnaEkran {

    // ── Renkler ─────────────────────────────────────────────────────
    private static final Color BG       = new Color(8, 11, 22);
    private static final Color CARD     = new Color(15, 20, 40);
    private static final Color CARD2    = new Color(20, 28, 52);
    private static final Color BORDER   = new Color(40, 52, 80);
    private static final Color BLUE     = new Color(59, 130, 246);
    private static final Color BLUE_L   = new Color(99, 179, 255);
    private static final Color GREEN    = new Color(34, 197, 94);
    private static final Color GREEN_D  = new Color(21, 128, 61);
    private static final Color RED      = new Color(239, 68, 68);
    private static final Color RED_D    = new Color(153, 27, 27);
    private static final Color GOLD     = new Color(251, 191, 36);
    private static final Color PURPLE   = new Color(168, 85, 247);
    private static final Color ORANGE   = new Color(249, 115, 22);
    private static final Color TEXT     = new Color(241, 245, 249);
    private static final Color TEXT_DIM = new Color(100, 116, 139);
    private static final Color TEXT_MID = new Color(148, 163, 184);

    // ── Fontlar ─────────────────────────────────────────────────────
    private static final Font F_BIG   = new Font("SansSerif", Font.BOLD, 20);
    private static final Font F_H1    = new Font("SansSerif", Font.BOLD, 13);
    private static final Font F_H2    = new Font("SansSerif", Font.BOLD, 12);
    private static final Font F_BODY  = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font F_SMALL = new Font("SansSerif", Font.BOLD, 10);
    private static final Font F_TINY  = new Font("SansSerif", Font.PLAIN, 9);

    // ── Alanlar ─────────────────────────────────────────────────────
    private final JFrame frame;
    private final KullaniciDenetleyici kullaniciDenetleyici;
    private IslemDenetleyici islemDenetleyici;
    private KategoriDenetleyici kategoriDenetleyici;
    private ButceDenetleyici butceDenetleyici;
    private TekrarlananIslemDenetleyici tekrarlananIslemDenetleyici;
    private HedefTasarrufDenetleyici hedefTasarrufDenetleyici;

    private JLabel lblBakiye, lblGelir, lblGider, lblIslemSayisi;
    private JLabel lblDonemBaslik;
    private DefaultTableModel tableModel;
    private JTable tablo;
    private JPanel kategoriRozetPanel;
    private BarGrafikPanel grafikPanel;
    private PastaGrafikPanel gelirPastaPanel;
    private PastaGrafikPanel giderPastaPanel;
    private JPanel uyariPaneli;
    private JPanel butceSatirlarPanel;
    private JPanel hedefSatirlarPanel;

    private List<Islem> mevcutTablo = new ArrayList<>();
    private final Map<String, Color> katRenkMap = new LinkedHashMap<>();

    private JTextField txtArama;
    private JComboBox<String> cmbTur;
    private JComboBox<String> cmbKategori;
    private JTextField txtMinMiktar;
    private JTextField txtMaxMiktar;
    private boolean filtreAktif = false;

    private int secilenAy, secilenYil;
    private LocalDate donemBas, donemBit;

    // ── Kurucu ──────────────────────────────────────────────────────
    public AnaEkran(KullaniciDenetleyici kullaniciDenetleyici) {
        this.kullaniciDenetleyici = kullaniciDenetleyici;
        ModernTema.uygula();
        LocalDate now = LocalDate.now();
        secilenAy  = now.getMonthValue();
        secilenYil = now.getYear();
        donemGuncelle();
        frame = new JFrame("SaveFlow");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 800);
        frame.setMinimumSize(new Dimension(1100, 700));
        frame.setLocationRelativeTo(null);
    }

    public void setDenetleyiciler(IslemDenetleyici id, KategoriDenetleyici kd) {
        this.islemDenetleyici    = id;
        this.kategoriDenetleyici = kd;
    }

    public void setEkDenetleyiciler(ButceDenetleyici bd,
                                    TekrarlananIslemDenetleyici tid,
                                    HedefTasarrufDenetleyici hd) {
        this.butceDenetleyici            = bd;
        this.tekrarlananIslemDenetleyici = tid;
        this.hedefTasarrufDenetleyici    = hd;
    }

    public void goster() {
        frame.setJMenuBar(menuOlustur());
        frame.setContentPane(anaLayout());
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
            if (tekrarlananIslemDenetleyici != null) {
                int n = tekrarlananIslemDenetleyici.vadesiGelenIslemleriUygula();
                if (n > 0)
                    JOptionPane.showMessageDialog(frame,
                        n + " adet tekrarlanan işlem otomatik olarak uygulandı.",
                        "🔄 Tekrarlanan İşlemler", JOptionPane.INFORMATION_MESSAGE);
            }
            yenile();
        });
    }

    private void donemGuncelle() {
        donemBas = LocalDate.of(secilenYil, secilenAy, 1);
        donemBit = donemBas.withDayOfMonth(donemBas.lengthOfMonth());
    }

    // ── MENÜ ────────────────────────────────────────────────────────
    private JMenuBar menuOlustur() {
        JMenuBar bar = new JMenuBar();
        bar.setBackground(CARD); bar.setOpaque(true);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        JLabel logo = new JLabel("  💰 SaveFlow  ");
        logo.setFont(new Font("SansSerif", Font.BOLD, 13));
        logo.setForeground(BLUE_L);
        bar.add(logo);

        JButton prev = menuIconBtn("‹");
        lblDonemBaslik = new JLabel();
        lblDonemBaslik.setFont(F_H2);
        lblDonemBaslik.setForeground(TEXT);
        lblDonemBaslik.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        JButton next = menuIconBtn("›");
        guncelleDonemLabel();
        prev.addActionListener(e -> ayDegistir(-1));
        next.addActionListener(e -> ayDegistir(+1));

        bar.add(Box.createHorizontalStrut(6));
        bar.add(prev); bar.add(lblDonemBaslik); bar.add(next);
        bar.add(Box.createHorizontalStrut(10));
        bar.add(menu("Bütçe",    mi("💰 Bütçe Limitleri",   e -> butceLimitiAc())));
        bar.add(menu("Hedefler", mi("🎯 Hedef Tasarruflar", e -> hedefleriAc())));
        bar.add(Box.createHorizontalGlue());

        String ad = kullaniciDenetleyici.getAktifKullanici() != null
            ? kullaniciDenetleyici.getAktifKullanici().getAd() : "";
        JLabel kulLbl = lbl("👤 " + ad, F_BODY, TEXT_MID);
        kulLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        bar.add(kulLbl);
        bar.add(menu("Hesap", mi("🚪 Çıkış Yap", e -> {
            kullaniciDenetleyici.cikisYap();
            frame.dispose();
            GirisEkrani ge = new GirisEkrani(kullaniciDenetleyici, islemDenetleyici, kategoriDenetleyici);
            ge.setEkDenetleyiciler(butceDenetleyici, tekrarlananIslemDenetleyici, hedefTasarrufDenetleyici);
            ge.goster();
        })));
        bar.add(Box.createHorizontalStrut(6));
        return bar;
    }

    private JButton menuIconBtn(String txt) {
        JButton b = new JButton(txt);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setForeground(TEXT_MID);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(BLUE_L); }
            public void mouseExited(MouseEvent e)  { b.setForeground(TEXT_MID); }
        });
        return b;
    }

    private JMenu menu(String ad, JMenuItem... items) {
        JMenu m = new JMenu(ad);
        m.setFont(F_BODY); m.setForeground(TEXT); m.setBackground(CARD); m.setOpaque(true);
        for (JMenuItem mi : items) m.add(mi);
        return m;
    }

    private JMenuItem mi(String ad, ActionListener al) {
        JMenuItem mi = new JMenuItem(ad);
        mi.setFont(F_BODY); mi.setBackground(CARD); mi.setForeground(TEXT); mi.setOpaque(true);
        mi.addActionListener(al);
        return mi;
    }

    private void butceLimitiAc() {
        if (butceDenetleyici == null) return;
        new ButceLimitiFormu(frame, butceDenetleyici, kategoriDenetleyici, this::yenile).goster();
    }

    private void tekrarlananIslemAc() {
        if (tekrarlananIslemDenetleyici == null) return;
        tekrarlananIslemDenetleyici.vadesiGelenIslemleriUygula();
        new TekrarlananIslemYonetimi(frame, tekrarlananIslemDenetleyici,
            kategoriDenetleyici, this::yenile).goster();
        yenile();
    }

    private void hedefleriAc() {
        if (hedefTasarrufDenetleyici == null) return;
        HedefTasarrufPanel p = new HedefTasarrufPanel(frame, hedefTasarrufDenetleyici, this::yenile);
        JDialog dlg = new JDialog(frame, "🎯 Hedef Tasarruflar", true);
        dlg.setSize(700, 560); dlg.setLocationRelativeTo(frame);
        dlg.setContentPane(p.getPanel()); dlg.setVisible(true);
    }

    // ═══════════════════════════════════════════════════════════════
    // ANA LAYOUT
    // Yapı:
    //   ROOT (BorderLayout)
    //     NORTH  → özet kartlar (sabit 80px) + uyarı şeridi
    //     CENTER → JSplitPane dikey:
    //                ÜST (sabit ağırlık) → grafik+pasta | bütçe+hedef (2 sütun)
    //                ALT (kalan alan)    → filtre + tablo
    //     SOUTH  → aksiyon bar (sabit 50px)
    // ═══════════════════════════════════════════════════════════════
    private JPanel anaLayout() {
        JPanel root = darkPanel(new BorderLayout(0, 0));

        // NORTH: kartlar + uyarı
        JPanel kuzey = darkPanel(new BorderLayout(0, 3));
        kuzey.setBorder(BorderFactory.createEmptyBorder(8, 14, 4, 14));
        kuzey.add(ozetKartlari(), BorderLayout.NORTH);
        uyariPaneli = new JPanel(new WrapLayout(FlowLayout.LEFT, 6, 2));
        uyariPaneli.setOpaque(false);
        uyariPaneli.setVisible(false);
        kuzey.add(uyariPaneli, BorderLayout.CENTER);
        root.add(kuzey, BorderLayout.NORTH);

        // CENTER: JSplitPane (üst blok + tablo)
        JPanel ustBlok = olusturUstBlok();
        JPanel tabloBlok = olusturTabloBlok();

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ustBlok, tabloBlok);
        split.setDividerSize(4);
        split.setDividerLocation(370);   // üst blok yüksekliği piksel cinsinden
        split.setResizeWeight(0.42);     // pencere büyütülünce oranı koru
        split.setBorder(null);
        split.setOpaque(false);
        split.setBackground(BG);
        // divider rengi
        split.setUI(new javax.swing.plaf.basic.BasicSplitPaneUI() {
            @Override public javax.swing.plaf.basic.BasicSplitPaneDivider createDefaultDivider() {
                javax.swing.plaf.basic.BasicSplitPaneDivider d = super.createDefaultDivider();
                d.setBackground(BORDER);
                return d;
            }
        });

        JPanel centerWrapper = darkPanel(new BorderLayout());
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(0, 14, 4, 14));
        centerWrapper.add(split, BorderLayout.CENTER);
        root.add(centerWrapper, BorderLayout.CENTER);

        // SOUTH: aksiyon bar
        root.add(altAksiyonBar(), BorderLayout.SOUTH);
        return root;
    }

    /** Üst blok: sol=grafik, sağ=pasta grafikleri | alt=bütçe+hedef */
    private JPanel olusturUstBlok() {
        JPanel p = darkPanel(new BorderLayout(0, 6));
        p.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        // Üst satır: bar grafik + pasta grafikler
        grafikPanel     = new BarGrafikPanel();
        gelirPastaPanel = new PastaGrafikPanel(GREEN, "Gelir");
        giderPastaPanel = new PastaGrafikPanel(RED,   "Gider");

        JPanel sagPasta = new JPanel(new GridLayout(2, 1, 0, 6));
        sagPasta.setOpaque(false);
        sagPasta.add(kartSargi("🟢 Gelir Kategorileri", gelirPastaPanel));
        sagPasta.add(kartSargi("🔴 Gider Kategorileri", giderPastaPanel));

        JPanel grafikSatir = new JPanel(new GridLayout(1, 2, 10, 0));
        grafikSatir.setOpaque(false);
        grafikSatir.setPreferredSize(new Dimension(0, 210));
        grafikSatir.add(kartSargi("📊 Aylık Gelir / Gider — " + secilenYil, grafikPanel));
        grafikSatir.add(sagPasta);

        // Alt satır: bütçe + hedef panelleri
        butceSatirlarPanel = new JPanel();
        butceSatirlarPanel.setOpaque(false);
        butceSatirlarPanel.setLayout(new BoxLayout(butceSatirlarPanel, BoxLayout.Y_AXIS));
        JScrollPane butceScroll = kompaktScroll(butceSatirlarPanel);

        hedefSatirlarPanel = new JPanel();
        hedefSatirlarPanel.setOpaque(false);
        hedefSatirlarPanel.setLayout(new BoxLayout(hedefSatirlarPanel, BoxLayout.Y_AXIS));
        JScrollPane hedefScroll = kompaktScroll(hedefSatirlarPanel);

        JPanel ozetPaneller = new JPanel(new GridLayout(1, 2, 10, 0));
        ozetPaneller.setOpaque(false);
        ozetPaneller.setPreferredSize(new Dimension(0, 130));
        ozetPaneller.add(kartSargi("💰 Aylık Bütçe Durumu", butceScroll));
        ozetPaneller.add(kartSargi("🎯 Tasarruf Hedefleri", hedefScroll));

        p.add(grafikSatir,   BorderLayout.CENTER);
        p.add(ozetPaneller,  BorderLayout.SOUTH);
        return p;
    }

    /** Alt blok: filtre çubuğu + tablo */
    private JPanel olusturTabloBlok() {
        JPanel p = darkPanel(new BorderLayout(0, 4));
        p.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        p.add(filtrePanel(),  BorderLayout.NORTH);
        p.add(islemTablosu(), BorderLayout.CENTER);
        return p;
    }

    private JScrollPane kompaktScroll(JPanel icPanel) {
        JScrollPane sp = new JScrollPane(icPanel);
        sp.setBorder(null); sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getViewport().setBackground(CARD);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(8);
        return sp;
    }

    // ── ALT AKSİYON BAR ─────────────────────────────────────────────
    private JPanel altAksiyonBar() {
        JPanel bar = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override protected void paintComponent(Graphics g) {
                g.setColor(CARD); g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bar.setOpaque(true);
        bar.setPreferredSize(new Dimension(0, 50));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            BorderFactory.createEmptyBorder(7, 16, 7, 16)));
        bar.setLayout(new BorderLayout(0, 0));

        // Sol: 3 buton, BoxLayout ile yan yana
        JPanel sol = new JPanel();
        sol.setOpaque(false);
        sol.setLayout(new BoxLayout(sol, BoxLayout.X_AXIS));

        JButton paraBtn    = aksiyonButon("💳  Para İşlemi",  BLUE,  Color.WHITE,  148);
        JButton tekBtn     = aksiyonButon("🔄  Tekrarlanan",  CARD2, TEXT_MID,     132);
        JButton katBtn     = aksiyonButon("🏷  Kategoriler",  CARD2, TEXT_MID,     132);

        paraBtn.addActionListener(e ->
            new IslemFormu(frame, islemDenetleyici, kategoriDenetleyici, null, this::yenile).goster());
        tekBtn.addActionListener(e -> tekrarlananIslemAc());
        katBtn.addActionListener(e ->
            new KategoriFormu(frame, kategoriDenetleyici, this::yenile).goster());

        sol.add(paraBtn);
        sol.add(Box.createHorizontalStrut(8));
        sol.add(tekBtn);
        sol.add(Box.createHorizontalStrut(8));
        sol.add(katBtn);

        // Sağ: rapor
        JButton raporBtn = aksiyonButon("📊  Rapor", CARD2, TEXT_MID, 110);
        raporBtn.addActionListener(e -> new RaporEkrani(frame, islemDenetleyici).goster());

        bar.add(sol,      BorderLayout.WEST);
        bar.add(raporBtn, BorderLayout.EAST);
        return bar;
    }

    private JButton aksiyonButon(String txt, Color bg, Color fg, int w) {
        JButton b = new JButton(txt) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                g2.dispose();
                FontMetrics fm = g.getFontMetrics(getFont());
                g.setFont(getFont()); g.setColor(getForeground());
                g.drawString(getText(),
                    (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        b.setFont(F_BODY); b.setForeground(fg); b.setBackground(bg);
        Dimension d = new Dimension(w, 34);
        b.setPreferredSize(d); b.setMaximumSize(d); b.setMinimumSize(d);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setOpaque(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── DÖNEM ───────────────────────────────────────────────────────
    private void ayDegistir(int d) {
        secilenAy += d;
        if (secilenAy < 1)  { secilenAy = 12; secilenYil--; }
        if (secilenAy > 12) { secilenAy = 1;  secilenYil++; }
        donemGuncelle(); guncelleDonemLabel(); yenile();
    }

    private void guncelleDonemLabel() {
        if (lblDonemBaslik == null) return;
        String[] aylar = {"Ocak","Şubat","Mart","Nisan","Mayıs","Haziran",
                          "Temmuz","Ağustos","Eylül","Ekim","Kasım","Aralık"};
        lblDonemBaslik.setText(aylar[secilenAy - 1] + " " + secilenYil);
    }

    // ── ÖZET KARTLARI ───────────────────────────────────────────────
    private JPanel ozetKartlari() {
        JPanel p = new JPanel(new GridLayout(1, 4, 8, 0));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(0, 76));

        lblBakiye      = valueLabel("0,00 ₺", BLUE_L);
        lblGelir       = valueLabel("0,00 ₺", GREEN);
        lblGider       = valueLabel("0,00 ₺", RED);
        lblIslemSayisi = valueLabel("0",       PURPLE);

        p.add(ozetKart("💳", "NET BAKİYE",   lblBakiye,      BLUE));
        p.add(ozetKart("📈", "TOPLAM GELİR", lblGelir,       GREEN));
        p.add(ozetKart("📉", "TOPLAM GİDER", lblGider,       RED));
        p.add(ozetKart("🔢", "İŞLEM SAYISI", lblIslemSayisi, PURPLE));
        return p;
    }

    private JLabel valueLabel(String txt, Color renk) {
        JLabel l = new JLabel(txt);
        l.setFont(F_BIG); l.setForeground(renk); l.setOpaque(false);
        return l;
    }

    private JPanel ozetKart(String emoji, String baslik, JLabel degerLbl, Color renk) {
        JPanel kart = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, CARD, getWidth(), getHeight(),
                    new Color(renk.getRed(), renk.getGreen(), renk.getBlue(), 30));
                g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(renk.getRed(), renk.getGreen(), renk.getBlue(), 80));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setColor(renk); g2.fillRoundRect(0, 12, 3, getHeight()-24, 3, 3);
                g2.dispose();
            }
        };
        kart.setOpaque(false);
        kart.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JPanel ust = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        ust.setOpaque(false);
        ust.add(lbl(emoji, new Font("SansSerif", Font.PLAIN, 11), renk));
        ust.add(lbl(baslik, F_TINY, TEXT_DIM));
        kart.add(ust, BorderLayout.NORTH);
        kart.add(degerLbl, BorderLayout.CENTER);
        return kart;
    }

    // ── BÜTÇE UYARI BANDİ ───────────────────────────────────────────
    private void uyariGuncelle(List<ButceAsimi> asimlar) {
        uyariPaneli.removeAll();
        if (asimlar == null || asimlar.isEmpty()) {
            uyariPaneli.setVisible(false);
        } else {
            uyariPaneli.setVisible(true);
            for (ButceAsimi a : asimlar) uyariPaneli.add(uyariBadge(a));
        }
        uyariPaneli.revalidate(); uyariPaneli.repaint();
    }

    private JPanel uyariBadge(ButceAsimi asim) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(239,68,68,20)); g2.fillRoundRect(0,0,getWidth(),getHeight(),18,18);
                g2.setColor(new Color(239,68,68,90)); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,18,18);
                g2.dispose();
            }
        };
        p.setOpaque(false); p.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 2));
        p.add(lbl("⚠", new Font("SansSerif",Font.PLAIN,10), RED));
        p.add(lbl(asim.getKategori().getAd(), F_SMALL, TEXT));
        p.add(lbl(ParaFormatlayici.formatlaKisa(asim.getHarcanan()) + "/" +
            ParaFormatlayici.formatlaKisa(asim.getLimit()) +
            String.format(" (+%.0f%%)", asim.asimYuzdesi()), F_TINY, RED));
        return p;
    }

    // ── FİLTRE PANELİ ───────────────────────────────────────────────
    private JPanel filtrePanel() {
        JPanel fp = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        fp.setOpaque(false);
        fp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
            BorderFactory.createEmptyBorder(2, 0, 2, 0)));

        txtArama = filtreField(13, "Açıklama veya kategori...");
        cmbTur = new JComboBox<>(new String[]{"Tümü", "Gelir", "Gider"});
        stilCombo(cmbTur);
        cmbKategori = new JComboBox<>();
        cmbKategori.addItem("Tüm Kategoriler");
        stilCombo(cmbKategori);
        cmbKategori.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                setBackground(sel ? CARD : CARD2); setForeground(TEXT); setFont(F_BODY);
                if (value != null && !value.toString().equals("Tüm Kategoriler")) {
                    setText("● " + value);
                    setForeground(katRenkMap.getOrDefault(value.toString(), TEXT_MID));
                }
                return this;
            }
        });

        txtMinMiktar = filtreField(5, "Min ₺");
        txtMaxMiktar = filtreField(5, "Max ₺");

        JButton filtreleBtn = roundBtn("🔍 Ara",    BLUE,  Color.WHITE);
        JButton temizleBtn  = roundBtn("✕ Temizle", CARD2, TEXT_MID);

        filtreleBtn.addActionListener(e -> { filtreAktif = true; filtrele(); });
        temizleBtn.addActionListener(e -> {
            filtreAktif = false;
            txtArama.setText(""); cmbTur.setSelectedIndex(0);
            if (cmbKategori.getItemCount() > 0) cmbKategori.setSelectedIndex(0);
            txtMinMiktar.setText(""); txtMaxMiktar.setText("");
            yenile();
        });

        fp.add(txtArama);
        fp.add(sepLbl()); fp.add(cmbTur); fp.add(cmbKategori);
        fp.add(sepLbl());
        fp.add(lbl("Min:", F_SMALL, TEXT_DIM)); fp.add(txtMinMiktar);
        fp.add(lbl("Max:", F_SMALL, TEXT_DIM)); fp.add(txtMaxMiktar);
        fp.add(filtreleBtn); fp.add(temizleBtn);
        return fp;
    }

    private JTextField filtreField(int cols, String tip) {
        JTextField tf = new JTextField(cols);
        tf.setBackground(CARD2); tf.setForeground(TEXT); tf.setCaretColor(TEXT);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        tf.setFont(F_BODY); tf.setToolTipText(tip);
        return tf;
    }

    private void stilCombo(JComboBox<?> cb) {
        cb.setBackground(CARD2); cb.setForeground(TEXT);
        cb.setFont(F_BODY); cb.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    private JLabel sepLbl() {
        JLabel l = new JLabel("│"); l.setForeground(BORDER);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return l;
    }

    private void filtrele() {
        String arama  = txtArama.getText().trim().toLowerCase();
        String turSec = (String) cmbTur.getSelectedItem();
        String katSec = (String) cmbKategori.getSelectedItem();
        String minStr = txtMinMiktar.getText().trim().replace(",", ".");
        String maxStr = txtMaxMiktar.getText().trim().replace(",", ".");
        double minM, maxM;
        try { minM = minStr.isEmpty() ? 0 : Double.parseDouble(minStr); } catch (NumberFormatException e) { minM = 0; }
        try { maxM = maxStr.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxStr); } catch (NumberFormatException e) { maxM = Double.MAX_VALUE; }
        final double fMin = minM, fMax = maxM;
        List<Islem> f = islemDenetleyici.islemleriGetir().stream().filter(i -> {
            if ("Gelir".equals(turSec) && i.getTur() != IslemTuru.GELIR) return false;
            if ("Gider".equals(turSec) && i.getTur() != IslemTuru.GIDER) return false;
            if (katSec != null && !"Tüm Kategoriler".equals(katSec) && !i.getKategori().getAd().equals(katSec)) return false;
            if (!arama.isEmpty()) {
                String ac = i.getAciklama() != null ? i.getAciklama().toLowerCase() : "";
                if (!ac.contains(arama) && !i.getKategori().getAd().toLowerCase().contains(arama)) return false;
            }
            return i.getMiktar() >= fMin && i.getMiktar() <= fMax;
        }).sorted((a,b) -> b.getTarih().compareTo(a.getTarih())).collect(Collectors.toList());
        tabloyuGuncelle(f);
    }

    // ── İŞLEM TABLOSU ───────────────────────────────────────────────
    private JPanel islemTablosu() {
        JPanel wrapper = darkPanel(new BorderLayout(0, 4));

        // Başlık + butonlar + rozetler
        JPanel ustBar = new JPanel(new BorderLayout());
        ustBar.setOpaque(false);
        ustBar.add(lbl("Son İşlemler", F_H1, TEXT), BorderLayout.WEST);
        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnP.setOpaque(false);
        JButton duzBtn = roundBtn("✏ Düzenle", CARD2, TEXT_MID);
        JButton silBtn = roundBtn("🗑 Sil",     RED_D, new Color(255, 150, 150));
        btnP.add(duzBtn); btnP.add(silBtn);
        ustBar.add(btnP, BorderLayout.EAST);

        kategoriRozetPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 5, 2));
        kategoriRozetPanel.setOpaque(false);

        JPanel ustBlok = darkPanel(new BorderLayout(0, 3));
        ustBlok.add(ustBar, BorderLayout.NORTH);
        ustBlok.add(kategoriRozetPanel, BorderLayout.CENTER);

        // Tablo
        String[] COLS = {"", "TÜR", "TARİH", "MİKTAR (TL)", "KATEGORİ", "AÇIKLAMA", "EKSTRA"};
        tableModel = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablo = new JTable(tableModel);
        tablo.setRowHeight(30);
        tablo.setShowGrid(false);
        tablo.setIntercellSpacing(new Dimension(0, 1));
        tablo.setBackground(CARD); tablo.setForeground(TEXT);
        tablo.setFont(F_BODY); tablo.setOpaque(true);
        tablo.setSelectionBackground(new Color(59, 130, 246, 60));
        tablo.setSelectionForeground(TEXT);
        tablo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablo.getColumnModel().getColumn(0).setMaxWidth(28);
        tablo.getColumnModel().getColumn(0).setMinWidth(28);

        JTableHeader hdr = tablo.getTableHeader();
        hdr.setBackground(CARD2); hdr.setForeground(TEXT_DIM); hdr.setFont(F_SMALL);
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        hdr.setOpaque(true); hdr.setReorderingAllowed(false);

        tablo.getColumnModel().getColumn(0).setCellRenderer((t, v, sel, foc, r, c) -> {
            JLabel l2 = new JLabel();
            l2.setHorizontalAlignment(SwingConstants.CENTER);
            boolean g = t.getModel().getValueAt(r, 1).toString().equals("Gelir");
            l2.setText(g ? "▲" : "▼"); l2.setFont(new Font("SansSerif", Font.BOLD, 11));
            l2.setForeground(g ? GREEN : RED);
            l2.setBackground(sel ? new Color(59,130,246,50) : (r%2==0 ? CARD : new Color(18,25,46)));
            l2.setOpaque(true); return l2;
        });

        tablo.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                Color rowBg = row%2==0 ? CARD : new Color(18,25,46);
                setBackground(sel ? new Color(59,130,246,50) : rowBg);
                setForeground(TEXT); setFont(F_BODY); setOpaque(true);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (col == 1 && v != null) {
                    boolean g = v.toString().equals("Gelir");
                    setText(g ? "▲ Gelir" : "▼ Gider");
                    setForeground(g ? GREEN : RED); setFont(F_SMALL);
                }
                if (col == 3 && v != null) {
                    boolean g = t.getModel().getValueAt(row,1).toString().equals("Gelir");
                    setText((g ? "+" : "−") + v + " ₺");
                    setForeground(g ? GREEN : RED);
                    setFont(new Font("SansSerif", Font.BOLD, 12));
                }
                if (col == 4 && v != null) {
                    setForeground(katRenkMap.getOrDefault(v.toString(), TEXT_MID));
                    setText("● " + v);
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tablo);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scroll.setBackground(CARD); scroll.getViewport().setBackground(CARD);
        scroll.getViewport().setOpaque(true); scroll.setOpaque(true);

        duzBtn.addActionListener(e -> {
            int s = tablo.getSelectedRow();
            if (s < 0) { bildirim("Lütfen bir işlem seçin."); return; }
            if (s < mevcutTablo.size())
                new IslemFormu(frame, islemDenetleyici, kategoriDenetleyici,
                    mevcutTablo.get(s), this::yenile).goster();
        });
        silBtn.addActionListener(e -> {
            int s = tablo.getSelectedRow();
            if (s < 0) { bildirim("Lütfen bir işlem seçin."); return; }
            if (s < mevcutTablo.size() &&
                JOptionPane.showConfirmDialog(frame, "Bu işlemi silmek istiyor musunuz?", "Sil",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                islemDenetleyici.islemSil(mevcutTablo.get(s).getId());
                yenile();
            }
        });

        wrapper.add(ustBlok, BorderLayout.NORTH);
        wrapper.add(scroll,  BorderLayout.CENTER);
        return wrapper;
    }

    // ── YENİLE ──────────────────────────────────────────────────────
    public void yenile() {
        if (islemDenetleyici == null) return;
        guncelleDonemLabel();

        double gelir = islemDenetleyici.toplamGelir(donemBas, donemBit);
        double gider = islemDenetleyici.toplamGider(donemBas, donemBit);
        double net   = gelir - gider;
        List<Islem> donemIslemler = islemDenetleyici.islemleriTarihAraliginaGore(donemBas, donemBit);

        lblGelir.setText("+" + ParaFormatlayici.formatla(gelir));
        lblGider.setText("−" + ParaFormatlayici.formatla(gider));
        lblBakiye.setText(ParaFormatlayici.formatla(net));
        lblBakiye.setForeground(net >= 0 ? BLUE_L : RED);
        lblIslemSayisi.setText(String.valueOf(donemIslemler.size()));

        List<Islem> sirali = new ArrayList<>(islemDenetleyici.islemleriGetir());
        sirali.sort((a, b) -> b.getTarih().compareTo(a.getTarih()));
        tabloyuGuncelle(sirali);

        katRenkMap.clear();
        for (Islem i : islemDenetleyici.islemleriGetir())
            katRenkMap.put(i.getKategori().getAd(), hexToColor(i.getKategori().getRenk()));

        Map<Kategori, Double> tumDagilim = islemDenetleyici.kategoriBazliDagilim(donemBas, donemBit);
        kategoriRozetPanel.removeAll();
        for (Map.Entry<Kategori, Double> e : tumDagilim.entrySet())
            kategoriRozetPanel.add(kategoriRozeti(e.getKey().getAd(), e.getValue(), hexToColor(e.getKey().getRenk())));
        kategoriRozetPanel.revalidate(); kategoriRozetPanel.repaint();

        guncelleKategoriCombo();

        grafikPanel.setVeri(islemDenetleyici, secilenYil);
        gelirPastaPanel.setVeri(islemDenetleyici.gelirKategoriBazliDagilim(donemBas, donemBit));
        giderPastaPanel.setVeri(islemDenetleyici.giderKategoriBazliDagilim(donemBas, donemBit));

        Map<Kategori, Double> giderDagilim = islemDenetleyici.giderKategoriBazliDagilim(donemBas, donemBit);
        if (butceDenetleyici != null)
            uyariGuncelle(butceDenetleyici.asimlariHesapla(giderDagilim));

        butceDurumuGuncelle(giderDagilim);
        hedefDurumuGuncelle();

        if (filtreAktif) filtrele();
    }

    private void guncelleKategoriCombo() {
        if (cmbKategori == null) return;
        String secili = (String) cmbKategori.getSelectedItem();
        cmbKategori.removeAllItems();
        cmbKategori.addItem("Tüm Kategoriler");
        Set<String> set = new LinkedHashSet<>();
        for (Islem i : islemDenetleyici.islemleriGetir()) set.add(i.getKategori().getAd());
        for (String ad : set) cmbKategori.addItem(ad);
        if (secili != null) cmbKategori.setSelectedItem(secili);
    }

    private void tabloyuGuncelle(List<Islem> islemler) {
        mevcutTablo = islemler;
        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.of("tr","TR"));
        for (Islem i : islemler) {
            boolean gMi = i.getTur() == IslemTuru.GELIR;
            String ekstra = (i instanceof model.Gelir g)  ? g.getKaynak()
                          : (i instanceof model.Gider gd) ? gd.getOdemeYontemi() : "";
            tableModel.addRow(new Object[]{null, gMi?"Gelir":"Gider",
                i.getTarih().format(fmt), ParaFormatlayici.formatlaSayi(i.getMiktar()),
                i.getKategori().getAd(), i.getAciklama(), ekstra});
        }
    }

    // ── KATEGORİ ROZETİ ─────────────────────────────────────────────
    private JPanel kategoriRozeti(String ad, double tutar, Color renk) {
        JPanel r = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(renk.getRed(),renk.getGreen(),renk.getBlue(),25));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
                g2.setColor(new Color(renk.getRed(),renk.getGreen(),renk.getBlue(),100));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,16,16);
                g2.dispose();
            }
        };
        r.setOpaque(false); r.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 2));
        r.add(lbl("●", new Font("SansSerif",Font.PLAIN,8), renk));
        r.add(lbl(ad, F_SMALL, TEXT));
        r.add(lbl(ParaFormatlayici.formatlaKisa(tutar), new Font("SansSerif",Font.BOLD,10), renk));
        return r;
    }

    // ── BAR GRAFİK ──────────────────────────────────────────────────
    class BarGrafikPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private double[] gelirler = new double[12];
        private double[] giderler = new double[12];
        BarGrafikPanel() { setOpaque(false); }

        void setVeri(IslemDenetleyici d, int yil) {
            for (int ay = 1; ay <= 12; ay++) {
                LocalDate b = LocalDate.of(yil,ay,1), e = b.withDayOfMonth(b.lengthOfMonth());
                gelirler[ay-1] = d.toplamGelir(b,e); giderler[ay-1] = d.toplamGider(b,e);
            }
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int pad = 22, bot = 16, drawH = h-bot-pad, drawW = w-pad*2;
            double max = 1;
            for (int i=0;i<12;i++) max = Math.max(max,Math.max(gelirler[i],giderler[i]));
            max *= 1.15;
            g2.setColor(new Color(40,55,80)); g2.setStroke(new BasicStroke(0.5f));
            for (int i=0;i<=4;i++) { int y=pad+drawH-(int)(drawH*i/4.0); g2.drawLine(pad,y,w-pad,y); }
            String[] AY={"O","Ş","M","N","M","H","T","A","E","E","K","A"};
            int barW=drawW/12;
            for (int ay=0;ay<12;ay++) {
                int x=pad+ay*barW, half=barW/2-2;
                int gH=(int)(drawH*gelirler[ay]/max), dH=(int)(drawH*giderler[ay]/max);
                if(gH>0){g2.setPaint(new GradientPaint(x,pad+drawH-gH,GREEN,x,pad+drawH,GREEN_D));g2.fillRoundRect(x+2,pad+drawH-gH,half,gH,3,3);}
                if(dH>0){g2.setPaint(new GradientPaint(x+half+2,pad+drawH-dH,RED,x+half+2,pad+drawH,RED_D));g2.fillRoundRect(x+half+3,pad+drawH-dH,half,dH,3,3);}
                g2.setFont(F_TINY); g2.setColor((ay+1)==secilenAy?BLUE_L:TEXT_DIM);
                g2.drawString(AY[ay],x+half-2,h-3);
            }
            g2.setColor(GREEN); g2.fillRoundRect(pad,4,7,5,2,2);
            g2.setFont(F_TINY); g2.setColor(TEXT_MID); g2.drawString("Gelir",pad+10,10);
            g2.setColor(RED); g2.fillRoundRect(pad+46,4,7,5,2,2);
            g2.setColor(TEXT_MID); g2.drawString("Gider",pad+57,10);
            g2.dispose();
        }
    }

    // ── PASTA GRAFİK ────────────────────────────────────────────────
    class PastaGrafikPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private transient Map<Kategori,Double> veri = new LinkedHashMap<>();
        private final Color accentColor;
        private final String etiket;

        PastaGrafikPanel(Color ac, String et) { this.accentColor=ac; this.etiket=et; setOpaque(false); }

        void setVeri(Map<Kategori,Double> v) { this.veri = v==null?new LinkedHashMap<>():v; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (veri.isEmpty()) {
                Graphics2D g2=(Graphics2D)g.create(); g2.setColor(TEXT_DIM); g2.setFont(F_TINY);
                String msg=etiket+" verisi yok"; FontMetrics fm=g2.getFontMetrics();
                g2.drawString(msg,(getWidth()-fm.stringWidth(msg))/2,getHeight()/2); g2.dispose(); return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight(), diam=Math.min(w*2/5,h-8);
            if(diam<10){g2.dispose();return;}
            int cx=diam/2+6, cy=h/2, x0=cx-diam/2, y0=cy-diam/2;
            double total=veri.values().stream().mapToDouble(d->d).sum(), startAngle=90;
            List<Map.Entry<Kategori,Double>> entries=new ArrayList<>(veri.entrySet());
            for (Map.Entry<Kategori,Double> e : entries) {
                double sw=(e.getValue()/total)*360;
                g2.setColor(hexToColor(e.getKey().getRenk()));
                g2.fillArc(x0,y0,diam,diam,(int)startAngle,(int)Math.ceil(sw));
                g2.setColor(BG); g2.setStroke(new BasicStroke(1.2f));
                double ang=Math.toRadians(startAngle);
                g2.drawLine(cx,cy,cx+(int)(diam/2*Math.cos(ang)),cy-(int)(diam/2*Math.sin(ang)));
                startAngle+=sw;
            }
            int inner=diam*55/100;
            g2.setColor(CARD); g2.fillOval(cx-inner/2,cy-inner/2,inner,inner);
            g2.setFont(new Font("SansSerif",Font.BOLD,9)); g2.setColor(accentColor);
            String ts=ParaFormatlayici.formatlaKisa(total); FontMetrics fm=g2.getFontMetrics();
            g2.drawString(ts,cx-fm.stringWidth(ts)/2,cy+3);
            int legX=diam+12, legY=6; g2.setFont(F_TINY);
            for (int i=0;i<Math.min(entries.size(),6);i++) {
                Color rc=hexToColor(entries.get(i).getKey().getRenk());
                g2.setColor(rc); g2.fillRoundRect(legX,legY+i*14,6,6,2,2);
                g2.setColor(TEXT_MID);
                String ls=entries.get(i).getKey().getAd();
                if(ls.length()>9) ls=ls.substring(0,8)+"…";
                double pct=(entries.get(i).getValue()/total)*100;
                g2.drawString(ls+String.format(" %.0f%%",pct),legX+9,legY+i*14+6);
            }
            g2.dispose();
        }
    }

    // ── BÜTÇE / HEDEF ───────────────────────────────────────────────
    private void butceDurumuGuncelle(Map<Kategori, Double> giderDagilim) {
        if (butceSatirlarPanel == null) return;
        butceSatirlarPanel.removeAll();
        if (butceDenetleyici == null) {
            butceSatirlarPanel.add(bosLbl("Bütçe limiti belirlenmemiş"));
        } else {
            List<model.ButceLimiti> limitler = butceDenetleyici.limitleriGetir();
            if (limitler.isEmpty()) {
                butceSatirlarPanel.add(bosLbl("Henüz bütçe limiti yok — Bütçe menüsünden ekleyin"));
            } else {
                Map<String,Kategori> idMap = new java.util.LinkedHashMap<>();
                for (Kategori k : giderDagilim.keySet()) idMap.put(k.getId(), k);
                for (Kategori k : kategoriDenetleyici.kategorileriGetir()) idMap.putIfAbsent(k.getId(), k);
                for (model.ButceLimiti bl : limitler) {
                    Kategori kat = idMap.get(bl.getKategoriId()); if(kat==null) continue;
                    double harcanan = giderDagilim.getOrDefault(kat, 0.0);
                    butceSatirlarPanel.add(butceKalemi(kat, harcanan, bl.getAylikLimit()));
                    butceSatirlarPanel.add(Box.createVerticalStrut(3));
                }
            }
        }
        butceSatirlarPanel.revalidate(); butceSatirlarPanel.repaint();
    }

    private void hedefDurumuGuncelle() {
        if (hedefSatirlarPanel == null) return;
        hedefSatirlarPanel.removeAll();
        if (hedefTasarrufDenetleyici == null) {
            hedefSatirlarPanel.add(bosLbl("Hedef modülü yüklenmedi"));
        } else {
            List<model.HedefTasarruf> hedefler = hedefTasarrufDenetleyici.listele();
            if (hedefler.isEmpty()) {
                hedefSatirlarPanel.add(bosLbl("Henüz hedef yok — Hedefler menüsünden ekleyin"));
            } else {
                for (model.HedefTasarruf h : hedefler) {
                    hedefSatirlarPanel.add(hedefKalemi(h));
                    hedefSatirlarPanel.add(Box.createVerticalStrut(3));
                }
            }
        }
        hedefSatirlarPanel.revalidate(); hedefSatirlarPanel.repaint();
    }

    private JPanel butceKalemi(Kategori kat, double harcanan, double limit) {
        double pct=limit>0?Math.min(100.0,harcanan/limit*100.0):0, kalan=Math.max(0,limit-harcanan);
        Color bar=pct>=100?RED:pct>=80?GOLD:GREEN, kr=hexToColor(kat.getRenk());
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        JPanel ust=new JPanel(new BorderLayout()); ust.setOpaque(false);
        ust.add(lbl("● "+kat.getAd(), F_SMALL, kr), BorderLayout.WEST);
        ust.add(lbl(String.format("%.0f%%",pct), F_SMALL, bar), BorderLayout.EAST);
        p.add(ust, BorderLayout.NORTH);
        p.add(lbl(ParaFormatlayici.formatlaKisa(harcanan)+" / "+ParaFormatlayici.formatlaKisa(limit)
            +"  —  "+ParaFormatlayici.formatlaKisa(kalan)+" kaldı", F_TINY, TEXT_MID), BorderLayout.CENTER);
        p.add(progresBar(pct, bar), BorderLayout.SOUTH);
        return p;
    }

    private JPanel hedefKalemi(model.HedefTasarruf h) {
        double pct=h.yuzde(), kalan=Math.max(0,h.getHedefMiktar()-h.getBirikilenMiktar());
        Color bar=h.tamamlandi()?GREEN:BLUE;
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        JPanel ust=new JPanel(new BorderLayout()); ust.setOpaque(false);
        ust.add(lbl(h.tamamlandi()?"✓ "+h.getAd():h.getAd(), F_SMALL, h.tamamlandi()?GREEN:TEXT), BorderLayout.WEST);
        ust.add(lbl(String.format("%.0f%%",pct), F_SMALL, bar), BorderLayout.EAST);
        String bilgi=h.tamamlandi()
            ? ParaFormatlayici.formatlaKisa(h.getBirikilenMiktar())+" / "+ParaFormatlayici.formatlaKisa(h.getHedefMiktar())+"  —  Tamamlandı!"
            : ParaFormatlayici.formatlaKisa(h.getBirikilenMiktar())+" / "+ParaFormatlayici.formatlaKisa(h.getHedefMiktar())+"  —  "+ParaFormatlayici.formatlaKisa(kalan)+" kaldı";
        p.add(ust, BorderLayout.NORTH);
        p.add(lbl(bilgi, F_TINY, TEXT_MID), BorderLayout.CENTER);
        p.add(progresBar(pct, bar), BorderLayout.SOUTH);
        return p;
    }

    private JPanel progresBar(double pct, Color renk) {
        final double fp = Math.min(100.0, pct);
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int bh=5, y=(getHeight()-bh)/2;
                g2.setColor(BORDER); g2.fillRoundRect(0,y,getWidth(),bh,bh,bh);
                int fw=(int)(getWidth()*fp/100.0);
                if(fw>2){g2.setPaint(new GradientPaint(0,0,renk.brighter(),fw,0,renk.darker()));g2.fillRoundRect(0,y,fw,bh,bh,bh);}
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 10));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        return bar;
    }

    private JLabel bosLbl(String msg) {
        JLabel l = lbl(msg, F_TINY, TEXT_DIM);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(3, 2, 0, 0));
        return l;
    }

    // ── YARDIMCI GÖRSEL BİLEŞENLER ──────────────────────────────────
    private JPanel kartSargi(String baslik, JComponent icerik) {
        JPanel kart = new JPanel(new BorderLayout(0, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(BORDER); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12); g2.dispose();
            }
        };
        kart.setOpaque(false);
        kart.setBorder(BorderFactory.createEmptyBorder(7, 9, 7, 9));
        kart.add(lbl(baslik, F_H2, TEXT), BorderLayout.NORTH);
        kart.add(icerik, BorderLayout.CENTER);
        return kart;
    }

    private JPanel darkPanel(LayoutManager lm) {
        JPanel p = new JPanel(lm) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(BG); g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setOpaque(true); return p;
    }

    private JLabel lbl(String t, Font f, Color c) {
        JLabel l = new JLabel(t); l.setFont(f); l.setForeground(c); l.setOpaque(false); return l;
    }

    private JButton roundBtn(String txt, Color bg, Color fg) {
        JButton b = new JButton(txt) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?bg.brighter():bg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8); g2.dispose();
                FontMetrics fm=g.getFontMetrics(getFont());
                g.setFont(getFont()); g.setColor(getForeground());
                g.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        b.setFont(F_SMALL); b.setForeground(fg); b.setBackground(bg);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setOpaque(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static Color hexToColor(String hex) {
        try {
            String h=(hex==null||hex.isEmpty())?"3B82F6":hex;
            if(h.startsWith("#")) h=h.substring(1);
            return new Color(Integer.parseInt(h.substring(0,2),16),Integer.parseInt(h.substring(2,4),16),Integer.parseInt(h.substring(4,6),16));
        } catch(Exception e) { return new Color(59,130,246); }
    }

    private void bildirim(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "Bilgi", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── WRAP LAYOUT ─────────────────────────────────────────────────
    static class WrapLayout extends FlowLayout {
        private static final long serialVersionUID = 1L;
        WrapLayout(int align, int hgap, int vgap) { super(align,hgap,vgap); }
        @Override public Dimension preferredLayoutSize(Container t) { return layoutSize(t,true); }
        @Override public Dimension minimumLayoutSize(Container t)   { return layoutSize(t,false); }
        private Dimension layoutSize(Container target, boolean pref) {
            synchronized(target.getTreeLock()) {
                int w=target.getWidth(); if(w==0) w=Integer.MAX_VALUE;
                Insets ins=target.getInsets(); int maxW=w-(ins.left+ins.right+getHgap()*2);
                int height=getVgap(), rowW=0, rowH=0;
                for(int i=0;i<target.getComponentCount();i++){
                    Component m=target.getComponent(i); if(!m.isVisible()) continue;
                    Dimension d=pref?m.getPreferredSize():m.getMinimumSize();
                    if(rowW+d.width>maxW){height+=rowH+getVgap();rowW=0;rowH=0;}
                    rowW+=d.width+getHgap(); rowH=Math.max(rowH,d.height);
                }
                height+=rowH+ins.top+ins.bottom+getVgap()*2;
                return new Dimension(w,height);
            }
        }
    }
}
