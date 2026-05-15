package ui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import service.InvoiceService;
import service.ReminderService;
import model.Invoice;
import model.InvoiceStatus;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * PureAcc - Ana Dashboard Arayüzü
 * Tüm modülleri (Fatura, Müşteri, Hatırlatma, Raporlar) tek pencerede yönetir.
 * CardLayout ile sekmeler arası geçiş sağlanır.
 */
public class DashboardUI extends JFrame {

    // ── Renkler (Rapordaki pembe tema) ──────────────────────────────────────
    private static final Color PINK_MAIN   = new Color(219, 112, 147); // Koyu pembe — butonlar
    private static final Color PINK_LIGHT  = new Color(255, 228, 235); // Açık pembe — arka plan
    private static final Color PINK_SOFT   = new Color(255, 182, 193); // Orta pembe — menü
    private static final Color WHITE       = Color.WHITE;
    private static final Color TEXT_DARK   = new Color(50, 50, 60);
    private static final Color TEXT_GRAY   = new Color(120, 120, 130);
    private static final Color GREEN_OK    = new Color(39, 174, 96);
    private static final Color ORANGE_WAIT = new Color(230, 126, 34);
    private static final Color RED_LATE    = new Color(192, 57, 43);

    // ── Servisler ────────────────────────────────────────────────────────────
    private final InvoiceService  invoiceService  = new InvoiceService();
    private final ReminderService reminderService = new ReminderService();

    // ── Ekran yönetimi ───────────────────────────────────────────────────────
    private JPanel     contentPanel;
    private CardLayout cardLayout;
    private JButton    activeMenuBtn; // Seçili menü butonu takibi

