package service;

/**
 * EmailNotification — E-posta bildirim stratejisi.
 * NotificationService arayüzünü implement eder.
 */
public class EmailNotification implements NotificationService {

    @Override
    public void send(String recipientId, String message) {
        // TODO: JavaMail veya benzeri kütüphaneyle gerçek e-posta gönderimi yapılacak.
        System.out.println("[EMAIL] Alıcı: " + recipientId + " | Mesaj: " + message);
    }
}
