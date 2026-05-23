package db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Veritabanının <strong>nerede</strong> ve <strong>nasıl adresleneceğini</strong> söyler.
 *
 * <p><strong>JDBC URL nedir?</strong> Java programının veritabanına bağlanmak için kullandığı
 * metin adresidir. SQLite için tipik biçim: {@code jdbc:sqlite:C:/yol/veri.db}
 *
 * <p>Gizli bilgiler bu sınıfta sabit kodlanmaz; PostgreSQL parolası gibi değerler yalnızca
 * JVM sistem özellikleri ({@value #PROP_JDBC_PASSWORD} vb.) üzerinden okunur.
 *
 * <p><strong>.env dosyası:</strong> Java bunu otomatik okumaz; IDE çalıştırma yapılandırması veya
 * {@code -D...} ile JVM özellikleri verilir. Gizli anahtarları repoda tutmayın — {@code .gitignore}
 * içinde {@code .env} listelenmiştir.
 */
public final class DatabaseConfig {

    /** Tam JDBC adresini vermek için JVM argümanı: {@code -Dpureacc.db.url=jdbc:sqlite:...} */
    public static final String PROP_JDBC_URL = "pureacc.db.url";

    /**
     * SQLite dosya yolunu vermek için JVM argümanı: {@code -Dpureacc.db.path=verilerim/hesap.db}
     * Verilmezse varsayılan: proje altında {@code data/pureacc.db}
     */
    public static final String PROP_DB_FILE = "pureacc.db.path";

    /** PostgreSQL kullanıcı adı: {@code -Dpureacc.db.user=...} */
    public static final String PROP_JDBC_USER = "pureacc.db.user";

    /** PostgreSQL parolası: {@code -Dpureacc.db.password=...} (repoda tutmayın) */
    public static final String PROP_JDBC_PASSWORD = "pureacc.db.password";

    /** HikariCP maksimum havuz boyutu: {@code -Dpureacc.db.pool.max=20} */
    public static final String PROP_POOL_MAX = "pureacc.db.pool.max";


    private DatabaseConfig() {
        // Sadece static yardımcılar; nesne oluşturulmaz.
    }

    /**
     * Uygulamanın bağlanacağı JDBC adresini döndürür.
     *
     * <ol>
     *   <li>Önce {@value #PROP_JDBC_URL} sistem özelliğine bakılır (geliştirici tam URL yazmışsa).</li>
     *   <li>Yoksa {@value #PROP_DB_FILE} ile göreli dosya yolu okunur, mutlak yola çevrilir.</li>
     *   <li>Son satırda SQLite sürücüsünün anlayacağı {@code jdbc:sqlite:...} biçimi üretilir.</li>
     * </ol>
     */
    public static String jdbcUrl() {
        String explicit = System.getProperty(PROP_JDBC_URL);
        if (explicit != null && !explicit.isBlank()) {
            return explicit.trim();
        }
        // Göreli yol: IDE çalıştırma dizinine göre çözülür; bu yüzden "Working directory" önemlidir.
        String relative = System.getProperty(PROP_DB_FILE, "data/pureacc.db");
        Path file = Paths.get(relative).toAbsolutePath().normalize();
        return "jdbc:sqlite:" + file;
    }

    /**
     * Veritabanı dosyasının bulunduğu klasör yoksa oluşturur (ör. ilk çalıştırmada {@code data/}).
     *
     * <p>Eğer URL SQLite dosyası değilse (ileride başka motor), bu metot hiçbir şey yapmaz.
     */
    public static void ensureDatabaseParentExists() throws IOException {
        String url = jdbcUrl();
        if (!url.startsWith("jdbc:sqlite:")) {
            return;
        }
        // "jdbc:sqlite:" önekini atıp gerçek dosya yolunu elde ediyoruz.
        Path file = Paths.get(url.substring("jdbc:sqlite:".length())).normalize();
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    /**
     * HikariCP {@code maximumPoolSize}.
     *
     * <p>SQLite dosya tabanlıdır; çok sayıda eşzamanlı yazıcı "database is locked" üretebilir.
     * Özellik açıkça verilmediyse SQLite için varsayılan {@code 1}, PostgreSQL için {@code 8} kullanılır.
     * JVM argümanı: {@value #PROP_POOL_MAX}.
     */
    public static int hikariMaxPoolSize() {
        String override = System.getProperty(PROP_POOL_MAX);
        if (override != null && !override.isBlank()) {
            try {
                int n = Integer.parseInt(override.trim());
                return Math.max(1, Math.min(n, 64));
            } catch (NumberFormatException e) {
                // geçersiz değer: aşağıdaki motor varsayılanına düş
            }
        }
        if (jdbcUrl().startsWith("jdbc:sqlite:")) {
            return 1;
        }
        return 8;
    }

    /** PostgreSQL için isteğe bağlı kullanıcı adı (boşsa Hikari varsayılanı). */
    public static String jdbcUsername() {
        String v = System.getProperty(PROP_JDBC_USER);
        return v != null ? v.trim() : "";
    }

    /** PostgreSQL için isteğe bağlı parola. */
    public static String jdbcPassword() {
        String v = System.getProperty(PROP_JDBC_PASSWORD);
        return v != null ? v : "";
    }
}
