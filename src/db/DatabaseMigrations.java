package db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Mevcut SQLite dosyalarına yeni sütunlar ekler; şema dosyasındaki {@code IF NOT EXISTS} tek başına
 * eski veritabanlarını güncellemez.
 */
public final class DatabaseMigrations {

    private DatabaseMigrations() {
    }

    /**
     * SQLite bağlantılarında eksik sütunları tamamlar; diğer motorlarda no-op.
     * {@code company} tablosu yoksa (şema henüz kurulmamışsa) hiçbir şey yapmaz.
     */
    public static void applySqlite(Connection connection) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        if (meta == null || !"SQLite".equalsIgnoreCase(meta.getDatabaseProductName())) {
            return;
        }
        if (!sqliteTableExists(connection, "company")) {
            return;
        }
        addCompanyPasswordHashColumn(connection);
    }

    private static boolean sqliteTableExists(Connection connection, String tableName) throws SQLException {
        final String sql = "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void addCompanyPasswordHashColumn(Connection connection) throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("ALTER TABLE company ADD COLUMN password_hash TEXT");
        } catch (SQLException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.contains("duplicate column name") || msg.toLowerCase().contains("already exists")) {
                return;
            }
            throw e;
        }
    }
}
