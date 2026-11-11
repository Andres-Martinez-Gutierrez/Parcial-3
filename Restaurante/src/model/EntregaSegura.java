package model;

import model.exception.EmpresaNoAceptaException;

/**
 * Empresa de domicilios: $7.000 base más $1.000 por km, hasta 20 km.
 * Si la distancia supera 5 km se agrega un recargo fijo de $3.000.
 */
public class EntregaSegura implements ServicioDomicilio {

    private static final double TARIFA_BASE = 7000;
    private static final double TARIFA_POR_KM = 1000;
    private static final double UMBRAL_RECARGO_KM = 5;
    private static final double RECARGO = 3000;
    private static final double DISTANCIA_MAXIMA_KM = 20;

    /** Crea la empresa EntregaSegura. No requiere datos; sus tarifas son constantes. */
    public EntregaSegura() {
    }

    @Override
    public boolean aceptaRecorrido(double distanciaKm) {
        return distanciaKm > 0 && distanciaKm <= DISTANCIA_MAXIMA_KM;
    }

    @Override
    public double calcularTarifa(double distanciaKm) {
        if (!aceptaRecorrido(distanciaKm))
            throw new EmpresaNoAceptaException("EntregaSegura no acepta un recorrido de " + distanciaKm + " km");
        double tarifa = TARIFA_BASE + TARIFA_POR_KM * distanciaKm;
        if (distanciaKm > UMBRAL_RECARGO_KM)
            tarifa += RECARGO;
        return tarifa;
    }

    @Override
    public String toString() {
        return "EntregaSegura{tarifaBase=" + TARIFA_BASE + ", tarifaPorKm=" + TARIFA_POR_KM
                + ", distanciaMaximaKm=" + DISTANCIA_MAXIMA_KM + "}";
    }
}
