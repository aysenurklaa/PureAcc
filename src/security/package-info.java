/**
 * Güvenlik yardımcıları: şifre hash'i, oturum token'ının tek yönlü özeti, dosya yolu sertleştirme.
 *
 * <p><strong>SQL güvenliği</strong> bu pakette değil, {@code db.dao} içinde sağlanır: tüm sorgular
 * {@link java.sql.PreparedStatement} ve {@code ?} yer tutucuları ile yazılmalıdır. Kullanıcı
 * girdisini asla SQL metnine {@code +} ile eklemeyin.
 *
 * <p><strong>Parola vs oturum token'ı:</strong> Parolalar için {@link security.PasswordHasher} (BCrypt);
 * rastgele oturum anahtarı için {@link security.TokenHasher} (SHA-256 özeti). İkisini karıştırmayın.
 */
package security;
