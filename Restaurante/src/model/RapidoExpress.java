package model;

import model.exception.EmpresaNoAceptaException;

/** Empresa de domicilios: $5.000 base más $1.500 por km, hasta 12 km. */
public class RapidoExpress implements ServicioDomicilio {

    private static final double TARIFA_BASE = 5000;
    private static final double TARIFA_POR_KM = 1500;
    private static final double DISTANCIA_MAXIMA_KM = 12;

    /** Crea la empresa RapidoExpress. No requiere datos; sus tarifas son constantes. */
    public RapidoExpress() {
    }

    @Override
    public boolean aceptaRecorrido(double distanciaKm) {
        return distanciaKm > 0 && distanciaKm <= DISTANCIA_MAXIMA_KM;
    }

    @Override
    public double calcularTarifa(double distanciaKm) {
        if (!aceptaRecorrido(distanciaKm))
            throw new EmpresaNoAceptaException("RapidoExpress no acepta un recorrido de " + distanciaKm + " km");
        return TARIFA_BASE + TARIFA_POR_KM * distanciaKm;
    }

    @Override
    public String toString() {
        return "RapidoExpress{tarifaBase=" + TARIFA_BASE + ", tarifaPorKm=" + TARIFA_POR_KM
                + ", distanciaMaximaKm=" + DISTANCIA_MAXIMA_KM + "}";
    }
}
