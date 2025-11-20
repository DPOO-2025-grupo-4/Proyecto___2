package Pruebas;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Usuarios.Cliente;
import repositorios.RepositorioOfertas;
import repositorios.OfertaReventa;
import Eventos.Evento;
import Eventos.Localidad;
import tiquetes.TiqueteIndividual;

/**
 * Pruebas para el marketplace de BoletaMaster,
 * ajustadas a las clases reales del proyecto.
 */
public class TestMarketPlace {

    /**
     * Crea un cliente de prueba con datos mínimos válidos.
     */
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

    private TiqueteIndividual crearTiquetePara(Cliente duenio) {
        Evento evento = new Evento();       
        Localidad localidad = new Localidad();
        localidad.setPrecio(100000.0);      
        TiqueteIndividual tiquete = new TiqueteIndividual(evento, localidad, duenio);

        duenio.agregarTiquete(tiquete);

        return tiquete;
    }

    @Test
    public void testPublicarTiqueteEnMarketplaceLoDejaActivoEnRepositorio() {
        RepositorioOfertas repo = new RepositorioOfertas();

        Cliente vendedor = crearCliente("vendedor1");
        TiqueteIndividual tiquete = crearTiquetePara(vendedor);

        double precio = 50_000.0;

        OfertaReventa oferta = vendedor.publicarTiqueteEnMarketplace(tiquete, precio, repo);

        assertNotNull(oferta, "La oferta no debería ser null");
        assertTrue(oferta.estaActiva(), "La oferta debe estar activa justo después de publicarse");
        assertEquals(precio, oferta.getPrecio(), 0.0001, "El precio de la oferta debe coincidir");
        assertEquals(vendedor, oferta.getVendedor(), "El vendedor debe ser el cliente que publicó");
        assertEquals(1, repo.obtenerActivas().size(), "Debe haber exactamente una oferta activa en el repositorio");
        assertTrue(repo.obtenerActivas().contains(oferta), "La oferta debe aparecer en las activas");
    }

    @Test
    public void testEjecutarVentaTransfiereTiqueteYActualizaSaldos() {
        RepositorioOfertas repo = new RepositorioOfertas();

        Cliente vendedor = crearCliente("vendedor2");
        Cliente comprador = crearCliente("comprador2");

        TiqueteIndividual tiquete = crearTiquetePara(vendedor);

        double precio = 80000.;

        OfertaReventa oferta = vendedor.publicarTiqueteEnMarketplace(tiquete, precio, repo);

        comprador.acreditarSaldo(precio);

        double saldoVendedorAntes = vendedor.getSaldo();
        double saldoCompradorAntes = comprador.getSaldo();

        comprador.comprarOfertaReventa(oferta, repo);

        assertFalse(oferta.estaActiva(), "La oferta debe dejar de estar activa tras la venta");
        assertFalse(repo.obtenerActivas().contains(oferta),
                "La oferta vendida no debe aparecer en las ofertas activas");

        assertEquals(saldoVendedorAntes + precio, vendedor.getSaldo(), 0.0001,
                "El saldo del vendedor debe aumentar en el valor de la venta");
        assertEquals(saldoCompradorAntes - precio, comprador.getSaldo(), 0.0001,
                "El saldo del comprador debe disminuir en el valor de la venta");

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
        TiqueteIndividual tiquete = crearTiquetePara(vendedor);

        double precio = 60_000.0;

        OfertaReventa oferta = vendedor.publicarTiqueteEnMarketplace(tiquete, precio, repo);

        assertTrue(oferta.estaActiva(), "La oferta debe iniciar activa");

        oferta.cancelarPorVendedor();

        assertFalse(oferta.estaActiva(), "La oferta cancelada por el vendedor no debe estar activa");
        assertFalse(repo.obtenerActivas().contains(oferta),
                "La oferta cancelada no debe aparecer en las activas");
    }
    @Test
    public void testRegistrarContraoferta() {
        RepositorioOfertas repo = new RepositorioOfertas();

        Cliente vendedor = crearCliente("vendedorContra");
        Cliente comprador = crearCliente("compradorContra");

        TiqueteIndividual tiquete = crearTiquetePara(vendedor);
        double precio = 70_000.0;

        OfertaReventa oferta = vendedor.publicarTiqueteEnMarketplace(tiquete, precio, repo);

        double nuevoPrecio = 60_000.0;
        comprador.hacerContraoferta(oferta, nuevoPrecio);

        assertTrue(oferta.tieneContraofertaPendiente(), "Debe haber una contraoferta pendiente");
        assertEquals(comprador, oferta.getCompradorProponente(), "El proponente debe ser el comprador");
        assertEquals(nuevoPrecio, oferta.getPrecioPropuesto(), 0.0001, "El precio propuesto debe coincidir");
    }
    @Test
    public void testAceptarContraofertaEjecutaVenta() {
        RepositorioOfertas repo = new RepositorioOfertas();

        Cliente vendedor = crearCliente("vendedorContra2");
        Cliente comprador = crearCliente("compradorContra2");

        TiqueteIndividual tiquete = crearTiquetePara(vendedor);
        double precioInicial = 90_000.0;

        OfertaReventa oferta = vendedor.publicarTiqueteEnMarketplace(tiquete, precioInicial, repo);

        double precioPropuesto = 75_000.0;
        comprador.hacerContraoferta(oferta, precioPropuesto);

        // Aseguramos saldo suficiente para el precio propuesto
        comprador.acreditarSaldo(precioPropuesto);

        double saldoVendedorAntes = vendedor.getSaldo();
        double saldoCompradorAntes = comprador.getSaldo();

        // El vendedor acepta la contraoferta
        vendedor.aceptarContraoferta(oferta);

        // La oferta ya debe estar vendida
        assertFalse(oferta.estaActiva(), "La oferta no debe seguir activa tras aceptar la contraoferta");
        assertEquals(saldoVendedorAntes + precioPropuesto, vendedor.getSaldo(), 0.0001);
        assertEquals(saldoCompradorAntes - precioPropuesto, comprador.getSaldo(), 0.0001);

        // El tiquete ahora debe ser del comprador
        assertTrue(comprador.getTiquet().contains(tiquete), "El tiquete debe pasar al comprador");
        assertFalse(vendedor.getTiquet().contains(tiquete), "El vendedor ya no debe tener el tiquete");
        assertEquals(comprador, tiquete.getDuenoActual(), "El dueño actual debe ser el comprador");
    }

