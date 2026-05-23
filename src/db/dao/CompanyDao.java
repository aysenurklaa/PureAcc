package db.dao;

import model.Company;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CompanyDao {

    private CompanyDao() {}

    public static long insert(Connection connection, Company company) throws SQLException {
        final String sql = "INSERT INTO company (tax_id, name) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, company.getTaxId());
            ps.setString(2, company.getName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("Company insert did not return generated id");
    }

    public static Optional<Company> findByTaxId(Connection connection, String taxId) throws SQLException {
        final String sql = "SELECT id, tax_id, name FROM company WHERE tax_id = ? COLLATE NOCASE LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, taxId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Company c = new Company(rs.getString("tax_id"), rs.getString("name"));
                c.setId(rs.getLong("id"));
                return Optional.of(c);
            }
        }
    }

    public static List<Company> listAll(Connection connection) throws SQLException {
        final String sql = "SELECT id, tax_id, name FROM company ORDER BY id";
        List<Company> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Company c = new Company(rs.getString("tax_id"), rs.getString("name"));
                c.setId(rs.getLong("id"));
                out.add(c);
            }
        }
        return out;
    }

    public static void updatePasswordHash(Connection connection, long companyId, String bcryptHash)
            throws SQLException {
        if (bcryptHash == null || bcryptHash.length() < 55 || bcryptHash.length() > 80)
            throw new IllegalArgumentException("password_hash gecerli bir BCrypt dizesi olmalidir");
        final String sql = "UPDATE company SET password_hash = ?, updated_at = datetime('now') WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, bcryptHash);
            ps.setLong(2, companyId);
            int n = ps.executeUpdate();
            if (n != 1) throw new SQLException("Sirket bulunamadi veya guncellenemedi (id=" + companyId + ")");
        }
    }

    public static Optional<String> findPasswordHashByCompanyId(Connection connection, long companyId)
            throws SQLException {
        final String sql = "SELECT password_hash FROM company WHERE id = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                String h = rs.getString(1);
                if (rs.wasNull() || h == null || h.isBlank()) return Optional.empty();
                return Optional.of(h);
            }
        }
    }
}
