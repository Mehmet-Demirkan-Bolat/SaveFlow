package ui;

import controller.*;
import exception.GirisHatasi;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class GirisEkrani {
    private final JFrame cerceve;
    private final KullaniciDenetleyici kullaniciDenetleyici;
    private final IslemDenetleyici islemDenetleyici;
    private final KategoriDenetleyici kategoriDenetleyici;
    private ButceDenetleyici butceDenetleyici;
    private TekrarlananIslemDenetleyici tekrarlananIslemDenetleyici;
    private HedefTasarrufDenetleyici hedefTasarrufDenetleyici;

    /** Ana constructor — tüm denetleyicilerle */
    public GirisEkrani(KullaniciDenetleyici kullaniciDenetleyici,
                       IslemDenetleyici islemDenetleyici,
                       KategoriDenetleyici kategoriDenetleyici) {
        this.kullaniciDenetleyici = kullaniciDenetleyici;
        this.islemDenetleyici     = islemDenetleyici;
        this.kategoriDenetleyici  = kategoriDenetleyici;
        ModernTema.uygula();
        cerceve = new JFrame("SaveFlow — Giriş");
        cerceve.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cerceve.setSize(460, 560);
        cerceve.setLocationRelativeTo(null);
        cerceve.setResizable(false);

        JPanel cp = new JPanel(new BorderLayout());
        cp.setBackground(ModernTema.BG_KOYU);
        cp.setOpaque(true);
        cp.add(panelOlustur());
        cerceve.setContentPane(cp);
    }

    /** Eski Supplier tabanlı constructor — geriye dönük uyumluluk */
    public GirisEkrani(KullaniciDenetleyici kullaniciDenetleyici,
                       Supplier<AnaEkran> anaEkranFactory) {
        // Bu constructor'da islemDenetleyici ve kategoriDenetleyici yok.
        // Giriş sonrası AnaEkran fabrikası çağrılır.
        this.kullaniciDenetleyici = kullaniciDenetleyici;
        this.islemDenetleyici     = null;
        this.kategoriDenetleyici  = null;
        this._legacyFactory       = anaEkranFactory;
        ModernTema.uygula();
        cerceve = new JFrame("SaveFlow — Giriş");
        cerceve.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cerceve.setSize(460, 560);
        cerceve.setLocationRelativeTo(null);
        cerceve.setResizable(false);

        JPanel cp = new JPanel(new BorderLayout());
        cp.setBackground(ModernTema.BG_KOYU);
        cp.setOpaque(true);
        cp.add(panelOlustur());
        cerceve.setContentPane(cp);
    }

    private Supplier<AnaEkran> _legacyFactory = null;

    /** Parametresiz — sadece KullaniciDenetleyici ile (KayitEkrani'ndan dönerken) */
    public GirisEkrani(KullaniciDenetleyici kullaniciDenetleyici) {
        this(kullaniciDenetleyici, (IslemDenetleyici)null, (KategoriDenetleyici)null);
    }

    public void setEkDenetleyiciler(ButceDenetleyici bd,
                                    TekrarlananIslemDenetleyici tid,
                                    HedefTasarrufDenetleyici hd) {
        this.butceDenetleyici            = bd;
        this.tekrarlananIslemDenetleyici = tid;
        this.hedefTasarrufDenetleyici    = hd;
    }

    private void girisYapVeAc(String eposta, String sifre, JLabel hataLabel) {
        hataLabel.setText("");
        try {
            kullaniciDenetleyici.girisYap(eposta, sifre);
            cerceve.dispose();

            // Legacy fabrika varsa onu kullan
            if (_legacyFactory != null) {
                AnaEkran ae = _legacyFactory.get();
                if (ae != null) { ae.goster(); return; }
            }

            // Yoksa kendi denetleyicilerimizle AnaEkran oluştur
            if (islemDenetleyici != null && kategoriDenetleyici != null) {
                AnaEkran ae = new AnaEkran(kullaniciDenetleyici);
                ae.setDenetleyiciler(islemDenetleyici, kategoriDenetleyici);
                ae.setEkDenetleyiciler(butceDenetleyici,
                    tekrarlananIslemDenetleyici, hedefTasarrufDenetleyici);
                ae.goster();
                return;
            }

            // Hiçbiri yoksa — Main'den gelmesi gereken durum, uyarı ver
            JOptionPane.showMessageDialog(null,
                "Denetleyiciler bulunamadı. Lütfen programı yeniden başlatın.",
                "Hata", JOptionPane.ERROR_MESSAGE);

        } catch (GirisHatasi ex) {
            hataLabel.setText("⚠  " + ex.getMessage());
        }
    }

    private JPanel panelOlustur() {
        JPanel ana = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ModernTema.BG_KOYU); g2.fillRect(0,0,getWidth(),getHeight()); g2.dispose();
            }
        };
        ana.setOpaque(true);

        JPanel kart = ModernTema.kartPanel();
        kart.setLayout(new BoxLayout(kart, BoxLayout.Y_AXIS));
        kart.setBorder(BorderFactory.createEmptyBorder(40, 44, 40, 44));
        kart.setPreferredSize(new Dimension(360, 450));

        JLabel logo = new JLabel("💰");
        logo.setFont(new Font("SansSerif", Font.PLAIN, 48));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel baslik = ModernTema.etiket("SaveFlow", ModernTema.FONT_BASLIK_BYK, ModernTema.METIN_BEYAZ);
        baslik.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel altYazi = ModernTema.etiket("Kişisel Finans Yönetimi", ModernTema.FONT_KUCUK, ModernTema.METIN_GRI);
        altYazi.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel epostaPanel = alanOlustur("E-POSTA");
        JTextField epostaAlan = ModernTema.modernTextField("");
        epostaAlan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        epostaPanel.add(epostaAlan);

        JPanel sifrePanel = alanOlustur("ŞİFRE");
        JPasswordField sifreAlan = ModernTema.modernPasswordField();
        sifreAlan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        sifrePanel.add(sifreAlan);

        JButton girisBtn = ModernTema.modernButon("Giriş Yap", ModernTema.VURGU_MAVI);
        girisBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        girisBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JLabel hataLabel = ModernTema.etiket("", ModernTema.FONT_KUCUK, ModernTema.GIDER_KIRMIZI);
        hataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton kayitBtn = ModernTema.linkButon("Hesabın yok mu? Kayıt ol →");
        kayitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        kart.add(logo);
        kart.add(Box.createVerticalStrut(10));
        kart.add(baslik);
        kart.add(Box.createVerticalStrut(4));
        kart.add(altYazi);
        kart.add(Box.createVerticalStrut(32));
        kart.add(epostaPanel);
        kart.add(Box.createVerticalStrut(14));
        kart.add(sifrePanel);
        kart.add(Box.createVerticalStrut(8));
        kart.add(hataLabel);
        kart.add(Box.createVerticalStrut(18));
        kart.add(girisBtn);
        kart.add(Box.createVerticalStrut(20));
        kart.add(kayitBtn);

        ana.add(kart);

        girisBtn.addActionListener(e ->
            girisYapVeAc(epostaAlan.getText().trim(), new String(sifreAlan.getPassword()), hataLabel));
        sifreAlan.addActionListener(e ->
            girisYapVeAc(epostaAlan.getText().trim(), new String(sifreAlan.getPassword()), hataLabel));
        epostaAlan.addActionListener(e -> sifreAlan.requestFocus());

        kayitBtn.addActionListener(e -> {
            cerceve.dispose();
            KayitEkrani ke = new KayitEkrani(kullaniciDenetleyici, islemDenetleyici, kategoriDenetleyici);
            ke.setEkDenetleyiciler(butceDenetleyici, tekrarlananIslemDenetleyici, hedefTasarrufDenetleyici);
            ke.goster();
        });

        return ana;
    }

    private JPanel alanOlustur(String etiketMetin) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel lbl = ModernTema.etiket(etiketMetin, new Font("SansSerif", Font.BOLD, 10), ModernTema.METIN_GRI);
        lbl.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
        panel.add(lbl);
        return panel;
    }

    public void goster() { SwingUtilities.invokeLater(() -> cerceve.setVisible(true)); }
}
