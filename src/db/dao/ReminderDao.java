package db.dao;

import model.Reminder;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class ReminderDao {

    private static final DateTimeFormatter STORAGE_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ReminderDao() {}

    public static long insert(Connection connection, long companyId, long invoiceId, Reminder reminder)
            throws SQLException {
        TenantAssertions.assertInvoiceOwnedByCompany(connection, companyId, invoiceId);
        final String sql = "INSERT INTO reminder (invoice_id, channel, scheduled_at) VALUES (?, ?, ?)";
        LocalDateTime ldt = LocalDateTime.ofInstant(
                reminder.getScheduledAt().toInstant(), ZoneId.systemDefault());
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, invoiceId);
            ps.setString(2, reminder.getChannel());
            ps.setString(3, ldt.format(STORAGE_TS));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("Reminder insert did not return generated id");
    }

    public static List<Reminder> listByInvoice(Connection connection, long companyId, long invoiceId)
            throws SQLException {
        final String sql = "SELECT r.id, r.channel, r.scheduled_at FROM reminder r INNER JOIN invoice i ON i.id = r.invoice_id WHERE r.invoice_id = ? AND i.company_id = ? ORDER BY r.scheduled_at";
        List<Reminder> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, invoiceId);
            ps.setLong(2, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String raw = rs.getString("scheduled_at");
                    LocalDateTime ldt = parseScheduledAt(raw);
                    Date when = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                    Reminder r = new Reminder(rs.getString("channel"), when);
                    r.setId(rs.getLong("id"));
                    out.add(r);
                }
            }
        }
        return out;
    }

    private static LocalDateTime parseScheduledAt(String raw) {
        try { return LocalDateTime.parse(raw, STORAGE_TS); }
        catch (DateTimeParseException e) { return LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME); }
    }
}
