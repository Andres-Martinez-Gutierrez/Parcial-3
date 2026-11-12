package model;

import model.enumeraciones.EstadoPedido;
import model.enumeraciones.ModalidadEntrega;
import model.exception.PedidoNoConfirmableException;
import model.exception.ProductoNoCumpleCalidadException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Pedido de un cliente con sus productos, su modalidad y, si aplica, su domicilio. */
public class Pedido {

    private final String codigo;
    private LocalDate fecha;
    private Cliente cliente;
    private ModalidadEntrega modalidad;
    private EstadoPedido estado = EstadoPedido.PENDIENTE;
    private final List<LineaPedido> lineas = new ArrayList<>();
    private Domicilio domicilio;

    /**
     * Crea un pedido en estado PENDIENTE, sin productos ni domicilio.
     *
     * @param codigo     código del pedido, obligatorio
     * @param fecha      fecha del pedido, obligatoria
     * @param cliente    cliente que hace el pedido, obligatorio
     * @param modalidad  modalidad de entrega, obligatoria
     * @throws IllegalArgumentException si alguno de los datos obligatorios falta
     */
    public Pedido(String codigo, LocalDate fecha, Cliente cliente, ModalidadEntrega modalidad) {
        this.codigo = Validaciones.requireNotBlank(codigo, "El código del pedido es obligatorio");
        this.fecha = Validaciones.requireNonNull(fecha, "La fecha es obligatoria");
        this.cliente = Validaciones.requireNonNull(cliente, "El cliente es obligatorio");
        this.modalidad = Validaciones.requireNonNull(modalidad, "La modalidad es obligatoria");
    }

    /** Agrega un producto con su cantidad. Solo se permite mientras el pedido está PENDIENTE. */
    public void agregarProducto(Producto producto, int cantidad) {
        if (estado != EstadoPedido.PENDIENTE)
            throw new IllegalStateException("No se pueden agregar productos a un pedido " + estado);
        lineas.add(new LineaPedido(producto, cantidad));
    }

    /** Asocia el domicilio contratado. Solo aplica a pedidos con modalidad DOMICILIO. */
    public void asignarDomicilio(Domicilio domicilio) {
        if (modalidad != ModalidadEntrega.DOMICILIO)
            throw new IllegalArgumentException("Solo los pedidos a domicilio pueden tener domicilio");
        if (estado != EstadoPedido.PENDIENTE)
            throw new IllegalStateException("No se puede asignar domicilio a un pedido " + estado);
        this.domicilio = Validaciones.requireNonNull(domicilio, "El domicilio es obligatorio");
    }

    /** Suma de los subtotales de los productos, sin el domicilio. */
    public double calcularValorProductos() {
        double total = 0;
        for (LineaPedido linea : lineas)
            total += linea.calcularSubtotal();
        return total;
    }

    /** Valor de los productos más el costo del domicilio cuando corresponde. */
    public double calcularTotal() {
        return calcularValorProductos() + (domicilio == null ? 0 : domicilio.calcularValor());
    }

    /**
     * Confirma el pedido si tiene productos, cumple la calidad de cada producto y, si es a domicilio,
     * tiene un domicilio aceptado. Un pedido ya confirmado o cancelado no puede volver a confirmarse.
     *
     * @throws PedidoNoConfirmableException si alguna condición no se cumple
     * @throws ProductoNoCumpleCalidadException si un producto no cumple la calidad
     */
    public void confirmar() {
        if (estado != EstadoPedido.PENDIENTE)
            throw new PedidoNoConfirmableException("El pedido " + codigo + " ya está " + estado);
        if (lineas.isEmpty())
            throw new PedidoNoConfirmableException("El pedido " + codigo + " no tiene productos");
        if (modalidad == ModalidadEntrega.DOMICILIO && domicilio == null)
            throw new PedidoNoConfirmableException("El pedido " + codigo + " a domicilio no tiene un domicilio asignado");
        for (LineaPedido linea : lineas) {
            if (!linea.getProducto().cumpleCondicionesCalidad())
                throw new ProductoNoCumpleCalidadException(linea.getProducto().getCodigo());
        }
        estado = EstadoPedido.CONFIRMADO;
    }

    /** Marca el pedido como entregado. Solo un pedido confirmado puede entregarse. */
    public void entregar() {
        if (estado != EstadoPedido.CONFIRMADO)
            throw new IllegalStateException("Solo se entregan pedidos confirmados");
        estado = EstadoPedido.ENTREGADO;
    }

    /** Cancela el pedido. Un pedido entregado no puede cancelarse. */
    public void cancelar() {
        if (estado == EstadoPedido.ENTREGADO)
            throw new IllegalStateException("Un pedido entregado no se puede cancelar");
        estado = EstadoPedido.CANCELADO;
    }

    public String getCodigo() { return codigo; }
    public LocalDate getFecha() { return fecha; }
    public Cliente getCliente() { return cliente; }
    public ModalidadEntrega getModalidad() { return modalidad; }
    public EstadoPedido getEstado() { return estado; }
    public Domicilio getDomicilio() { return domicilio; }
    public List<LineaPedido> getLineas() { return Collections.unmodifiableList(lineas); }

    public void setFecha(LocalDate fecha) {
        this.fecha = Validaciones.requireNonNull(fecha, "La fecha es obligatoria");
    }

    public void setCliente(Cliente cliente) {
        this.cliente = Validaciones.requireNonNull(cliente, "El cliente es obligatorio");
    }

    public void setModalidad(ModalidadEntrega modalidad) {
        this.modalidad = Validaciones.requireNonNull(modalidad, "La modalidad es obligatoria");
    }

    /** Asigna o quita el domicilio (null). Solo puede tenerlo un pedido con modalidad DOMICILIO. */
    public void setDomicilio(Domicilio domicilio) {
        if (domicilio != null && modalidad != ModalidadEntrega.DOMICILIO)
            throw new IllegalArgumentException("Solo los pedidos a domicilio pueden tener domicilio");
        this.domicilio = domicilio;
    }

    @Override
    public String toString() {
        return "Pedido{codigo='" + codigo + "', fecha=" + fecha + ", cliente=" + cliente.getNombreCompleto()
                + ", modalidad=" + modalidad + ", estado=" + estado + ", lineas=" + lineas.size()
                + ", total=" + calcularTotal() + "}";
    }
}
