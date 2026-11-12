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
import model.exception.ElBuenComerException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
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

    /** Opciones del menú principal, en el orden en que se muestran. */
    private enum Opcion {
        REGISTRAR_PRODUCTO("Registrar producto"),
        REGISTRAR_CLIENTE("Registrar cliente"),
        CREAR_PEDIDO("Crear pedido"),
        GESTIONAR_PEDIDO("Gestionar pedido"),
        PEDIDOS_PENDIENTES("Pedidos pendientes"),
        CONSULTAR_PEDIDO("Consultar pedido y total"),
        HISTORIAL_CLIENTE("Historial de cliente"),
        REPORTES("Reportes"),
        CONSULTAR_PRODUCTOS("Consultar productos"),
        SALIR("Salir");

        private final String etiqueta;

        Opcion(String etiqueta) {
            this.etiqueta = etiqueta;
        }
    }

    /**
     * Punto de entrada del programa. Carga los datos de demostración y muestra el menú principal.
     *
     * @param args argumentos de línea de comandos (no se usan)
     */
    public static void main(String[] args) {
        cargarDatosDePrueba();
        cargarMenuPrincipla();
    }

    /** Se inicializa el menu principal del restaurante y todas sus opciones */
    private static void cargarMenuPrincipla() {
        Opcion[] opciones = Opcion.values();
        String[] etiquetas = new String[opciones.length];
        for (int i = 0; i < opciones.length; i++)
            etiquetas[i] = opciones[i].etiqueta;

        while (true) {
            int indice = elegirEnMenu(etiquetas);
            if (indice < 0 || opciones[indice] == Opcion.SALIR)
                break;
            try {
                ejecutar(opciones[indice]);
            } catch (OperacionCancelada ignored) {
                // Cancelar una ventana vuelve al menú principal sin modificar el registro.
            } catch (ElBuenComerException | IllegalArgumentException | IllegalStateException e) {
                mostrarError(e.getMessage());
            } catch (RuntimeException e) {
                mostrarError("Ocurrió un error inesperado. Revise la consola para más detalles.");
            }
        }
    }

    /**
     * Muestra el menú principal como una lista vertical. Devuelve el índice elegido,
     * o -1 si el usuario elige "Salir" o cierra la ventana.
     */
    private static int elegirEnMenu(String[] etiquetas) {
        int[] seleccion = {-1};
        JDialog dialogo = new JDialog((Frame) null,
                RESTAURANTE.getNombreComercial() + " | Menú principal", true);

        JLabel encabezado = new JLabel("Seleccione la operación que desea realizar:");
        encabezado.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        encabezado.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel botones = new JPanel(new GridLayout(0, 1, 0, 8));
        for (int i = 0; i < etiquetas.length; i++) {
            final int indice = i;
            JButton boton = new JButton(etiquetas[i]);
            boton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            boton.setFocusPainted(false);
            boton.addActionListener(e -> {
                seleccion[0] = indice;
                dialogo.dispose();
            });
            botones.add(boton);
        }

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        contenido.add(encabezado, BorderLayout.NORTH);
        contenido.add(botones, BorderLayout.CENTER);

        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.setContentPane(contenido);
        dialogo.setResizable(false);
        dialogo.pack();
        dialogo.setLocationRelativeTo(null);
        dialogo.setVisible(true);
        return seleccion[0];
    }

    /**
     * Ejecuta la operación elegida en el menú principal.
     *
     * @param opcion operación seleccionada por el usuario
     */
    private static void ejecutar(Opcion opcion) {
        switch (opcion) {
            case REGISTRAR_PRODUCTO -> registrarProducto();
            case REGISTRAR_CLIENTE -> registrarCliente();
            case CREAR_PEDIDO -> crearPedido();
            case GESTIONAR_PEDIDO -> gestionarPedido();
            case PEDIDOS_PENDIENTES -> mostrarPedidosPendientes();
            case CONSULTAR_PEDIDO -> consultarPedido();
            case HISTORIAL_CLIENTE -> consultarHistorial();
            case REPORTES -> mostrarReportes();
            case CONSULTAR_PRODUCTOS -> mostrarMenuProductos();
            default -> { }
        }
    }

    /** Registra en el restaurante productos, clientes y un pedido de demostración. */
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

    /** Pide los datos de un producto según su categoría y lo registra en el menú. */
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

    /** Pide los datos de un cliente y lo registra en el restaurante. */
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

    /**
     * Crea un pedido paso a paso: código, cliente, modalidad, domicilio si aplica y productos.
     * Al final el usuario puede confirmarlo, guardarlo como borrador o descartarlo.
     */
    private static void crearPedido() {
        String codigo = leerTexto("Crear pedido", "Código del pedido:");
        if (buscarPedidoEnSistema(codigo) != null)
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
            double distancia = leerDistanciaAceptada(empresa);
            pedido.asignarDomicilio(new Domicilio(distancia, empresa));
        }

        boolean agregarMas = true;
        while (agregarMas) {
            Producto producto = elegirProducto();
            int cantidad = leerEnteroPositivo("Agregar producto", "Cantidad:");
            pedido.agregarProducto(producto, cantidad);
            agregarMas = confirmar("Crear pedido", "¿Desea agregar otro producto?");
        }

        if (pedido.getLineasPedido().isEmpty())
            throw new IllegalArgumentException("Debe agregar al menos un producto.");

        Object[] decisiones = {"Confirmar", "Guardar borrador", "Descartar"};
        int decision = elegir("Confirmar pedido",
                detallePedido(pedido) + "\n\n¿Qué desea hacer con este pedido?", decisiones);
        if (decision == 0) {
            RESTAURANTE.registrarPedido(pedido);
            mostrarExito("Pedido " + pedido.getCodigo() + " confirmado correctamente.");
        } else if (decision == 1) {
            RESTAURANTE.guardarBorrador(pedido);
            mostrarExito("El pedido quedó guardado como borrador pendiente de confirmación.");
        }
    }

    /**
     * Gestiona un pedido por su código. Si es un borrador, delega en {@link #gestionarBorrador(Pedido)};
     * si es un pedido registrado, ofrece solo las acciones válidas para su estado.
     */
    private static void gestionarPedido() {
        String codigo = leerTexto("Gestionar pedido", "Código del pedido:");
        Pedido borrador = RESTAURANTE.buscarBorrador(codigo);
        if (borrador != null) {
            gestionarBorrador(borrador);
            return;
        }

        Pedido pedido = RESTAURANTE.buscarPedido(codigo);
        if (pedido == null)
            throw new IllegalArgumentException("No se encontró el pedido.");

        List<String> acciones = new ArrayList<>();
        if (pedido.getEstado() == EstadoPedido.CONFIRMADO)
            acciones.add("Entregar");
        if (pedido.getEstado() != EstadoPedido.ENTREGADO && pedido.getEstado() != EstadoPedido.CANCELADO)
            acciones.add("Cancelar");
        if (acciones.isEmpty())
            throw new IllegalStateException("El pedido " + codigo + " está " + pedido.getEstado()
                    + " y no admite cambios.");

        int accion = elegir("Gestionar pedido",
                "Pedido " + codigo + " | Estado: " + pedido.getEstado() + "\nSeleccione una acción:",
                acciones.toArray());
        if (acciones.get(accion).equals("Entregar")) {
            pedido.entregar();
            mostrarExito("Pedido " + codigo + " marcado como entregado.");
        } else {
            pedido.cancelar();
            mostrarExito("Pedido " + codigo + " cancelado.");
        }
    }

    /** Acciones sobre un borrador: confirmarlo o eliminarlo. */
    private static void gestionarBorrador(Pedido borrador) {
        String codigo = borrador.getCodigo();
        int accion = elegir("Gestionar pedido",
                "Borrador " + codigo + " | Estado: PENDIENTE\nSeleccione una acción:",
                new Object[]{"Confirmar", "Eliminar borrador"});
        if (accion == 0) {
            RESTAURANTE.confirmarBorrador(borrador);
            mostrarExito("Pedido " + codigo + " confirmado correctamente.");
        } else {
            RESTAURANTE.eliminarBorrador(borrador);
            mostrarExito("El borrador " + codigo + " fue eliminado.");
        }
    }

    /** Muestra la lista de borradores pendientes de confirmación con su cliente y total. */
    private static void mostrarPedidosPendientes() {
        List<Pedido> borradores = RESTAURANTE.getBorradores();
        if (borradores.isEmpty()) {
            mostrarTexto("Pedidos pendientes", "No hay pedidos pendientes de confirmación.");
            return;
        }
        StringBuilder texto = new StringBuilder();
        for (Pedido pedido : borradores) {
            texto.append(pedido.getCodigo()).append(" | ")
                    .append(pedido.getCliente().getNombreCompleto()).append(" | ")
                    .append(dinero(pedido.calcularTotal())).append('\n');
        }
        mostrarTexto("Pedidos pendientes", texto.toString());
    }

    /** Muestra el detalle y el total de un pedido, confirmado o borrador. */
    private static void consultarPedido() {
        Pedido pedido = buscarPedidoEnSistema(leerTexto("Consultar pedido", "Código del pedido:"));
        if (pedido == null)
            throw new IllegalArgumentException("No se encontró el pedido.");
        mostrarTexto("Pedido " + pedido.getCodigo(), detallePedido(pedido));
    }

    /**
     * Arma el texto con los datos, productos, domicilio y total de un pedido.
     *
     * @param pedido pedido a describir
     * @return texto listo para mostrar en un diálogo
     */
    private static String detallePedido(Pedido pedido) {
        StringBuilder detalle = new StringBuilder();
        detalle.append("Código: ").append(pedido.getCodigo())
                .append("\nFecha: ").append(pedido.getFecha())
                .append("\nEstado: ").append(pedido.getEstado())
                .append("\nCliente: ").append(pedido.getCliente().getNombreCompleto())
                .append(" (").append(pedido.getCliente().getNumeroDocumento()).append(')')
                .append("\nModalidad: ").append(pedido.getModalidad())
                .append("\n\nPRODUCTOS\n");
        for (LineaPedido linea : pedido.getLineasPedido()) {
            Producto producto = linea.getProducto();
            detalle.append(producto.getNombre()).append(" [").append(producto.getCodigo()).append("]")
                    .append("\n  ").append(linea.getCantidad()).append(" x ")
                    .append(dinero(producto.calcularPrecioVenta())).append(" = ")
                    .append(dinero(linea.calcularSubtotal())).append('\n');
        }
        detalle.append("\nSubtotal productos: ").append(dinero(pedido.calcularValorProductos()));
        if (pedido.getDomicilio() != null) {
            Domicilio domicilio = pedido.getDomicilio();
            detalle.append("\nDomicilio: ").append(domicilio.getDomicilio().getClass().getSimpleName())
                    .append("\nDistancia: ").append(domicilio.getDistanciaKm()).append(" km")
                    .append("\nCosto domicilio: ").append(dinero(domicilio.calcularValor()));
        } else {
            detalle.append("\nDomicilio: No aplica");
        }
        detalle.append("\n\nTOTAL: ").append(dinero(pedido.calcularTotal()));
        return detalle.toString();
    }

    /** Muestra los pedidos confirmados de un cliente, consultado por su documento. */
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

    /** Muestra los indicadores de ventas y la lista de productos no conformes. */
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

    /** Muestra el catálogo de productos con su categoría, precio y estado de calidad. */
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

    /**
     * Pide al usuario que escoja un producto del menú.
     *
     * @return producto elegido
     * @throws IllegalArgumentException si el menú no tiene productos
     */
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

    /** Pide la distancia hasta que la empresa seleccionada la acepte. */
    private static double leerDistanciaAceptada(ServicioDomicilio empresa) {
        while (true) {
            double distancia = leerDoublePositivo("Domicilio", "Distancia en kilómetros:");
            if (empresa.aceptaRecorrido(distancia))
                return distancia;
            mostrarError("La empresa seleccionada no acepta entregas a " + distancia
                    + " km. Ingrese otra distancia o cancele y elija otra empresa.");
        }
    }

    /** Busca un pedido confirmado o un borrador por código. */
    private static Pedido buscarPedidoEnSistema(String codigo) {
        Pedido pedido = RESTAURANTE.buscarPedido(codigo);
        return pedido != null ? pedido : RESTAURANTE.buscarBorrador(codigo);
    }

    /**
     * Pide el tipo de preparación de un plato fuerte.
     *
     * @return tipo de preparación elegido
     */
    private static TipoPreparacion elegirTipoPreparacion() {
        TipoPreparacion[] tipos = TipoPreparacion.values();
        return tipos[elegir("Plato fuerte", "Seleccione el tipo de preparación:", tipos)];
    }

    /**
     * Muestra un diálogo con botones y devuelve el índice del botón elegido.
     *
     * @param titulo  título de la ventana
     * @param mensaje texto que se muestra sobre los botones
     * @param opciones etiquetas de los botones
     * @return índice de la opción elegida
     * @throws OperacionCancelada si el usuario cierra la ventana
     */
    private static int elegir(String titulo, String mensaje, Object[] opciones) {
        int seleccion = JOptionPane.showOptionDialog(null, mensaje, titulo,
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                opciones, opciones[0]);
        if (seleccion == JOptionPane.CLOSED_OPTION)
            throw new OperacionCancelada();
        return seleccion;
    }

    /**
     * Pide un texto obligatorio. Repite la solicitud mientras el usuario deje el campo vacío.
     *
     * @return texto sin espacios al inicio ni al final
     * @throws OperacionCancelada si el usuario cancela el diálogo
     */
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

    /**
     * Pide un texto opcional. Puede devolver una cadena vacía.
     *
     * @return texto sin espacios al inicio ni al final
     * @throws OperacionCancelada si el usuario cancela el diálogo
     */
    private static String leerOpcional(String titulo, String mensaje) {
        String valor = (String) JOptionPane.showInputDialog(null, mensaje, titulo,
                JOptionPane.QUESTION_MESSAGE);
        if (valor == null)
            throw new OperacionCancelada();
        return valor.trim();
    }

    /**
     * Pide un número entero mayor que cero. Repite la solicitud si el dato no es válido.
     *
     * @return número entero positivo
     */
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

    /**
     * Pide un número decimal mayor que cero. Repite la solicitud si el dato no es válido.
     *
     * @return número positivo
     */
    private static double leerDoublePositivo(String titulo, String mensaje) {
        while (true) {
            double valor = leerDouble(titulo, mensaje);
            if (valor > 0)
                return valor;
            mostrarError("El valor debe ser mayor que cero.");
        }
    }

    /**
     * Pide un número decimal. Acepta la coma como separador. Repite la solicitud si no es válido.
     *
     * @return número decimal finito
     */
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

    /**
     * Muestra una pregunta de Sí/No.
     *
     * @return true si el usuario responde Sí, false si responde No
     * @throws OperacionCancelada si el usuario cancela o cierra la ventana
     */
    private static boolean confirmar(String titulo, String mensaje) {
        int resultado = JOptionPane.showConfirmDialog(null, mensaje, titulo,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resultado == JOptionPane.CANCEL_OPTION || resultado == JOptionPane.CLOSED_OPTION)
            throw new OperacionCancelada();
        return resultado == JOptionPane.YES_OPTION;
    }

    /** Muestra un texto largo, con desplazamiento y fuente monoespaciada. */
    private static void mostrarTexto(String titulo, String contenido) {
        JTextArea area = new JTextArea(contenido, 18, 54);
        area.setEditable(false);
        area.setCaretPosition(0);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JOptionPane.showMessageDialog(null, new JScrollPane(area), titulo,
                JOptionPane.INFORMATION_MESSAGE);
    }

    /** Muestra un mensaje de error al usuario. */
    private static void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "No se pudo completar",
                JOptionPane.ERROR_MESSAGE);
    }

    /** Muestra un mensaje de confirmación de una operación exitosa. */
    private static void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Operación exitosa",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Da formato de moneda colombiana a un valor.
     *
     * @param valor cantidad en pesos
     * @return cantidad formateada, por ejemplo "$ 1.000"
     */
    private static String dinero(double valor) {
        return MONEDA.format(valor);
    }

    /** Señal interna: el usuario canceló una ventana y no debe mostrarse ningún error. */
    private static final class OperacionCancelada extends RuntimeException {
    }
}
