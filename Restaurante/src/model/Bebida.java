package model;

import model.enumeraciones.CategoriaProducto;

/**
 * Bebida: precio con 10 % de incremento más recargo fijo de $3.000 si requiere preparación especial.
 * La temperatura de servicio debe estar entre 0 y 10 °C si es fría, o entre 55 y 85 °C si es caliente.
 */
public class Bebida extends Producto {

    private static final double RECARGO_PREPARACION_ESPECIAL = 3000;

    private int volumenMl;
    private double temperaturaServicio;
    private boolean esFria;
    private boolean requierePreparacionEspecial;

    /**
     * Crea una bebida. Los datos comunes se pasan a {@link Producto}.
     *
     * @param volumenMl                  volumen en mililitros, debe ser positivo
     * @param temperaturaServicio        temperatura de servicio en °C
     * @param esFria                     true si la bebida se sirve fría, false si se sirve caliente
     * @param requierePreparacionEspecial true si tiene recargo fijo por preparación especial
     * @throws IllegalArgumentException si el volumen no es positivo
     */
    public Bebida(String codigo, String nombre, String descripcion, double precioBase, int tiempoPreparacion,
                  int volumenMl, double temperaturaServicio, boolean esFria, boolean requierePreparacionEspecial) {
        super(codigo, nombre, descripcion, precioBase, tiempoPreparacion);
        this.volumenMl = Validaciones.requirePositive(volumenMl, "El volumen debe ser positivo");
        this.temperaturaServicio = temperaturaServicio;
        this.esFria = esFria;
        this.requierePreparacionEspecial = requierePreparacionEspecial;
    }

    @Override
    public double calcularPrecioVenta() {
        double precio = getPrecioBase() * 1.10;
        if (requierePreparacionEspecial)
            precio += RECARGO_PREPARACION_ESPECIAL;
        return precio;
    }

    /** Volumen entre 150 y 1.500 ml y temperatura según sea fría o caliente. */
    @Override
    public boolean cumpleCondicionesCalidad() {
        if (volumenMl < 150 || volumenMl > 1500)
            return false;
        if (esFria)
            return temperaturaServicio >= 0 && temperaturaServicio <= 10;
        return temperaturaServicio >= 55 && temperaturaServicio <= 85;
    }

    @Override
    public CategoriaProducto getCategoria() { return CategoriaProducto.BEBIDA; }

    public int getVolumenMl() { return volumenMl; }
    public double getTemperaturaServicio() { return temperaturaServicio; }
    public boolean isEsFria() { return esFria; }
    public boolean isRequierePreparacionEspecial() { return requierePreparacionEspecial; }

    public void setVolumenMl(int volumenMl) {
        this.volumenMl = Validaciones.requirePositive(volumenMl, "El volumen debe ser positivo");
    }

    public void setTemperaturaServicio(double temperaturaServicio) {
        this.temperaturaServicio = temperaturaServicio;
    }

    public void setEsFria(boolean esFria) {
        this.esFria = esFria;
    }

    public void setRequierePreparacionEspecial(boolean requierePreparacionEspecial) {
        this.requierePreparacionEspecial = requierePreparacionEspecial;
    }

    @Override
    public String toString() {
        return "Bebida{" + super.toString() + ", volumenMl=" + volumenMl + ", temperaturaServicio=" + temperaturaServicio
                + ", esFria=" + esFria + ", requierePreparacionEspecial=" + requierePreparacionEspecial + "}";
    }
}
