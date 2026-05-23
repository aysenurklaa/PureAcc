import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Bagimsiz (standalone) veritabani olusturucu.
 * Yalnizca SQLite JDBC JAR'i ile calisir — HikariCP veya diger bagimliliklar gerekmez.
 *
 * Kullanim:
 *   java -cp sqlite-jdbc-3.47.1.0.jar;. CreateDatabase
 */
public class CreateDatabase {

    public static void main(String[] args) throws Exception {
        String dbPath = "data/pureacc.db";
        Path dbFile = Paths.get(dbPath).toAbsolutePath().normalize();
        Path parent = dbFile.getParent();
        if (parent != null) Files.createDirectories(parent);

        String jdbcUrl = "jdbc:sqlite:" + dbFile;
        System.out.println("=== PureAcc Veritabani Olusturucu ===");
        System.out.println("DB Yolu: " + dbFile);

        try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
            try (Statement st = conn.createStatement()) {
                st.execute("PRAGMA journal_mode = WAL");
                st.execute("PRAGMA foreign_keys = ON");
            }
            conn.setAutoCommit(false);
            System.out.println("[1/2] Sema kuruluyor...");
            executeSqlFile(conn, "sql/schema.sql");
            System.out.println("      Tablolar olusturuldu.");
            System.out.println("[2/2] Demo verileri yukleniyor...");
            executeSqlFile(conn, "sql/seed_data.sql");
            System.out.println("      Demo verileri yuklendi.");
            conn.commit();
        }

        System.out.println();
        System.out.println("=== Dogrulama ===");
        try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
            try (Statement st = conn.createStatement()) { st.execute("PRAGMA foreign_keys = ON"); }
            verifyTables(conn);
            verifyCounts(conn);
            verifyIntegrity(conn);
        }
        System.out.println();
        System.out.println("=== Kurulum tamamlandi! ===");
    }

    private static void executeSqlFile(Connection conn, String filePath) throws IOException, SQLException {
        Path path = Paths.get(filePath).toAbsolutePath().normalize();
        if (!Files.isRegularFile(path)) throw new IOException("SQL dosyasi bulunamadi: " + path);
        String raw = Files.readString(path, StandardCharsets.UTF_8);
        String cleaned = stripComments(raw);
        int count = 0;
        for (String part : cleaned.split(";")) {
            String sql = part.trim();
            if (sql.isEmpty()) continue;
            try (Statement st = conn.createStatement()) { st.execute(sql); count++; }
        }
        System.out.println("      " + count + " SQL komutu calistirildi (" + filePath + ")");
    }

    private static String stripComments(String sql) {
        StringBuilder sb = new StringBuilder();
        for (String line : sql.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("--")) continue;
            int pos = line.indexOf("--");
            if (pos >= 0) sb.append(line, 0, pos).append('\n');
            else sb.append(line).append('\n');
        }
        return sb.toString();
    }

    private static void verifyTables(Connection conn) throws SQLException {
        String[] expected = {"company", "customer", "invoice", "financial_transaction",
                             "reminder", "auth_session", "audit_log"};
        System.out.println("Tablolar:");
        for (String table : expected) {
            String sql = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='" + table + "'";
            try (Statement st = conn.createStatement(); var rs = st.executeQuery(sql)) {
                rs.next();
                int exists = rs.getInt(1);
                System.out.println("  " + (exists == 1 ? "OK" : "EKSIK") + " " + table);
                if (exists != 1) throw new SQLException("Eksik tablo: " + table);
            }
        }
    }

    private static void verifyCounts(Connection conn) throws SQLException {
        String[][] checks = {
            {"company", "Sirket"}, {"customer", "Musteri/Cari"}, {"invoice", "Fatura"},
            {"financial_transaction", "Finansal Hareket"}, {"reminder", "Hatirlatici"},
            {"audit_log", "Denetim Kaydi"}
        };
        System.out.println("\nKayit sayilari:");
        for (String[] check : checks) {
            try (Statement st = conn.createStatement();
                 var rs = st.executeQuery("SELECT COUNT(*) FROM " + check[0])) {
                rs.next();
                System.out.println("  " + check[1] + ": " + rs.getInt(1) + " kayit");
            }
        }
        System.out.println("\nFinansal Ozet:");
        try (Statement st = conn.createStatement();
             var rs = st.executeQuery(
                "SELECT COALESCE(SUM(CASE WHEN type='income' THEN amount ELSE 0 END), 0) as gelir, " +
                "COALESCE(SUM(CASE WHEN type='expense' THEN amount ELSE 0 END), 0) as gider " +
                "FROM financial_transaction")) {
            rs.next();
            double gelir = rs.getDouble("gelir");
            double gider = rs.getDouble("gider");
            System.out.printf("  Toplam Gelir:  %.0f%n", gelir);
            System.out.printf("  Toplam Gider:  %.0f%n", gider);
            System.out.printf("  Net Bakiye:    %.0f%n", gelir - gider);
        }
    }

    private static void verifyIntegrity(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             var rs = st.executeQuery("PRAGMA integrity_check")) {
            rs.next();
            String result = rs.getString(1);
            System.out.println("\nPRAGMA integrity_check: " + result);
            if (!"ok".equalsIgnoreCase(result)) throw new SQLException("Butunluk kontrolu basarisiz: " + result);
        }
        try (Statement st = conn.createStatement();
             var rs = st.executeQuery("PRAGMA foreign_key_check")) {
            if (rs.next()) throw new SQLException("Foreign key ihlali tespit edildi!");
            System.out.println("PRAGMA foreign_key_check: OK (ihlal yok)");
        }
    }
}
