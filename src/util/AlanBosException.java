package util;

// Zorunlu bir alan boş bırakıldığında fırlatılır.
public class AlanBosException extends RuntimeException {
    public AlanBosException(String mesaj) {
        super(mesaj);
    }
}
