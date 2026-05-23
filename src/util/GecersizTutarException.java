package util;

// Negatif veya sıfır tutar girildiğinde fırlatılır.
public class GecersizTutarException extends RuntimeException {
    public GecersizTutarException(String mesaj) {
        super(mesaj);
    }
}
