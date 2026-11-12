package model;

import model.enumeraciones.CategoriaProducto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PostreTest {

    private Postre postre(int peso, double temperatura, boolean refrigeracion) {
        return new Postre("P1", "Flan", "Casero", 1000, 5, peso, temperatura, refrigeracion);
    }

    @Test
    void calcularPrecioVentaSinRefrigeracionAplicaQuinceporciento() {
        assertEquals(1150, postre(100, 20, false).calcularPrecioVenta(), 0.001);
    }

    @Test
    void calcularPrecioVentaConRefrigeracionSumaCincoPorciento() {
        assertEquals(1200, postre(100, 4, true).calcularPrecioVenta(), 0.001);
    }

    @Test
    void cumpleCalidadRefrigeradoDentroDeRango() {
        assertTrue(postre(100, 3, true).cumpleCondicionesCalidad());
    }

    @Test
    void noCumpleRefrigeradoFueraDeRango() {
        assertFalse(postre(100, 7, true).cumpleCondicionesCalidad());
    }

    @Test
    void cumpleSinRefrigeracionDentroDeRango() {
        assertTrue(postre(100, 20, false).cumpleCondicionesCalidad());
    }

    @Test
    void noCumpleSinRefrigeracionFueraDeRango() {
        assertFalse(postre(100, 10, false).cumpleCondicionesCalidad());
    }

    @Test
    void noCumpleSiPesoEsMenorA80() {
        assertFalse(postre(79, 20, false).cumpleCondicionesCalidad());
    }

    @Test
    void noCumpleSiPesoEsMayorA500() {
        assertFalse(postre(501, 20, false).cumpleCondicionesCalidad());
    }

    @Test
    void aceptaPesoEnLosLimites() {
        assertTrue(postre(80, 20, false).cumpleCondicionesCalidad());
        assertTrue(postre(500, 20, false).cumpleCondicionesCalidad());
    }

    @Test
    void aceptaTemperaturasRefrigeradasEnLosLimites() {
        assertTrue(postre(100, 0, true).cumpleCondicionesCalidad());
        assertTrue(postre(100, 6, true).cumpleCondicionesCalidad());
    }

    @Test
    void getCategoriaEsPostre() {
        assertEquals(CategoriaProducto.POSTRE, postre(100, 20, false).getCategoria());
    }
}
