/**
 * PureAcc <strong>veri tabanı katmanı</strong>: uygulamanın finansal verileri kalıcı olarak
 * sakladığı yerdir. Java tarafında bunu <em>JDBC</em> (Java Database Connectivity) ile yapıyoruz;
 * motor olarak <em>SQLite</em> kullanılıyor — yani veriler tek bir dosyada tutulur (varsayılan:
 * {@code data/pureacc.db}).
 *
 * <h2>Yeni başlayanlar için okuma sırası</h2>
 * <ol>
 *   <li>{@link db.DatabaseConfig} — Veritabanı dosyasının nerede duracağını veya tam JDBC URL'ini
 *       belirler.</li>
 *   <li>{@link db.ConnectionManager} — Her seferinde SQLite'a açılan bağlantıyı oluşturur;
 *       güvenlik ve tutarlılık için bazı <em>PRAGMA</em> ayarlarını uygular.</li>
 *   <li>{@link db.SchemaInitializer} — {@code sql/schema.sql} içindeki tablo tanımlarını
 *       veritabanına işler (ilk kurulum).</li>
 *   <li>{@link db.DatabaseBootstrap} — Şemayı kurmak için çalıştırılacak basit
 *       {@code main} giriş noktası.</li>
 *   <li>{@link db.DatabaseVerification} — Kurulumun doğru olduğunu otomatik test eder.</li>
 *   <li>{@code db.dao} paketi — Her iş tablosu için SQL yazan ama <strong>asla düz metin birleştirme
 *       yapmayan</strong> yardımcı sınıflar (DAO = Data Access Object).</li>
 * </ol>
 *
 * <h2>Temel kavramlar</h2>
 * <ul>
 *   <li><strong>Tablo</strong>: Excel sayfasına benzer; sütunlar (kolonlar) ve satırlar vardır.</li>
 *   <li><strong>Birincil anahtar (PRIMARY KEY)</strong>: Her satırı benzersiz tanımlayan numara
 *       (burada çoğunlukla {@code id}).</li>
 *   <li><strong>Yabancı anahtar (FOREIGN KEY)</strong>: Bir satırın başka tablodaki bir satıra
 *       "bağlı" olduğunu garanti eder (ör. fatura mutlaka geçerli bir cariye bağlı olmalı).</li>
 *   <li><strong>PreparedStatement ve ?</strong>: SQL içine kullanıcı verisini güvenli şekilde
 *       yerleştirir; kötü niyetli metnin komut gibi çalışmasını (SQL injection) engeller.</li>
 * </ul>
 */
package db;
