package model.exception;

/** Base de las excepciones del dominio El Buen Comer. Es unchecked. */
public class ElBuenComerException extends RuntimeException {

    public ElBuenComerException(String mensaje) {
        super(mensaje);
    }
}
