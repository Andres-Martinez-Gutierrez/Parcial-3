package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Cliente registrado en el restaurante, con su historial de pedidos. */
public class Cliente {

    private final String numeroDocumento;
    private String nombreCompleto;
    private String telefono;
    private String correo;
    private String direccion;
    private final List<Pedido> pedidos = new ArrayList<>();

    /**
     * Crea un cliente sin pedidos en su historial.
     *
     * @param numeroDocumento documento de identidad, obligatorio
     * @param nombreCompleto  nombre del cliente, obligatorio
     * @param telefono        teléfono de contacto, puede ser null
     * @param correo          correo electrónico, puede ser null
     * @param direccion       dirección de residencia, puede ser null
     * @throws IllegalArgumentException si el documento o el nombre están vacíos
     */
    public Cliente(String numeroDocumento, String nombreCompleto, String telefono, String correo, String direccion) {
        this.numeroDocumento = Validaciones.requireNotBlank(numeroDocumento, "El número de documento es obligatorio");
        this.nombreCompleto = Validaciones.requireNotBlank(nombreCompleto, "El nombre del cliente es obligatorio");
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
    }

    /** Agrega un pedido confirmado al historial. Lo usa el restaurante al registrar el pedido. */
    void agregarPedido(Pedido pedido) {
        pedidos.add(pedido);
    }

    /** Devuelve una copia de solo lectura de los pedidos del cliente. */
    public List<Pedido> historialPedidos() {
        return Collections.unmodifiableList(new ArrayList<>(pedidos));
    }

    public String getNumeroDocumento() { return numeroDocumento; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getDireccion() { return direccion; }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = Validaciones.requireNotBlank(nombreCompleto, "El nombre del cliente es obligatorio");
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "Cliente{numeroDocumento='" + numeroDocumento + "', nombreCompleto='" + nombreCompleto
                + "', telefono='" + telefono + "', correo='" + correo + "', direccion='" + direccion
                + "', pedidos=" + pedidos.size() + "}";
    }
}
