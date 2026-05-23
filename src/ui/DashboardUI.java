package ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Locale;

public class DashboardUI extends JFrame {

    // ── Renkler (HTML --pink:#C8607A temasından) ─────────────────────────────
    private static final Color PINK       = new Color(200,  96, 122);
    private static final Color PINK_H     = new Color(168,  64,  96);
    private static final Color PINK_L     = new Color(248, 238, 241);
    private static final Color BG         = new Color(253, 252, 249);
    private static final Color CARD       = Color.WHITE;
    private static final Color BORDER_COL = new Color(234, 230, 223);
    private static final Color TEXT       = new Color( 42,  42,  42);
    private static final Color MUTED      = new Color(154, 144, 128);
    private static final Color GREEN      = new Color( 39, 174,  96);
    private static final Color ORANGE     = new Color(230, 126,  34);
    private static final Color RED        = new Color(192,  57,  43);
    private static final Color HEADER_BG  = new Color(245, 242, 236);

    // ── Demo giriş bilgileri ──────────────────────────────────────────────────
    private static final String DEMO_VKN  = "1234567890";
    private static final String DEMO_PASS = "pureAcc2026";

    // ── Veri ─────────────────────────────────────────────────────────────────
    private static final Object[][] CUSTOMERS = {
        {"C001","Yıldız Makine A.Ş.",  "Müşteri",   "1112223334","info@yildizmakine.com",  "0212 555 10 10","₺24.500"},
        {"C002","Demir Çelik San. Ltd.","Müşteri",   "2223334445","muhasebe@demircelik.com","0312 444 20 20","₺8.900"},
        {"C003","Akar Tedarik Tic.",    "Tedarikçi", "3334445556","akar@akartedarik.com",   "0232 333 30 30","₺12.000 (Borç)"},
        {"C004","Güneş Enerji Yat.",    "Müşteri",   "4445556667","gunes@gunesenerji.com",  "0262 222 40 40","₺31.750"},
        {"C005","Polat Endüstri Ltd.",  "Tedarikçi", "5556667778","polat@polatend.com",      "0224 111 50 50","₺5.400 (Borç)"},
    };

    private static final Object[][] INVOICES = {
        {"INV-001","Yıldız Makine A.Ş.",  "2026-04-01","2026-04-30","₺18.500","Ödendi"},
        {"INV-002","Demir Çelik San. Ltd.","2026-04-10","2026-05-10","₺8.900", "Gecikti"},
        {"INV-003","Güneş Enerji Yat.",   "2026-04-15","2026-05-15","₺12.400","Vadesi Yaklaştı"},
        {"INV-004","Yıldız Makine A.Ş.",  "2026-05-01","2026-05-31","₺22.000","Beklemede"},
        {"INV-005","Akar Tedarik Tic.",   "2026-03-20","2026-04-20","₺6.750", "Gecikti"},
        {"INV-006","Polat Endüstri Ltd.", "2026-05-05","2026-06-05","₺9.200", "Taslak"},
        {"INV-007","Güneş Enerji Yat.",   "2026-05-10","2026-06-10","₺14.100","Gönderildi"},
    };

    private static final Object[][] TRANSACTIONS = {
        {"TX001","2026-04-05","GELİR","Satış Geliri",    "₺18.500"},
        {"TX002","2026-04-12","GİDER","İşçilik",         "₺5.200"},
        {"TX003","2026-04-18","GELİR","Hizmet Bedeli",   "₺12.400"},
        {"TX004","2026-04-25","GİDER","Hammadde",        "₺8.700"},
        {"TX005","2026-05-02","GELİR","Satış Geliri",    "₺22.000"},
        {"TX006","2026-05-08","GİDER","Kira & Faturalar","₺3.500"},
        {"TX007","2026-05-12","GELİR","Danışmanlık",     "₺9.200"},
        {"TX008","2026-05-14","GİDER","Lojistik",        "₺4.100"},
    };

    // ── Navigasyon ────────────────────────────────────────────────────────────
    private CardLayout cardLayout;
    private JPanel     contentPanel;
    private JButton    activeSideBtn;

    // ── Fatura formu state ───────────────────────────────────────────────────
    private JComboBox<String> custCombo;
    private JTextField fiTax, fiEmail, fiPhone, fiDate, fiDue;
    private JComboBox<String> vatCombo;
    private JPanel lineContainer;
    private JLabel lblSubtotal, lblVat, lblGrandTotal;
    private final java.util.List<double[]> lineData = new java.util.ArrayList<>();

