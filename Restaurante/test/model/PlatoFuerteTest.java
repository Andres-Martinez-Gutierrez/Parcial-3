package model;

import model.enumeraciones.CategoriaProducto;
import model.enumeraciones.TipoPreparacion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlatoFuerteTest {

    private PlatoFuerte plato(int peso, int tiempo, boolean coccion) {
        return new PlatoFuerte("PF1", "Bandeja", "Paisa", 1000, tiempo, peso, TipoPreparacion.ASADO, coccion);
    }

    @Test
    void calcularPrecioVentaAplicaIncrementoDelVeinte() {
        assertEquals(1200, plato(500, 30, true).calcularPrecioVenta(), 0.001);
    }

    @Test
    void cumpleCalidadConDatosValidos() {
        assertTrue(plato(500, 30, true).cumpleCondicionesCalidad());
    }

    @Test
    void noCumpleSiPesoEsMenorA200() {
        assertFalse(plato(199, 30, true).cumpleCondicionesCalidad());
    }

    @Test
    void noCumpleSiPesoEsMayorA900() {
        assertFalse(plato(901, 30, true).cumpleCondicionesCalidad());
    }

    @Test
    void aceptaPesoEnLosLimites() {
        assertTrue(plato(200, 30, true).cumpleCondicionesCalidad());
        assertTrue(plato(900, 30, true).cumpleCondicionesCalidad());
    }

    @Test
    void noCumpleSiTiempoEsMenorA10() {
        assertFalse(plato(500, 9, true).cumpleCondicionesCalidad());
    }

    @Test
    void noCumpleSiTiempoEsMayorA60() {
        assertFalse(plato(500, 61, true).cumpleCondicionesCalidad());
    }

    @Test
    void aceptaTiempoEnLosLimites() {
        assertTrue(plato(500, 10, true).cumpleCondicionesCalidad());
        assertTrue(plato(500, 60, true).cumpleCondicionesCalidad());
    }

    @Test
    void noCumpleSiLaCoccionNoFueAprobada() {
        assertFalse(plato(500, 30, false).cumpleCondicionesCalidad());
    }

    @Test
    void getCategoriaEsPlatoFuerte() {
        assertEquals(CategoriaProducto.PLATO_FUERTE, plato(500, 30, true).getCategoria());
    }
}
