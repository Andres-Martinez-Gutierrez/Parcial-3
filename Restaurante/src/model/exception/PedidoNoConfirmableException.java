package model.exception;

/** El pedido no puede confirmarse porque incumple alguna condición. */
public class PedidoNoConfirmableException extends ElBuenComerException {

    public PedidoNoConfirmableException(String mensaje) {
        super(mensaje);
    }
}
