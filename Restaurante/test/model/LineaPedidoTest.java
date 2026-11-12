package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LineaPedidoTest {

    @Test
    void calcularSubtotalMultiplicaPrecioPorCantidad() {
        Producto bebida = new Bebida("B1", "Jugo", "Natural", 1000, 5, 300, 5, true, false);
        LineaPedido linea = new LineaPedido(bebida, 3);
        assertEquals(3300, linea.calcularSubtotal(), 0.001);
    }

    @Test
    void calcularSubtotalUsaElPrecioDeVentaDelProducto() {
        Producto postre = new Postre("P1", "Flan", "Casero", 1000, 5, 100, 20, false);
        LineaPedido linea = new LineaPedido(postre, 2);
        assertEquals(2300, linea.calcularSubtotal(), 0.001);
    }
}
