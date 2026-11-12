package model;

import model.exception.EmpresaNoAceptaException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RapidoExpressTest {

    private final RapidoExpress empresa = new RapidoExpress();

    @Test
    void aceptaRecorridoPositivoHastaDoceKm() {
        assertTrue(empresa.aceptaRecorrido(0.5));
        assertTrue(empresa.aceptaRecorrido(12));
    }

    @Test
    void noAceptaRecorridoNoPositivoNiMayorADoceKm() {
        assertFalse(empresa.aceptaRecorrido(0));
        assertFalse(empresa.aceptaRecorrido(12.01));
    }

    @Test
    void calcularTarifaSumaBaseMasKilometros() {
        assertEquals(11000, empresa.calcularTarifa(4), 0.001);
    }

    @Test
    void calcularTarifaRechazaRecorridoLargo() {
        assertThrows(EmpresaNoAceptaException.class, () -> empresa.calcularTarifa(13));
    }
}
