package service;

/**
 * SmsNotification — SMS bildirim stratejisi.
 * NotificationService arayüzünü implement eder.
 */
public class SmsNotification implements NotificationService {

    @Override
    public void send(String recipientId, String message) {
        // TODO: Twilio veya benzeri SMS servisiyle gerçek gönderim yapılacak.
        System.out.println("[SMS] Alıcı: " + recipientId + " | Mesaj: " + message);
    }
}
