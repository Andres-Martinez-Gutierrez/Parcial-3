package model;

import model.exception.EmpresaNoAceptaException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EntregaSeguraTest {

    private final EntregaSegura empresa = new EntregaSegura();

    @Test
    void aceptaRecorridoPositivoHastaVeinteKm() {
        assertTrue(empresa.aceptaRecorrido(0.5));
        assertTrue(empresa.aceptaRecorrido(20));
    }

    @Test
    void noAceptaRecorridoNoPositivoNiMayorAVeinteKm() {
        assertFalse(empresa.aceptaRecorrido(0));
        assertFalse(empresa.aceptaRecorrido(20.01));
    }

    @Test
    void calcularTarifaSinRecargoHastaCincoKm() {
        assertEquals(12000, empresa.calcularTarifa(5), 0.001);
    }

    @Test
    void calcularTarifaConRecargoMayorACincoKm() {
        assertEquals(15500, empresa.calcularTarifa(5.5), 0.001);
    }

    @Test
    void calcularTarifaRechazaRecorridoLargo() {
        assertThrows(EmpresaNoAceptaException.class, () -> empresa.calcularTarifa(21));
    }
}