    // ════════════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ════════════════════════════════════════════════════════════════════════
    public DashboardUI() {
        setTitle("PureAcc — Muhasebe ve Finans Yönetim Sistemi");
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(WHITE);

        JPanel mainLayout = new JPanel(new BorderLayout());
        mainLayout.add(buildSideMenu(),    BorderLayout.WEST);
        mainLayout.add(buildContentArea(), BorderLayout.CENTER);
        add(mainLayout);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  GİRİŞ EKRANI
    // ════════════════════════════════════════════════════════════════════════
    /**
     * Uygulama başladığında gösterilen giriş penceresi.
     * Kullanıcı adı ve şifre doğrulaması simüle edilir.
     */
    public void showLoginScreen() {
        JDialog loginDialog = new JDialog();
        loginDialog.setTitle("PureAcc — Sisteme Giriş");
        loginDialog.setSize(420, 420);
        loginDialog.setModal(true);
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loginDialog.setResizable(false);

        // Arka plan paneli
        JPanel bg = new JPanel(null);
        bg.setBackground(PINK_LIGHT);

        // Başlık
        JLabel logo = new JLabel("PureAcc", SwingConstants.CENTER);
        logo.setFont(new Font("Georgia", Font.BOLD, 32));
        logo.setForeground(PINK_MAIN);
        logo.setBounds(60, 30, 300, 45);

        JLabel subtitle = new JLabel("Muhasebe ve Finans Yönetim Sistemi", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_GRAY);
        subtitle.setBounds(60, 75, 300, 20);

        // Ayraç çizgi
        JSeparator sep = new JSeparator();
        sep.setBounds(60, 105, 300, 2);
        sep.setForeground(PINK_SOFT);

        // Vergi Numarası
        JLabel lblUser = new JLabel("Vergi Numarası");
        lblUser.setFont(new Font("Arial", Font.BOLD, 12));
        lblUser.setForeground(TEXT_DARK);
        lblUser.setBounds(60, 125, 300, 20);

        JTextField txtUser = createStyledTextField("Vergi numaranızı girin");
        txtUser.setBounds(60, 148, 300, 38);

        // Şifre
        JLabel lblPass = new JLabel("Şifre");
        lblPass.setFont(new Font("Arial", Font.BOLD, 12));
        lblPass.setForeground(TEXT_DARK);
        lblPass.setBounds(60, 200, 300, 20);

        JPasswordField passField = new JPasswordField();
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_SOFT, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        passField.setFont(new Font("Arial", Font.PLAIN, 13));
        passField.setBounds(60, 223, 300, 38);

        // Hata mesajı (başta gizli)
        JLabel lblError = new JLabel("Hatalı giriş. Lütfen tekrar deneyin.");
        lblError.setFont(new Font("Arial", Font.PLAIN, 11));
        lblError.setForeground(RED_LATE);
        lblError.setBounds(60, 268, 300, 18);
        lblError.setVisible(false);

        // Giriş Butonu
        JButton loginBtn = createPinkButton("Sisteme Giriş Yap");
        loginBtn.setBounds(60, 295, 300, 42);

        // Demo bilgisi
        JLabel demoInfo = new JLabel("Demo: Herhangi bir bilgi ile giriş yapabilirsiniz.", SwingConstants.CENTER);
        demoInfo.setFont(new Font("Arial", Font.ITALIC, 10));
        demoInfo.setForeground(TEXT_GRAY);
        demoInfo.setBounds(60, 348, 300, 16);

        // Giriş aksiyonu
        ActionListener loginAction = e -> {
            String user = txtUser.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            if (user.isEmpty() || pass.isEmpty()) {
                // Boş alan uyarısı
                lblError.setText("Lütfen tüm alanları doldurun.");
                lblError.setVisible(true);
            } else {
                // Başarılı giriş simülasyonu
                loginDialog.dispose();
                DashboardUI.this.setVisible(true);
            }
        };
        loginBtn.addActionListener(loginAction);
        passField.addActionListener(loginAction); // Enter ile de giriş

        bg.add(logo); bg.add(subtitle); bg.add(sep);
        bg.add(lblUser); bg.add(txtUser);
        bg.add(lblPass); bg.add(passField);
        bg.add(lblError); bg.add(loginBtn); bg.add(demoInfo);

        loginDialog.add(bg);
        loginDialog.setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SOL MENÜ
    // ════════════════════════════════════════════════════════════════════════
    /**
     * Sol taraftaki navigasyon menüsünü oluşturur.
     * Her butona basıldığında CardLayout ile ilgili sayfa gösterilir.
     */
    private JPanel buildSideMenu() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(220, 680));
        side.setBackground(PINK_SOFT);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Logo alanı
        JPanel logoArea = new JPanel(new BorderLayout());
        logoArea.setBackground(PINK_MAIN);
        logoArea.setMaximumSize(new Dimension(220, 80));
        logoArea.setMinimumSize(new Dimension(220, 80));
        logoArea.setPreferredSize(new Dimension(220, 80));
        JLabel logoLbl = new JLabel("PureAcc", SwingConstants.CENTER);
        logoLbl.setFont(new Font("Georgia", Font.BOLD, 26));
        logoLbl.setForeground(WHITE);
        logoArea.add(logoLbl, BorderLayout.CENTER);
        side.add(logoArea);

        // Alt başlık
        JLabel sysLabel = new JLabel("  Muhasebe Sistemi", SwingConstants.LEFT);
        sysLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        sysLabel.setForeground(WHITE);
        sysLabel.setOpaque(true);
        sysLabel.setBackground(PINK_MAIN);
        sysLabel.setMaximumSize(new Dimension(220, 24));
        sysLabel.setPreferredSize(new Dimension(220, 24));
        side.add(sysLabel);

        side.add(Box.createVerticalStrut(16));

        // Menü butonları
        String[][] menuItems = {
            {"Ana Sayfa",         "home"},
            {"Fatura Oluştur",    "invoice"},
            {"Müşteri Listesi",   "customers"},
            {"Hatırlatıcılar",    "reminders"},
            {"Finansal Raporlar", "reports"}
        };

        for (String[] item : menuItems) {
            JButton btn = buildMenuButton(item[0], item[1]);
            side.add(btn);
            side.add(Box.createVerticalStrut(4));
            // İlk butonu aktif yap
            if ("home".equals(item[1])) {
                setActiveMenu(btn);
                activeMenuBtn = btn;
            }
        }

        side.add(Box.createVerticalGlue());

        // Alt kullanıcı bilgisi
        JPanel userArea = new JPanel(new BorderLayout());
        userArea.setBackground(PINK_MAIN);
        userArea.setMaximumSize(new Dimension(220, 55));
        userArea.setPreferredSize(new Dimension(220, 55));
        userArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel userName = new JLabel("Safiye Ural");
        userName.setFont(new Font("Arial", Font.BOLD, 13));
        userName.setForeground(WHITE);
        JLabel userRole = new JLabel("Muhasebe Uzmanı");
        userRole.setFont(new Font("Arial", Font.PLAIN, 10));
        userRole.setForeground(new Color(255, 220, 230));
        JPanel userInfo = new JPanel(new GridLayout(2, 1));
        userInfo.setOpaque(false);
        userInfo.add(userName);
        userInfo.add(userRole);
        userArea.add(userInfo, BorderLayout.CENTER);
        side.add(userArea);

        return side;
    }

