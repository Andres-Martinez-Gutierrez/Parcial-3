package model.exception;

/** Un producto del pedido no cumple las condiciones de calidad. */
public class ProductoNoCumpleCalidadException extends ElBuenComerException {

    public ProductoNoCumpleCalidadException(String codigoProducto) {
        super("El producto " + codigoProducto + " no cumple las condiciones de calidad");
    }
}
