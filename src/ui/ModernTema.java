package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * SaveFlow Modern UI Tema — tüm ekranlarda kullanılan ortak stiller.
 */
public class ModernTema {

    // ── Renk Paleti ──────────────────────────────────────────────
    public static final Color BG_KOYU       = new Color(10, 14, 26);
    public static final Color BG_KART       = new Color(18, 24, 42);
    public static final Color BG_PANEL      = new Color(24, 32, 56);
    public static final Color VURGU_MAVI    = new Color(59, 130, 246);
    public static final Color VURGU_ACIK    = new Color(99, 179, 255);
    public static final Color GELIR_YESIL   = new Color(52, 211, 153);
    public static final Color GIDER_KIRMIZI = new Color(251, 113, 133);
    public static final Color METIN_BEYAZ   = new Color(241, 245, 249);
    public static final Color METIN_GRI     = new Color(148, 163, 184);
    public static final Color SINIR_RENGI   = new Color(51, 65, 85);
    public static final Color HOVER_RENGI   = new Color(30, 41, 70);
    public static final Color ALTIN         = new Color(251, 191, 36);

    // ── Fontlar ───────────────────────────────────────────────────
    public static final Font FONT_BASLIK_BYK = new Font("SansSerif", Font.BOLD, 28);
    public static final Font FONT_BASLIK     = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_ALT_BASLIK = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_NORMAL     = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_KUCUK      = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_MONO       = new Font("Monospaced", Font.PLAIN, 12);

