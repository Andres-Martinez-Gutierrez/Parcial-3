package model;

import model.enumeraciones.CategoriaProducto;
import model.enumeraciones.EstadoPedido;
import model.exception.ClienteNoRegistradoException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** El restaurante: menú, clientes, pedidos e indicadores de ventas. */
public class Restaurante {

    private String nombreComercial;
    private String nit;
    private String direccion;
    private final List<Producto> menu = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Pedido> pedidos = new ArrayList<>();

    /**
     * Crea el restaurante con su menú, clientes y pedidos vacíos.
     *
     * @param nombreComercial nombre comercial, obligatorio
     * @param nit             número de identificación tributaria, obligatorio
     * @param direccion       dirección del local, puede ser null
     * @throws IllegalArgumentException si el nombre comercial o el NIT están vacíos
     */
    public Restaurante(String nombreComercial, String nit, String direccion) {
        this.nombreComercial = Validaciones.requireNotBlank(nombreComercial, "El nombre comercial es obligatorio");
        this.nit = Validaciones.requireNotBlank(nit, "El NIT es obligatorio");
        this.direccion = direccion;
    }

    /** Agrega un producto al menú. No se permiten códigos duplicados. */
    public void registrarProducto(Producto producto) {
        Validaciones.requireNonNull(producto, "El producto es obligatorio");
        for (Producto p : menu)
            if (p.getCodigo().equals(producto.getCodigo()))
                throw new IllegalArgumentException("Ya existe un producto con el código " + producto.getCodigo());
        menu.add(producto);
    }

    /** Registra un cliente. No se permiten documentos duplicados. */
    public void registrarCliente(Cliente cliente) {
        Validaciones.requireNonNull(cliente, "El cliente es obligatorio");
        for (Cliente c : clientes)
            if (c.getNumeroDocumento().equals(cliente.getNumeroDocumento()))
                throw new IllegalArgumentException("Ya existe un cliente con el documento " + cliente.getNumeroDocumento());
        clientes.add(cliente);
    }

    /**
     * Busca un cliente registrado por su documento.
     *
     * @throws ClienteNoRegistradoException si no existe
     */
    public Cliente buscarCliente(String documento) {
        for (Cliente c : clientes)
            if (c.getNumeroDocumento().equals(documento))
                return c;
        throw new ClienteNoRegistradoException(documento);
    }

    /**
     * Confirma y registra un pedido. Verifica que el cliente esté registrado, que los productos
     * pertenezcan al menú y que el pedido pueda confirmarse. Un código de pedido no puede repetirse.
     *
     * @throws ClienteNoRegistradoException si el cliente no está registrado
     * @throws IllegalArgumentException si un producto no está en el menú o el código está repetido
     */
    public void registrarPedido(Pedido pedido) {
        Validaciones.requireNonNull(pedido, "El pedido es obligatorio");
        Cliente registrado = buscarCliente(pedido.getCliente().getNumeroDocumento());
        for (Pedido p : pedidos)
            if (p.getCodigo().equals(pedido.getCodigo()))
                throw new IllegalArgumentException("Ya existe un pedido con el código " + pedido.getCodigo());
        for (LineaPedido linea : pedido.getLineas()) {
            Producto productoRegistrado = null;
            for (Producto p : menu)
                if (p.getCodigo().equals(linea.getProducto().getCodigo()))
                    productoRegistrado = p;
            if (productoRegistrado == null)
                throw new IllegalArgumentException("El producto " + linea.getProducto().getCodigo() + " no pertenece al menú");
            linea.setProducto(productoRegistrado);
        }
        pedido.setCliente(registrado);
        pedido.confirmar();
        pedidos.add(pedido);
        registrado.agregarPedido(pedido);
    }

    /** Pedido con ese código, o null si no existe. */
    public Pedido buscarPedido(String codigo) {
        for (Pedido p : pedidos)
            if (p.getCodigo().equals(codigo))
                return p;
        return null;
    }

    /** Ingresos por productos de los pedidos no cancelados, sin domicilios. */
    public double calcularIngresos() {
        double total = 0;
        for (Pedido p : pedidos)
            if (p.getEstado() != EstadoPedido.CANCELADO)
                total += p.calcularValorProductos();
        return total;
    }

    /** Valor de los domicilios de los pedidos no cancelados. Es independiente de los ingresos del restaurante. */
    public double calcularIngresosDomicilios() {
        double total = 0;
        for (Pedido p : pedidos)
            if (p.getEstado() != EstadoPedido.CANCELADO && p.getDomicilio() != null)
                total += p.getDomicilio().calcularValor();
        return total;
    }

    /** Producto con más unidades vendidas en pedidos no cancelados, o null si no hay ventas. */
    public Producto productoMasVendido() {
        Map<Producto, Integer> unidades = new HashMap<>();
        for (Pedido p : pedidos)
            if (p.getEstado() != EstadoPedido.CANCELADO)
                for (LineaPedido linea : p.getLineas())
                    unidades.merge(linea.getProducto(), linea.getCantidad(), Integer::sum);
        Producto mejor = null;
        int max = 0;
        for (Map.Entry<Producto, Integer> e : unidades.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                mejor = e.getKey();
            }
        }
        return mejor;
    }

    /** Categoría con mayores ingresos por productos, o null si no hay ventas. */
    public CategoriaProducto categoriaMayorIngreso() {
        Map<CategoriaProducto, Double> ingresos = new EnumMap<>(CategoriaProducto.class);
        for (Pedido p : pedidos)
            if (p.getEstado() != EstadoPedido.CANCELADO)
                for (LineaPedido linea : p.getLineas())
                    ingresos.merge(linea.getProducto().getCategoria(), linea.calcularSubtotal(), Double::sum);
        CategoriaProducto mejor = null;
        double max = -1;
        for (Map.Entry<CategoriaProducto, Double> e : ingresos.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                mejor = e.getKey();
            }
        }
        return mejor;
    }

    /** Productos del menú que hoy no cumplen las condiciones de calidad. */
    public List<Producto> productosNoConformes() {
        List<Producto> noConformes = new ArrayList<>();
        for (Producto p : menu)
            if (!p.cumpleCondicionesCalidad())
                noConformes.add(p);
        return noConformes;
    }

    public List<Producto> getMenu() { return Collections.unmodifiableList(menu); }
    public List<Cliente> getClientes() { return Collections.unmodifiableList(clientes); }
    public List<Pedido> getPedidos() { return Collections.unmodifiableList(pedidos); }
    public String getNombreComercial() { return nombreComercial; }
    public String getNit() { return nit; }
    public String getDireccion() { return direccion; }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = Validaciones.requireNotBlank(nombreComercial, "El nombre comercial es obligatorio");
    }

    public void setNit(String nit) {
        this.nit = Validaciones.requireNotBlank(nit, "El NIT es obligatorio");
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "Restaurante{nombreComercial='" + nombreComercial + "', nit='" + nit + "', direccion='" + direccion
                + "', productos=" + menu.size() + ", clientes=" + clientes.size()
                + ", pedidos=" + pedidos.size() + "}";
    }
}
