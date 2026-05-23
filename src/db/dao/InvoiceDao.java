package db.dao;

import model.Invoice;
import model.InvoiceStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class InvoiceDao {

    private InvoiceDao() {}

    public static long insert(Connection connection, long companyId, long customerId, Invoice invoice)
            throws SQLException {
        TenantAssertions.assertCustomerOwnedByCompany(connection, companyId, customerId);
        final String sql = "INSERT INTO invoice (company_id, customer_id, invoice_number, invoice_date, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, companyId);
            ps.setLong(2, customerId);
            ps.setString(3, invoice.getInvoiceId());
            ps.setString(4, new Date(invoice.getDate().getTime()).toLocalDate().toString());
            ps.setString(5, invoice.getStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("Invoice insert did not return generated id");
    }

    public static void updateStatus(Connection connection, long companyId, String invoiceNumber, InvoiceStatus status)
            throws SQLException {
        final String sql = "UPDATE invoice SET status = ?, updated_at = datetime('now') WHERE company_id = ? AND invoice_number = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, companyId);
            ps.setString(3, invoiceNumber);
            int n = ps.executeUpdate();
            if (n != 1) throw new SQLException("Expected one row updated, got " + n);
        }
    }

    public static List<Invoice> listByCompany(Connection connection, long companyId) throws SQLException {
        final String sql = "SELECT id, invoice_number, invoice_date, status FROM invoice WHERE company_id = ? ORDER BY invoice_date DESC, id DESC";
        List<Invoice> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date d = Date.valueOf(rs.getString("invoice_date"));
                    Invoice inv = new Invoice(rs.getString("invoice_number"),
                            new java.util.Date(d.getTime()), InvoiceStatus.valueOf(rs.getString("status")));
                    inv.setId(rs.getLong("id"));
                    out.add(inv);
                }
            }
        }
        return out;
    }

    public static Optional<Long> findIdByNumber(Connection connection, long companyId, String invoiceNumber)
            throws SQLException {
        final String sql = "SELECT id FROM invoice WHERE company_id = ? AND invoice_number = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, companyId);
            ps.setString(2, invoiceNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(rs.getLong(1));
            }
        }
    }
}
