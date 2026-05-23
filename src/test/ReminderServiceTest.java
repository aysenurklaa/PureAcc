package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

// Hatırlatma modelinin temel fonksiyonlarını test eden sınıf.
public class ReminderTest {

    @Test
    public void testReminderCreation() {
        // Yeni bir hatırlatma nesnesi oluşturalım
        Date scheduleDate = new Date();
        Reminder reminder = new Reminder("SMS", scheduleDate);

        assertEquals("SMS", reminder.getChannel());          // Kanal doğru mu?
        assertEquals(scheduleDate, reminder.getScheduledAt()); // Planlanan tarih doğru mu?
    }

    @Test
    public void testReminderWithRetries() {
        // maxRetries ve intervalDays alanları doğru set ediliyor mu?
        Date scheduleDate = new Date();
        Reminder reminder = new Reminder("email", scheduleDate, 3, 7);

        assertEquals("email", reminder.getChannel());      // Kanal doğru mu?
        assertEquals(3, reminder.getMaxRetries());         // Maksimum deneme sayısı doğru mu?
        assertEquals(7, reminder.getIntervalDays());       // Hatırlatma aralığı doğru mu?
    }

    @Test
    public void testChannelChange() {
        // Kanal değişikliği doğru çalışıyor mu?
        Reminder reminder = new Reminder("SMS", new Date());
        reminder.setChannel("email");
        assertEquals("email", reminder.getChannel()); // Kanal güncellendi mi?
    }

    @Test
    public void testMaxRetriesUpdate() {
        // maxRetries güncelleme doğru çalışıyor mu?
        Reminder reminder = new Reminder("email", new Date(), 3, 7);
        reminder.setMaxRetries(5);
        assertEquals(5, reminder.getMaxRetries()); // Güncellenen deneme sayısı doğru mu?
    }

    @Test
    public void testIntervalDaysUpdate() {
        // intervalDays güncelleme doğru çalışıyor mu?
        Reminder reminder = new Reminder("SMS", new Date(), 3, 7);
        reminder.setIntervalDays(14);
        assertEquals(14, reminder.getIntervalDays()); // Güncellenen aralık doğru mu?
    }
}
