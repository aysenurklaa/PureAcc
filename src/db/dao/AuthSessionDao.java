package db.dao;

import security.TokenHasher;
import java.sql.*;
import java.util.Optional;

public final class AuthSessionDao {

    private static final int MIN_OPAQUE_TOKEN_LEN = 16;

    private AuthSessionDao() {}

    public static long insert(Connection connection, long companyId, String opaqueToken, String expiresAtIso)
            throws SQLException {
        if (opaqueToken == null || opaqueToken.length() < MIN_OPAQUE_TOKEN_LEN)
            throw new IllegalArgumentException("Oturum token'i cok kisa veya null");
        if (expiresAtIso == null || expiresAtIso.isBlank())
            throw new IllegalArgumentException("expires_at bos olamaz");
        String hash = TokenHasher.sha256Hex(opaqueToken);
        final String sql = "INSERT INTO auth_session (company_id, token_hash, expires_at) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, companyId);
            ps.setString(2, hash);
            ps.setString(3, expiresAtIso.trim());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("auth_session insert did not return generated id");
    }

    public static Optional<Long> findActiveCompanyByOpaqueToken(Connection connection, String opaqueToken)
            throws SQLException {
        if (opaqueToken == null || opaqueToken.length() < MIN_OPAQUE_TOKEN_LEN) return Optional.empty();
        String hash = TokenHasher.sha256Hex(opaqueToken);
        final String sql = "SELECT company_id FROM auth_session WHERE token_hash = ? AND invalidated = 0 AND datetime(replace(expires_at, 'T', ' ')) > datetime('now') LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, hash);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(rs.getLong(1));
            }
        }
    }

    public static int invalidateByOpaqueToken(Connection connection, String opaqueToken) throws SQLException {
        if (opaqueToken == null || opaqueToken.length() < MIN_OPAQUE_TOKEN_LEN) return 0;
        String hash = TokenHasher.sha256Hex(opaqueToken);
        final String sql = "UPDATE auth_session SET invalidated = 1 WHERE token_hash = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, hash);
            return ps.executeUpdate();
        }
    }
}
