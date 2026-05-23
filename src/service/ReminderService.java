package service;

import model.Invoice;
import model.InvoiceStatus;
import model.Reminder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Ödeme hatırlatmalarını yöneten servis sınıfı.
public class ReminderService {

    private final List<Reminder> reminderLog = new ArrayList<>();

    // Vadesi geçen veya ödenmemiş faturaları filtreler.
    public List<Invoice> collectUnpaids(List<Invoice> allInvoices) {
        List<Invoice> unpaids = new ArrayList<>();
        for (Invoice inv : allInvoices) {
            if (inv.getStatus() == InvoiceStatus.BEKLEMEDE ||
                inv.getStatus() == InvoiceStatus.GECIKTI) {
                unpaids.add(inv);
            }
        }
        return unpaids;
    }

    // Varsayılan değerlerle hatırlatma gönderir (maxRetries=3, intervalDays=7).
    public void sendReminder(String customerName, String channel) {
        sendReminder(customerName, channel, 3, 7);
    }

    // Müşteriye hatırlatma gönderir.
    // channel: "email" veya "sms"
    // maxRetries: gönderilemezse kaç kez tekrar denenecek
    // intervalDays: kaç günde bir hatırlatılacak
    public void sendReminder(String customerName, String channel, int maxRetries, int intervalDays) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("Müşteri adı boş olamaz.");
        }
        if (!channel.equalsIgnoreCase("email") && !channel.equalsIgnoreCase("sms")) {
            throw new IllegalArgumentException("Geçersiz kanal: " + channel + ". 'email' veya 'sms' olmalıdır.");
        }

        // Bildirim stratejisini seç
        NotificationService notifier;
        if (channel.equalsIgnoreCase("sms")) {
            notifier = new SmsNotification();
        } else {
            notifier = new EmailNotification();
        }

        String message = "Sayın " + customerName + ", vadesi geçmiş ödemeniz bulunmaktadır. "
                       + intervalDays + " gün içinde ödeme yapmanızı rica ederiz.";

        // Gönderim — başarısız olursa maxRetries kadar tekrar dene
        boolean sent = false;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                notifier.send(customerName, message);
                sent = true;
                break;
            } catch (Exception e) {
                System.out.println("Gönderim başarısız (deneme " + attempt + "/" + maxRetries + "): " + e.getMessage());
            }
        }

        if (!sent) {
            System.out.println("Hata: " + maxRetries + " denemeden sonra hatırlatma gönderilemedi. Müşteri: " + customerName);
        }

        // Hatırlatmayı kayıt altına al
        Reminder reminder = new Reminder(channel, new Date(), maxRetries, intervalDays);
        reminderLog.add(reminder);
    }

    // Gönderilmiş tüm hatırlatmaların geçmişini döndürür.
    public List<Reminder> getReminderLog() {
        return new ArrayList<>(reminderLog);
    }
}
