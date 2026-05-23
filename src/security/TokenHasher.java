package security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Oturum token'ları gibi <strong>rastgele sırlar</strong> veritabanında SHA-256 özeti olarak
 * saklanabilir. Kullanıcı parolaları için {@link PasswordHasher} kullanın.
 */
public final class TokenHasher {

    private TokenHasher() {
    }

    /** UTF-8 metnin SHA-256 özeti (64 hex karakter). */
    public static String sha256Hex(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Hash'lenecek değer null olamaz");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
