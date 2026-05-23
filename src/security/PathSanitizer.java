package security;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Kullanıcı veya dış kaynaktan gelen göreli yolların <strong>path traversal</strong>
 * ({@code ../}) ile sistem dışına çıkmasını engellemek için basit sertleştirme.
 */
public final class PathSanitizer {

    private PathSanitizer() {
    }

    /**
     * {@code userSegment} içinde '..' veya mutlak yol sürprizi yoksa güvenli kabul edilir;
     * {@code baseDir.resolve(userSegment).normalize()} döner; aksi halde istisna.
     *
     * @param baseDir uygulamanın izin verdiği kök (ör. onaylı arşiv klasörü)
     * @param userSegment tek bir dosya adı veya göreli alt yol (platform ayırıcıları normalize edilir)
     */
    public static Path safeResolve(Path baseDir, String userSegment) {
        Objects.requireNonNull(baseDir, "baseDir");
        if (userSegment == null || userSegment.isBlank()) {
            throw new IllegalArgumentException("userSegment boş olamaz");
        }
        if (userSegment.contains("..")) {
            throw new IllegalArgumentException("İzin verilmeyen yol bileşeni: ..");
        }
        Path resolved = baseDir.resolve(userSegment).normalize();
        if (!resolved.startsWith(baseDir.normalize())) {
            throw new IllegalArgumentException("Yol taban dizinin dışına çıkıyor");
        }
        return resolved;
    }
}
