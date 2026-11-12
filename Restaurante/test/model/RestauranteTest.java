package model;

import model.enumeraciones.CategoriaProducto;
import model.enumeraciones.EstadoPedido;
import model.enumeraciones.ModalidadEntrega;
import model.exception.ClienteNoRegistradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class RestauranteTest {

    private Restaurante restaurante;
    private Cliente cliente;
    private Bebida bebida;
    private Postre postre;

    @BeforeEach
    void setUp() {
        restaurante = new Restaurante("El Buen Comer", "900123", "Calle 1");
        cliente = new Cliente("123", "Ana Pérez", null, null, null);
        bebida = new Bebida("B1", "Jugo", "Natural", 1000, 5, 300, 5, true, false);
        postre = new Postre("P1", "Flan", "Casero", 1000, 5, 100, 20, false);
    }

    private Pedido pedido(String codigo, Cliente cliente, ModalidadEntrega modalidad) {
        return new Pedido(codigo, LocalDate.of(2026, 11, 11), cliente, modalidad);
    }

    @Test
    void registrarProductoAgregaAlMenu() {
        restaurante.registrarProducto(bebida);

        assertEquals(1, restaurante.getMenu().size());
    }

    @Test
    void registrarProductoRechazaCodigoDuplicado() {
        restaurante.registrarProducto(bebida);
        Bebida duplicada = new Bebida("B1", "Otro", "x", 500, 5, 300, 5, true, false);

        assertThrows(IllegalArgumentException.class, () -> restaurante.registrarProducto(duplicada));
    }

    @Test
    void registrarClienteRechazaDocumentoDuplicado() {
        restaurante.registrarCliente(cliente);
        Cliente duplicado = new Cliente("123", "Otra", null, null, null);

        assertThrows(IllegalArgumentException.class, () -> restaurante.registrarCliente(duplicado));
    }

    @Test
    void buscarClienteDevuelveElClienteRegistrado() {
        restaurante.registrarCliente(cliente);

        assertSame(cliente, restaurante.buscarCliente("123"));
    }

    @Test
    void buscarClienteNoRegistradoLanzaExcepcion() {
        assertThrows(ClienteNoRegistradoException.class, () -> restaurante.buscarCliente("999"));
    }

    @Test
    void registrarPedidoRechazaClienteNoRegistrado() {
        restaurante.registrarProducto(bebida);
        Pedido pedido = pedido("PED1", cliente, ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        pedido.agregarProducto(bebida, 1);

        assertThrows(ClienteNoRegistradoException.class, () -> restaurante.registrarPedido(pedido));
    }

    @Test
    void registrarPedidoRechazaProductoFueraDelMenu() {
        restaurante.registrarCliente(cliente);
        Pedido pedido = pedido("PED1", cliente, ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        pedido.agregarProducto(bebida, 1);

        assertThrows(IllegalArgumentException.class, () -> restaurante.registrarPedido(pedido));
    }

    @Test
    void registrarPedidoRechazaCodigoRepetido() {
        restaurante.registrarCliente(cliente);
        restaurante.registrarProducto(bebida);
        Pedido primero = pedido("PED1", cliente, ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        primero.agregarProducto(bebida, 1);
        restaurante.registrarPedido(primero);

        Pedido repetido = pedido("PED1", cliente, ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        repetido.agregarProducto(bebida, 1);

        assertThrows(IllegalArgumentException.class, () -> restaurante.registrarPedido(repetido));
    }

    @Test
    void registrarPedidoConfirmaYGuardaElPedido() {
        restaurante.registrarCliente(cliente);
        restaurante.registrarProducto(bebida);
        Pedido pedido = pedido("PED1", cliente, ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        pedido.agregarProducto(bebida, 1);

        restaurante.registrarPedido(pedido);

        assertSame(pedido, restaurante.buscarPedido("PED1"));
        assertEquals(EstadoPedido.CONFIRMADO, pedido.getEstado());
        assertEquals(1, cliente.historialPedidos().size());
    }

    @Test
    void buscarPedidoInexistenteDevuelveNull() {
        assertNull(restaurante.buscarPedido("NOEXISTE"));
    }

    @Test
    void calcularIngresosExcluyeLosPedidosCancelados() {
        restaurante.registrarCliente(cliente);
        restaurante.registrarProducto(bebida);
        restaurante.registrarProducto(postre);

        Pedido activo = pedido("PED1", cliente, ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        activo.agregarProducto(bebida, 2);
        restaurante.registrarPedido(activo);

        Pedido cancelado = pedido("PED2", cliente, ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        cancelado.agregarProducto(postre, 1);
        restaurante.registrarPedido(cancelado);
        cancelado.cancelar();

        assertEquals(2200, restaurante.calcularIngresos(), 0.001);
    }

    @Test
    void calcularIngresosDomiciliosSumaLosDomiciliosActivos() {
        restaurante.registrarCliente(cliente);
        restaurante.registrarProducto(bebida);
        Pedido pedido = pedido("PED1", cliente, ModalidadEntrega.DOMICILIO);
        pedido.agregarProducto(bebida, 1);
        pedido.asignarDomicilio(new Domicilio(3, new RapidoExpress()));
        restaurante.registrarPedido(pedido);

        assertEquals(9500, restaurante.calcularIngresosDomicilios(), 0.001);
    }

    @Test
    void productoMasVendidoSinVentasEsNull() {
        assertNull(restaurante.productoMasVendido());
    }

    @Test
    void productoMasVendidoDevuelveElDeMayorCantidad() {
        restaurante.registrarCliente(cliente);
        restaurante.registrarProducto(bebida);
        restaurante.registrarProducto(postre);

        Pedido uno = pedido("PED1", cliente, ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        uno.agregarProducto(bebida, 2);
        restaurante.registrarPedido(uno);

        Pedido dos = pedido("PED2", cliente, ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        dos.agregarProducto(postre, 1);
        restaurante.registrarPedido(dos);

        assertSame(bebida, restaurante.productoMasVendido());
    }

    @Test
    void categoriaMayorIngresoSinVentasEsNull() {
        assertNull(restaurante.categoriaMayorIngreso());
    }

    @Test
    void categoriaMayorIngresoDevuelveLaCategoriaConMasIngresos() {
        restaurante.registrarCliente(cliente);
        restaurante.registrarProducto(bebida);
        restaurante.registrarProducto(postre);

        Pedido uno = pedido("PED1", cliente, ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        uno.agregarProducto(bebida, 2);
        restaurante.registrarPedido(uno);

        Pedido dos = pedido("PED2", cliente, ModalidadEntrega.CONSUMO_EN_RESTAURANTE);
        dos.agregarProducto(postre, 1);
        restaurante.registrarPedido(dos);

        assertEquals(CategoriaProducto.BEBIDA, restaurante.categoriaMayorIngreso());
    }

    @Test
    void productosNoConformesIncluyeSoloLosProductosInvalidos() {
        Bebida invalida = new Bebida("B2", "Jugo", "Malo", 1000, 5, 2000, 5, true, false);
        restaurante.registrarProducto(bebida);
        restaurante.registrarProducto(invalida);

        assertEquals(1, restaurante.productosNoConformes().size());
        assertSame(invalida, restaurante.productosNoConformes().get(0));
    }
}
