package service;

 // NotificationService — Bildirim gönderim stratejileri için ortak arayüz.

public interface NotificationService {
     // Belirtilen alıcıya mesaj gönderir.
    void send(String recipientId, String message);
}
