package model.exception;

/** La empresa de domicilio no acepta el recorrido solicitado. */
public class EmpresaNoAceptaException extends ElBuenComerException {

    public EmpresaNoAceptaException(String mensaje) {
        super(mensaje);
    }
}