    @Test
    public void testRechazarContraofertaLimpiaYMantieneOfertaActiva() {
        RepositorioOfertas repo = new RepositorioOfertas();

        Cliente vendedor = crearCliente("vendedorContra3");
        Cliente comprador = crearCliente("compradorContra3");

        TiqueteIndividual tiquete = crearTiquetePara(vendedor);
        double precio = 65_000.0;

        OfertaReventa oferta = vendedor.publicarTiqueteEnMarketplace(tiquete, precio, repo);

        comprador.hacerContraoferta(oferta, 55_000.0);

        assertTrue(oferta.tieneContraofertaPendiente(), "Antes de rechazar debe haber contraoferta");

        vendedor.rechazarContraoferta(oferta);

        assertFalse(oferta.tieneContraofertaPendiente(), "Después de rechazar no debe haber contraoferta pendiente");
        assertNull(oferta.getCompradorProponente(), "Comprador proponente debe quedar null");
        assertNull(oferta.getPrecioPropuesto(), "Precio propuesto debe quedar null");
        assertTrue(oferta.estaActiva(), "La oferta debe seguir activa tras rechazar la contraoferta");
    }
    @Test
    public void testObtenerOfertasDeVendedorSoloActivas() {
        RepositorioOfertas repo = new RepositorioOfertas();

        Cliente vendedor1 = crearCliente("vendA");
        Cliente vendedor2 = crearCliente("vendB");

        TiqueteIndividual t1 = crearTiquetePara(vendedor1);
        TiqueteIndividual t2 = crearTiquetePara(vendedor1);
        TiqueteIndividual t3 = crearTiquetePara(vendedor2);

        OfertaReventa o1 = vendedor1.publicarTiqueteEnMarketplace(t1, 40_000.0, repo);
        OfertaReventa o2 = vendedor1.publicarTiqueteEnMarketplace(t2, 50_000.0, repo);
        OfertaReventa o3 = vendedor2.publicarTiqueteEnMarketplace(t3, 60_000.0, repo);

        // Cancelamos una de vendedor1
        o2.cancelarPorVendedor();

        var ofertasVend1 = repo.obtenerOfertasDeVendedor(vendedor1);
        var ofertasVend2 = repo.obtenerOfertasDeVendedor(vendedor2);

        // Solo o1 (activa) debe aparecer para vendedor1
        assertEquals(1, ofertasVend1.size(), "vendedor1 debe tener 1 oferta activa");
        assertTrue(ofertasVend1.contains(o1));
        assertFalse(ofertasVend1.contains(o2));

        // Para vendedor2 solo debe aparecer o3
        assertEquals(1, ofertasVend2.size(), "vendedor2 debe tener 1 oferta activa");
        assertTrue(ofertasVend2.contains(o3));
    }

}
