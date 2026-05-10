package ui;

import controller.*;
import exception.KayitHatasi;

import javax.swing.*;
import java.awt.*;

public class KayitEkrani {
    private final JFrame cerceve;
    private final KullaniciDenetleyici kullaniciDenetleyici;
    private final IslemDenetleyici islemDenetleyici;
    private final KategoriDenetleyici kategoriDenetleyici;
    private ButceDenetleyici butceDenetleyici;
    private TekrarlananIslemDenetleyici tekrarlananIslemDenetleyici;
    private HedefTasarrufDenetleyici hedefTasarrufDenetleyici;

    public KayitEkrani(KullaniciDenetleyici kullaniciDenetleyici,
                       IslemDenetleyici islemDenetleyici,
                       KategoriDenetleyici kategoriDenetleyici) {
        this.kullaniciDenetleyici = kullaniciDenetleyici;
        this.islemDenetleyici     = islemDenetleyici;
        this.kategoriDenetleyici  = kategoriDenetleyici;
        ModernTema.uygula();
        cerceve = new JFrame("SaveFlow — Kayıt Ol");
        cerceve.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cerceve.setSize(460, 600);
        cerceve.setLocationRelativeTo(null);
        cerceve.setResizable(false);

        JPanel cp = new JPanel(new BorderLayout());
        cp.setBackground(ModernTema.BG_KOYU);
        cp.setOpaque(true);
        cp.add(panelOlustur());
        cerceve.setContentPane(cp);
    }

    /** Eski imza — geriye dönük uyumluluk */
    public KayitEkrani(KullaniciDenetleyici kullaniciDenetleyici) {
        this(kullaniciDenetleyici, null, null);
    }

    public void setEkDenetleyiciler(ButceDenetleyici bd,
                                    TekrarlananIslemDenetleyici tid,
                                    HedefTasarrufDenetleyici hd) {
        this.butceDenetleyici            = bd;
        this.tekrarlananIslemDenetleyici = tid;
        this.hedefTasarrufDenetleyici    = hd;
    }

    private JPanel panelOlustur() {
        JPanel ana = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(ModernTema.BG_KOYU); g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        ana.setOpaque(true);

        JPanel kart = ModernTema.kartPanel();
        kart.setLayout(new BoxLayout(kart, BoxLayout.Y_AXIS));
        kart.setBorder(BorderFactory.createEmptyBorder(40, 44, 40, 44));
        kart.setPreferredSize(new Dimension(360, 490));

        JLabel logo = new JLabel("📝");
        logo.setFont(new Font("SansSerif", Font.PLAIN, 42));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel baslik = ModernTema.etiket("Hesap Oluştur", ModernTema.FONT_BASLIK, ModernTema.METIN_BEYAZ);
        baslik.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel altYazi = ModernTema.etiket("SaveFlow'a hoş geldiniz", ModernTema.FONT_KUCUK, ModernTema.METIN_GRI);
        altYazi.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel adPanel = alanOlustur("AD SOYAD");
        JTextField adAlan = ModernTema.modernTextField("");
        adAlan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        adPanel.add(adAlan);

        JPanel epostaPanel = alanOlustur("E-POSTA");
        JTextField epostaAlan = ModernTema.modernTextField("");
        epostaAlan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        epostaPanel.add(epostaAlan);

        JPanel sifrePanel = alanOlustur("ŞİFRE");
        JPasswordField sifreAlan = ModernTema.modernPasswordField();
        sifreAlan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        sifrePanel.add(sifreAlan);

        JLabel hataLabel = ModernTema.etiket("", ModernTema.FONT_KUCUK, ModernTema.GIDER_KIRMIZI);
        hataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton kayitBtn = ModernTema.modernButon("Kayıt Ol", new Color(52, 211, 153));
        kayitBtn.setForeground(new Color(10, 30, 20));
        kayitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        kayitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JButton geriBtn = ModernTema.linkButon("← Zaten hesabın var mı? Giriş yap");
        geriBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        kart.add(logo);
        kart.add(Box.createVerticalStrut(10));
        kart.add(baslik);
        kart.add(Box.createVerticalStrut(4));
        kart.add(altYazi);
        kart.add(Box.createVerticalStrut(28));
        kart.add(adPanel);
        kart.add(Box.createVerticalStrut(12));
        kart.add(epostaPanel);
        kart.add(Box.createVerticalStrut(12));
        kart.add(sifrePanel);
        kart.add(Box.createVerticalStrut(8));
        kart.add(hataLabel);
        kart.add(Box.createVerticalStrut(18));
        kart.add(kayitBtn);
        kart.add(Box.createVerticalStrut(16));
        kart.add(geriBtn);

        ana.add(kart);

        kayitBtn.addActionListener(e -> {
            String ad     = adAlan.getText().trim();
            String eposta = epostaAlan.getText().trim();
            String sifre  = new String(sifreAlan.getPassword());
            hataLabel.setText("");
            try {
                kullaniciDenetleyici.kayitOl(ad, eposta, sifre);
                JOptionPane.showMessageDialog(cerceve,
                    "✅  Kayıt başarılı! Giriş yapabilirsiniz.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                cerceve.dispose();
                GirisEkrani ge = new GirisEkrani(kullaniciDenetleyici, islemDenetleyici, kategoriDenetleyici);
                ge.setEkDenetleyiciler(butceDenetleyici, tekrarlananIslemDenetleyici, hedefTasarrufDenetleyici);
                ge.goster();
            } catch (KayitHatasi ex) {
                hataLabel.setText("⚠  " + ex.getMessage());
            }
        });

        geriBtn.addActionListener(e -> {
            cerceve.dispose();
            GirisEkrani ge = new GirisEkrani(kullaniciDenetleyici, islemDenetleyici, kategoriDenetleyici);
            ge.setEkDenetleyiciler(butceDenetleyici, tekrarlananIslemDenetleyici, hedefTasarrufDenetleyici);
            ge.goster();
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
