package model.exception;

/** Se intentó usar un cliente que no está registrado en el restaurante. */
public class ClienteNoRegistradoException extends ElBuenComerException {

    public ClienteNoRegistradoException(String documento) {
        super("El cliente con documento " + documento + " no está registrado");
    }
}
