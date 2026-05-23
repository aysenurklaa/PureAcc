package security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Kullanıcı veya servis hesabı <strong>parolaları</strong> için BCrypt hash'i.
 * Parolayı düz metin olarak veritabanına yazmayın; yalnızca {@link #hash(String)} çıktısını saklayın.
 */
public final class PasswordHasher {

    /** BCrypt teknik üst sınırı (bayt); daha uzun parolalar kesilmeden reddedilir. */
    public static final int BCRYPT_MAX_PASSWORD_BYTES = 72;

    private PasswordHasher() {
    }

    /**
     * Paroladan tek yönlü, tuzlu hash üretir.
     *
     * @throws IllegalArgumentException boş veya çok uzun parola
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Parola boş olamaz");
        }
        if (plainPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > BCRYPT_MAX_PASSWORD_BYTES) {
            throw new IllegalArgumentException("Parola " + BCRYPT_MAX_PASSWORD_BYTES + " bayttan uzun olamaz (BCrypt sınırı)");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /** Düz metin parola ile saklanan hash eşleşiyor mu? */
    public static boolean verify(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, storedHash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