    // ══════════════════════════════════════════════════════════════════════════
    public DashboardUI() {
        setTitle("PureAcc — Muhasebe ve Finans Yönetim Sistemi");
        setSize(1150, 700);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildNavbar(),  BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);
        setContentPane(root);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LOGIN
    // ══════════════════════════════════════════════════════════════════════════
    public void showLoginScreen() {
        JDialog dlg = new JDialog();
        dlg.setTitle("PureAcc — Sisteme Giriş");
        dlg.setSize(400, 440);
        dlg.setModal(true);
        dlg.setLocationRelativeTo(null);
        dlg.setResizable(false);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Ortalanmış kart
        JPanel bg = new JPanel(new GridBagLayout());
        bg.setBackground(new Color(244, 241, 235));

        JPanel card = new JPanel(null);
        card.setBackground(CARD);
        card.setPreferredSize(new Dimension(340, 380));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        JLabel logo = new JLabel("PureAcc", SwingConstants.CENTER);
        logo.setFont(new Font("Georgia", Font.BOLD, 30));
        logo.setForeground(PINK);
        logo.setBounds(20, 28, 300, 40);

        JLabel sub = new JLabel("<html><center>Sanayi İşletmeleri İçin<br>Muhasebe &amp; Finans Yönetim Sistemi</center></html>", SwingConstants.CENTER);
        sub.setFont(new Font("Arial", Font.PLAIN, 11));
        sub.setForeground(MUTED);
        sub.setBounds(20, 72, 300, 36);

        JLabel lVkn = new JLabel("Vergi Numarası");
        lVkn.setFont(new Font("Arial", Font.BOLD, 11));
        lVkn.setForeground(MUTED);
        lVkn.setBounds(30, 118, 280, 16);

        JTextField tfVkn = styledField("1234567890");
        tfVkn.setBounds(30, 136, 280, 36);

        JLabel lPass = new JLabel("Şifre");
        lPass.setFont(new Font("Arial", Font.BOLD, 11));
        lPass.setForeground(MUTED);
        lPass.setBounds(30, 182, 280, 16);

        JPasswordField pfPass = new JPasswordField();
        pfPass.setFont(new Font("Arial", Font.PLAIN, 13));
        pfPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        pfPass.setBounds(30, 200, 280, 36);

        JLabel lErr = new JLabel("Vergi numarası veya şifre hatalı.");
        lErr.setFont(new Font("Arial", Font.PLAIN, 11));
        lErr.setForeground(RED);
        lErr.setBounds(30, 244, 280, 16);
        lErr.setVisible(false);

        JButton btnLogin = pinkButton("Giriş Yap");
        btnLogin.setBounds(30, 268, 280, 40);

        JLabel demo = new JLabel("Demo  ·  VKN: 1234567890  |  Şifre: pureAcc2026", SwingConstants.CENTER);
        demo.setFont(new Font("Arial", Font.PLAIN, 10));
        demo.setForeground(new Color(200, 190, 180));
        demo.setBounds(20, 320, 300, 16);

        ActionListener loginAct = e -> {
            String v = tfVkn.getText().trim();
            String p = new String(pfPass.getPassword()).trim();
            if (DEMO_VKN.equals(v) && DEMO_PASS.equals(p)) {
                dlg.dispose();
                DashboardUI.this.setVisible(true);
            } else {
                lErr.setVisible(true);
            }
        };
        btnLogin.addActionListener(loginAct);
        pfPass.addActionListener(loginAct);

        card.add(logo); card.add(sub);
        card.add(lVkn); card.add(tfVkn);
        card.add(lPass); card.add(pfPass);
        card.add(lErr); card.add(btnLogin); card.add(demo);
        bg.add(card);
        dlg.add(bg);
        dlg.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  NAVBAR
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildNavbar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(CARD);
        nav.setPreferredSize(new Dimension(0, 54));
        nav.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL));

        JLabel logo = new JLabel("  PureAcc");
        logo.setFont(new Font("Georgia", Font.BOLD, 20));
        logo.setForeground(PINK);
        logo.setPreferredSize(new Dimension(160, 54));
        logo.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COL));
        nav.add(logo, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 14));
        right.setOpaque(false);
        JPanel avatar = new JPanel(new GridBagLayout());
        avatar.setBackground(PINK);
        avatar.setPreferredSize(new Dimension(30, 30));
        JLabel av = new JLabel("AK");
        av.setFont(new Font("Arial", Font.BOLD, 10));
        av.setForeground(Color.WHITE);
        avatar.add(av);
        JButton logout = new JButton("Çıkış");
        logout.setFont(new Font("Arial", Font.PLAIN, 11));
        logout.setForeground(MUTED);
        logout.setBackground(CARD);
        logout.setBorder(BorderFactory.createLineBorder(BORDER_COL, 1, true));
        logout.setFocusPainted(false);
        logout.addActionListener(e -> System.exit(0));
        right.add(avatar);
        right.add(logout);
        nav.add(right, BorderLayout.EAST);

        return nav;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  SIDEBAR
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(CARD);
        side.setPreferredSize(new Dimension(224, 0));
        side.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COL),
            BorderFactory.createEmptyBorder(14, 9, 14, 9)));

        side.add(sideGroup("Fatura Yönetimi"));
        side.add(sideBtn("➕  Fatura Oluştur",    "create-invoice", false));
        side.add(sideBtn("📋  Fatura Listesi",     "invoices",       false));
        side.add(sideBtn("⏰  Vadesi Gelenler",    "overdue",        false));
        side.add(Box.createVerticalStrut(10));
        side.add(sideGroup("Diğer Modüller"));
        JButton dash = sideBtn("📊  Dashboard",        "dashboard",      true);
        side.add(dash);
        activeSideBtn = dash;
        side.add(sideBtn("👤  Müşteriler",         "customers",      false));
        side.add(sideBtn("🔔  Hatırlatmalar",      "reminders",      false));
        side.add(sideBtn("📈  Raporlama",          "reports",        false));
        side.add(Box.createVerticalGlue());

        return side;
    }

    private JLabel sideGroup(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(new Font("Arial", Font.BOLD, 10));
        lbl.setForeground(new Color(192, 184, 172));
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 10, 6, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        return lbl;
    }

    private JButton sideBtn(String text, String card, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        applySideBtnStyle(btn, active);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != activeSideBtn) applySideBtnStyle(btn, true);
            }
            public void mouseExited(MouseEvent e) {
                if (btn != activeSideBtn) applySideBtnStyle(btn, false);
            }
        });

        btn.addActionListener(e -> {
            if (activeSideBtn != null) applySideBtnStyle(activeSideBtn, false);
            activeSideBtn = btn;
            applySideBtnStyle(btn, true);
            cardLayout.show(contentPanel, card);
        });

        return btn;
    }

    private void applySideBtnStyle(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(PINK_L);
            btn.setForeground(PINK);
            btn.setFont(new Font("Arial", Font.BOLD, 13));
        } else {
            btn.setBackground(CARD);
            btn.setForeground(new Color(100, 100, 100));
            btn.setFont(new Font("Arial", Font.PLAIN, 13));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CONTENT AREA
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildContent() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);

        contentPanel.add(buildDashboard(),      "dashboard");
        contentPanel.add(buildCreateInvoice(),  "create-invoice");
        contentPanel.add(buildInvoiceList(),    "invoices");
        contentPanel.add(buildCustomers(),      "customers");
        contentPanel.add(buildReminders(),      "reminders");
        contentPanel.add(buildReports(),        "reports");
        contentPanel.add(buildOverdue(),        "overdue");

        return contentPanel;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DASHBOARD
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildDashboard() {
        JPanel p = page();

        p.add(pageHeader("Dashboard", "Genel Mali Özet — Mayıs 2026"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        // Özet kartlar
        JPanel cards = new JPanel(new GridLayout(1, 4, 13, 0));
        cards.setOpaque(false);
        cards.add(summaryCard("Toplam Gelir",     "₺61.100", GREEN,  "left"));
        cards.add(summaryCard("Toplam Gider",     "₺21.500", RED,    "left"));
        cards.add(summaryCard("Net Kar / Zarar",  "₺39.600", PINK,   "left"));
        cards.add(summaryCard("Gecikmiş Fatura",  "2",       ORANGE, "left"));
        body.add(cards, BorderLayout.NORTH);

        // Son işlemler tablosu
        JPanel tableWrap = cardPanel("Son İşlemler");
        String[] txCols = {"Tarih", "Açıklama", "Tür", "Tutar"};
        Object[][] txData = {
            {"2026-05-14","Lojistik",         "GİDER","₺4.100"},
            {"2026-05-12","Danışmanlık",       "GELİR","₺9.200"},
            {"2026-05-08","Kira & Faturalar",  "GİDER","₺3.500"},
            {"2026-05-02","Satış Geliri",      "GELİR","₺22.000"},
            {"2026-04-25","Hammadde",          "GİDER","₺8.700"},
        };
        JTable tbl = styledTable(txData, txCols);
        tbl.getColumnModel().getColumn(2).setCellRenderer(typeBadgeRenderer());
        tbl.getColumnModel().getColumn(3).setCellRenderer(amountRenderer());
        tableWrap.add(scrollOf(tbl));
        body.add(tableWrap, BorderLayout.CENTER);

        p.add(body, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  FATURA OLUŞTUR
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildCreateInvoice() {
        JPanel p = page();

        // Üst başlık + "Listeye Dön" butonu
        JPanel hdrRow = new JPanel(new BorderLayout());
        hdrRow.setBackground(CARD);
        hdrRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL),
            BorderFactory.createEmptyBorder(14, 24, 10, 24)));
        JPanel hdrTxt = new JPanel(new GridLayout(2, 1, 0, 2));
        hdrTxt.setOpaque(false);
        JLabel hT = new JLabel("Yeni Fatura Oluştur");
        hT.setFont(new Font("Arial", Font.BOLD, 20));
        hT.setForeground(new Color(26, 26, 26));
        JLabel hS = new JLabel("Fatura bilgilerini doldurun — tutar ve KDV otomatik hesaplanır");
        hS.setFont(new Font("Arial", Font.PLAIN, 12));
        hS.setForeground(MUTED);
        hdrTxt.add(hT); hdrTxt.add(hS);
        JButton backBtn = outlineButton("← Fatura Listesine Dön");
        backBtn.addActionListener(e -> cardLayout.show(contentPanel, "invoices"));
        hdrRow.add(hdrTxt, BorderLayout.WEST);
        hdrRow.add(backBtn, BorderLayout.EAST);
        p.add(hdrRow, BorderLayout.NORTH);

        // Ana içerik: sol form + sağ panel
        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // ── SOL SÜTUN ────────────────────────────────────────────────────────
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        // 1. Müşteri Bilgileri
        JPanel custSec = invSection("Müşteri Bilgileri");
        JPanel custGrid = new JPanel(new GridLayout(2, 2, 12, 10));
        custGrid.setOpaque(false);
        custGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] custNames = {"— Müşteri seçin —",
            "Yıldız Makine A.Ş.", "Demir Çelik San. Ltd.",
            "Akar Tedarik Tic.", "Güneş Enerji Yat.", "Polat Endüstri Ltd."};
        custCombo = new JComboBox<>(custNames);
        custCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        custCombo.setBackground(CARD);
        custCombo.setBorder(BorderFactory.createLineBorder(BORDER_COL, 1));
        fiTax   = readonlyField("Müşteri seçince dolar");
        fiEmail = readonlyField("Müşteri seçince dolar");
        fiPhone = readonlyField("Müşteri seçince dolar");
        custCombo.addActionListener(e -> fillCustomerFields());

        custGrid.add(invField("Müşteri Seç *", custCombo));
        custGrid.add(invField("Vergi Numarası", fiTax));
        custGrid.add(invField("E-Posta", fiEmail));
        custGrid.add(invField("Telefon", fiPhone));
        custSec.add(custGrid);
        left.add(custSec);
        left.add(Box.createVerticalStrut(12));

        // 2. Fatura Detayları
        JPanel detSec = invSection("Fatura Detayları");
        JPanel detGrid = new JPanel(new GridLayout(1, 3, 12, 0));
        detGrid.setOpaque(false);
        detGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        fiDate = styledField("2026-05-23");
        fiDue  = styledField("2026-06-23");
        vatCombo = new JComboBox<>(new String[]{"KDV Yok (%0)", "%1 İndirimli", "%8 İndirimli", "%18 Standart", "%20"});
        vatCombo.setSelectedIndex(3);
        vatCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        vatCombo.setBackground(CARD);
        vatCombo.setBorder(BorderFactory.createLineBorder(BORDER_COL, 1));
        vatCombo.addActionListener(e -> recalcTotals());
        detGrid.add(invField("Fatura Tarihi *", fiDate));
        detGrid.add(invField("Vade Tarihi *", fiDue));
        detGrid.add(invField("KDV Oranı", vatCombo));
        detSec.add(detGrid);

        JPanel descWrap = new JPanel(new BorderLayout());
        descWrap.setOpaque(false);
        descWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        descWrap.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JLabel descLbl = new JLabel("Açıklama / Not");
        descLbl.setFont(new Font("Arial", Font.BOLD, 10));
        descLbl.setForeground(MUTED);
        descLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        JTextArea descTa = new JTextArea(2, 20);
        descTa.setFont(new Font("Arial", Font.PLAIN, 13));
        descTa.setLineWrap(true);
        descTa.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1, true),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        descWrap.add(descLbl, BorderLayout.NORTH);
        descWrap.add(new JScrollPane(descTa), BorderLayout.CENTER);
        detSec.add(descWrap);
        left.add(detSec);
        left.add(Box.createVerticalStrut(12));

        // 3. İş Kalemleri
        JPanel itemSec = invSection("İş Kalemleri");

        // Kalem tablosu başlığı
        JPanel lineHeader = new JPanel(new GridBagLayout());
        lineHeader.setOpaque(false);
        lineHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        lineHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        GridBagConstraints lhg = new GridBagConstraints();
        lhg.fill = GridBagConstraints.HORIZONTAL; lhg.gridy = 0;
        lhg.weightx = 1.0; lhg.gridx = 0;
        lineHeader.add(colHeader("Hizmet / Ürün", SwingConstants.LEFT), lhg);
        lhg.weightx = 0; lhg.gridx = 1; lineHeader.add(colHeader("Miktar", SwingConstants.CENTER), lhg);
        lhg.gridx = 2; lineHeader.add(colHeader("Birim Fiyat (₺)", SwingConstants.CENTER), lhg);
        lhg.gridx = 3; lineHeader.add(colHeader("Toplam", SwingConstants.RIGHT), lhg);
        lhg.gridx = 4; lineHeader.add(new JLabel(""), lhg);
        itemSec.add(lineHeader);

        // Kalem satırları konteyneri
        lineContainer = new JPanel();
        lineContainer.setLayout(new BoxLayout(lineContainer, BoxLayout.Y_AXIS));
        lineContainer.setOpaque(false);
        lineContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        itemSec.add(lineContainer);
        addLineRow(); // Başlangıçta 1 boş satır

        // + Kalem ekle butonu
        JButton addLine = new JButton("+ Kalem ekle");
        addLine.setFont(new Font("Arial", Font.BOLD, 12));
        addLine.setForeground(PINK);
        addLine.setBackground(new Color(0,0,0,0));
        addLine.setBorderPainted(false);
        addLine.setFocusPainted(false);
        addLine.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        addLine.addActionListener(e -> { addLineRow(); lineContainer.revalidate(); });
        itemSec.add(addLine);

        // Toplamlar
        JSeparator totSep = new JSeparator();
        totSep.setForeground(BORDER_COL);
        totSep.setAlignmentX(Component.LEFT_ALIGNMENT);
        totSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        itemSec.add(Box.createVerticalStrut(8));
        itemSec.add(totSep);

        lblSubtotal   = totRow("Ara toplam",  "₺0,00", false);
        lblVat        = totRow("KDV (%18)",   "₺0,00", false);
        lblGrandTotal = totRow("Genel Toplam","₺0,00", true);
        itemSec.add(buildTotRow("Ara toplam",  lblSubtotal,  false));
        itemSec.add(buildTotRow("KDV (%18)",   lblVat,       false));
        itemSec.add(buildTotRow("Genel Toplam",lblGrandTotal,true));

        // Aksiyon butonları
        JPanel acts = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        acts.setOpaque(false);
        acts.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton create = pinkButton("Faturayı Oluştur");
        create.setPreferredSize(new Dimension(160, 38));
        JButton preview = outlineButton("Ön İzleme");
        preview.setPreferredSize(new Dimension(110, 38));
        JButton cancel = new JButton("İptal");
        cancel.setFont(new Font("Arial", Font.PLAIN, 12));
        cancel.setForeground(MUTED);
        cancel.setBackground(new Color(0,0,0,0));
        cancel.setBorderPainted(false);
        cancel.setFocusPainted(false);
        create.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Fatura başarıyla oluşturuldu!", "Başarılı", JOptionPane.INFORMATION_MESSAGE));
        cancel.addActionListener(e -> { lineData.clear(); lineContainer.removeAll();
            addLineRow(); lineContainer.revalidate(); recalcTotals(); });
        acts.add(create); acts.add(preview); acts.add(cancel);
        itemSec.add(acts);
        left.add(itemSec);

        JScrollPane leftScroll = new JScrollPane(left);
        leftScroll.setBorder(BorderFactory.createEmptyBorder());
        leftScroll.getViewport().setOpaque(false);
        leftScroll.setOpaque(false);
        leftScroll.getVerticalScrollBar().setUnitIncrement(12);
        body.add(leftScroll, BorderLayout.CENTER);

        // ── SAĞ SÜTUN ────────────────────────────────────────────────────────
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(240, 0));

        // Fatura Durumu kartı
        JPanel statusCard = new JPanel();
        statusCard.setLayout(new BoxLayout(statusCard, BoxLayout.Y_AXIS));
        statusCard.setBackground(CARD);
        statusCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        statusCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        JLabel scTitle = new JLabel("Fatura Durumu");
        scTitle.setFont(new Font("Arial", Font.BOLD, 11));
        scTitle.setForeground(MUTED);
        scTitle.setBackground(HEADER_BG);
        scTitle.setOpaque(true);
        scTitle.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        scTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        scTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        statusCard.add(scTitle);

        JPanel scBody = new JPanel();
        scBody.setLayout(new BoxLayout(scBody, BoxLayout.Y_AXIS));
        scBody.setBackground(CARD);
        scBody.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JLabel badgeLbl = new JLabel("Taslak");
        badgeLbl.setFont(new Font("Arial", Font.BOLD, 11));
        badgeLbl.setForeground(new Color(85, 85, 85));
        badgeLbl.setBackground(new Color(236, 236, 236));
        badgeLbl.setOpaque(true);
        badgeLbl.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        scBody.add(scInfoRow("Durum", badgeLbl));
        scBody.add(Box.createVerticalStrut(6));
        scBody.add(scInfoRow2("Fatura No", "Otomatik atanır"));
        statusCard.add(scBody);
        right.add(statusCard);
        right.add(Box.createVerticalStrut(12));

        // Olay Akışı kartı
        JPanel stepsCard = new JPanel();
        stepsCard.setLayout(new BoxLayout(stepsCard, BoxLayout.Y_AXIS));
        stepsCard.setBackground(CARD);
        stepsCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        stepsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        JLabel stTitle = new JLabel("Olay Akışı");
        stTitle.setFont(new Font("Arial", Font.BOLD, 11));
        stTitle.setForeground(MUTED);
        stTitle.setBackground(HEADER_BG);
        stTitle.setOpaque(true);
        stTitle.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        stTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        stTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        stepsCard.add(stTitle);

        JPanel stBody = new JPanel();
        stBody.setLayout(new BoxLayout(stBody, BoxLayout.Y_AXIS));
        stBody.setBackground(CARD);
        stBody.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        String[] steps = {
            "Müşteriyi seçin veya ekleyin",
            "Tarih ve KDV oranını belirleyin",
            "İş kalemlerini ve fiyatları girin",
            "Sistem toplamı otomatik hesaplar",
            "Faturayı oluşturun ve kaydedin"
        };
        for (int i = 0; i < steps.length; i++) {
            stBody.add(stepRow(i + 1, steps[i]));
            if (i < steps.length - 1) stBody.add(Box.createVerticalStrut(8));
        }
        stepsCard.add(stBody);
        right.add(stepsCard);
        right.add(Box.createVerticalGlue());

        body.add(right, BorderLayout.EAST);
        p.add(body, BorderLayout.CENTER);
        return p;
    }

    // Fatura formu yardımcı metodlar
    private JPanel invSection(String title) {
        JPanel sec = new JPanel();
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setBackground(CARD);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        sec.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        hdr.setBackground(new Color(250, 250, 247));
        hdr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        hdr.setAlignmentX(Component.LEFT_ALIGNMENT);
        hdr.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL),
            BorderFactory.createEmptyBorder(8, 0, 8, 0)));
        JLabel dot = new JLabel("●");
        dot.setForeground(PINK);
        dot.setFont(new Font("Arial", Font.PLAIN, 8));
        JLabel ttl = new JLabel(title);
        ttl.setFont(new Font("Arial", Font.BOLD, 13));
        ttl.setForeground(TEXT);
        hdr.add(dot); hdr.add(ttl);
        sec.add(hdr);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        body.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.add(body);
        return body; // body döndür, öğeler body'e eklenir
    }

    private JPanel invField(String label, JComponent comp) {
        JPanel wrap = new JPanel(new BorderLayout(0, 4));
        wrap.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 10));
        lbl.setForeground(MUTED);
        wrap.add(lbl, BorderLayout.NORTH);
        wrap.add(comp, BorderLayout.CENTER);
        return wrap;
    }

    private JTextField readonlyField(String hint) {
        JTextField f = new JTextField(hint);
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setForeground(MUTED);
        f.setBackground(new Color(242, 239, 233));
        f.setEditable(false);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private void fillCustomerFields() {
        String sel = (String) custCombo.getSelectedItem();
        if (sel == null || sel.startsWith("—")) {
            fiTax.setText("Müşteri seçince dolar");
            fiEmail.setText("Müşteri seçince dolar");
            fiPhone.setText("Müşteri seçince dolar");
            return;
        }
        for (Object[] c : CUSTOMERS) {
            if (c[1].equals(sel)) {
                fiTax.setText((String) c[3]);
                fiEmail.setText((String) c[4]);
                fiPhone.setText((String) c[5]);
                return;
            }
        }
    }

    private void addLineRow() {
        double[] data = {1.0, 0.0};
        lineData.add(data);
        int idx = lineData.size() - 1;

        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 236, 230)));

        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0; g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(2, 2, 2, 2);

        JTextField desc = new JTextField();
        desc.setFont(new Font("Arial", Font.PLAIN, 12));
        desc.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1, true),
            BorderFactory.createEmptyBorder(4, 7, 4, 7)));

        JTextField qty = new JTextField("1");
        qty.setFont(new Font("Arial", Font.PLAIN, 12));
        qty.setHorizontalAlignment(SwingConstants.CENTER);
        qty.setPreferredSize(new Dimension(55, 28));
        qty.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1, true),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        JTextField price = new JTextField("0.00");
        price.setFont(new Font("Arial", Font.PLAIN, 12));
        price.setHorizontalAlignment(SwingConstants.RIGHT);
        price.setPreferredSize(new Dimension(95, 28));
        price.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1, true),
            BorderFactory.createEmptyBorder(4, 7, 4, 7)));

        JLabel total = new JLabel("₺0,00", SwingConstants.RIGHT);
        total.setFont(new Font("Arial", Font.BOLD, 12));
        total.setForeground(TEXT);
        total.setPreferredSize(new Dimension(85, 28));

        JButton del = new JButton("✕");
        del.setFont(new Font("Arial", Font.PLAIN, 11));
        del.setForeground(RED);
        del.setBackground(CARD);
        del.setBorderPainted(false);
        del.setFocusPainted(false);
        del.setPreferredSize(new Dimension(26, 26));
        del.setCursor(new Cursor(Cursor.HAND_CURSOR));

        qty.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
            void update() {
                try { data[0] = Double.parseDouble(qty.getText()); } catch (Exception ex) { data[0] = 0; }
                total.setText(fmtLine(data[0] * data[1]));
                recalcTotals();
            }
        });
        price.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
            void update() {
                try { data[1] = Double.parseDouble(price.getText()); } catch (Exception ex) { data[1] = 0; }
                total.setText(fmtLine(data[0] * data[1]));
                recalcTotals();
            }
        });
        del.addActionListener(e -> {
            lineData.remove(data);
            lineContainer.remove(row);
            lineContainer.revalidate();
            lineContainer.repaint();
            recalcTotals();
        });

        g.weightx = 1.0; g.gridx = 0; row.add(desc,  g);
        g.weightx = 0;   g.gridx = 1; row.add(qty,   g);
        g.gridx = 2; row.add(price, g);
        g.gridx = 3; row.add(total, g);
        g.gridx = 4; row.add(del,   g);

        lineContainer.add(row);
    }

    private void recalcTotals() {
        double sub = 0;
        for (double[] d : lineData) sub += d[0] * d[1];
        int vatPct = 18;
        String vs = vatCombo != null ? (String) vatCombo.getSelectedItem() : "%18 Standart";
        if (vs != null) {
            if      (vs.contains("0"))  vatPct = 0;
            else if (vs.contains("%1 ")) vatPct = 1;
            else if (vs.contains("%8"))  vatPct = 8;
            else if (vs.contains("%18")) vatPct = 18;
            else if (vs.contains("%20")) vatPct = 20;
        }
        double vat   = sub * vatPct / 100.0;
        double grand = sub + vat;
        if (lblSubtotal   != null) lblSubtotal.setText(fmtLine(sub));
        if (lblVat        != null) { lblVat.setText(fmtLine(vat)); }
        if (lblGrandTotal != null) lblGrandTotal.setText(fmtLine(grand));
    }

    private String fmtLine(double v) {
        return String.format("₺%,.2f", v).replace(",", "X").replace(".", ",").replace("X", ".");
    }

    private JLabel colHeader(String text, int align) {
        JLabel l = new JLabel(text, align);
        l.setFont(new Font("Arial", Font.BOLD, 10));
        l.setForeground(MUTED);
        l.setBackground(HEADER_BG);
        l.setOpaque(true);
        l.setBorder(BorderFactory.createEmptyBorder(5, 6, 5, 6));
        l.setPreferredSize(new Dimension(0, 26));
        return l;
    }

    private JPanel buildTotRow(String label, JLabel valLabel, boolean grand) {
        JPanel r = new JPanel(new BorderLayout());
        r.setOpaque(false);
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, grand ? 36 : 28));
        if (grand) r.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, BORDER_COL),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        else r.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        JLabel lbl = new JLabel(label);
        lbl.setFont(grand ? new Font("Arial", Font.BOLD, 14) : new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(grand ? PINK_H : new Color(100, 100, 100));
        valLabel.setFont(grand ? new Font("Arial", Font.BOLD, 14) : new Font("Arial", Font.PLAIN, 12));
        valLabel.setForeground(grand ? PINK_H : TEXT);
        r.add(lbl, BorderLayout.WEST);
        r.add(valLabel, BorderLayout.EAST);
        return r;
    }

    private JLabel totRow(String ignored, String init, boolean ignored2) {
        JLabel l = new JLabel(init, SwingConstants.RIGHT);
        return l;
    }

    private JPanel scInfoRow(String label, JComponent value) {
        JPanel r = new JPanel(new BorderLayout(8, 0));
        r.setOpaque(false);
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        l.setForeground(MUTED);
        r.add(l, BorderLayout.WEST);
        r.add(value, BorderLayout.EAST);
        return r;
    }

    private JPanel scInfoRow2(String label, String value) {
        JLabel v = new JLabel(value);
        v.setFont(new Font("Arial", Font.PLAIN, 11));
        v.setForeground(new Color(187, 187, 187));
        return scInfoRow(label, v);
    }

    private JPanel stepRow(int num, String text) {
        JPanel r = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        r.setOpaque(false);
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel n = new JLabel(String.valueOf(num));
        n.setFont(new Font("Arial", Font.BOLD, 10));
        n.setForeground(PINK);
        n.setBackground(PINK_L);
        n.setOpaque(true);
        n.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        JLabel t = new JLabel(text);
        t.setFont(new Font("Arial", Font.PLAIN, 12));
        t.setForeground(new Color(120, 120, 120));
        r.add(n); r.add(t);
        return r;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  FATURA LİSTESİ
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildInvoiceList() {
        JPanel p = page();
        p.add(pageHeader("Fatura Listesi", "Oluşturulan tüm faturalar"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        // Toolbar
        JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tb.setOpaque(false);
        JTextField search = styledField("Fatura no, müşteri veya açıklama ara...");
        search.setPreferredSize(new Dimension(300, 34));
        JButton newInv = pinkButton("+ Yeni Fatura");
        newInv.setPreferredSize(new Dimension(120, 34));
        newInv.addActionListener(e -> cardLayout.show(contentPanel, "create-invoice"));
        tb.add(search);
        tb.add(newInv);
        body.add(tb, BorderLayout.NORTH);

        // Tablo
        String[] cols = {"Fatura No", "Müşteri", "Tarih", "Vade", "Tutar", "Durum", "İşlem"};
        Object[][] data = new Object[INVOICES.length][7];
        for (int i = 0; i < INVOICES.length; i++) {
            System.arraycopy(INVOICES[i], 0, data[i], 0, 6);
            data[i][6] = "···";
        }

        JTable tbl = styledTable(data, cols);
        tbl.getColumnModel().getColumn(5).setCellRenderer(statusBadgeRenderer());
        tbl.getColumnModel().getColumn(4).setCellRenderer(amountRenderer());

        JPanel wrap = cardPanel(null);
        wrap.add(scrollOf(tbl));
        body.add(wrap, BorderLayout.CENTER);

        p.add(body, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MÜŞTERİLER
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildCustomers() {
        JPanel p = page();
        p.add(pageHeader("Müşteri Yönetimi", "Kayıtlı Müşteri ve Tedarikçiler"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tb.setOpaque(false);
        JTextField search = styledField("Müşteri adı, vergi no veya e-posta ara...");
        search.setPreferredSize(new Dimension(300, 34));
        JButton add = pinkButton("+ Yeni Müşteri");
        add.setPreferredSize(new Dimension(130, 34));
        add.addActionListener(e -> openNewCustomerDialog());
        tb.add(search); tb.add(add);
        body.add(tb, BorderLayout.NORTH);

        String[] cols = {"Firma / Ad", "Tür", "Vergi No", "E-Posta", "Telefon", "Bakiye"};
        JTable tbl = styledTable(CUSTOMERS, cols);
        tbl.getColumnModel().getColumn(1).setCellRenderer(custTypeBadgeRenderer());
        tbl.getColumnModel().getColumn(5).setCellRenderer(balanceRenderer());

        JPanel wrap = cardPanel(null);
        wrap.add(scrollOf(tbl));
        body.add(wrap, BorderLayout.CENTER);

        p.add(body, BorderLayout.CENTER);
        return p;
    }

    private void openNewCustomerDialog() {
        JDialog dlg = new JDialog(this, "Yeni Müşteri Ekle", true);
        dlg.setSize(460, 440);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // Başlık
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(CARD);
        hdr.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL),
            BorderFactory.createEmptyBorder(14, 20, 14, 20)));
        JLabel title = new JLabel("Yeni Müşteri Ekle");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(TEXT);
        JLabel sub = new JLabel("Müşteri veya tedarikçi bilgilerini girin");
        sub.setFont(new Font("Arial", Font.PLAIN, 11));
        sub.setForeground(MUTED);
        JPanel hdrTxt = new JPanel(new GridLayout(2, 1, 0, 2));
        hdrTxt.setOpaque(false);
        hdrTxt.add(title); hdrTxt.add(sub);
        hdr.add(hdrTxt, BorderLayout.WEST);
        root.add(hdr, BorderLayout.NORTH);

        // Form alanları
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 0, 12, 0);

        JTextField fName  = styledField("Firma adı veya kişi adı");
        JTextField fTax   = styledField("10 haneli vergi numarası");
        JTextField fEmail = styledField("ornek@firma.com");
        JTextField fPhone = styledField("05XX XXX XX XX");
        JComboBox<String> fType = new JComboBox<>(new String[]{"Müşteri", "Tedarikçi"});
        fType.setFont(new Font("Arial", Font.PLAIN, 13));
        fType.setBackground(CARD);

        String[][] rows = {
            {"Firma / Ad *", null},
            {"Vergi Numarası", null},
            {"E-Posta", null},
            {"Telefon", null},
            {"Tür *", null}
        };
        JComponent[] fields = {fName, fTax, fEmail, fPhone, fType};
        String[] labels = {"Firma / Ad *", "Vergi Numarası", "E-Posta", "Telefon", "Tür *"};

        for (int i = 0; i < labels.length; i++) {
            gc.gridy = i * 2; gc.gridx = 0; gc.weightx = 1.0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 10));
            lbl.setForeground(MUTED);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
            gc.insets = new Insets(0, 0, 2, 0);
            form.add(lbl, gc);
            gc.gridy = i * 2 + 1;
            gc.insets = new Insets(0, 0, 10, 0);
            form.add(fields[i], gc);
        }

        JScrollPane sp = new JScrollPane(form);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setBackground(BG);
        root.add(sp, BorderLayout.CENTER);

        // Butonlar
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btns.setBackground(CARD);
        btns.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COL));
        JButton cancel = new JButton("İptal");
        cancel.setFont(new Font("Arial", Font.PLAIN, 12));
        cancel.setForeground(MUTED);
        cancel.setBackground(CARD);
        cancel.setBorder(BorderFactory.createLineBorder(BORDER_COL, 1, true));
        cancel.setFocusPainted(false);
        cancel.setPreferredSize(new Dimension(80, 34));
        cancel.addActionListener(e -> dlg.dispose());
        JButton save = pinkButton("Kaydet");
        save.setPreferredSize(new Dimension(100, 34));
        save.addActionListener(e -> {
            if (fName.getText().trim().isEmpty()) {
                fName.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(RED, 1, true),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
                return;
            }
            JOptionPane.showMessageDialog(dlg,
                "\"" + fName.getText().trim() + "\" başarıyla kaydedildi.",
                "Kaydedildi", JOptionPane.INFORMATION_MESSAGE);
            dlg.dispose();
        });
        btns.add(cancel); btns.add(save);
        root.add(btns, BorderLayout.SOUTH);

        dlg.setContentPane(root);
        dlg.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HATIRLATMALAR
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildReminders() {
        JPanel p = page();
        p.add(pageHeader("Ödeme Hatırlatıcı", "Vadesi yaklaşan ve gecikmiş ödemeler"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        JLabel emptyLbl = new JLabel("Bekleyen hatırlatıcı yok. 🎉", SwingConstants.CENTER);
        emptyLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        emptyLbl.setForeground(MUTED);
        emptyLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        emptyLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        emptyLbl.setVisible(false);
        list.add(emptyLbl);

        Object[][] remData = {
            {"Demir Çelik San. Ltd.", "INV-002 · Vade: 2026-05-10 · Hammadde tedariki",  "₺8.900",  "gecikti"},
            {"Akar Tedarik Tic.",     "INV-005 · Vade: 2026-04-20 · Montaj işçiliği",    "₺6.750",  "gecikti"},
            {"Güneş Enerji Yat.",     "INV-003 · Vade: 2026-05-15 · Danışmanlık hizmeti","₺12.400", "vadesi"},
            {"Yıldız Makine A.Ş.",   "INV-004 · Vade: 2026-05-31 · Yedek parça satışı", "₺22.000", "beklemede"},
        };

        int[] visibleCount = {remData.length};

        for (Object[] r : remData) {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
            wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
            wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

            Runnable onPaid = () -> {
                wrapper.setVisible(false);
                wrapper.setMaximumSize(new Dimension(0, 0));
                visibleCount[0]--;
                if (visibleCount[0] == 0) emptyLbl.setVisible(true);
                list.revalidate();
                list.repaint();
            };

            wrapper.add(reminderRow((String)r[0], (String)r[1], (String)r[2], (String)r[3], onPaid));
            list.add(wrapper);
        }

        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);
        body.add(sp, BorderLayout.CENTER);
        p.add(body, BorderLayout.CENTER);
        return p;
    }

    private JPanel reminderRow(String name, String detail, String amount, String type, Runnable onPaid) {
        Color accent = "gecikti".equals(type) ? RED : "vadesi".equals(type) ? ORANGE : new Color(41, 128, 185);
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(CARD);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12))));

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 3));
        info.setOpaque(false);
        JLabel n = new JLabel(name);
        n.setFont(new Font("Arial", Font.BOLD, 13));
        n.setForeground(TEXT);
        JLabel d = new JLabel(detail);
        d.setFont(new Font("Arial", Font.PLAIN, 11));
        d.setForeground(MUTED);
        info.add(n); info.add(d);

        JLabel amt = new JLabel(amount);
        amt.setFont(new Font("Arial", Font.BOLD, 14));
        amt.setForeground(PINK_H);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btns.setOpaque(false);
        JButton sms   = outlineButton("SMS");
        JButton email = outlineButton("E-posta");
        JButton paid  = pinkButton("Ödendi");
        paid.setFont(new Font("Arial", Font.BOLD, 11));
        sms.addActionListener(e   -> JOptionPane.showMessageDialog(this, name + " — SMS gönderildi.", "SMS", JOptionPane.INFORMATION_MESSAGE));
        email.addActionListener(e -> JOptionPane.showMessageDialog(this, name + " — E-posta gönderildi.", "E-posta", JOptionPane.INFORMATION_MESSAGE));
        paid.addActionListener(e  -> onPaid.run());
        btns.add(sms); btns.add(email); btns.add(paid);

        JPanel right = new JPanel(new BorderLayout(8, 0));
        right.setOpaque(false);
        right.add(amt,  BorderLayout.WEST);
        right.add(btns, BorderLayout.EAST);

        row.add(info,  BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  RAPORLAMA
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildReports() {
        JPanel p = page();
        p.add(pageHeader("Finansal Raporlar", "Kar-Zarar Özeti & Gider Analizi"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        JPanel topCards = new JPanel(new GridLayout(1, 3, 13, 0));
        topCards.setOpaque(false);
        topCards.add(summaryCard("Toplam Gelir",    "₺61.100", GREEN, "left"));
        topCards.add(summaryCard("Toplam Gider",    "₺21.500", RED,   "left"));
        topCards.add(summaryCard("Net Kar / Zarar", "₺39.600", PINK,  "left"));
        body.add(topCards, BorderLayout.NORTH);

        // İşlem geçmişi tablosu
        JPanel wrap = cardPanel("İşlem Geçmişi");
        String[] cols = {"ID", "Tarih", "Tür", "Kategori", "Tutar"};
        JTable tbl = styledTable(TRANSACTIONS, cols);
        tbl.getColumnModel().getColumn(2).setCellRenderer(typeBadgeRenderer());
        tbl.getColumnModel().getColumn(4).setCellRenderer(amountRenderer());

        JPanel tbRow = new JPanel(new BorderLayout());
        tbRow.setOpaque(false);
        tbRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        tbRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JButton csv = outlineButton("CSV Dışa Aktar");
        csv.addActionListener(e -> JOptionPane.showMessageDialog(this, "CSV dışa aktarıldı.", "Başarılı", JOptionPane.INFORMATION_MESSAGE));
        tbRow.add(csv, BorderLayout.EAST);
        wrap.add(tbRow);
        wrap.add(scrollOf(tbl));
        body.add(wrap, BorderLayout.CENTER);

        p.add(body, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  VADESİ GELENLER
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildOverdue() {
        JPanel p = page();
        p.add(pageHeader("Vadesi Gelenler", "Vadesi geçmiş fatura listesi"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        Object[][] data = {
            {"INV-002","Demir Çelik San. Ltd.","Hammadde tedariki",  "₺8.900", "2026-05-10"},
            {"INV-005","Akar Tedarik Tic.",    "Montaj işçiliği",    "₺6.750", "2026-04-20"},
        };

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        JLabel emptyLbl2 = new JLabel("Gecikmiş ödeme yok. 🎉", SwingConstants.CENTER);
        emptyLbl2.setFont(new Font("Arial", Font.BOLD, 13));
        emptyLbl2.setForeground(GREEN);
        emptyLbl2.setAlignmentX(Component.LEFT_ALIGNMENT);
        emptyLbl2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        emptyLbl2.setVisible(false);
        list.add(emptyLbl2);

        int[] visibleCount2 = {data.length};

        for (Object[] r : data) {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
            wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 73));
            wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setBackground(new Color(255, 248, 247));
            row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(245, 198, 192), 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

            JPanel info = new JPanel(new GridLayout(2, 1, 0, 3));
            info.setOpaque(false);
            JLabel n = new JLabel((String) r[1]);
            n.setFont(new Font("Arial", Font.BOLD, 13));
            n.setForeground(RED);
            JLabel d = new JLabel(r[0] + " · " + r[2]);
            d.setFont(new Font("Arial", Font.PLAIN, 11));
            d.setForeground(MUTED);
            info.add(n); info.add(d);

            JPanel right = new JPanel(new BorderLayout(10, 0));
            right.setOpaque(false);
            JPanel amtDate = new JPanel(new GridLayout(2, 1, 0, 2));
            amtDate.setOpaque(false);
            JLabel amt = new JLabel((String) r[3], SwingConstants.RIGHT);
            amt.setFont(new Font("Arial", Font.BOLD, 14));
            amt.setForeground(RED);
            JLabel due = new JLabel("Vade: " + r[4], SwingConstants.RIGHT);
            due.setFont(new Font("Arial", Font.PLAIN, 11));
            due.setForeground(ORANGE);
            amtDate.add(amt); amtDate.add(due);

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            btns.setOpaque(false);
            JButton remind = outlineButton("Hatırlat");
            JButton paid   = pinkButton("Ödendi");
            String custName = (String) r[1];
            remind.addActionListener(e -> JOptionPane.showMessageDialog(this,
                custName + " için hatırlatıcı gönderildi.", "Hatırlatıcı", JOptionPane.INFORMATION_MESSAGE));
            paid.addActionListener(e -> {
                wrapper.setVisible(false);
                wrapper.setMaximumSize(new Dimension(0, 0));
                visibleCount2[0]--;
                if (visibleCount2[0] == 0) emptyLbl2.setVisible(true);
                list.revalidate();
                list.repaint();
            });
            btns.add(remind); btns.add(paid);

            right.add(amtDate, BorderLayout.CENTER);
            right.add(btns, BorderLayout.EAST);

            row.add(info, BorderLayout.CENTER);
            row.add(right, BorderLayout.EAST);
            wrapper.add(row, BorderLayout.CENTER);
            list.add(wrapper);
        }

        body.add(list, BorderLayout.NORTH);
        p.add(body, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  YARDIMCI FACTORY METODLAR
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel page() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        return p;
    }

    private JPanel pageHeader(String title, String sub) {
        JPanel h = new JPanel(new GridLayout(2, 1, 0, 2));
        h.setBackground(CARD);
        h.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL),
            BorderFactory.createEmptyBorder(16, 24, 12, 24)));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 20));
        t.setForeground(new Color(26, 26, 26));
        JLabel s = new JLabel(sub);
        s.setFont(new Font("Arial", Font.PLAIN, 12));
        s.setForeground(MUTED);
        h.add(t); h.add(s);
        return h;
    }

    private JPanel cardPanel(String title) {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1, true),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        if (title != null) {
            JLabel lbl = new JLabel(title);
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            lbl.setForeground(TEXT);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            c.add(lbl);
        }
        return c;
    }

    private JPanel summaryCard(String label, String value, Color color, String ignored) {
        JPanel c = new JPanel(null);
        c.setBackground(CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, color),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL, 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16))));
        c.setPreferredSize(new Dimension(0, 90));

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("Arial", Font.PLAIN, 10));
        lbl.setForeground(MUTED);
        lbl.setBounds(16, 14, 200, 14);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.BOLD, 22));
        val.setForeground(color);
        val.setBounds(16, 32, 200, 30);

        c.add(lbl); c.add(val);
        return c;
    }

    private JPanel infoRow(String label, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        row.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        l.setForeground(MUTED);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Arial", Font.BOLD, 12));
        v.setForeground(valueColor);
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 11));
        l.setForeground(MUTED);
        return l;
    }

    private JTextField styledField(String hint) {
        JTextField f = new JTextField(hint);
        f.setFont(new Font("Arial", Font.PLAIN, 13));
        f.setForeground(MUTED);
        f.setBackground(new Color(250, 250, 247));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COL, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(hint)) { f.setText(""); f.setForeground(TEXT); }
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PINK, 1, true),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
                f.setBackground(CARD);
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(hint); f.setForeground(MUTED); }
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COL, 1, true),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
                f.setBackground(new Color(250, 250, 247));
            }
        });
        return f;
    }

    private JButton pinkButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setBackground(PINK);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(PINK_H); }
            public void mouseExited(MouseEvent e)  { b.setBackground(PINK);   }
        });
        return b;
    }

    private JButton outlineButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.PLAIN, 11));
        b.setForeground(PINK);
        b.setBackground(CARD);
        b.setBorder(BorderFactory.createLineBorder(PINK, 1, true));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(PINK_L); }
            public void mouseExited(MouseEvent e)  { b.setBackground(CARD);   }
        });
        return b;
    }

    private JTable styledTable(Object[][] data, String[] cols) {
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(model);
        t.setRowHeight(36);
        t.setFont(new Font("Arial", Font.PLAIN, 13));
        t.setGridColor(new Color(240, 236, 230));
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setSelectionBackground(PINK_L);
        t.setSelectionForeground(TEXT);
        t.setBackground(CARD);
        t.setIntercellSpacing(new Dimension(0, 0));
        JTableHeader th = t.getTableHeader();
        th.setBackground(HEADER_BG);
        th.setForeground(MUTED);
        th.setFont(new Font("Arial", Font.BOLD, 11));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COL));
        th.setReorderingAllowed(false);
        return t;
    }

    private JScrollPane scrollOf(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return sp;
    }

    // ── Özel cell renderer'lar ────────────────────────────────────────────────
    private TableCellRenderer statusBadgeRenderer() {
        return (table, value, isSelected, hasFocus, row, col) -> {
            JLabel lbl = new JLabel(value != null ? value.toString() : "", SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 11));
            lbl.setOpaque(true);
            String v = value != null ? value.toString() : "";
            switch (v) {
                case "Ödendi":          lbl.setBackground(new Color(213,245,227)); lbl.setForeground(new Color(30,132,73));  break;
                case "Beklemede":       lbl.setBackground(new Color(253,235,208)); lbl.setForeground(new Color(183,119,13)); break;
                case "Gecikti":         lbl.setBackground(new Color(250,219,216)); lbl.setForeground(new Color(146,43,33));  break;
                case "Taslak":          lbl.setBackground(new Color(236,236,236)); lbl.setForeground(new Color(85,85,85));   break;
                case "Gönderildi":      lbl.setBackground(new Color(214,234,248)); lbl.setForeground(new Color(26,82,118));  break;
                case "Vadesi Yaklaştı": lbl.setBackground(new Color(254,249,231)); lbl.setForeground(new Color(154,125,10)); break;
                default:                lbl.setBackground(HEADER_BG);             lbl.setForeground(MUTED);
            }
            if (isSelected) lbl.setBackground(PINK_L);
            return lbl;
        };
    }

    private TableCellRenderer custTypeBadgeRenderer() {
        return (table, value, isSelected, hasFocus, row, col) -> {
            JLabel lbl = new JLabel(value != null ? value.toString() : "", SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 11));
            lbl.setOpaque(true);
            if ("Müşteri".equals(value)) {
                lbl.setBackground(new Color(214,234,248)); lbl.setForeground(new Color(26,82,118));
            } else {
                lbl.setBackground(new Color(232,218,239)); lbl.setForeground(new Color(108,52,131));
            }
            if (isSelected) lbl.setBackground(PINK_L);
            return lbl;
        };
    }

    private TableCellRenderer typeBadgeRenderer() {
        return (table, value, isSelected, hasFocus, row, col) -> {
            JLabel lbl = new JLabel(value != null ? value.toString() : "", SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 11));
            lbl.setOpaque(true);
            if ("GELİR".equals(value)) {
                lbl.setBackground(new Color(213,245,227)); lbl.setForeground(new Color(30,132,73));
            } else {
                lbl.setBackground(new Color(250,219,216)); lbl.setForeground(new Color(146,43,33));
            }
            if (isSelected) lbl.setBackground(PINK_L);
            return lbl;
        };
    }

    private TableCellRenderer amountRenderer() {
        return (table, value, isSelected, hasFocus, row, col) -> {
            JLabel lbl = new JLabel(value != null ? value.toString() : "", SwingConstants.RIGHT);
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            lbl.setForeground(TEXT);
            lbl.setOpaque(true);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
            lbl.setBackground(isSelected ? PINK_L : CARD);
            return lbl;
        };
    }

    private TableCellRenderer balanceRenderer() {
        return (table, value, isSelected, hasFocus, row, col) -> {
            String v = value != null ? value.toString() : "";
            JLabel lbl = new JLabel(v, SwingConstants.RIGHT);
            lbl.setFont(new Font("Arial", Font.BOLD, 13));
            lbl.setForeground(v.contains("Borç") ? RED : GREEN);
            lbl.setOpaque(true);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
            lbl.setBackground(isSelected ? PINK_L : CARD);
            return lbl;
        };
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            DashboardUI ui = new DashboardUI();
            ui.showLoginScreen();
        });
    }
}
