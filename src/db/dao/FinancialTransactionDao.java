package db.dao;

import model.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class FinancialTransactionDao {

    private FinancialTransactionDao() {}

    public static long insert(Connection connection, long companyId, Transaction transaction) throws SQLException {
        Long inv = transaction.getInvoiceId();
        if (inv != null) TenantAssertions.assertInvoiceOwnedByCompany(connection, companyId, inv);
        final String sql = "INSERT INTO financial_transaction (company_id, ref, type, amount, invoice_id, is_automatic) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, companyId);
            ps.setString(2, transaction.getId());
            ps.setString(3, transaction.getType());
            ps.setDouble(4, transaction.getAmount());
            if (transaction.getInvoiceId() != null) ps.setLong(5, transaction.getInvoiceId());
            else ps.setNull(5, Types.INTEGER);
            ps.setInt(6, transaction.isAutomatic() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("Transaction insert did not return generated id");
    }

    public static List<Transaction> listByCompany(Connection connection, long companyId) throws SQLException {
        final String sql = "SELECT id, ref, type, amount, invoice_id, is_automatic FROM financial_transaction WHERE company_id = ? ORDER BY id DESC";
        List<Transaction> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction(rs.getString("ref"), rs.getString("type"), rs.getDouble("amount"));
                    t.setDbId(rs.getLong("id"));
                    long invId = rs.getLong("invoice_id");
                    if (!rs.wasNull()) t.setInvoiceId(invId);
                    t.setAutomatic(rs.getInt("is_automatic") == 1);
                    out.add(t);
                }
            }
        }
        return out;
    }
}
