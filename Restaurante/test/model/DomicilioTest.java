package model;

import model.exception.EmpresaNoAceptaException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DomicilioTest {

    @Test
    void calcularValorUsaLaTarifaDeRapidoExpress() {
        Domicilio domicilio = new Domicilio(3, new RapidoExpress());
        assertEquals(9500, domicilio.calcularValor(), 0.001);
    }

    @Test
    void calcularValorUsaLaTarifaDeEntregaSeguraSinRecargo() {
        Domicilio domicilio = new Domicilio(3, new EntregaSegura());
        assertEquals(10000, domicilio.calcularValor(), 0.001);
    }

    @Test
    void calcularValorUsaLaTarifaDeEntregaSeguraConRecargo() {
        Domicilio domicilio = new Domicilio(6, new EntregaSegura());
        assertEquals(16000, domicilio.calcularValor(), 0.001);
    }

    @Test
    void rechazaUnRecorridoQueLaEmpresaNoAcepta() {
        assertThrows(EmpresaNoAceptaException.class, () -> new Domicilio(13, new RapidoExpress()));
    }

    @Test
    void rechazaUnaDistanciaNoPositiva() {
        assertThrows(IllegalArgumentException.class, () -> new Domicilio(0, new RapidoExpress()));
    }

    @Test
    void rechazaUnaEmpresaNula() {
        assertThrows(IllegalArgumentException.class, () -> new Domicilio(3, null));
    }
}
