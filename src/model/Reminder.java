package model;

import java.util.Date;

@SuppressWarnings("unused")
public class Reminder {

    private String channel;
    private Date scheduledAt;
    private int maxRetries;
    private int intervalDays;

    public Reminder() {}

    public Reminder(String channel, Date scheduledAt) {
        this.channel = channel;
        this.scheduledAt = scheduledAt;
    }

    public Reminder(String channel, Date scheduledAt, int maxRetries, int intervalDays) {
        this.channel = channel;
        this.scheduledAt = scheduledAt;
        this.maxRetries = maxRetries;
        this.intervalDays = intervalDays;
    }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public Date getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(Date scheduledAt) { this.scheduledAt = scheduledAt; }

    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

    public int getIntervalDays() { return intervalDays; }
    public void setIntervalDays(int intervalDays) { this.intervalDays = intervalDays; }
}