    /**
     * Sistem L&F KULLANMADAN doğrudan cross-platform (Metal) ile başla,
     * ardından tüm UI key'lerini koyu tema ile doldur.
     * Bu sayede Windows'un beyaz arka planı ezilir.
     */
    public static void uygula() {
        try {
            // Metal (cross-platform) L&F — Windows L&F'yi devre dışı bırakır
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception ignored) {}

        // Panel / Dialog arka planları
        UIManager.put("Panel.background",                BG_KOYU);
        UIManager.put("Panel.foreground",                METIN_BEYAZ);
        UIManager.put("RootPane.background",             BG_KOYU);
        UIManager.put("Frame.background",                BG_KOYU);
        UIManager.put("ContentPane.background",          BG_KOYU);

        UIManager.put("OptionPane.background",           BG_KART);
        UIManager.put("OptionPane.messageForeground",    METIN_BEYAZ);

        // Butonlar
        UIManager.put("Button.background",               VURGU_MAVI);
        UIManager.put("Button.foreground",               Color.WHITE);
        UIManager.put("Button.focus",                    new Color(0,0,0,0));
        UIManager.put("Button.select",                   VURGU_MAVI.darker());

        // Metin alanları
        UIManager.put("TextField.background",            BG_PANEL);
        UIManager.put("TextField.foreground",            METIN_BEYAZ);
        UIManager.put("TextField.caretForeground",       VURGU_ACIK);
        UIManager.put("TextField.selectionBackground",   VURGU_MAVI);
        UIManager.put("TextField.selectionForeground",   Color.WHITE);
        UIManager.put("TextField.inactiveForeground",    METIN_GRI);

        UIManager.put("PasswordField.background",        BG_PANEL);
        UIManager.put("PasswordField.foreground",        METIN_BEYAZ);
        UIManager.put("PasswordField.caretForeground",   VURGU_ACIK);
        UIManager.put("PasswordField.selectionBackground", VURGU_MAVI);
        UIManager.put("PasswordField.selectionForeground", Color.WHITE);

        UIManager.put("TextArea.background",             BG_PANEL);
        UIManager.put("TextArea.foreground",             METIN_BEYAZ);
        UIManager.put("TextArea.caretForeground",        VURGU_ACIK);
        UIManager.put("TextArea.selectionBackground",    VURGU_MAVI);
        UIManager.put("TextArea.selectionForeground",    Color.WHITE);
        UIManager.put("TextArea.inactiveForeground",     METIN_GRI);

        // Etiketler
        UIManager.put("Label.foreground",                METIN_BEYAZ);
        UIManager.put("Label.background",                BG_KOYU);

        // ComboBox
        UIManager.put("ComboBox.background",             BG_PANEL);
        UIManager.put("ComboBox.foreground",             METIN_BEYAZ);
        UIManager.put("ComboBox.selectionBackground",    VURGU_MAVI);
        UIManager.put("ComboBox.selectionForeground",    Color.WHITE);
        UIManager.put("ComboBox.buttonBackground",       BG_PANEL);

        // List
        UIManager.put("List.background",                 BG_PANEL);
        UIManager.put("List.foreground",                 METIN_BEYAZ);
        UIManager.put("List.selectionBackground",        VURGU_MAVI);
        UIManager.put("List.selectionForeground",        Color.WHITE);

        // Tablo
        UIManager.put("Table.background",                BG_KART);
        UIManager.put("Table.foreground",                METIN_BEYAZ);
        UIManager.put("Table.gridColor",                 SINIR_RENGI);
        UIManager.put("Table.selectionBackground",       new Color(59, 130, 246, 80));
        UIManager.put("Table.selectionForeground",       METIN_BEYAZ);
        UIManager.put("TableHeader.background",          BG_PANEL);
        UIManager.put("TableHeader.foreground",          METIN_GRI);
        UIManager.put("TableHeader.cellBorder",          BorderFactory.createMatteBorder(0,0,1,0,SINIR_RENGI));

        // ScrollPane / ScrollBar
        UIManager.put("ScrollPane.background",           BG_KART);
        UIManager.put("ScrollBar.background",            BG_KART);
        UIManager.put("ScrollBar.thumb",                 SINIR_RENGI);
        UIManager.put("ScrollBar.thumbHighlight",        SINIR_RENGI);
        UIManager.put("ScrollBar.thumbDarkShadow",       BG_KOYU);
        UIManager.put("ScrollBar.track",                 BG_KART);
        UIManager.put("ScrollBar.trackHighlight",        BG_KART);
        UIManager.put("Viewport.background",             BG_KART);

        // Menü
        UIManager.put("MenuBar.background",              BG_KART);
        UIManager.put("MenuBar.foreground",              METIN_BEYAZ);
        UIManager.put("Menu.background",                 BG_KART);
        UIManager.put("Menu.foreground",                 METIN_BEYAZ);
        UIManager.put("Menu.selectionBackground",        VURGU_MAVI);
        UIManager.put("Menu.selectionForeground",        Color.WHITE);
        UIManager.put("MenuItem.background",             BG_KART);
        UIManager.put("MenuItem.foreground",             METIN_BEYAZ);
        UIManager.put("MenuItem.selectionBackground",    VURGU_MAVI);
        UIManager.put("MenuItem.selectionForeground",    Color.WHITE);
        UIManager.put("PopupMenu.background",            BG_KART);
        UIManager.put("PopupMenu.foreground",            METIN_BEYAZ);
        UIManager.put("PopupMenu.border",                BorderFactory.createLineBorder(SINIR_RENGI));
        UIManager.put("Separator.foreground",            SINIR_RENGI);
        UIManager.put("Separator.background",            BG_KART);

        // Dialog / Window
        UIManager.put("Dialog.background",               BG_KOYU);
        UIManager.put("Window.background",               BG_KOYU);

        // CheckBox / RadioButton
        UIManager.put("CheckBox.background",             BG_KOYU);
        UIManager.put("CheckBox.foreground",             METIN_BEYAZ);
        UIManager.put("RadioButton.background",          BG_KOYU);
        UIManager.put("RadioButton.foreground",          METIN_BEYAZ);

        // ToolTip
        UIManager.put("ToolTip.background",              BG_PANEL);
        UIManager.put("ToolTip.foreground",              METIN_BEYAZ);
        UIManager.put("ToolTip.border",                  BorderFactory.createLineBorder(SINIR_RENGI));

        // Metal tema özel ayarları
        UIManager.put("control",                         BG_KOYU);
        UIManager.put("controlHighlight",                BG_PANEL);
        UIManager.put("controlLtHighlight",              BG_PANEL);
        UIManager.put("controlShadow",                   SINIR_RENGI);
        UIManager.put("controlDkShadow",                 BG_KOYU);
        UIManager.put("controlText",                     METIN_BEYAZ);
        UIManager.put("activeCaption",                   BG_PANEL);
        UIManager.put("activeCaptionText",               METIN_BEYAZ);
        UIManager.put("inactiveCaption",                 BG_KOYU);
        UIManager.put("inactiveCaptionText",             METIN_GRI);
        UIManager.put("window",                          BG_KOYU);
        UIManager.put("windowText",                      METIN_BEYAZ);
        UIManager.put("menu",                            BG_KART);
        UIManager.put("menuText",                        METIN_BEYAZ);
        UIManager.put("text",                            BG_PANEL);
        UIManager.put("textText",                        METIN_BEYAZ);
        UIManager.put("textHighlight",                   VURGU_MAVI);
        UIManager.put("textHighlightText",               Color.WHITE);
        UIManager.put("textInactiveText",                METIN_GRI);
        UIManager.put("desktop",                         BG_KOYU);
    }

