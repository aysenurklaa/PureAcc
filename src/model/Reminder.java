package model;
import java.util.Date;
@SuppressWarnings("unused")
public class Reminder {
    private String channel;
    private Date scheduledAt;

    public Reminder() {}

    public Reminder(String channel, Date scheduledAt) {
        this.channel = channel;
        this.scheduledAt = scheduledAt;
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
