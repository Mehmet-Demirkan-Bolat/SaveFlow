package ui;

import controller.KategoriDenetleyici;
import controller.TekrarlananIslemDenetleyici;
import model.*;
import util.ParaFormatlayici;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TekrarlananIslemYonetimi {

    private static final Color BG     = new Color(8, 11, 22);
    private static final Color CARD   = new Color(15, 20, 40);
    private static final Color CARD2  = new Color(20, 28, 52);
    private static final Color BORDER_C = new Color(40, 52, 80);
    private static final Color BLUE   = new Color(59, 130, 246);
    private static final Color GREEN  = new Color(34, 197, 94);
    private static final Color RED    = new Color(239, 68, 68);
    private static final Color RED_D  = new Color(153, 27, 27);
    private static final Color GOLD   = new Color(251, 191, 36);
    private static final Color TEXT   = new Color(241, 245, 249);
    private static final Color TEXT_DIM = new Color(100, 116, 139);
    private static final Color TEXT_MID = new Color(148, 163, 184);

    private static final Font F_H1    = new Font("SansSerif", Font.BOLD, 18);
    private static final Font F_H2    = new Font("SansSerif", Font.BOLD, 13);
    private static final Font F_BODY  = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font F_SMALL = new Font("SansSerif", Font.BOLD, 11);

    private final JDialog dialog;
    private final TekrarlananIslemDenetleyici tekrarlananIslemDenetleyici;
    private final KategoriDenetleyici kategoriDenetleyici;
    private final Runnable yenileCallback;

    private DefaultTableModel tableModel;
    private JTable tablo;

    public TekrarlananIslemYonetimi(JFrame parent,
                                    TekrarlananIslemDenetleyici tekrarlananIslemDenetleyici,
                                    KategoriDenetleyici kategoriDenetleyici,
                                    Runnable yenileCallback) {
        this.tekrarlananIslemDenetleyici = tekrarlananIslemDenetleyici;
        this.kategoriDenetleyici = kategoriDenetleyici;
        this.yenileCallback = yenileCallback;

        dialog = new JDialog(parent, "🔄 Tekrarlanan İşlemler", true);
        dialog.setSize(680, 500);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(true);
        dialog.setContentPane(icerikOlustur());
        tabloYenile();
    }

    private JPanel icerikOlustur() {
        JPanel root = darkPanel(new BorderLayout(0, 12));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Üst başlık + buton
        JPanel ustPanel = new JPanel(new BorderLayout());
        ustPanel.setOpaque(false);
        JLabel baslik = lbl("Tekrarlanan İşlemler", F_H1, TEXT);
        JButton ekleBtn = roundBtn("+ Yeni Ekle", BLUE, Color.WHITE, 110, 36);
        ustPanel.add(baslik, BorderLayout.WEST);
        ustPanel.add(ekleBtn, BorderLayout.EAST);
        root.add(ustPanel, BorderLayout.NORTH);

        // Tablo
        String[] cols = {"TÜR", "BAŞLIK", "MİKTAR", "KATEGORİ", "TEKRAR", "SONRAKİ TARİH"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablo = new JTable(tableModel);
        tablo.setRowHeight(40);
        tablo.setShowGrid(false);
        tablo.setIntercellSpacing(new Dimension(0, 1));
        tablo.setBackground(CARD); tablo.setForeground(TEXT);
        tablo.setFont(F_BODY); tablo.setOpaque(true);
        tablo.setSelectionBackground(new Color(59, 130, 246, 60));
        tablo.setSelectionForeground(TEXT);
        tablo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader hdr = tablo.getTableHeader();
        hdr.setBackground(CARD2); hdr.setForeground(TEXT_DIM);
        hdr.setFont(F_SMALL);
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_C));
        hdr.setOpaque(true); hdr.setReorderingAllowed(false);

        tablo.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                Color rowBg = row % 2 == 0 ? CARD : new Color(18, 25, 46);
                setBackground(sel ? new Color(59, 130, 246, 50) : rowBg);
                setForeground(TEXT); setFont(F_BODY); setOpaque(true);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (col == 0 && v != null) {
                    boolean gelir = v.toString().startsWith("▲");
                    setForeground(gelir ? GREEN : RED);
                    setFont(F_SMALL);
                }
                if (col == 2 && v != null) {
                    Object turVal = t.getModel().getValueAt(row, 0);
                    boolean gelir = turVal != null && turVal.toString().startsWith("▲");
                    setForeground(gelir ? GREEN : RED);
                    setFont(new Font("SansSerif", Font.BOLD, 13));
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tablo);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_C));
        scroll.setBackground(CARD); scroll.getViewport().setBackground(CARD);
        root.add(scroll, BorderLayout.CENTER);

        // Alt butonlar
        JPanel altPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        altPanel.setOpaque(false);
        JButton silBtn = roundBtn("🗑 Sil", RED_D, new Color(255, 150, 150), 80, 34);
        JButton kapat = roundBtn("Kapat", CARD2, TEXT_MID, 80, 34);

        silBtn.addActionListener(e -> {
            int s = tablo.getSelectedRow();
            if (s < 0) { JOptionPane.showMessageDialog(dialog, "Bir işlem seçin."); return; }
            List<TekrarlananIslem> liste = tekrarlananIslemDenetleyici.listele();
            if (s < liste.size() &&
                JOptionPane.showConfirmDialog(dialog, "Seçili tekrarlanan işlemi silmek istiyor musunuz?",
                    "Onayla", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                tekrarlananIslemDenetleyici.sil(liste.get(s).getId());
                yenileCallback.run();
                tabloYenile();
            }
        });
        kapat.addActionListener(e -> dialog.dispose());

        altPanel.add(silBtn);
        altPanel.add(kapat);
        root.add(altPanel, BorderLayout.SOUTH);

        ekleBtn.addActionListener(e ->
            new TekrarlananIslemFormu(dialog, tekrarlananIslemDenetleyici,
                kategoriDenetleyici, () -> { yenileCallback.run(); tabloYenile(); }).goster());

        return root;
    }

    private void tabloYenile() {
        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.of("tr", "TR"));
        for (TekrarlananIslem ti : tekrarlananIslemDenetleyici.listele()) {
            boolean gelir = ti.getTur() == IslemTuru.GELIR;
            String tekrarTxt = switch (ti.getTekrarTipi()) {
                case GUNLUK  -> "Günlük";
                case HAFTALIK -> "Haftalık";
                case AYLIK   -> "Aylık";
            };
            tableModel.addRow(new Object[]{
                gelir ? "▲ Gelir" : "▼ Gider",
                ti.getBaslik(),
                ParaFormatlayici.formatla(ti.getMiktar()),
                ti.getKategori().getAd(),
                tekrarTxt,
                ti.getSonrakiTarih().format(fmt)
            });
        }
    }

    public void goster() { dialog.setVisible(true); }

    private JPanel darkPanel(LayoutManager lm) {
        JPanel p = new JPanel(lm); p.setBackground(BG); p.setOpaque(true); return p;
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
        b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setOpaque(false);
        b.setPreferredSize(new Dimension(w, h));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
