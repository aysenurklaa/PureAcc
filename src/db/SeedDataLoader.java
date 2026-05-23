package db;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class SeedDataLoader {

    public static final String PROP_SEED_PATH = "pureacc.seed.path";

    private SeedDataLoader() {}

    static String stripSqlLineComments(String sql) {
        StringBuilder sb = new StringBuilder();
        for (String line : sql.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("--")) { continue; }
            int pos = line.indexOf("--");
            if (pos >= 0) { sb.append(line, 0, pos).append('\n'); }
            else { sb.append(line).append('\n'); }
        }
        return sb.toString();
    }

    public static void loadSeedData(Connection connection) throws IOException, SQLException {
        Path path = Paths.get(System.getProperty(PROP_SEED_PATH, "sql/seed_data.sql"))
                .toAbsolutePath().normalize();
        if (!Files.isRegularFile(path)) {
            throw new IOException("Seed dosyasi bulunamadi: " + path);
        }
        String raw = Files.readString(path, StandardCharsets.UTF_8);
        String cleaned = stripSqlLineComments(raw);
        int executed = 0;
        for (String part : cleaned.split(";")) {
            String s = part.trim();
            if (s.isEmpty()) { continue; }
            try (Statement st = connection.createStatement()) { st.execute(s); executed++; }
        }
        System.out.println("[SeedDataLoader] " + executed + " SQL komutu basariyla calistirildi.");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== PureAcc Veritabani Kurulumu ===");
        System.out.println("[1/2] Sema kuruluyor...");
        DatabaseBootstrap.installSchema();
        System.out.println("      Tablolar olusturuldu.");
        System.out.println("[2/2] Demo verileri yukleniyor...");
        try (Connection connection = ConnectionManager.open()) {
            connection.setAutoCommit(false);
            try {
                loadSeedData(connection);
                connection.commit();
                System.out.println("      Demo verileri basariyla yuklendi.");
            } catch (IOException | SQLException e) {
                try { connection.rollback(); } catch (SQLException ignored) {}
                throw e;
            }
        }
        System.out.println("=== Kurulum tamamlandi! ===");
        System.out.println("Veritabani konumu: " + DatabaseConfig.jdbcUrl());
    }
}
