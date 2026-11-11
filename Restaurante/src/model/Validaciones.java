package model;

import java.util.Objects;

/** Validaciones reutilizables para constructores y setters de los modelos. */
public final class Validaciones {

    private Validaciones() {
    }

    /**
     * Verifica que el texto no sea null ni esté vacío.
     *
     * @return el mismo texto validado
     * @throws IllegalArgumentException si el texto es null o está en blanco
     */
    public static String requireNotBlank(String valor, String mensaje) {
        if (valor == null || valor.isBlank())
            throw new IllegalArgumentException(mensaje);
        return valor;
    }

    /**
     * Verifica que el objeto no sea null.
     *
     * @return el mismo objeto validado
     * @throws IllegalArgumentException si el objeto es null
     */
    public static <T> T requireNonNull(T valor, String mensaje) {
        if (valor == null)
            throw new IllegalArgumentException(mensaje);
        return valor;
    }

    /**
     * Verifica que el número sea mayor que cero.
     *
     * @return el mismo número validado
     * @throws IllegalArgumentException si el número no es positivo
     */
    public static double requirePositive(double valor, String mensaje) {
        if (valor <= 0)
            throw new IllegalArgumentException(mensaje);
        return valor;
    }

    /**
     * Verifica que el entero sea mayor que cero.
     *
     * @return el mismo entero validado
     * @throws IllegalArgumentException si el entero no es positivo
     */
    public static int requirePositive(int valor, String mensaje) {
        if (valor <= 0)
            throw new IllegalArgumentException(mensaje);
        return valor;
    }
}
