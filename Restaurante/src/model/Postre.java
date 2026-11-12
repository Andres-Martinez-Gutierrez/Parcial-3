package model;

import model.enumeraciones.CategoriaProducto;

/**
 * Postre: precio con 15 % de incremento y 5 % adicional si requiere refrigeración.
 * Peso entre 80 y 500 g. Temperatura entre 0 y 6 °C si se refrigera, o entre 15 y 30 °C si no.
 */
public class Postre extends Producto {

    private int pesoGramos;
    private double temperaturaAlmacenamiento;
    private boolean requiereRefrigeracion;

    /**
     * Crea un postre. Los datos comunes se pasan a {@link Producto}.
     *
     * @param pesoGramos                peso del postre en gramos, debe ser positivo
     * @param temperaturaAlmacenamiento temperatura de almacenamiento en °C
     * @param requiereRefrigeracion     true si el postre debe refrigerarse
     * @throws IllegalArgumentException si el peso no es positivo
     */
    public Postre(String codigo, String nombre, String descripcion, double precioBase, int tiempoPreparacion,
                  int pesoGramos, double temperaturaAlmacenamiento, boolean requiereRefrigeracion) {
        super(codigo, nombre, descripcion, precioBase, tiempoPreparacion);
        this.pesoGramos = Validaciones.requirePositive(pesoGramos, "El peso debe ser positivo");
        this.temperaturaAlmacenamiento = temperaturaAlmacenamiento;
        this.requiereRefrigeracion = requiereRefrigeracion;
    }

    @Override
    public double calcularPrecioVenta() {
        double precio = getPrecioBase() * 1.15;
        if (requiereRefrigeracion)
            precio += getPrecioBase() * 0.05;
        return precio;
    }

    @Override
    public boolean cumpleCondicionesCalidad() {
        if (pesoGramos < 80 || pesoGramos > 500)
            return false;
        if (requiereRefrigeracion)
            return temperaturaAlmacenamiento >= 0 && temperaturaAlmacenamiento <= 6;
        return temperaturaAlmacenamiento >= 15 && temperaturaAlmacenamiento <= 30;
    }

    @Override
    public CategoriaProducto getCategoria() { return CategoriaProducto.POSTRE; }

    public int getPesoGramos() { return pesoGramos; }
    public double getTemperaturaAlmacenamiento() { return temperaturaAlmacenamiento; }
    public boolean isRequiereRefrigeracion() { return requiereRefrigeracion; }

    public void setPesoGramos(int pesoGramos) {
        this.pesoGramos = Validaciones.requirePositive(pesoGramos, "El peso debe ser positivo");
    }

    public void setTemperaturaAlmacenamiento(double temperaturaAlmacenamiento) {
        this.temperaturaAlmacenamiento = temperaturaAlmacenamiento;
    }

    public void setRequiereRefrigeracion(boolean requiereRefrigeracion) {
        this.requiereRefrigeracion = requiereRefrigeracion;
    }

    @Override
    public String toString() {
        return "Postre{" + super.toString() + ", pesoGramos=" + pesoGramos
                + ", temperaturaAlmacenamiento=" + temperaturaAlmacenamiento
                + ", requiereRefrigeracion=" + requiereRefrigeracion + "}";
    }
}
