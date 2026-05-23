package db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.IOException;

/**
 * <strong>Bağlantı havuzu (HikariCP)</strong>: havuzdan kısa süreli bağlantı ödünç alınır.
 * SQLite için varsayılan havuz boyutu küçük tutulur (dosya kilidi riski); PostgreSQL için daha büyük.
 *
 * <p>PRAGMA yalnızca SQLite bağlantılarında {@code connectionInitSql} ile uygulanır.
 */
public final class ConnectionManager {

    private static final Object LOCK = new Object();
    private static final AtomicBoolean SHUTDOWN_HOOK_REGISTERED = new AtomicBoolean(false);
    private static volatile HikariDataSource pool;

    private ConnectionManager() {
    }

    public static Connection open() throws SQLException {
        return dataSource().getConnection();
    }

    public static DataSource dataSource() throws SQLException {
        return pool();
    }

    private static HikariDataSource pool() throws SQLException {
        if (pool == null) {
            synchronized (LOCK) {
                if (pool == null) {
                    pool = buildPool();
                    try (Connection migrate = pool.getConnection()) {
                        DatabaseMigrations.applySqlite(migrate);
                    }
                    if (SHUTDOWN_HOOK_REGISTERED.compareAndSet(false, true)) {
                        Runtime.getRuntime().addShutdownHook(new Thread(ConnectionManager::shutdownQuietly));
                    }
                }
            }
        }
        return pool;
    }

    private static HikariDataSource buildPool() throws SQLException {
        String url = DatabaseConfig.jdbcUrl();
        try {
            DatabaseConfig.ensureDatabaseParentExists();
        } catch (IOException e) {
            throw new RuntimeException("Veritabanı klasörü oluşturulamadı!", e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setMaximumPoolSize(DatabaseConfig.hikariMaxPoolSize());
        config.setPoolName("pureacc-pool");

        if (url.startsWith("jdbc:sqlite:")) {
            config.setDriverClassName("org.sqlite.JDBC");
            config.setConnectionInitSql(
                    "PRAGMA foreign_keys = ON; PRAGMA journal_mode = WAL; PRAGMA synchronous = NORMAL;"
            );
        } else if (url.startsWith("jdbc:postgresql:")) {
            config.setDriverClassName("org.postgresql.Driver");
            String user = DatabaseConfig.jdbcUsername();
            if (!user.isEmpty()) {
                config.setUsername(user);
            }
            config.setPassword(DatabaseConfig.jdbcPassword());
        } else {
            throw new SQLException("Desteklenmeyen JDBC URL (sqlite veya postgresql beklenir): " + url);
        }

        return new HikariDataSource(config);
    }

    public static void shutdownQuietly() {
        synchronized (LOCK) {
            if (pool != null) {
                pool.close();
                pool = null;
            }
        }
    }
}
