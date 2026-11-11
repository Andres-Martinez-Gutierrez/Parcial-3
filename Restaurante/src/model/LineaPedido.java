package model;

/** Producto y cantidad dentro de un pedido (clase de asociación entre Pedido y Producto). */
public class LineaPedido {

    private Producto producto;
    private int cantidad;

    /**
     * Crea una línea de pedido con un producto y su cantidad.
     *
     * @param producto producto del menú, no puede ser null
     * @param cantidad unidades del producto, debe ser mayor que cero
     * @throws IllegalArgumentException si el producto es null o la cantidad no es positiva
     */
    public LineaPedido(Producto producto, int cantidad) {
        this.producto = Validaciones.requireNonNull(producto, "El producto es obligatorio");
        this.cantidad = Validaciones.requirePositive(cantidad, "La cantidad debe ser mayor que cero");
    }

    /** Precio de venta del producto multiplicado por la cantidad. */
    public double calcularSubtotal() {
        return producto.calcularPrecioVenta() * cantidad;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }

    public void setProducto(Producto producto) {
        this.producto = Validaciones.requireNonNull(producto, "El producto es obligatorio");
    }

    public void setCantidad(int cantidad) {
        this.cantidad = Validaciones.requirePositive(cantidad, "La cantidad debe ser mayor que cero");
    }

    @Override
    public String toString() {
        return "LineaPedido{producto=" + producto.getNombre() + ", cantidad=" + cantidad
                + ", subtotal=" + calcularSubtotal() + "}";
    }
}