    // ── Hazır Bileşenler ─────────────────────────────────────────

    /** Modern yuvarlak buton */
    public static JButton modernButon(String metin, Color bg) {
        JButton btn = new JButton(metin) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color renk = getModel().isPressed() ? bg.darker()
                           : getModel().isRollover() ? bg.brighter()
                           : bg;
                g2.setColor(renk);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(255,255,255,30));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
                // Metni elle çiz (üst sınıf fillRect'ini atla)
                FontMetrics fm = g.getFontMetrics(getFont());
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g.setFont(getFont());
                g.setColor(getForeground());
                g.drawString(getText(), tx, ty);
            }
        };
        btn.setFont(FONT_ALT_BASLIK);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 24, 40));
        return btn;
    }

    /** Şeffaf link buton */
    public static JButton linkButon(String metin) {
        JButton btn = new JButton(metin);
        btn.setFont(FONT_NORMAL);
        btn.setForeground(VURGU_ACIK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(METIN_BEYAZ); }
            public void mouseExited(MouseEvent e)  { btn.setForeground(VURGU_ACIK); }
        });
        return btn;
    }

    /** Modern metin alanı */
    public static JTextField modernTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(FONT_NORMAL);
        tf.setForeground(METIN_BEYAZ);
        tf.setBackground(BG_PANEL);
        tf.setCaretColor(VURGU_ACIK);
        tf.setOpaque(true);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SINIR_RENGI, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return tf;
    }

    /** Modern şifre alanı */
    public static JPasswordField modernPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_NORMAL);
        pf.setForeground(METIN_BEYAZ);
        pf.setBackground(BG_PANEL);
        pf.setCaretColor(VURGU_ACIK);
        pf.setOpaque(true);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SINIR_RENGI, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return pf;
    }

    /** Modern ComboBox */
    public static <T> JComboBox<T> modernComboBox(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setFont(FONT_NORMAL);
        cb.setBackground(BG_PANEL);
        cb.setForeground(METIN_BEYAZ);
        cb.setOpaque(true);
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? VURGU_MAVI : BG_PANEL);
                setForeground(METIN_BEYAZ);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                setOpaque(true);
                return this;
            }
        });
        return cb;
    }

    /** Etiket fabrikası */
    public static JLabel etiket(String metin, Font font, Color renk) {
        JLabel lbl = new JLabel(metin);
        lbl.setFont(font);
        lbl.setForeground(renk);
        lbl.setOpaque(false);
        return lbl;
    }

    /**
     * Kart paneli — arka planı elle çizer, opaque=false kalır.
     * Bu sayede Windows Metal L&F'nin beyaz fill'i geçersiz olur.
     */
    public static JPanel kartPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_KART);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(SINIR_RENGI);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    /**
     * Arka planı BG_KOYU ile doldurur.
     * JFrame ve JDialog content pane'lerine uygula.
     */
    public static JPanel kouyuPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(BG_KOYU);
                g.fillRect(0,0,getWidth(),getHeight());
                super.paintComponent(g);
            }
        };
        p.setOpaque(true);
        p.setBackground(BG_KOYU);
        return p;
    }

    /** Degradeli arka plan paneli */
    public static JPanel degradePanel(Color ust, Color alt) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ust, 0, getHeight(), alt);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        p.setOpaque(true);
        return p;
    }

    /** Yuvarlak kenarlık sınıfı */
    public static class RoundedBorder implements Border {
        private final Color renk;
        private final int radius;
        public RoundedBorder(Color renk, int radius) { this.renk = renk; this.radius = radius; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(renk);
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(4,4,4,4); }
        @Override public boolean isBorderOpaque() { return false; }
    }
}
