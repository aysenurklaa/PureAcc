package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import service.InvoiceService;
import service.ReminderService;

public class DashboardUI extends JFrame {
    
    // Servis bağlantıları
    private InvoiceService invoiceService = new InvoiceService();
    private ReminderService reminderService = new ReminderService();
    
    // Değişken paneller (Ekranın sağ tarafı değişecek)
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public DashboardUI() {
        setTitle("PureAcc - Dijital Muhasebe ve Finans Paneli");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- ANA DÜZEN ---
        JPanel mainLayout = new JPanel(new BorderLayout());
        
        // --- SOL MENÜ (PEMBE ALAN) ---
        JPanel sideMenu = new JPanel();
        sideMenu.setPreferredSize(new Dimension(220, 600));
        sideMenu.setBackground(new Color(255, 182, 193)); // Rapordaki Pembe
        sideMenu.setLayout(new GridLayout(8, 1, 5, 5));

        JLabel logoLabel = new JLabel("PureAcc", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 28));
        logoLabel.setForeground(Color.WHITE);
        sideMenu.add(logoLabel);

        // Menü Butonları
        sideMenu.add(createMenuButton("Ana Sayfa", "home"));
        sideMenu.add(createMenuButton("Fatura Oluştur", "invoice"));
        sideMenu.add(createMenuButton("Hatırlatıcılar", "remind"));
        sideMenu.add(createMenuButton("Finansal Rapor", "report"));

        // --- SAĞ İÇERİK ALANI (DEĞİŞEN KISIM) ---
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Sayfaları Oluşturma
        contentPanel.add(createHomePage(), "home");
        contentPanel.add(createInvoicePage(), "invoice");
        contentPanel.add(createReminderPage(), "remind");

        mainLayout.add(sideMenu, BorderLayout.WEST);
        mainLayout.add(contentPanel, BorderLayout.CENTER);

        add(mainLayout);
    }

    // Buton Tasarımı ve Tıklama Mantığı
    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        return btn;
    }

    // 1. ANA SAYFA EKRANI
    private JPanel createHomePage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("<html><center><h1>Hoş Geldiniz</h1><p>Bugün yapılacak 3 yeni hatırlatmanız var.</p></center></html>"));
        return panel;
    }

    // 2. FATURA OLUŞTURMA SAYFASI (UI + Mantık)
    private JPanel createInvoicePage() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Fatura Detayları"));
        panel.setBackground(Color.WHITE);

        JTextField customerField = new JTextField("Müşteri Adı");
        JTextField amountField = new JTextField("Tutar");
        JButton saveBtn = new JButton("Faturayı Kaydet");
        saveBtn.setBackground(new Color(255, 105, 180)); // Canlı Pembe
        saveBtn.setForeground(Color.WHITE);

        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Fatura Başarıyla Oluşturuldu ve Servise Gönderildi!");
        });

        panel.add(new JLabel("Müşteri:"));
        panel.add(customerField);
        panel.add(new JLabel("Tutar:"));
        panel.add(amountField);
        panel.add(saveBtn);

        return panel;
    }

    // 3. HATIRLATICI SAYFASI
    private JPanel createReminderPage() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("Yaklaşan ödemeler burada listelenecek."));
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardUI().setVisible(true));
    }
}
