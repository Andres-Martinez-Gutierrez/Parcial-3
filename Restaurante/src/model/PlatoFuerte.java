package model;

import model.enumeraciones.CategoriaProducto;
import model.enumeraciones.TipoPreparacion;

/** Plato fuerte: precio con 20 % de incremento y calidad según peso, tiempo y cocción. */
public class PlatoFuerte extends Producto {

    private int pesoGramos;
    private TipoPreparacion tipoPreparacion;
    private boolean coccionAprobada;

    /**
     * Crea un plato fuerte. Los datos comunes se pasan a {@link Producto}.
     *
     * @param pesoGramos       peso del plato en gramos, debe ser positivo
     * @param tipoPreparacion  tipo de preparación, no puede ser null
     * @param coccionAprobada  true si la cocción fue aprobada
     * @throws IllegalArgumentException si el peso no es positivo o el tipo de preparación es null
     */
    public PlatoFuerte(String codigo, String nombre, String descripcion, double precioBase, int tiempoPreparacion,
                       int pesoGramos, TipoPreparacion tipoPreparacion, boolean coccionAprobada) {
        super(codigo, nombre, descripcion, precioBase, tiempoPreparacion);
        this.pesoGramos = Validaciones.requirePositive(pesoGramos, "El peso debe ser positivo");
        this.tipoPreparacion = Validaciones.requireNonNull(tipoPreparacion, "El tipo de preparación es obligatorio");
        this.coccionAprobada = coccionAprobada;
    }

    @Override
    public double calcularPrecioVenta() {
        return getPrecioBase() * 1.20;
    }

    /** Peso entre 200 y 900 g, tiempo entre 10 y 60 min y cocción aprobada. */
    @Override
    public boolean cumpleCondicionesCalidad() {
        return pesoGramos >= 200 && pesoGramos <= 900
                && getTiempoPreparacion() >= 10 && getTiempoPreparacion() <= 60
                && coccionAprobada;
    }

    @Override
    public CategoriaProducto getCategoria() { return CategoriaProducto.PLATO_FUERTE; }

    public int getPesoGramos() { return pesoGramos; }
    public TipoPreparacion getTipoPreparacion() { return tipoPreparacion; }
    public boolean isCoccionAprobada() { return coccionAprobada; }

    public void setPesoGramos(int pesoGramos) {
        this.pesoGramos = Validaciones.requirePositive(pesoGramos, "El peso debe ser positivo");
    }

    public void setTipoPreparacion(TipoPreparacion tipoPreparacion) {
        this.tipoPreparacion = Validaciones.requireNonNull(tipoPreparacion, "El tipo de preparación es obligatorio");
    }

    public void setCoccionAprobada(boolean coccionAprobada) {
        this.coccionAprobada = coccionAprobada;
    }

    @Override
    public String toString() {
        return "PlatoFuerte{" + super.toString() + ", pesoGramos=" + pesoGramos + ", tipoPreparacion=" + tipoPreparacion
                + ", coccionAprobada=" + coccionAprobada + "}";
    }
}
