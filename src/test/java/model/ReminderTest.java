package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

// Hatırlatma modelinin temel fonksiyonlarını test eden sınıf.
public class ReminderTest {

    @Test
    public void testReminderCreation() {
        Date scheduleDate = new Date();
        Reminder reminder = new Reminder("SMS", scheduleDate);

        assertEquals("SMS", reminder.getChannel());
        assertEquals(scheduleDate, reminder.getScheduledAt());
    }

    @Test
    public void testReminderWithRetries() {
        Date scheduleDate = new Date();
        Reminder reminder = new Reminder("email", scheduleDate, 3, 7);

        assertEquals("email", reminder.getChannel());
        assertEquals(3, reminder.getMaxRetries());
        assertEquals(7, reminder.getIntervalDays());
    }

    @Test
    public void testChannelChange() {
        Reminder reminder = new Reminder("SMS", new Date());
        reminder.setChannel("email");
        assertEquals("email", reminder.getChannel());
    }

    @Test
    public void testMaxRetriesUpdate() {
        Reminder reminder = new Reminder("email", new Date(), 3, 7);
        reminder.setMaxRetries(5);
        assertEquals(5, reminder.getMaxRetries());
    }

    @Test
    public void testIntervalDaysUpdate() {
        Reminder reminder = new Reminder("SMS", new Date(), 3, 7);
        reminder.setIntervalDays(14);
        assertEquals(14, reminder.getIntervalDays());
    }
}
