package model;

import model.enumeraciones.CategoriaProducto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BebidaTest {
    private Bebida bebida(double precio, int volumen, double temperatura, boolean fria, boolean especial) {
        return new Bebida("B1", "Jugo", "Natural", precio, 5, volumen, temperatura, fria, especial);
    }

    @Test
    void calcularPrecioVentaAplicaIncrementoDelDiez() {
        assertEquals(1100, bebida(1000, 300, 5, true, false).calcularPrecioVenta(), 0.001);
    }

    @Test
    void calcularPrecioVentaSumaRecargoDePreparacionEspecial() {
        assertEquals(4100, bebida(1000, 300, 5, true, true).calcularPrecioVenta(), 0.001);
    }

    @Test
    void cumpleCalidadFriaDentroDeRango() {
        assertTrue(bebida(1000, 300, 5, true, false).cumpleCondicionesCalidad());
    }

    @Test
    void cumpleCalidadCalienteDentroDeRango() {
        assertTrue(bebida(1000, 300, 60, false, false).cumpleCondicionesCalidad());
    }

    @Test
    void noCumpleSiVolumenEsMenorA150() {
        assertFalse(bebida(1000, 149, 5, true, false).cumpleCondicionesCalidad());
    }

    @Test
    void noCumpleSiVolumenEsMayorA1500() {
        assertFalse(bebida(1000, 1501, 5, true, false).cumpleCondicionesCalidad());
    }

    @Test
    void aceptaVolumenEnLosLimites() {
        assertTrue(bebida(1000, 150, 5, true, false).cumpleCondicionesCalidad());
        assertTrue(bebida(1000, 1500, 5, true, false).cumpleCondicionesCalidad());
    }

    @Test
    void noCumpleFriaFueraDeRango() {
        assertFalse(bebida(1000, 300, 11, true, false).cumpleCondicionesCalidad());
    }

    @Test
    void noCumpleCalienteFueraDeRango() {
        assertFalse(bebida(1000, 300, 50, false, false).cumpleCondicionesCalidad());
    }

    @Test
    void aceptaTemperaturasFriasEnLosLimites() {
        assertTrue(bebida(1000, 300, 0, true, false).cumpleCondicionesCalidad());
        assertTrue(bebida(1000, 300, 10, true, false).cumpleCondicionesCalidad());
    }

    @Test
    void getCategoriaEsBebida() {
        assertEquals(CategoriaProducto.BEBIDA, bebida(1000, 300, 5, true, false).getCategoria());
    }
}