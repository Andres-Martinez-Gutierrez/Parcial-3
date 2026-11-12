import model.Bebida;
import model.Cliente;
import model.Domicilio;
import model.EntregaSegura;
import model.LineaPedido;
import model.Pedido;
import model.PlatoFuerte;
import model.Postre;
import model.Producto;
import model.RapidoExpress;
import model.Restaurante;
import model.ServicioDomicilio;
import model.enumeraciones.CategoriaProducto;
import model.enumeraciones.EstadoPedido;
import model.enumeraciones.ModalidadEntrega;
import model.enumeraciones.TipoPreparacion;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Font;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main {

    private static final NumberFormat MONEDA =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
    private static final Restaurante RESTAURANTE =
            new Restaurante("El Buen Comer", "901456789-1", "Armenia, Quindío");
    private static final List<Pedido> PEDIDOS_PENDIENTES = new ArrayList<>();
    private static final String[] OPCIONES_MENU = {
            "Registrar producto", "Registrar cliente", "Crear pedido",
            "Gestionar pedido", "Consultar pedido y total", "Historial de cliente",
            "Reportes", "Consultar productos", "Salir"
    };

    public static void main(String[] args) {
        cargarDatosDePrueba();
        JOptionPane.showMessageDialog(null,
                "Bienvenido al sistema de gestión de " + RESTAURANTE.getNombreComercial()
                        + "\nArmenia, Quindío\n\nSe cargaron productos, clientes y un pedido de demostración.",
                "El Buen Comer", JOptionPane.INFORMATION_MESSAGE);

        boolean continuar = true;
        while (continuar) {
            int opcion = JOptionPane.showOptionDialog(null,
                    "Seleccione la operación que desea realizar:",
                    RESTAURANTE.getNombreComercial() + " | Menú principal",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                    OPCIONES_MENU, OPCIONES_MENU[0]);
            if (opcion == JOptionPane.CLOSED_OPTION || opcion == 8) {
                continuar = false;
                continue;
            }
            try {
                switch (opcion) {
                    case 0:
                        registrarProducto();
                        break;
                    case 1:
                        registrarCliente();
                        break;
                    case 2:
                        crearPedido();
                        break;
                    case 3:
                        gestionarPedido();
                        break;
                    case 4:
                        consultarPedido();
                        break;
                    case 5:
                        consultarHistorial();
                        break;
                    case 6:
                        mostrarReportes();
                        break;
                    case 7:
                        mostrarMenuProductos();
                        break;
                    default:
                        break;
                }
            } catch (OperacionCancelada ignored) {
                // Cancelar una ventana vuelve al menú principal sin modificar el registro.
            } catch (RuntimeException e) {
                mostrarError(e.getMessage());
            }
        }
    }

    private static void cargarDatosDePrueba() {
        PlatoFuerte bandeja = new PlatoFuerte("P001", "Bandeja de la casa",
                "Carne, arroz, fríjoles y acompañantes", 28000, 25, 450,
                TipoPreparacion.ASADO, true);
        Bebida limonada = new Bebida("B001", "Limonada natural", "Limonada fría",
                5000, 5, 350, 6, true, false);
        Postre tresLeches = new Postre("D001", "Torta tres leches", "Porción refrigerada",
                8000, 5, 140, 4, true);
        RESTAURANTE.registrarProducto(bandeja);
        RESTAURANTE.registrarProducto(limonada);
        RESTAURANTE.registrarProducto(tresLeches);

        Cliente ana = new Cliente("1001001001", "Ana María Gómez", "3001234567",
                "ana@example.com", "Armenia");
        Cliente juan = new Cliente("1002002002", "Juan David López", "3107654321",
                "juan@example.com", "Armenia");
        RESTAURANTE.registrarCliente(ana);
        RESTAURANTE.registrarCliente(juan);

        Pedido pedido = new Pedido("PED-1001", LocalDate.now(), ana, ModalidadEntrega.DOMICILIO);
        pedido.agregarProducto(bandeja, 2);
        pedido.agregarProducto(limonada, 2);
        pedido.asignarDomicilio(new Domicilio(4, new EntregaSegura()));
        RESTAURANTE.registrarPedido(pedido);
    }

    private static void registrarProducto() {
        Object[] tipos = {"Plato fuerte", "Bebida", "Postre"};
        int tipo = elegir("Registrar producto", "Seleccione la categoría:", tipos);
        String codigo = leerTexto("Registrar producto", "Código único:");
        String nombre = leerTexto("Registrar producto", "Nombre:");
        String descripcion = leerOpcional("Registrar producto", "Descripción (opcional):");
        double precioBase = leerDoublePositivo("Registrar producto", "Precio base:");
        int tiempo = leerEnteroPositivo("Registrar producto", "Tiempo de preparación (minutos):");

        Producto producto;
        if (tipo == 0) {
            int peso = leerEnteroPositivo("Plato fuerte", "Peso (gramos):");
            TipoPreparacion preparacion = elegirTipoPreparacion();
            boolean coccion = confirmar("Plato fuerte", "¿La cocción está aprobada?");
            producto = new PlatoFuerte(codigo, nombre, descripcion, precioBase, tiempo,
                    peso, preparacion, coccion);
        } else if (tipo == 1) {
            int volumen = leerEnteroPositivo("Bebida", "Volumen (ml):");
            double temperatura = leerDouble("Bebida", "Temperatura de servicio (°C):");
            boolean fria = confirmar("Bebida", "¿Se sirve fría?");
            boolean especial = confirmar("Bebida", "¿Requiere preparación especial?");
            producto = new Bebida(codigo, nombre, descripcion, precioBase, tiempo,
                    volumen, temperatura, fria, especial);
        } else {
            int peso = leerEnteroPositivo("Postre", "Peso (gramos):");
            double temperatura = leerDouble("Postre", "Temperatura de almacenamiento (°C):");
            boolean refrigeracion = confirmar("Postre", "¿Requiere refrigeración?");
            producto = new Postre(codigo, nombre, descripcion, precioBase, tiempo,
                    peso, temperatura, refrigeracion);
        }
        RESTAURANTE.registrarProducto(producto);
        JOptionPane.showMessageDialog(null,
                "Producto registrado correctamente.\n\n" + producto.getNombre()
                        + "\nPrecio de venta: " + dinero(producto.calcularPrecioVenta())
                        + "\nCalidad: " + (producto.cumpleCondicionesCalidad() ? "Conforme" : "No conforme"),
                "Producto registrado", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void registrarCliente() {
        String documento = leerTexto("Registrar cliente", "Número de documento:");
        String nombre = leerTexto("Registrar cliente", "Nombre completo:");
        String telefono = leerOpcional("Registrar cliente", "Teléfono (opcional):");
        String correo = leerOpcional("Registrar cliente", "Correo (opcional):");
        String direccion = leerOpcional("Registrar cliente", "Dirección (opcional):");
        Cliente cliente = new Cliente(documento, nombre, telefono, correo, direccion);
        RESTAURANTE.registrarCliente(cliente);
        JOptionPane.showMessageDialog(null, "Cliente registrado: " + cliente.getNombreCompleto(),
                "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void crearPedido() {
        String codigo = leerTexto("Crear pedido", "Código del pedido:");
        if (RESTAURANTE.buscarPedido(codigo) != null || buscarBorrador(codigo) != null)
            throw new IllegalArgumentException("Ya existe un pedido con el código " + codigo);

        Cliente cliente = RESTAURANTE.buscarCliente(leerTexto("Crear pedido", "Documento del cliente:"));
        Object[] modalidades = {"Consumo en restaurante", "Domicilio"};
        int seleccionModalidad = elegir("Crear pedido", "Seleccione la modalidad:", modalidades);
        ModalidadEntrega modalidad = seleccionModalidad == 0
                ? ModalidadEntrega.CONSUMO_EN_RESTAURANTE : ModalidadEntrega.DOMICILIO;
        Pedido pedido = new Pedido(codigo, LocalDate.now(), cliente, modalidad);

        if (modalidad == ModalidadEntrega.DOMICILIO) {
            Object[] empresas = {"RapidoExpress (máximo 12 km)", "EntregaSegura (máximo 20 km)"};
            ServicioDomicilio empresa = elegir("Domicilio", "Seleccione la empresa:", empresas) == 0
                    ? new RapidoExpress() : new EntregaSegura();
            double distancia = leerDoublePositivo("Domicilio", "Distancia en kilómetros:");
            pedido.asignarDomicilio(new Domicilio(distancia, empresa));
        }

        boolean agregarMas = true;
        while (agregarMas) {
            Producto producto = elegirProducto();
            int cantidad = leerEnteroPositivo("Agregar producto", "Cantidad:");
            pedido.agregarProducto(producto, cantidad);
            agregarMas = confirmar("Crear pedido", "¿Desea agregar otro producto?");
        }

        if (pedido.getLineas().isEmpty())
            throw new IllegalArgumentException("Debe agregar al menos un producto.");
        if (confirmar("Confirmar pedido", detallePedido(pedido) + "\n\n¿Desea confirmar este pedido?")) {
            RESTAURANTE.registrarPedido(pedido);
            mostrarExito("Pedido " + pedido.getCodigo() + " confirmado correctamente.");
        } else {
            PEDIDOS_PENDIENTES.add(pedido);
            mostrarExito("El pedido quedó guardado como borrador pendiente de confirmación.");
        }
    }

    private static void gestionarPedido() {
        Pedido pedido = buscarPedidoEnSistema(leerTexto("Gestionar pedido", "Código del pedido:"));
        if (pedido == null)
            throw new IllegalArgumentException("No se encontró el pedido.");

        Object[] acciones = {"Confirmar", "Entregar", "Cancelar"};
        int accion = elegir("Gestionar pedido",
                "Pedido " + pedido.getCodigo() + " | Estado: " + pedido.getEstado()
                        + "\nSeleccione una acción:", acciones);
        if (accion == 0) {
            if (RESTAURANTE.buscarPedido(pedido.getCodigo()) != null)
                throw new IllegalArgumentException("El pedido ya fue registrado.");
            RESTAURANTE.registrarPedido(pedido);
            PEDIDOS_PENDIENTES.remove(pedido);
            mostrarExito("Pedido " + pedido.getCodigo() + " confirmado correctamente.");
        } else if (accion == 1) {
            pedido.entregar();
            mostrarExito("Pedido " + pedido.getCodigo() + " marcado como entregado.");
        } else {
            pedido.cancelar();
            mostrarExito("Pedido " + pedido.getCodigo() + " cancelado.");
        }
    }

    private static void consultarPedido() {
        Pedido pedido = buscarPedidoEnSistema(leerTexto("Consultar pedido", "Código del pedido:"));
        if (pedido == null)
            throw new IllegalArgumentException("No se encontró el pedido.");
        mostrarTexto("Pedido " + pedido.getCodigo(), detallePedido(pedido));
    }

    private static String detallePedido(Pedido pedido) {
        StringBuilder detalle = new StringBuilder();
        detalle.append("Código: ").append(pedido.getCodigo())
                .append("\nFecha: ").append(pedido.getFecha())
                .append("\nEstado: ").append(pedido.getEstado())
                .append("\nCliente: ").append(pedido.getCliente().getNombreCompleto())
                .append(" (").append(pedido.getCliente().getNumeroDocumento()).append(')')
                .append("\nModalidad: ").append(pedido.getModalidad())
                .append("\n\nPRODUCTOS\n");
        for (LineaPedido linea : pedido.getLineas()) {
            Producto producto = linea.getProducto();
            detalle.append(producto.getNombre()).append(" [").append(producto.getCodigo()).append("]")
                    .append("\n  ").append(linea.getCantidad()).append(" x ")
                    .append(dinero(producto.calcularPrecioVenta())).append(" = ")
                    .append(dinero(linea.calcularSubtotal())).append('\n');
        }
        detalle.append("\nSubtotal productos: ").append(dinero(pedido.calcularValorProductos()));
        if (pedido.getDomicilio() != null) {
            Domicilio domicilio = pedido.getDomicilio();
            detalle.append("\nDomicilio: ").append(domicilio.getEmpresa().getClass().getSimpleName())
                    .append("\nDistancia: ").append(domicilio.getDistanciaKm()).append(" km")
                    .append("\nCosto domicilio: ").append(dinero(domicilio.calcularValor()));
        } else {
            detalle.append("\nDomicilio: No aplica");
        }
        detalle.append("\n\nTOTAL: ").append(dinero(pedido.calcularTotal()));
        return detalle.toString();
    }

    private static void consultarHistorial() {
        Cliente cliente = RESTAURANTE.buscarCliente(leerTexto("Historial de pedidos", "Documento del cliente:"));
        StringBuilder historial = new StringBuilder("Cliente: ")
                .append(cliente.getNombreCompleto()).append("\nDocumento: ")
                .append(cliente.getNumeroDocumento()).append("\n\n");
        if (cliente.historialPedidos().isEmpty()) {
            historial.append("No hay pedidos confirmados en el historial.");
        } else {
            for (Pedido pedido : cliente.historialPedidos()) {
                historial.append("Código: ").append(pedido.getCodigo())
                        .append(" | Fecha: ").append(pedido.getFecha())
                        .append(" | Estado: ").append(pedido.getEstado())
                        .append(" | Total: ").append(dinero(pedido.calcularTotal())).append('\n');
            }
        }
        mostrarTexto("Historial del cliente", historial.toString());
    }

    private static void mostrarReportes() {
        Producto masVendido = RESTAURANTE.productoMasVendido();
        CategoriaProducto categoria = RESTAURANTE.categoriaMayorIngreso();
        List<Producto> noConformes = RESTAURANTE.productosNoConformes();
        StringBuilder reporte = new StringBuilder();
        reporte.append("Ingresos del restaurante (productos): ")
                .append(dinero(RESTAURANTE.calcularIngresos()))
                .append("\nIngresos por domicilios: ")
                .append(dinero(RESTAURANTE.calcularIngresosDomicilios()))
                .append("\nProducto más vendido: ")
                .append(masVendido == null ? "Sin ventas" : masVendido.getNombre()
                        + " (" + masVendido.getCodigo() + ")")
                .append("\nCategoría con mayor ingreso: ")
                .append(categoria == null ? "Sin ventas" : categoria)
                .append("\n\nPRODUCTOS NO CONFORMES\n");
        if (noConformes.isEmpty()) {
            reporte.append("Ninguno.");
        } else {
            for (Producto producto : noConformes)
                reporte.append(producto.getCodigo()).append(" - ")
                        .append(producto.getNombre()).append('\n');
        }
        mostrarTexto("Reportes del restaurante", reporte.toString());
    }

    private static void mostrarMenuProductos() {
        StringBuilder menu = new StringBuilder();
        if (RESTAURANTE.getMenu().isEmpty()) {
            menu.append("No hay productos registrados.");
        } else {
            for (Producto producto : RESTAURANTE.getMenu()) {
                menu.append(producto.getCodigo()).append(" | ").append(producto.getNombre())
                        .append(" | ").append(producto.getCategoria())
                        .append(" | ").append(dinero(producto.calcularPrecioVenta()))
                        .append(" | Calidad: ")
                        .append(producto.cumpleCondicionesCalidad() ? "Conforme" : "NO CONFORME")
                        .append('\n');
            }
        }
        mostrarTexto("Productos disponibles", menu.toString());
    }

    private static Producto elegirProducto() {
        List<Producto> productos = RESTAURANTE.getMenu();
        if (productos.isEmpty())
            throw new IllegalArgumentException("No hay productos registrados en el menú.");
        Object[] opciones = new Object[productos.size()];
        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            opciones[i] = producto.getCodigo() + " - " + producto.getNombre()
                    + " (" + dinero(producto.calcularPrecioVenta()) + ")";
        }
        return productos.get(elegir("Agregar producto", "Seleccione un producto:", opciones));
    }

    private static Pedido buscarBorrador(String codigo) {
        for (Pedido pedido : PEDIDOS_PENDIENTES)
            if (pedido.getCodigo().equals(codigo))
                return pedido;
        return null;
    }

    private static Pedido buscarPedidoEnSistema(String codigo) {
        Pedido pedido = RESTAURANTE.buscarPedido(codigo);
        return pedido != null ? pedido : buscarBorrador(codigo);
    }

    private static TipoPreparacion elegirTipoPreparacion() {
        TipoPreparacion[] tipos = TipoPreparacion.values();
        Object[] opciones = new Object[tipos.length];
        System.arraycopy(tipos, 0, opciones, 0, tipos.length);
        return tipos[elegir("Plato fuerte", "Seleccione el tipo de preparación:", opciones)];
    }

    private static int elegir(String titulo, String mensaje, Object[] opciones) {
        int seleccion = JOptionPane.showOptionDialog(null, mensaje, titulo,
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                opciones, opciones[0]);
        if (seleccion == JOptionPane.CLOSED_OPTION)
            throw new OperacionCancelada();
        return seleccion;
    }

    private static String leerTexto(String titulo, String mensaje) {
        while (true) {
            String valor = (String) JOptionPane.showInputDialog(null, mensaje, titulo,
                    JOptionPane.QUESTION_MESSAGE);
            if (valor == null)
                throw new OperacionCancelada();
            if (!valor.trim().isEmpty())
                return valor.trim();
            mostrarError("Este dato es obligatorio.");
        }
    }

    private static String leerOpcional(String titulo, String mensaje) {
        String valor = (String) JOptionPane.showInputDialog(null, mensaje, titulo,
                JOptionPane.QUESTION_MESSAGE);
        if (valor == null)
            throw new OperacionCancelada();
        return valor.trim();
    }

    private static int leerEnteroPositivo(String titulo, String mensaje) {
        while (true) {
            String texto = leerTexto(titulo, mensaje);
            try {
                int valor = Integer.parseInt(texto);
                if (valor > 0)
                    return valor;
            } catch (NumberFormatException ignored) {
                // Se muestra la validación y se vuelve a solicitar el dato.
            }
            mostrarError("Ingrese un número entero mayor que cero.");
        }
    }

    private static double leerDoublePositivo(String titulo, String mensaje) {
        while (true) {
            double valor = leerDouble(titulo, mensaje);
            if (valor > 0)
                return valor;
            mostrarError("El valor debe ser mayor que cero.");
        }
    }

    private static double leerDouble(String titulo, String mensaje) {
        while (true) {
            String texto = leerTexto(titulo, mensaje).replace(',', '.');
            try {
                double valor = Double.parseDouble(texto);
                if (Double.isFinite(valor))
                    return valor;
            } catch (NumberFormatException ignored) {
                // Se muestra la validación y se vuelve a solicitar el dato.
            }
            mostrarError("Ingrese un número válido.");
        }
    }

    private static boolean confirmar(String titulo, String mensaje) {
        int resultado = JOptionPane.showConfirmDialog(null, mensaje, titulo,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resultado == JOptionPane.CANCEL_OPTION || resultado == JOptionPane.CLOSED_OPTION)
            throw new OperacionCancelada();
        return resultado == JOptionPane.YES_OPTION;
    }

    private static void mostrarTexto(String titulo, String contenido) {
        JTextArea area = new JTextArea(contenido, 18, 54);
        area.setEditable(false);
        area.setCaretPosition(0);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JOptionPane.showMessageDialog(null, new JScrollPane(area), titulo,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "No se pudo completar",
                JOptionPane.ERROR_MESSAGE);
    }

    private static void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Operación exitosa",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static String dinero(double valor) {
        return MONEDA.format(valor);
    }

    private static final class OperacionCancelada extends RuntimeException {
    }
}
