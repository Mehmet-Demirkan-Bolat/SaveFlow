package ui;

import controller.KategoriDenetleyici;
import model.IslemTuru;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class KategoriFormu {
    private static final String[] PALET = {
        "#3B82F6","#22C55E","#EF4444","#A855F7","#F97316",
        "#14B8A6","#EC4899","#FBBF24","#60A5FA","#10B981",
        "#F59E0B","#8B5CF6"
    };

    private final JDialog dialog;
    private final KategoriDenetleyici kategoriDenetleyici;
    private final Runnable yenileCallback;
    private String secilenRenk = PALET[0];

    public KategoriFormu(JFrame parent, KategoriDenetleyici kategoriDenetleyici, Runnable yenileCallback) {
        this.kategoriDenetleyici = kategoriDenetleyici;
        this.yenileCallback      = yenileCallback;
        dialog = new JDialog(parent, "Yeni Kategori", true);
        dialog.setSize(400, 430);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);

        JPanel cp = new JPanel(new BorderLayout());
        cp.setBackground(ModernTema.BG_KOYU);
        cp.setOpaque(true);
        cp.add(panelOlustur());
        dialog.setContentPane(cp);
    }

    private JPanel panelOlustur() {
        JPanel ana = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(ModernTema.BG_KOYU); g.fillRect(0,0,getWidth(),getHeight());
            }
        };
        ana.setOpaque(true);
        ana.setLayout(new BoxLayout(ana, BoxLayout.Y_AXIS));
        ana.setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));

        JLabel baslik = ModernTema.etiket("🏷  Yeni Kategori", ModernTema.FONT_BASLIK, ModernTema.METIN_BEYAZ);
        baslik.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel altYazi = ModernTema.etiket("Kategori bilgilerini giriniz", ModernTema.FONT_KUCUK, ModernTema.METIN_GRI);
        altYazi.setAlignmentX(Component.LEFT_ALIGNMENT);
        altYazi.setBorder(BorderFactory.createEmptyBorder(4,0,24,0));

        JLabel adLabel = etiket("KATEGORİ ADI");
        JTextField adAlan = ModernTema.modernTextField("");
        adAlan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        adAlan.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel turLbl = etiket("TÜRÜ");
        JComboBox<IslemTuru> turCombo = ModernTema.modernComboBox(IslemTuru.values());
        turCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        turCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel renkLbl = etiket("RENK");
        JPanel renkPaleti = olusturRenkPaleti();
        renkPaleti.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hataLabel = ModernTema.etiket("", ModernTema.FONT_KUCUK, ModernTema.GIDER_KIRMIZI);
        hataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton ekleBtn = ModernTema.modernButon("  ✓  Kategori Ekle  ", ModernTema.ALTIN);
        ekleBtn.setForeground(new Color(30, 20, 0));
        ekleBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        ekleBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        ana.add(baslik);
        ana.add(altYazi);
        ana.add(adLabel);   ana.add(Box.createVerticalStrut(5));
        ana.add(adAlan);    ana.add(Box.createVerticalStrut(16));
        ana.add(turLbl);    ana.add(Box.createVerticalStrut(5));
        ana.add(turCombo);  ana.add(Box.createVerticalStrut(16));
        ana.add(renkLbl);   ana.add(Box.createVerticalStrut(8));
        ana.add(renkPaleti); ana.add(Box.createVerticalStrut(10));
        ana.add(hataLabel); ana.add(Box.createVerticalStrut(20));
        ana.add(ekleBtn);

        ekleBtn.addActionListener(e -> {
            String ad = adAlan.getText().trim();
            if (ad.isEmpty()) { hataLabel.setText("⚠  Kategori adı boş olamaz."); return; }
            kategoriDenetleyici.kategoriEkle(ad, (IslemTuru) turCombo.getSelectedItem(), secilenRenk);
            yenileCallback.run();
            dialog.dispose();
        });

        return ana;
    }

    private JPanel olusturRenkPaleti() {
        JPanel paletPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        paletPanel.setOpaque(false);
        paletPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel[] daireler = new JLabel[PALET.length];
        for (int i = 0; i < PALET.length; i++) {
            final String hex = PALET[i];
            final int idx = i;
            Color renk = hexToColor(hex);
            JLabel daire = new JLabel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(renk);
                    g2.fillOval(2, 2, getWidth()-4, getHeight()-4);
                    if (hex.equals(secilenRenk)) {
                        g2.setColor(Color.WHITE);
                        g2.setStroke(new BasicStroke(2f));
                        g2.drawOval(1, 1, getWidth()-3, getHeight()-3);
                    }
                    g2.dispose();
                }
            };
            daire.setPreferredSize(new Dimension(26, 26));
            daire.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            daire.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    secilenRenk = hex;
                    for (JLabel d : daireler) d.repaint();
                }
            });
            daireler[i] = daire;
            paletPanel.add(daire);
        }
        return paletPanel;
    }

    private static Color hexToColor(String hex) {
        try {
            String h = hex.startsWith("#") ? hex.substring(1) : hex;
            return new Color(
                Integer.parseInt(h.substring(0,2),16),
                Integer.parseInt(h.substring(2,4),16),
                Integer.parseInt(h.substring(4,6),16));
        } catch (Exception e) { return Color.BLUE; }
    }

    private JLabel etiket(String metin) {
        JLabel lbl = ModernTema.etiket(metin, new Font("SansSerif", Font.BOLD, 10), ModernTema.METIN_GRI);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    public void goster() { dialog.setVisible(true); }
}
