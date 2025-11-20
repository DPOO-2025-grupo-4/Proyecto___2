package tiquetes;

import Eventos.Evento;
import Eventos.Localidad;
import Eventos.Oferta;
import Usuarios.Cliente;

/**
 * Tiquete normal para una localidad de un evento.
 */
public class TiqueteIndividual extends Tiquete {

    private static int contadorIds = 1;

    private Localidad localidad;

        
        public TiqueteIndividual() {
            super();
        }

    public TiqueteIndividual(Evento evento, Localidad localidad) {
        if (evento == null || localidad == null) {
            throw new IllegalArgumentException("evento y localidad no pueden ser nulos");
        }
        this.id = "T-" + contadorIds++;
        this.evento = evento;
        this.localidad = localidad;
        this.duenoActual = null;
    }

    public TiqueteIndividual(Evento evento, Localidad localidad, Cliente comprador) {
        this(evento, localidad);
        this.duenoActual = comprador;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    @Override
    public double getPrecioTotal() {
        double precio = localidad.getPrecio();

        if (localidad.tieneOferta()) {
            Oferta oferta = localidad.getOferta();
            if (oferta != null) {
                double descuento = oferta.aplicarDescuento(); // usa precio de la localidad
                precio = precio - descuento;
            }
        }

        if (evento != null) {
            double porcentaje = evento.getPorcentajeServicio();
            double emision = evento.getCobroEmision();

            double recargo = precio * porcentaje / 100.0;
            precio = precio + recargo + emision;
        }

        return precio;
    }
}
