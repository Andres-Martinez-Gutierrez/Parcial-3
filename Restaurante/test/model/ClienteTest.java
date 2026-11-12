package model;

import model.enumeraciones.ModalidadEntrega;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    private Cliente cliente() {
        return new Cliente("123", "Ana Pérez", "3001234567", "ana@mail.com", "Calle 1");
    }

    private Pedido pedido(Cliente cliente, String codigo) {
        return new Pedido(codigo, LocalDate.of(2026, 11, 11), cliente, ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
    }

    @Test
    void historialEstaVacioAlCrearElCliente() {
        assertTrue(cliente().historialPedidos().isEmpty());
    }

    @Test
    void agregarPedidoActualizaElHistorial() {
        Cliente cliente = cliente();
        Pedido pedido = pedido(cliente, "P1");

        cliente.agregarPedido(pedido);

        assertEquals(1, cliente.historialPedidos().size());
        assertSame(pedido, cliente.historialPedidos().get(0));
    }

    @Test
    void historialEsDeSoloLectura() {
        Cliente cliente = cliente();
        Pedido pedido = pedido(cliente, "P1");

        assertThrows(UnsupportedOperationException.class, () -> cliente.historialPedidos().add(pedido));
    }
}