    // Menü butonu stilini uygular ve CardLayout geçişini bağlar. 
    private JButton buildMenuButton(String text, String cardName) {
        JButton btn = new JButton("  " + text);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setForeground(TEXT_DARK);
        btn.setBackground(WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(220, 42));
        btn.setPreferredSize(new Dimension(220, 42));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != activeMenuBtn) btn.setBackground(PINK_LIGHT);
            }
            public void mouseExited(MouseEvent e) {
                if (btn != activeMenuBtn) btn.setBackground(WHITE);
            }
        });

        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            if (activeMenuBtn != null) {
                activeMenuBtn.setBackground(WHITE);
                activeMenuBtn.setForeground(TEXT_DARK);
                activeMenuBtn.setFont(new Font("Arial", Font.PLAIN, 14));
            }
            setActiveMenu(btn);
            activeMenuBtn = btn;
        });
        return btn;
    }

    // Seçili menü butonunun görünümünü vurgular. 
    private void setActiveMenu(JButton btn) {
        btn.setBackground(PINK_MAIN);
        btn.setForeground(WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SAĞ İÇERİK ALANI
    // ════════════════════════════════════════════════════════════════════════
    // Tüm sayfaları CardLayout içine ekler. 
    private JPanel buildContentArea() {
        cardLayout    = new CardLayout();
        contentPanel  = new JPanel(cardLayout);
        contentPanel.setBackground(WHITE);

        contentPanel.add(buildHomePage(),     "home");
        contentPanel.add(buildInvoicePage(),  "invoice");
        contentPanel.add(buildCustomerPage(), "customers");
        contentPanel.add(buildReminderPage(), "reminders");
        contentPanel.add(buildReportPage(),   "reports");

        return contentPanel;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SAYFA 1 — ANA SAYFA
    // ════════════════════════════════════════════════════════════════════════
    /**
     * Genel finansal özet kartlarını gösterir:
     * Toplam gelir, toplam gider ve net bakiye.
     */
    private JPanel buildHomePage() {
        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setBackground(PINK_LIGHT);

        // Üst başlık çubuğu
        page.add(buildPageHeader("Ana Sayfa", "Finansal genel durumunuz"), BorderLayout.NORTH);

        // Özet kartlar
        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setOpaque(false);
        cards.setBorder(BorderFactory.createEmptyBorder(24, 24, 16, 24));
        cards.add(buildSummaryCard("Toplam Gelir",  "₺ 84.500",  GREEN_OK,    "Bu ay +%12"));
        cards.add(buildSummaryCard("Toplam Gider",  "₺ 31.200",  RED_LATE,    "Bu ay -%5"));
        cards.add(buildSummaryCard("Net Bakiye",    "₺ 53.300",  PINK_MAIN,   "Güncellendi"));
        page.add(cards, BorderLayout.CENTER);

        // Alt bilgi
        JLabel info = new JLabel("  Son güncelleme: Bugün  |  Aktif fatura: 4  |  Gecikmiş ödeme: 2", SwingConstants.LEFT);
        info.setFont(new Font("Arial", Font.PLAIN, 11));
        info.setForeground(TEXT_GRAY);
        info.setBorder(BorderFactory.createEmptyBorder(0, 24, 16, 24));
        page.add(info, BorderLayout.SOUTH);

        return page;
    }

    // Özet kart bileşeni oluşturur (gelir, gider, bakiye gibi). 
    private JPanel buildSummaryCard(String title, String value, Color accentColor, String sub) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 220, 225), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;

        // Üst renkli çizgi (vurgu)
        JPanel accent = new JPanel();
        accent.setBackground(accentColor);
        accent.setPreferredSize(new Dimension(40, 4));
        card.add(accent, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(12, 0, 4, 0);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLbl.setForeground(TEXT_GRAY);
        card.add(titleLbl, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 8, 0);
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Georgia", Font.BOLD, 26));
        valueLbl.setForeground(accentColor);
        card.add(valueLbl, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 0, 0);
        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        subLbl.setForeground(TEXT_GRAY);
        card.add(subLbl, gbc);

        return card;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SAYFA 2 — FATURA OLUŞTUR
    // ════════════════════════════════════════════════════════════════════════
    /**
     * Yeni fatura oluşturma formu.
     * Müşteri, tutar, tarih, KDV oranı ve açıklama alanları içerir.
     * "Kaydet" butonu InvoiceService.createInvoice() metodunu çağırır.
     */
    private JPanel buildInvoicePage() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(PINK_LIGHT);
        page.add(buildPageHeader("Fatura Oluştur", "Yeni fatura düzenle ve sisteme kaydet"), BorderLayout.NORTH);

        // Form paneli
        JPanel form = new JPanel(null);
        form.setBackground(WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Sol sütun etiketleri ve alanları
        addFormRow(form, "Müşteri Adı *",     createStyledTextField("Müşteri seçin veya yazın"),  50,  80);
        addFormRow(form, "Fatura No",          createStyledTextField("Otomatik atanır"),            50, 145);
        addFormRow(form, "Toplam Tutar (₺) *", createStyledTextField("0.00"),                      50, 210);
        addFormRow(form, "KDV Oranı",          createStyledTextField("%18 (Standart)"),             50, 275);
        addFormRow(form, "Vade Tarihi",        createStyledTextField("gg/aa/yyyy"),                 50, 340);

        JLabel lblNot = new JLabel("Açıklama / Not:");
        lblNot.setFont(new Font("Arial", Font.BOLD, 12));
        lblNot.setForeground(TEXT_DARK);
        lblNot.setBounds(50, 405, 200, 20);
        form.add(lblNot);

        JTextArea txtNote = new JTextArea();
        txtNote.setFont(new Font("Arial", Font.PLAIN, 12));
        txtNote.setLineWrap(true);
        JScrollPane noteSp = new JScrollPane(txtNote);
        noteSp.setBounds(50, 428, 450, 70);
        noteSp.setBorder(BorderFactory.createLineBorder(PINK_SOFT, 1));
        form.add(noteSp);

        // Kaydet butonu
        JButton saveBtn = createPinkButton("Faturayı Oluştur ve Kaydet");
        saveBtn.setBounds(50, 520, 250, 42);
        form.add(saveBtn);

        // İptal butonu
        JButton cancelBtn = new JButton("Temizle");
        cancelBtn.setBounds(315, 520, 120, 42);
        cancelBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        cancelBtn.setForeground(TEXT_GRAY);
        cancelBtn.setBackground(WHITE);
        cancelBtn.setBorder(BorderFactory.createLineBorder(PINK_SOFT, 1, true));
        cancelBtn.setFocusPainted(false);
        form.add(cancelBtn);

        // Kaydet aksiyonu — InvoiceService bağlantısı
        saveBtn.addActionListener(e -> {
            // Aşama 4'te gerçek Invoice nesnesi oluşturulup servise gönderilecek
            // invoiceService.createInvoice(newInvoice);
            JOptionPane.showMessageDialog(this,
                "Fatura başarıyla oluşturuldu ve sisteme kaydedildi.",
                "İşlem Başarılı", JOptionPane.INFORMATION_MESSAGE);
        });

        cancelBtn.addActionListener(e -> {
            // Tüm form alanlarını temizle — Aşama 4'te geliştirilecek
            JOptionPane.showMessageDialog(this, "Form temizlendi.", "Bilgi", JOptionPane.PLAIN_MESSAGE);
        });

        page.add(new JScrollPane(form), BorderLayout.CENTER);
        return page;
    }

    /** Form satırı (etiket + alan) ekler — fatura formu için yardımcı metod. */
    private void addFormRow(JPanel panel, String label, JTextField field, int x, int y) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(TEXT_DARK);
        lbl.setBounds(x, y, 200, 20);
        field.setBounds(x, y + 24, 450, 36);
        panel.add(lbl);
        panel.add(field);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SAYFA 3 — MÜŞTERİ LİSTESİ
    // ════════════════════════════════════════════════════════════════════════
    /**
     * Kayıtlı tüm müşterileri tablo halinde listeler.
     * Durum sütunu renk kodlu rozet ile gösterilir.
     */
    private JPanel buildCustomerPage() {
        JPanel page = new JPanel(new BorderLayout(0, 8));
        page.setBackground(PINK_LIGHT);
        page.add(buildPageHeader("Müşteri Listesi", "Kayıtlı tüm müşteriler ve cari bakiyeleri"), BorderLayout.NORTH);

        // Arama ve filtre çubuğu
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        toolbar.setOpaque(false);
        JTextField searchField = createStyledTextField("Müşteri adı veya vergi no ile ara...");
        searchField.setPreferredSize(new Dimension(280, 34));
        JButton searchBtn = createPinkButton("Ara");
        searchBtn.setPreferredSize(new Dimension(80, 34));
        JButton addBtn = new JButton("+ Yeni Müşteri");
        addBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addBtn.setForeground(PINK_MAIN);
        addBtn.setBackground(WHITE);
        addBtn.setBorder(BorderFactory.createLineBorder(PINK_MAIN, 1, true));
        addBtn.setFocusPainted(false);
        addBtn.setPreferredSize(new Dimension(130, 34));
        toolbar.add(searchField);
        toolbar.add(searchBtn);
        toolbar.add(addBtn);
        page.add(toolbar, BorderLayout.NORTH);  // üste ekle (header'ın altına gelecek)

        // Tablo
        String[] cols = {"Müşteri ID", "Ünvan", "Sektör", "Vergi No", "Açık Bakiye", "Son İşlem", "Durum"};
        Object[][] data = {
            {"101", "Ural Teknoloji A.Ş.",   "Yazılım",   "1234567890", "₺15.500", "12 May 2026", "Aktif"},
            {"102", "Erdem Lojistik Ltd.",    "Lojistik",  "9876543210", "₺2.100",  "10 May 2026", "Bekliyor"},
            {"103", "Yılmaz Yazılım A.Ş.",   "Teknoloji", "1122334455", "₺0",      "05 May 2026", "Aktif"},
            {"104", "Öztürk Gıda San.",       "Gıda",      "5566778899", "₺45.000", "01 May 2026", "Gecikmiş"},
            {"105", "Kaya İnşaat Ltd.",       "İnşaat",    "3344556677", "₺8.750",  "28 Nis 2026", "Bekliyor"},
        };

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; } // Düzenlemeyi kapat
        };
        JTable table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(PINK_LIGHT);
        table.getTableHeader().setForeground(TEXT_DARK);
        table.setGridColor(new Color(240, 235, 238));
        table.setSelectionBackground(PINK_LIGHT);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        // "Durum" sütununu renkli göster
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
        sp.getViewport().setBackground(WHITE);

        // Yeni müşteri ekleme simülasyonu
        addBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Yeni müşteri formu açılıyor...\n(Aşama 4'te tam form bağlanacak)",
                "Müşteri Ekle", JOptionPane.INFORMATION_MESSAGE));

        page.add(sp, BorderLayout.CENTER);
        return page;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SAYFA 4 — HATIRLATICILARI
    // ════════════════════════════════════════════════════════════════════════
    /**
     * Vadesi geçen faturaları listeler ve hatırlatma gönderim geçmişini gösterir.
     * ReminderService.collectUnpaids() ve sendReminder() metodlarına bağlıdır.
     */
    private JPanel buildReminderPage() {
        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setBackground(PINK_LIGHT);
        page.add(buildPageHeader("Hatırlatıcılar", "Vadesi geçen ödemeler ve gönderim geçmişi"), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(1, 2, 12, 0));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));

        // --- Sol: Bekleyen hatırlatmalar ---
        JPanel leftCard = buildCard("⚠  Bekleyen Hatırlatmalar");

        // Hatırlatma satırları (simülasyon — ileride ReminderService'ten gelecek)
        String[][] reminders = {
            {"Öztürk Gıda San.",  "₺45.000", "32 gün gecikti", "INV-2026-0041"},
            {"Kaya İnşaat Ltd.",  "₺8.750",  "18 gün gecikti", "INV-2026-0038"},
            {"Erdem Lojistik",    "₺2.100",  "5 gün gecikti",  "INV-2026-0039"},
        };

        for (String[] r : reminders) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 230, 235)),
                BorderFactory.createEmptyBorder(10, 4, 10, 4)));

            JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
            info.setOpaque(false);
            JLabel name = new JLabel(r[0]);
            name.setFont(new Font("Arial", Font.BOLD, 13));
            name.setForeground(TEXT_DARK);
            JLabel detail = new JLabel(r[3] + "  •  " + r[2]);
            detail.setFont(new Font("Arial", Font.PLAIN, 11));
            detail.setForeground(TEXT_GRAY);
            info.add(name); info.add(detail);

            JPanel right = new JPanel(new GridLayout(2, 1, 0, 4));
            right.setOpaque(false);
            JLabel amount = new JLabel(r[1], SwingConstants.RIGHT);
            amount.setFont(new Font("Georgia", Font.BOLD, 14));
            amount.setForeground(RED_LATE);
            JButton sendBtn = new JButton("Gönder");
            sendBtn.setFont(new Font("Arial", Font.BOLD, 11));
            sendBtn.setBackground(PINK_MAIN);
            sendBtn.setForeground(WHITE);
            sendBtn.setBorderPainted(false);
            sendBtn.setFocusPainted(false);
            final String custName = r[0];
            // ReminderService.sendReminder() çağrısı
            sendBtn.addActionListener(e -> {
                reminderService.sendReminder(custName, "E-posta");
                JOptionPane.showMessageDialog(page,
                    custName + " müşterisine hatırlatma gönderildi.",
                    "Hatırlatma Gönderildi", JOptionPane.INFORMATION_MESSAGE);
            });
            right.add(amount); right.add(sendBtn);

            row.add(info, BorderLayout.CENTER);
            row.add(right, BorderLayout.EAST);
            leftCard.add(row);
        }

        // Toplu gönder butonu
        JButton bulkBtn = createPinkButton("Tümüne Toplu Hatırlatma Gönder");
        bulkBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        bulkBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(page,
                "Tüm gecikmiş müşterilere hatırlatma gönderildi.",
                "Toplu Gönderim", JOptionPane.INFORMATION_MESSAGE));
        leftCard.add(Box.createVerticalStrut(12));
        leftCard.add(bulkBtn);

        // --- Sağ: Gönderim geçmişi ---
        JPanel rightCard = buildCard("✓  Gönderim Geçmişi");

        String[][] history = {
            {"Ural Teknoloji",  "E-posta", "12 May 2026"},
            {"Erdem Lojistik",  "SMS",     "10 May 2026"},
            {"Öztürk Gıda",     "E-posta", "07 May 2026"},
            {"Kaya İnşaat",     "SMS",     "05 May 2026"},
        };

        for (String[] h : history) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 230, 235)));

            JLabel nameLbl = new JLabel("  " + h[0]);
            nameLbl.setFont(new Font("Arial", Font.PLAIN, 13));
            nameLbl.setForeground(TEXT_DARK);

            JLabel channelLbl = new JLabel(h[1] + "  ");
            channelLbl.setFont(new Font("Arial", Font.BOLD, 11));
            channelLbl.setForeground(GREEN_OK);

            JLabel dateLbl = new JLabel(h[2] + "  ", SwingConstants.RIGHT);
            dateLbl.setFont(new Font("Arial", Font.PLAIN, 11));
            dateLbl.setForeground(TEXT_GRAY);

            row.add(nameLbl, BorderLayout.WEST);
            row.add(channelLbl, BorderLayout.CENTER);
            row.add(dateLbl, BorderLayout.EAST);
            row.setPreferredSize(new Dimension(0, 38));
            rightCard.add(row);
        }

        body.add(leftCard);
        body.add(rightCard);
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SAYFA 5 — FİNANSAL RAPORLAR
    // ════════════════════════════════════════════════════════════════════════
    /**
     * Kar-zarar özeti ve dönemsel finansal verileri gösterir.
     * SituationService.calculateProfitLoss() ile bağlanacak.
     */
    private JPanel buildReportPage() {
        JPanel page = new JPanel(new BorderLayout(0, 0));
        page.setBackground(PINK_LIGHT);
        page.add(buildPageHeader("Finansal Raporlar", "Dönemsel kar-zarar ve gelir/gider analizi"), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(2, 2, 12, 12));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Kar-Zarar Özeti
        JPanel klCard = buildCard("Kar / Zarar Özeti — Mayıs 2026");
        addReportRow(klCard, "Toplam Gelir",  "₺84.500", GREEN_OK);
        addReportRow(klCard, "Toplam Gider",  "₺31.200", RED_LATE);
        addReportRow(klCard, "Net Kar",       "₺53.300", PINK_MAIN);
        body.add(klCard);

        // Gelir Kaynakları
        JPanel gelirCard = buildCard("Gelir Kaynakları");
        addReportRow(gelirCard, "Fatura Gelirleri",   "₺72.000", TEXT_DARK);
        addReportRow(gelirCard, "Diğer Gelirler",     "₺12.500", TEXT_DARK);
        body.add(gelirCard);

        // Gider Dağılımı
        JPanel giderCard = buildCard("Gider Dağılımı");
        addReportRow(giderCard, "Operasyonel Giderler", "₺18.000", TEXT_DARK);
        addReportRow(giderCard, "Tedarikçi Ödemeleri",  "₺13.200", TEXT_DARK);
        body.add(giderCard);

        // Gecikmiş Ödemeler
        JPanel gecikCard = buildCard("Gecikmiş Ödemeler");
        addReportRow(gecikCard, "Öztürk Gıda San.",  "₺45.000", RED_LATE);
        addReportRow(gecikCard, "Kaya İnşaat Ltd.",  "₺8.750",  ORANGE_WAIT);
        addReportRow(gecikCard, "Erdem Lojistik",    "₺2.100",  ORANGE_WAIT);
        body.add(gecikCard);

        // CSV Export butonu (Simay'ın Python modülüne bağlanacak)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        btnPanel.setOpaque(false);
        JButton exportBtn = createPinkButton("CSV Olarak Dışa Aktar (Python Analizi)");
        exportBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(page,
                "Veriler Python analiz modülüne aktarıldı.\n(Simay'ın export metodu Aşama 4'te bağlanacak)",
                "Dışa Aktarma", JOptionPane.INFORMATION_MESSAGE));
        btnPanel.add(exportBtn);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(body, BorderLayout.CENTER);
        south.add(btnPanel, BorderLayout.SOUTH);
        page.add(south, BorderLayout.CENTER);

        return page;
    }

    /** Rapor kartına etiket-değer satırı ekler. */
    private void addReportRow(JPanel card, String label, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 230, 235)));
        row.setPreferredSize(new Dimension(0, 36));
        JLabel lbl = new JLabel("  " + label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setForeground(TEXT_DARK);
        JLabel val = new JLabel(value + "  ");
        val.setFont(new Font("Georgia", Font.BOLD, 14));
        val.setForeground(valueColor);
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        card.add(row);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  YARDIMCI BILEŞENLER
    // ════════════════════════════════════════════════════════════════════════

    /** Sayfa üst başlık çubuğu oluşturur. */
    private JPanel buildPageHeader(String title, String subtitle) {
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 220, 225)),
            BorderFactory.createEmptyBorder(16, 24, 12, 24)));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Georgia", Font.BOLD, 22));
        t.setForeground(TEXT_DARK);
        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Arial", Font.PLAIN, 12));
        s.setForeground(TEXT_GRAY);
        header.add(t); header.add(s);
        return header;
    }

    /** İçerik kartı (beyaz arka planlı kutu) oluşturur. */
    private JPanel buildCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 220, 225), 1, true),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(PINK_MAIN);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lbl);
        card.add(Box.createVerticalStrut(10));
        return card;
    }

    /** Pembe stilinde buton oluşturur. */
    private JButton createPinkButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(PINK_MAIN);
        btn.setForeground(WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Pembe kenarlıklı metin alanı oluşturur. */
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setForeground(TEXT_GRAY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_SOFT, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        // Placeholder davranışı
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText(""); field.setForeground(TEXT_DARK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder); field.setForeground(TEXT_GRAY);
                }
            }
        });
        return field;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  "DURUM" SÜTUNU ÖZEL RENDERER
    // ════════════════════════════════════════════════════════════════════════
    /**
     * Müşteri tablosunda "Durum" sütununu renk kodlu gösterir:
     * Aktif = Yeşil | Bekliyor = Turuncu | Gecikmiş = Kırmızı
     */
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, col);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setOpaque(true);
            String status = value.toString();
            switch (status) {
                case "Aktif":     lbl.setForeground(new Color(39, 174, 96));  break;
                case "Bekliyor":  lbl.setForeground(new Color(230, 126, 34)); break;
                case "Gecikmiş":  lbl.setForeground(new Color(192, 57, 43));  break;
                default:          lbl.setForeground(Color.GRAY);
            }
            lbl.setBackground(isSelected ? new Color(255, 228, 235) : Color.WHITE);
            return lbl;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  MAIN — PROGRAM BAŞLANGIÇ NOKTASI
    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DashboardUI ui = new DashboardUI();
            ui.showLoginScreen(); // Önce giriş ekranını göster
        });
    }
}
