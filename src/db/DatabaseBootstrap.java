package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;

/**
 * <strong>İlk kurulum (bootstrap)</strong>: uygulama veya geliştirici bu sınıfın
 * {@link #main(String[])} metodunu bir kez çalıştırdığında tablolar oluşturulur.
 *
 * <p>Hata durumunda {@code rollback} ile DDL paketi geri alınır; böylece yarım kalmış şema
 * bırakılması olasılığı azalır.
 */
public final class DatabaseBootstrap {

    private DatabaseBootstrap() {
    }

    /**
     * Tabloları oluşturur veya zaten varsa {@code CREATE TABLE IF NOT EXISTS} sayesinde
     * dokunmadan geçer.
     *
     * @throws Exception bağlantı, dosya okuma veya SQL hatası
     */
    public static void installSchema() throws Exception {
        try (Connection connection = ConnectionManager.open()) {
            connection.setAutoCommit(false);
            try {
                SchemaInitializer.install(connection);
                DatabaseMigrations.applySqlite(connection);
                connection.commit();
            } catch (IOException | SQLException e) {
                silentRollback(connection);
                throw e;
            } catch (RuntimeException e) {
                silentRollback(connection);
                throw e;
            }
        }
    }

    private static void silentRollback(Connection connection) {
        try {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }
        } catch (SQLException ignored) {
            // bağlantı zaten kapanıyorsa ikincil hata yutulur
        }
    }

    /**
     * IDE'den "Run" dendiğinde çağrılır: sadece şemayı kurar.
     */
    public static void main(String[] args) throws Exception {
        installSchema();
    }
}
