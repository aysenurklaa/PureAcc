package service;

import model.Invoice;
import model.InvoiceStatus;
import java.util.ArrayList;
import java.util.List;

/**
 * OdemeHatirlatmaService — Vadesi yaklaşan ve geçen ödemeleri tespit eder.
 */
public class ReminderService {
    
    // Vadesi geçen veya ödenmemiş faturaları filtrele
    public List<Invoice> collectUnpaids(List<Invoice> allInvoices) {
        List<Invoice> unpaids = new ArrayList<>();
        for (Invoice inv : allInvoices) {
            // Rapor: Sadece BEKLEMEDE veya GECIKTI olanları listeye ekle
            if (inv.getStatus() == InvoiceStatus.BEKLEMEDE || inv.getStatus() == InvoiceStatus.GECIKTI) {
                unpaids.add(inv);
            }
        }
        return unpaids;
    }

    // Müşteriye bildirim gönderildiğini sisteme kaydet
    public void sendReminder(String customerName, String channel) {
        // Rapor: E-posta veya SMS yoluyla hatırlatma mesajı simülasyonu
        System.out.println(customerName + " adlı müşteriye " + channel + " üzerinden ödeme hatırlatması iletildi.");
    }
}
