package model;

import model.exception.EmpresaNoAceptaException;

/**
 * Domicilio contratado a una empresa externa. Solo se crea si la empresa acepta el recorrido,
 * y guarda el valor que cobra esa empresa.
 */
public class Domicilio {

    private double distanciaKm;
    private ServicioDomicilio empresa;
    private double valorDomicilio;

    /**
     * Contrata un domicilio con una empresa. El valor se calcula con la tarifa de la empresa.
     *
     * @param distanciaKm distancia del recorrido en km, debe ser positiva y aceptada por la empresa
     * @param empresa     empresa que realiza el domicilio, no puede ser null
     * @throws IllegalArgumentException   si la empresa es null o la distancia no es positiva
     * @throws EmpresaNoAceptaException   si la empresa no acepta el recorrido
     */
    public Domicilio(double distanciaKm, ServicioDomicilio empresa) {
        this.empresa = Validaciones.requireNonNull(empresa, "La empresa de domicilio es obligatoria");
        this.distanciaKm = validarDistancia(distanciaKm, this.empresa);
        this.valorDomicilio = empresa.calcularTarifa(distanciaKm);
    }

    /** Valor del domicilio que cobra la empresa. Se diferencia de los ingresos del restaurante. */
    public double calcularValor() {
        return valorDomicilio;
    }

    public double getDistanciaKm() { return distanciaKm; }
    public ServicioDomicilio getEmpresa() { return empresa; }

    /** Cambia la distancia y recalcula el valor. Debe estar dentro del recorrido que acepta la empresa. */
    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = validarDistancia(distanciaKm, empresa);
        this.valorDomicilio = empresa.calcularTarifa(distanciaKm);
    }

    /** Cambia la empresa y recalcula el valor. La nueva empresa debe aceptar la distancia actual. */
    public void setEmpresa(ServicioDomicilio empresa) {
        Validaciones.requireNonNull(empresa, "La empresa de domicilio es obligatoria");
        validarDistancia(distanciaKm, empresa);
        this.empresa = empresa;
        this.valorDomicilio = empresa.calcularTarifa(distanciaKm);
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
        return "Domicilio{distanciaKm=" + distanciaKm + ", empresa=" + empresa.getClass().getSimpleName()
                + ", valorDomicilio=" + valorDomicilio + "}";
    }
}
