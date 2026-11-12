package model;

import model.exception.EmpresaNoAceptaException;

/**
 * Domicilio contratado a una empresa externa. Solo se crea si la empresa acepta el recorrido,
 * y guarda el valor que cobra esa empresa.
 */
public class Domicilio {

    private double distanciaKm;
    private ServicioDomicilio domicilio;
    private double valorDomicilio;

    /**
     * Contrata un domicilio con una empresa. El valor se calcula con la tarifa de la empresa.
     *
     * @param distanciaKm distancia del recorrido en km, debe ser positiva y aceptada por la empresa
     * @param domicilio     empresa que realiza el domicilio, no puede ser null
     * @throws IllegalArgumentException   si la empresa es null o la distancia no es positiva
     * @throws EmpresaNoAceptaException   si la empresa no acepta el recorrido
     */
    public Domicilio(double distanciaKm, ServicioDomicilio domicilio) {
        this.domicilio = Validaciones.requireNonNull(domicilio, "La empresa de domicilio es obligatoria");
        this.distanciaKm = validarDistancia(distanciaKm, this.domicilio);
        this.valorDomicilio = domicilio.calcularTarifa(distanciaKm);
    }

    /** Valor del domicilio que cobra la empresa. Se diferencia de los ingresos del restaurante. */
    public double calcularValor() {
        return valorDomicilio;
    }

    public double getDistanciaKm() { return distanciaKm; }
    public ServicioDomicilio getDomicilio() { return domicilio; }

    /** Cambia la distancia y recalcula el valor. Debe estar dentro del recorrido que acepta la empresa. */
    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = validarDistancia(distanciaKm, domicilio);
        this.valorDomicilio = domicilio.calcularTarifa(distanciaKm);
    }

    /** Cambia la empresa y recalcula el valor. La nueva empresa debe aceptar la distancia actual. */
    public void setDomicilio(ServicioDomicilio domicilio) {
        Validaciones.requireNonNull(domicilio, "La empresa de domicilio es obligatoria");
        validarDistancia(distanciaKm, domicilio);
        this.domicilio = domicilio;
        this.valorDomicilio = domicilio.calcularTarifa(distanciaKm);
    }

    /** Valida que la distancia sea positiva y que la empresa la acepte. Devuelve la distancia validada. */
    private static double validarDistancia(double distanciaKm, ServicioDomicilio empresa) {
        Validaciones.requirePositive(distanciaKm, "La distancia debe ser positiva");
        if (!empresa.aceptaRecorrido(distanciaKm))
            throw new EmpresaNoAceptaException("La empresa no acepta un recorrido de " + distanciaKm + " km");
        return distanciaKm;
    }

    @Override
    public String toString() {
        return "Domicilio{distanciaKm=" + distanciaKm + ", empresa=" + domicilio.getClass().getSimpleName()
                + ", valorDomicilio=" + valorDomicilio + "}";
    }
}
