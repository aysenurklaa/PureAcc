package db.dao;

import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CustomerDao {

    private CustomerDao() {}

    public static long insert(Connection connection, long companyId, Customer customer) throws SQLException {
        if (customer.getType() == null) throw new SQLException("Customer type is required");
        final String sql = "INSERT INTO customer (company_id, customer_code, name, type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, companyId);
            ps.setString(2, customer.getCustomerId());
            ps.setString(3, customer.getName());
            ps.setString(4, customer.getType().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("Customer insert did not return generated id");
    }

    public static Optional<Long> findIdByCode(Connection connection, long companyId, String customerCode)
            throws SQLException {
        final String sql = "SELECT id FROM customer WHERE company_id = ? AND customer_code = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, companyId);
            ps.setString(2, customerCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(rs.getLong(1));
            }
        }
    }

    public static List<Customer> listByCompany(Connection connection, long companyId) throws SQLException {
        final String sql = "SELECT id, customer_code, name, type FROM customer WHERE company_id = ? ORDER BY id";
        List<Customer> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Customer c = new Customer(rs.getString("customer_code"), rs.getString("name"));
                    c.setId(rs.getLong("id"));
                    c.setType(Customer.CustomerType.valueOf(rs.getString("type")));
                    out.add(c);
                }
            }
        }
        return out;
    }
}
