package model;

import model.enumeraciones.EstadoPedido;
import model.enumeraciones.ModalidadEntrega;
import model.exception.PedidoNoConfirmableException;
import model.exception.ProductoNoCumpleCalidadException;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    private final Cliente cliente = new Cliente("123", "Ana Pérez", null, null, null);

    private Bebida bebidaValida() {
        return new Bebida("B1", "Jugo", "Natural", 1000, 5, 300, 5, true, false);
    }

    private Postre postreValido() {
        return new Postre("P1", "Flan", "Casero", 1000, 5, 100, 20, false);
    }

    private Pedido pedido(ModalidadEntrega modalidad) {
        return new Pedido("PED1", LocalDate.of(2026, 11, 11), cliente, modalidad);
    }

    @Test
    void agregarProductoYCalcularValorProductos() {
        Pedido pedido = pedido(ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        pedido.agregarProducto(bebidaValida(), 2);
        pedido.agregarProducto(postreValido(), 1);

        assertEquals(3350, pedido.calcularValorProductos(), 0.001);
    }

    @Test
    void agregarProductoFallaSiElPedidoYaFueConfirmado() {
        Pedido pedido = pedido(ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        pedido.agregarProducto(bebidaValida(), 1);
        pedido.confirmar();

        assertThrows(IllegalStateException.class, () -> pedido.agregarProducto(postreValido(), 1));
    }

    @Test
    void calcularTotalSinDomicilioEsElValorDeLosProductos() {
        Pedido pedido = pedido(ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        pedido.agregarProducto(bebidaValida(), 2);
        pedido.agregarProducto(postreValido(), 1);

        assertEquals(3350, pedido.calcularTotal(), 0.001);
    }

    @Test
    void calcularTotalIncluyeElValorDelDomicilio() {
        Pedido pedido = pedido(ModalidadEntrega.DOMICILIO);
        pedido.agregarProducto(bebidaValida(), 2);
        pedido.agregarProducto(postreValido(), 1);
        pedido.asignarDomicilio(new Domicilio(3, new RapidoExpress()));

        assertEquals(12850, pedido.calcularTotal(), 0.001);
    }

    @Test
    void asignarDomicilioRechazaPedidoDeConsumoEnRestaurante() {
        Pedido pedido = pedido(ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        Domicilio domicilio = new Domicilio(3, new RapidoExpress());

        assertThrows(IllegalArgumentException.class, () -> pedido.asignarDomicilio(domicilio));
    }

    @Test
    void confirmarRechazaPedidoSinProductos() {
        Pedido pedido = pedido(ModalidadEntrega.CONSUMO_EN_RESTAURANTE);

        assertThrows(PedidoNoConfirmableException.class, pedido::confirmar);
    }

    @Test
    void confirmarRechazaPedidoADomicilioSinDomicilioAsignado() {
        Pedido pedido = pedido(ModalidadEntrega.DOMICILIO);
        pedido.agregarProducto(bebidaValida(), 1);

        assertThrows(PedidoNoConfirmableException.class, pedido::confirmar);
    }

    @Test
    void confirmarRechazaProductoQueNoCumpleCalidad() {
        Pedido pedido = pedido(ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        pedido.agregarProducto(new Bebida("B2", "Jugo", "Malo", 1000, 5, 2000, 5, true, false), 1);

        assertThrows(ProductoNoCumpleCalidadException.class, pedido::confirmar);
    }

    @Test
    void confirmarCambiaElEstadoAConfirmado() {
        Pedido pedido = pedido(ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        pedido.agregarProducto(bebidaValida(), 1);

        pedido.confirmar();

        assertEquals(EstadoPedido.CONFIRMADO, pedido.getEstado());
    }

    @Test
    void confirmarDosVecesRechazaLaSegundaConfirmacion() {
        Pedido pedido = pedido(ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        pedido.agregarProducto(bebidaValida(), 1);
        pedido.confirmar();

        assertThrows(PedidoNoConfirmableException.class, pedido::confirmar);
    }

    @Test
    void entregarRechazaPedidoNoConfirmado() {
        Pedido pedido = pedido(ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        pedido.agregarProducto(bebidaValida(), 1);

        assertThrows(IllegalStateException.class, pedido::entregar);
    }

    @Test
    void entregarCambiaElEstadoAEntregado() {
        Pedido pedido = pedido(ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        pedido.agregarProducto(bebidaValida(), 1);
        pedido.confirmar();

        pedido.entregar();

        assertEquals(EstadoPedido.ENTREGADO, pedido.getEstado());
    }

    @Test
    void cancelarPedidoPendienteCambiaElEstado() {
        Pedido pedido = pedido(ModalidadEntrega.CONSUMO_EN_RESTAURANTE);

        pedido.cancelar();

        assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
    }

    @Test
    void cancelarPedidoEntregadoRechazaLaOperacion() {
        Pedido pedido = pedido(ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        pedido.agregarProducto(bebidaValida(), 1);
        pedido.confirmar();
        pedido.entregar();

        assertThrows(IllegalStateException.class, pedido::cancelar);
    }
}
