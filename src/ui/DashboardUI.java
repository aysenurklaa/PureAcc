package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import service.InvoiceService;
import service.ReminderService;


 // PureAcc Dijital Muhasebe Sistemi - Ana Arayüz Sınıfı
public class DashboardUI extends JFrame {
    
    // İş mantığı için servis bağlantıları
    private InvoiceService invoiceService = new InvoiceService();
    private ReminderService reminderService = new ReminderService();
    
    // Ekranlar arası geçişi sağlayan yapı
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public DashboardUI() {
        // Pencere genel ayarları
        setTitle("PureAcc - Yönetim Paneli");
        setSize(1050, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Ekranın merkezinde açılması için

        // Ana düzen oluşturma
        JPanel mainLayout = new JPanel(new BorderLayout());
        
        // --- 1. SOL MENÜ  ---
        JPanel sideMenu = new JPanel();
        sideMenu.setPreferredSize(new Dimension(230, 650));
        sideMenu.setBackground(new Color(255, 182, 193)); // Rapordaki imza pembe rengimiz
        sideMenu.setLayout(new GridLayout(10, 1, 5, 5));

        // Logo Alanı
        JLabel logoLabel = new JLabel("PureAcc", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 30));
        logoLabel.setForeground(Color.WHITE);
        sideMenu.add(logoLabel);

        // Menü Butonları (Tıklanınca sağ taraf değişecek)
        sideMenu.add(createMenuButton("Ana Sayfa", "home"));
        sideMenu.add(createMenuButton("Fatura Oluştur", "invoice"));
        sideMenu.add(createMenuButton("Müşteri Listesi", "customers"));
        sideMenu.add(createMenuButton("Hatırlatıcılar", "reminders"));
        sideMenu.add(createMenuButton("Finansal Raporlar", "reports"));

        // --- 2. SAĞ İÇERİK ALANI (DEĞİŞKEN PANELLER) ---
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Sayfaları sisteme ekleme
        contentPanel.add(createHomePage(), "home");
        contentPanel.add(createInvoicePage(), "invoice");
        contentPanel.add(createCustomerPage(), "customers");
        contentPanel.add(createReminderPage(), "reminders");

        // Düzeni birleştirme
        mainLayout.add(sideMenu, BorderLayout.WEST);
        mainLayout.add(contentPanel, BorderLayout.CENTER);

        add(mainLayout);
    }

    
     // Login (Giriş) Ekranı
    public void showLoginScreen() {
        JFrame loginFrame = new JFrame("PureAcc - Giriş Yap");
        loginFrame.setSize(400, 350);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(new Color(255, 182, 193)); // Pembe arka plan

        JLabel title = new JLabel("PUREACC GİRİŞ", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JTextField userField = new JTextField("Kullanıcı Adı");
        JPasswordField passField = new JPasswordField();
        
        JButton loginBtn = new JButton("Sisteme Giriş");
        loginBtn.setBackground(Color.WHITE);
        loginBtn.setFocusPainted(false);

        // Giriş butonu aksiyonu
        loginBtn.addActionListener(e -> {
            loginFrame.dispose(); // Login penceresini kapat
            this.setVisible(true); // Ana uygulamayı aç
        });

        panel.add(title);
        panel.add(userField);
        panel.add(new JLabel("Şifre:"));
        panel.add(passField);
        panel.add(loginBtn);

        loginFrame.add(panel);
        loginFrame.setVisible(true);
    }

    // Menü butonu oluşturma ve stil ayarları
    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 15));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 182, 193), 2));
        
        // Butona basınca ilgili sayfayı göster
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        return btn;
    }

    // 1. Sayfa: Ana Sayfa
    private JPanel createHomePage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel("<html><div style='text-align: center;'><h1>Hoş Geldiniz, Safiye</h1><p>PureAcc ile finansal kontrol parmaklarınızın ucunda.</p></div></html>");
        panel.add(label);
        return panel;
    }

    // 2. Sayfa: Fatura Oluşturma (Form Alanı)
    private JPanel createInvoicePage() {
        JPanel panel = new JPanel(null); // Özel konumlandırma için null layout
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Yeni Fatura Düzenle");
        title.setBounds(50, 30, 300, 30);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel lblCust = new JLabel("Müşteri Seçiniz:");
        lblCust.setBounds(50, 100, 150, 25);
        JTextField txtCust = new JTextField();
        txtCust.setBounds(200, 100, 250, 30);

        JLabel lblAmount = new JLabel("Toplam Tutar (TL):");
        lblAmount.setBounds(50, 150, 150, 25);
        JTextField txtAmount = new JTextField();
        txtAmount.setBounds(200, 150, 250, 30);

        JButton saveBtn = new JButton("Faturayı Kaydet ve Onayla");
        saveBtn.setBounds(200, 220, 250, 40);
        saveBtn.setBackground(new Color(219, 112, 147)); // Koyu pembe buton
        saveBtn.setForeground(Color.WHITE);

        // Kaydetme butonu aksiyonu
        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Fatura başarıyla oluşturuldu ve sisteme işlendi.");
        });

        panel.add(title); panel.add(lblCust); panel.add(txtCust);
        panel.add(lblAmount); panel.add(txtAmount); panel.add(saveBtn);
        return panel;
    }

    // 3. Sayfa: Müşteri Listesi (Tablo Görünümü)
    private JPanel createCustomerPage() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Müşteri Veritabanı", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        // Tablo verileri ve kolonları
        String[] columns = {"ID", "Müşteri Ünvanı", "Bakiye", "Durum"};
        Object[][] data = {
            {"101", "Ural Teknoloji", "15.500 TL", "Aktif"},
            {"102", "Erdem Lojistik", "2.100 TL", "Ödeme Bekliyor"},
            {"103", "Yılmaz Yazılım", "0 TL", "Düzenli"},
            {"104", "Öztürk Gıda", "45.000 TL", "Gecikmiş"}
        };

        JTable table = new JTable(new DefaultTableModel(data, columns));
        table.setRowHeight(30);
        JScrollPane sp = new JScrollPane(table);

        panel.add(title, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    // 4. Sayfa: Hatırlatıcılar
    private JPanel createReminderPage() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("Yaklaşan ödeme ve tahsilat hatırlatmaları burada görünecektir."));
        return panel;
    }

     // Program Başlangıç Noktası
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DashboardUI ui = new DashboardUI();
            ui.showLoginScreen(); // Programı giriş ekranıyla başlat
        });
    }
}
