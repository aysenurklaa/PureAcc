/**
 * <strong>DAO (Data Access Object)</strong> paketi: veri tabanına giden tüm SQL komutları burada
 * toplanır. Her DAO sınıfı genelde <strong>bir tablo</strong> ile ilgilenir.
 * Metotlar dışarıdan {@link java.sql.Connection} alır; böylece aynı işlem (transaction) içinde
 * birden fazla DAO çağrılabilir.
 *
 * <p><strong>Neden {@code ?} kullanılıyor?</strong> SQL metninde {@code ?} yer tutucudur;
 * değerler {@code ps.setString(1, ...)} gibi metotlarla bağlanır. Metni birleştirerek
 * yazmak tehlikelidir (SQL injection); bu pakette öyle bir desen kullanılmaz.
 */
package db.dao;
