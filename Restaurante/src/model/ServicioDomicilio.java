package model;

import model.exception.EmpresaNoAceptaException;

/** Procedimiento común que cumplen las empresas de domicilio contratadas. */
public interface ServicioDomicilio {

    /** Indica si la empresa acepta un recorrido de la distancia dada, en kilómetros. */
    boolean aceptaRecorrido(double distanciaKm);

    /**
     * Tarifa del domicilio para la distancia dada.
     *
     * @throws EmpresaNoAceptaException si la empresa no acepta ese recorrido
     */
    double calcularTarifa(double distanciaKm);
}
