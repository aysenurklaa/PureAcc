package model;

import java.util.Date;

/**
 * Reminder — fatura hatırlatıcısı (e-posta veya SMS ile gönderilecek).
 */
@SuppressWarnings("unused")
public class Reminder {
    private long id;
    private String channel;
    private Date scheduledAt;

    public Reminder() {}

    public Reminder(String channel, Date scheduledAt) {
        this.channel = channel;
        this.scheduledAt = scheduledAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Date getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Date scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
}
