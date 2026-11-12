package model;

import model.enumeraciones.CategoriaProducto;

/** Producto del menú. Cada categoría define su precio de venta y sus condiciones de calidad. */
public abstract class Producto {

    private final String codigo;
    private String nombre;
    private String descripcion;
    private double precioBase;
    private int tiempoPreparacion;

    /**
     * Crea un producto con sus datos básicos.
     *
     * @throws IllegalArgumentException si el código o el nombre están vacíos, o si el precio o el tiempo no son positivos
     */
    protected Producto(String codigo, String nombre, String descripcion, double precioBase, int tiempoPreparacion) {
        this.codigo = Validaciones.requireNotBlank(codigo, "El código del producto es obligatorio");
        this.nombre = Validaciones.requireNotBlank(nombre, "El nombre del producto es obligatorio");
        this.descripcion = descripcion;
        this.precioBase = Validaciones.requirePositive(precioBase, "El precio base debe ser positivo");
        this.tiempoPreparacion = Validaciones.requirePositive(tiempoPreparacion, "El tiempo de preparación debe ser positivo");
    }

    /** Precio que se cobra al cliente, según la categoría del producto. */
    public abstract double calcularPrecioVenta();

    /** Indica si el producto puede incluirse en un pedido confirmado. */
    public abstract boolean cumpleCondicionesCalidad();

    public abstract CategoriaProducto getCategoria();

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecioBase() { return precioBase; }
    public int getTiempoPreparacion() { return tiempoPreparacion; }

    public void setNombre(String nombre) {
        this.nombre = Validaciones.requireNotBlank(nombre, "El nombre del producto es obligatorio");
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = Validaciones.requirePositive(precioBase, "El precio base debe ser positivo");
    }

    public void setTiempoPreparacion(int tiempoPreparacion) {
        this.tiempoPreparacion = Validaciones.requirePositive(tiempoPreparacion, "El tiempo de preparación debe ser positivo");
    }

    @Override
    public String toString() {
        return "Producto{codigo='" + codigo + "', nombre='" + nombre + "', descripcion='" + descripcion
                + "', precioBase=" + precioBase + ", tiempoPreparacion=" + tiempoPreparacion + "}";
    }
}
