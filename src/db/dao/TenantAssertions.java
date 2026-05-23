package db.dao;

import java.sql.*;

public final class TenantAssertions {

    private TenantAssertions() {}

    public static void assertCustomerOwnedByCompany(Connection connection, long companyId, long customerId)
            throws SQLException {
        final String sql = "SELECT 1 FROM customer WHERE id = ? AND company_id = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            ps.setLong(2, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Cari bu sirkete ait degil veya bulunamadi (customer_id=" + customerId + ")");
                }
            }
        }
    }

    public static void assertInvoiceOwnedByCompany(Connection connection, long companyId, long invoiceId)
            throws SQLException {
        final String sql = "SELECT 1 FROM invoice WHERE id = ? AND company_id = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, invoiceId);
            ps.setLong(2, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Fatura bu sirkete ait degil veya bulunamadi (invoice_id=" + invoiceId + ")");
                }
            }
        }
    }
}
