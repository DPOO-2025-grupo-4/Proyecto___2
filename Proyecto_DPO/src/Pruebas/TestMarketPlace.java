package Pruebas;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import Usuarios.Cliente;
import Logs.LogSystem;
import repositorios.OfertaReventa;
import repositorios.RepositorioOfertas;
import tiquetes.Tiquete;

/**
 * Pruebas básicas para la funcionalidad de marketplace y el sistema de logs.
 */
public class TestMarketPlace {

    /**
     * Tiquete dummy para pruebas: solo implementa getPrecioTotal.
     * No depende de Evento ni Localidad.
     */
    private static class TiqueteDummy extends Tiquete {

        public TiqueteDummy(String id) {
            this.id = id;
        }

        @Override
        public double getPrecioTotal() {
            return 100_000.0;
        }
    }

    private Cliente crearCliente(String login) {
        return new Cliente(
                "Nombre " + login,
                login + "@mail.com",
                login,
                "1234",
                "123456789",
                "3000000000"
        );
    }

    @Test
    public void testLogSystemRegistraEventos() {
        // Tamaño antes
        int antes = LogSystem.obtenerRegistros().size();

        LogSystem.registrar("TEST_EVENTO", "Mensaje de prueba para el log");

        List<String> registros = LogSystem.obtenerRegistros();
        assertEquals(antes + 1, registros.size(), "Debe haberse agregado una nueva línea al log");

        String ultimo = registros.get(registros.size() - 1);
        assertTrue(ultimo.contains("TEST_EVENTO"));
        assertTrue(ultimo.contains("Mensaje de prueba"));
    }

    @Test
    public void testPublicarTiqueteEnMarketplaceLoDejaActivoEnRepositorio() {
        RepositorioOfertas repo = new RepositorioOfertas();

        Cliente vendedor = crearCliente("vendedor");
        TiqueteDummy tiquete = new TiqueteDummy("T-1");

        // El vendedor debe tener el tiquete
        vendedor.agregarTiquete(tiquete);
        tiquete.setDuenoActual(vendedor);

        double precio = 50_000.0;

        OfertaReventa oferta = vendedor.publicarTiqueteEnMarketplace(tiquete, precio, repo);

        assertNotNull(oferta, "La oferta no debería ser null");
        assertTrue(oferta.estaActiva(), "La oferta debe estar activa justo después de crearse");

        List<OfertaReventa> activas = repo.obtenerActivas();
        assertEquals(1, activas.size(), "Debe haber exactamente una oferta activa en el repositorio");
        assertTrue(activas.contains(oferta), "La oferta publicada debe estar en la lista de activas");
    }

    @Test
    public void testEjecutarVentaTransfiereTiqueteYActualizaSaldos() {
        RepositorioOfertas repo = new RepositorioOfertas();

        Cliente vendedor = crearCliente("vendedor2");
        Cliente comprador = crearCliente("comprador2");

        TiqueteDummy tiquete = new TiqueteDummy("T-2");
        vendedor.agregarTiquete(tiquete);
        tiquete.setDuenoActual(vendedor);

        double precio = 80_000.0;

        // Publicar oferta
        OfertaReventa oferta = vendedor.publicarTiqueteEnMarketplace(tiquete, precio, repo);

        // El comprador debe tener saldo suficiente
        comprador.acreditarSaldo(precio);

        double saldoVendedorAntes = vendedor.getSaldo();
        double saldoCompradorAntes = comprador.getSaldo();

        // Ejecutar venta
        comprador.comprarOfertaReventa(oferta, repo);

        // La oferta ya no debe estar activa
        assertFalse(oferta.estaActiva(), "La oferta debe dejar de estar activa tras la venta");
        assertFalse(repo.obtenerActivas().contains(oferta),
                "La oferta vendida no debe aparecer en las ofertas activas");

        // Saldos
        assertEquals(saldoVendedorAntes + precio, vendedor.getSaldo(), 0.001,
                "El saldo del vendedor debe aumentar en el valor de la venta");
        assertEquals(saldoCompradorAntes - precio, comprador.getSaldo(), 0.001,
                "El saldo del comprador debe disminuir en el valor de la venta");

        // Propiedad del tiquete
        assertTrue(comprador.getTiquet().contains(tiquete),
                "El tiquete debe pasar al comprador");
        assertFalse(vendedor.getTiquet().contains(tiquete),
                "El vendedor ya no debe tener el tiquete");
        assertEquals(comprador, tiquete.getDuenoActual(),
                "El dueño actual del tiquete debe ser el comprador");
    }

    @Test
    public void testCancelarOfertaPorVendedorLaVuelveInactiva() {
        RepositorioOfertas repo = new RepositorioOfertas();

        Cliente vendedor = crearCliente("vendedor3");
        TiqueteDummy tiquete = new TiqueteDummy("T-3");
        vendedor.agregarTiquete(tiquete);
        tiquete.setDuenoActual(vendedor);

        OfertaReventa oferta = vendedor.publicarTiqueteEnMarketplace(tiquete, 60_000.0, repo);

        assertTrue(oferta.estaActiva(), "La oferta debe iniciar activa");

        // El vendedor cancela la oferta
        oferta.cancelarPorVendedor();

        assertFalse(oferta.estaActiva(), "La oferta cancelada por el vendedor no debe estar activa");
        assertFalse(repo.obtenerActivas().contains(oferta),
                "La oferta cancelada no debe aparecer en las activas");
    }
}
