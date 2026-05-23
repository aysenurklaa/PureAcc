package db;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <strong>Şema kurulumu</strong>: {@code sql/schema.sql} dosyasındaki SQL komutlarını okuyup
 * veritabanında tabloları, indeksleri ve kuralları oluşturur.
 *
 * <p><strong>DDL nedir?</strong> Data Definition Language — yani CREATE TABLE, CREATE INDEX gibi
 * "yapıyı tanımlayan" komutlar. Veri eklemez; iskeleti kurar.
 *
 * <p>Dosya {@code ;} ile ayrılmış komutlara bölünür. Satır başı ve satır sonu {@code --} yorumları
 * ayrıştırmadan önce budanır; böylece CREATE içindeki açıklama satırları yanlışlıkla komut
 * parçasına karışmaz.
 */
public final class SchemaInitializer {

    /** DDL dosyası yolu: {@code -Dpureacc.schema.path=C:/tam/yol/schema.sql} */
    public static final String PROP_SCHEMA_PATH = "pureacc.schema.path";

    private SchemaInitializer() {
    }

    /**
     * Satır ve satır-sonu {@code --} yorumlarını kaldırır (string literal içindeki {@code --}
     * nadir; bu şema dosyasında kullanılmaz).
     */
    static String stripSqlLineComments(String ddl) {
        StringBuilder sb = new StringBuilder();
        for (String line : ddl.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("--")) {
                continue;
            }
            int pos = line.indexOf("--");
            if (pos >= 0) {
                sb.append(line, 0, pos).append('\n');
            } else {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verilen açık bağlantı üzerinde şema dosyasını çalıştırır.
     *
     * @param connection üzerinde DDL çalıştırılacak JDBC bağlantısı (genelde autocommit kapalıdır)
     */
    public static void install(Connection connection) throws IOException, SQLException {
        String product = connection.getMetaData().getDatabaseProductName();
        if (!"SQLite".equalsIgnoreCase(product)) {
            throw new IOException(
                    "sql/schema.sql yalnızca SQLite sözdizimindedir. Bağlantı motoru: " + product
                            + ". PostgreSQL için Flyway/Liquibase ile ayrı DDL kullanın."
            );
        }
        Path path = Paths.get(System.getProperty(PROP_SCHEMA_PATH, "sql/schema.sql"))
                .toAbsolutePath()
                .normalize();
        if (!Files.isRegularFile(path)) {
            throw new IOException("Schema file missing: " + path);
        }
        String ddl = Files.readString(path, StandardCharsets.UTF_8);
        String cleaned = stripSqlLineComments(ddl);
        for (String part : cleaned.split(";")) {
            String sql = part.trim();
            if (sql.isEmpty()) {
                continue;
            }
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }
    }
}
