package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

// Hatırlatma modelinin temel fonksiyonlarını test eden sınıf.
public class ReminderTest {

    @Test
    public void testReminderCreation() {
        // Yeni bir hatırlatma planı oluşturalım
        Date scheduleDate = new Date();
        Reminder reminder = new Reminder("SMS", scheduleDate);

        // Bilgileri doğrular
        assertEquals("SMS", reminder.getChannel()); // Kanal doğru mu?
        assertEquals(scheduleDate, reminder.getScheduledAt()); // Planlanan tarih doğru mu?
    }
}
